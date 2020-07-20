package com.example.rightbiotic;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;

import com.example.rightbiotic.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Path = "path";
    public static String[] monthLookUp = {"ETC", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    boolean click = true;
    public boolean connection;
    Dialog dialog;
    public String fileName;
    public String finalPath;
    InputStream inputStream;
    TextView ipadd;
    Button mainBtn;
    Button openFiles;
    OutputStream outputStream;
    TextView outputView;
    TextView pathDir;
    String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_WIFI_STATE", "android.permission.INTERNET"};
    PopupWindow popUp;
    Button popUpClose;
    byte[] readBuffer;
    int readBufferPosition;
    public Uri selectedFile;
    public static ServerSocket serverSocket;
    Thread serverThread = null;
    SharedPreferences sharedpreferences;
    BluetoothSocket socket;
    Button startServer;
    volatile boolean stopWorker;
    Handler updateConversationHandler;
    String value = "";
    Thread workerThread;

    class CommunicationThread implements Runnable {
        List<String> Datalist = new ArrayList();
        private Socket clientSocket;
        private BufferedReader input;
        String ipadd = "";
        int nooflines = 0;
        String read;

        CommunicationThread(Socket clientSocket2) {
            this.clientSocket = clientSocket2;
            this.ipadd = clientSocket2.getInetAddress().toString();
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String readLine = this.input.readLine();
                this.read = readLine;
                this.Datalist.add(readLine);
                MainActivity.this.updateConversationHandler.post(new updateUIThread(this.read, this.ipadd));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    String readLine2 = this.input.readLine();
                    if (readLine2 == null) {
                        break;
                    }
                    this.nooflines++;
                    if (readLine2.contains("END")) {
                        this.Datalist.add(readLine2);
                        break;
                    }
                    this.Datalist.add(readLine2);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                this.input.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            try {
                this.clientSocket.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            if (MainActivity.this.MakeDirectory((String) this.Datalist.get(1))) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.pathDir.setText(MainActivity.this.finalPath);
                    }
                });
                new File(MainActivity.this.finalPath);
                String[] pid = ((String) this.Datalist.get(0)).split(":");
                if (pid.length > 0 && pid[1].length() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(pid[1]);
                    sb.append("-");
                    sb.append(MainActivity.this.fileName);
                    sb.append(".txt");
                    String sb2 = sb.toString();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(Environment.getExternalStorageDirectory());
                    sb3.append(MainActivity.this.finalPath);
                    File newFile = new File(sb3.toString(), sb2);
                    try {
                        if (!newFile.getParentFile().exists()) {
                            newFile.getParentFile().mkdirs();
                        }
                        if (!newFile.exists()) {
                            boolean outcome = newFile.createNewFile();
                        }
                        FileOutputStream fOut = new FileOutputStream(newFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        for (String item : this.Datalist) {
                            StringBuilder sb22 = new StringBuilder();
                            sb22.append(item);
                            sb22.append("\r\n");
                            myOutWriter.append(sb22.toString());
                        }
                        myOutWriter.flush();
                        fOut.close();
                    } catch (IOException e32) {
                        Log.d("File write:", String.valueOf(false));
                        e32.printStackTrace();
                    }
                }
            } else {
                Log.d("Cannot Store:", "File");
            }
        }
    }

    class ServerThread implements Runnable {
        ServerThread() {
        }

        public void run() {
            try {
                serverSocket = new ServerSocket(3000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    new Thread(new CommunicationThread(serverSocket.accept())).start();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable {
        private String ipaddress;
        private String msg;

        public updateUIThread(String str, String add) {
            this.msg = str;
            this.ipaddress = add;
        }

        public void run() {
            TextView textView = MainActivity.this.outputView;
            StringBuilder sb = new StringBuilder();
            sb.append(MainActivity.this.outputView.getText().toString());
            sb.append("Received From: ");
            sb.append(this.ipaddress);
            sb.append(": ");
            sb.append(this.msg);
            sb.append("\n");
            textView.setText(sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, this.permissions, 1);
        this.pathDir = (TextView) findViewById(R.id.pathOfFolder);
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, 0);
        this.sharedpreferences = sharedPreferences;
        this.pathDir.setText(sharedPreferences.getString(Path, "/storage/sdcard0/RightBiotic"));
        this.openFiles = (Button) findViewById(R.id.showFiles);
        TextView textView = (TextView) findViewById(R.id.serverAddress);
        this.ipadd = textView;
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        textView.setText(ip);
        TextView textView2 = (TextView) findViewById(R.id.outputTextView);
        this.outputView = textView2;
        textView2.setMovementMethod(new ScrollingMovementMethod());
        StartServer();
    }

//    public void popUp() {
//        this.popUp = new PopupWindow(this);
//        final LinearLayout layout = new LinearLayout(this);
//        LinearLayout mainLayout = new LinearLayout(this);
//        TextView tv = new TextView(this);
//        Button but = new Button(this);
//        but.setText("Click Me");
//        but.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                if (MainActivity.this.click) {
//                    MainActivity.this.popUp.showAtLocation(layout, 80, 10, 10);
//                    MainActivity.this.popUp.update(50, 50, 300, 80);
//                    MainActivity.this.click = false;
//                    return;
//                }
//                MainActivity.this.popUp.dismiss();
//                MainActivity.this.click = true;
//            }
//        });
//        LayoutParams params = new LayoutParams(-2, -2);
//        layout.setOrientation(1);
//        tv.setText("Hi this is a sample text for popup window");
//        layout.addView(tv, params);
//        this.popUp.setContentView(layout);
//        mainLayout.addView(but, params);
//        setContentView((View) mainLayout);
//    }

    public void StartServer() {
        this.updateConversationHandler = new Handler();
        Thread thread = new Thread(new ServerThread());
        this.serverThread = thread;
        thread.start();
    }

    private void verifyPermissions() {
        Log.d("MainActivity", "verifyPermissions: asking user for permission");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), this.permissions[0]) != 0 || ContextCompat.checkSelfPermission(getApplicationContext(), this.permissions[1]) != 0 || ContextCompat.checkSelfPermission(getApplicationContext(), this.permissions[2]) != 0 || ContextCompat.checkSelfPermission(getApplicationContext(), this.permissions[3]) != 0 || ContextCompat.checkSelfPermission(getApplicationContext(), this.permissions[4]) != 0) {
            ActivityCompat.requestPermissions(this, this.permissions, 1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions2, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions2, grantResults);
    }

    public boolean MakeDirectory(String pa) {
        String[] das = pa.split("[/:]");
        String[] yea = das[2].split(" ");
        String str = "/";
        StringBuilder sb = new StringBuilder();
        sb.append("/RightBiotic/20");
        sb.append(yea[0]);
        sb.append(str);
        sb.append(monthLookUp[Integer.parseInt(das[1])]);
        sb.append(str);
        sb.append(das[0]);
        this.finalPath = sb.toString();
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), this.finalPath);
        if (!mediaStorageDir.exists()) {
            return mediaStorageDir.mkdirs();
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == -1) {
            this.selectedFile = data.getData();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(this.selectedFile)));
                this.outputView.setText("");
                while (true) {
                    String rtemp = br.readLine();
                    if (!rtemp.contains("END")) {
                        TextView textView = this.outputView;
                        StringBuilder sb = new StringBuilder();
                        sb.append(rtemp);
                        sb.append("\n");
                        textView.append(sb.toString());
                    } else {
                        return;
                    }
                }
            } catch (IOException e) {
                Log.d("error", "ERROR");
            }
        }
    }

    public void OnCLickExit(View view) {
        finish();
    }

    @SuppressLint("WrongConstant")
    public void OnClickPrint(View view) {
        if (((BluetoothManager) getApplicationContext().getSystemService("bluetooth")).getConnectedDevices(7).isEmpty()) {
            startActivityForResult(new Intent("android.settings.BLUETOOTH_SETTINGS"), 0);
        } else {
        }
    }

    public void IntentPrint(String txtvalue) {
        byte[] PrintHeader = {-86, 85, 2, 0};
        PrintHeader[3] = (byte) txtvalue.getBytes().length;
        InitPrinter();
        if (PrintHeader.length > 128) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.value);
            sb.append("\nValue is more than 128 size\n");
            String sb2 = sb.toString();
            this.value = sb2;
            Toast.makeText(this, sb2, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            this.outputStream.write(txtvalue.getBytes());
            this.outputStream.close();
            this.socket.close();
        } catch (Exception ex) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(this.value);
            sb3.append(ex.toString());
            sb3.append("\nExcep IntentPrint \n");
            String sb4 = sb3.toString();
            this.value = sb4;
            Toast.makeText(this, sb4, Toast.LENGTH_LONG).show();
        }
    }

    public void InitPrinter() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        try {
            if (!defaultAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                Iterator it = pairedDevices.iterator();
                if (it.hasNext()) {
                    this.bluetoothDevice = (BluetoothDevice) it.next();
                }
                UUID fromString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                this.socket = (BluetoothSocket) this.bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE}).invoke(this.bluetoothDevice, new Object[]{Integer.valueOf(1)});
                this.bluetoothAdapter.cancelDiscovery();
                this.socket.connect();
                this.outputStream = this.socket.getOutputStream();
                this.inputStream = this.socket.getInputStream();
                beginListenForData();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.value);
            sb.append("No Devices found");
            String sb2 = sb.toString();
            this.value = sb2;
            Toast.makeText(this, sb2, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(this.value);
            sb3.append(ex.toString());
            sb3.append("\n InitPrinter \n");
            String sb4 = sb3.toString();
            this.value = sb4;
            Toast.makeText(this, sb4, Toast.LENGTH_LONG).show();
        }
    }

    /* access modifiers changed from: 0000 */
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            this.stopWorker = false;
            this.readBufferPosition = 0;
            this.readBuffer = new byte[1024];
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !MainActivity.this.stopWorker) {
                        try {
                            int bytesAvailable = MainActivity.this.inputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                MainActivity.this.inputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == 10) {
                                        byte[] encodedBytes = new byte[MainActivity.this.readBufferPosition];
                                        System.arraycopy(MainActivity.this.readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                        MainActivity.this.readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.d("e", data);
                                            }
                                        });
                                    } else {
                                        byte[] bArr = MainActivity.this.readBuffer;
                                        MainActivity mainActivity = MainActivity.this;
                                        int i2 = mainActivity.readBufferPosition;
                                        mainActivity.readBufferPosition = i2 + 1;
                                        bArr[i2] = b;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            MainActivity.this.stopWorker = true;
                        }
                    }
                }
            });
            this.workerThread = thread;
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}