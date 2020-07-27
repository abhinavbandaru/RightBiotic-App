package com.example.rightbiotic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    PdfDocument.PageInfo pageInfo = null;
    PdfDocument.Page page = null;
    Canvas canvas = null;
    Dialog dialog;
    public String fileName;
    public String finalPath;
    private static final int READ_REQUEST_CODE = 42;
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
                File newFile;
                FileOutputStream fOut;
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
                    newFile = new File(sb3.toString(), sb2);
                    StringBuilder lol = new StringBuilder();
                    boolean outcome = false;
                    try {
                        if (!newFile.getParentFile().exists()) {
                            newFile.getParentFile().mkdirs();
                        }
                        if (!newFile.exists()) {
                            outcome = newFile.createNewFile();
                        }
                        fOut = new FileOutputStream(newFile);

                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        for (String item : this.Datalist) {
                            StringBuilder sb22 = new StringBuilder();
                            sb22.append(item);
                            lol.append(item);
                            sb22.append("\r\n");
                            lol.append("\r\n");
                            myOutWriter.append(sb22.toString());
                        }
                        myOutWriter.flush();
                        fOut.close();

                    } catch (IOException e32) {
                        Log.d("File write:", String.valueOf(outcome));
                        e32.printStackTrace();
                    }

                    try {
                        PdfDocument pdfDocument = new PdfDocument();
                        Paint paint = new Paint();
                        pageInfo = new PdfDocument.PageInfo.Builder(1200,2120, 1).create();
                        page = pdfDocument.startPage(pageInfo);
                        canvas = page.getCanvas();
                        String[] str = lol.toString().split("\n");
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(35f);
                        paint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(str[0], 20, 50, paint);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        canvas.drawRect(20, 80, 1180, 860, paint);
                        paint.setTextAlign(Paint.Align.LEFT);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawText("Sno.", 40, 830, paint);
                        canvas.drawText("Anti-Biotic", 200, 830, paint);
                        canvas.drawText("Result", 700, 830, paint);

                        canvas.drawLine(180,790, 180, 840, paint);
                        canvas.drawLine(680,790, 680, 840, paint);
                        int x = 0;
                        for (String line: str) {
                            if(!line.equals("END") && x>4){
                                canvas.drawText(Integer.toString(x-4), 40, 950 + (x-5)*100, paint);
                                String[] val = line.split(",");
                                canvas.drawText(val[0], 200, 950 + (x-5)*100, paint);
                                canvas.drawText(val[1], 700, 950 + (x-5)*100, paint);
                            }
                            x++;
                            if(line.equals("END"))
                                break;
                        }
                        pdfDocument.finishPage(page);
                        pdfDocument.writeTo(new FileOutputStream(newFile));
                    } catch (Exception e){
                        e.printStackTrace();
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, this.permissions, 1);
        this.pathDir = (TextView) findViewById(R.id.pathOfFolder);
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, 0);
        this.sharedpreferences = sharedPreferences;
        this.pathDir.setText(sharedPreferences.getString(Path, "/storage/sdcard0/RightBiotic"));
        this.openFiles = (Button) findViewById(R.id.showFiles);
        openFiles.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                performSearch();
            }
        });
        this.ipadd = findViewById(R.id.serverAddress);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        this.ipadd.setText(ip);
        this.outputView = findViewById(R.id.outputTextView);
        outputView.setMovementMethod(new ScrollingMovementMethod());
        StartServer();
    }

    public String readText(String input){
        File file = new File(Environment.getExternalStorageDirectory(), input);
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine())!= null){
                sb.append(line);
                sb.append("\n");
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void performSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

    }

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

    public void onRequestPermissionsResult(int requestCode, String[] permissions2, @NonNull int[] grantResults) {
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
        else if(requestCode == READ_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            Uri uri = data.getData();
            String path = uri.getPath();
            path = path.substring(path.indexOf(":")+1);
            if(path.contains("emulated")) {
                path = path.substring(path.indexOf("0") + 1);
            }
            outputView.setText(readText(path));
        }
    }


}