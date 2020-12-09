package com.example.rightbiotic;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Path = "path";

    public static String[] monthLookUp = {"ETC", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public String finalPath;
    Calculation calculation;
    private static final int READ_REQUEST_CODE = 42;
    float[] PP1_450nm = new float[8], PP2_450nm = new float[8], PP3_450nm = new float[8], PP4_450nm = new float[8], PP5_450nm = new float[8], PP6_450nm = new float[8], PP7_450nm = new float[8], PP8_450nm = new float[8];
    float[] PP1_630nm = new float[8], PP2_630nm = new float[8], PP3_630nm = new float[8], PP4_630nm = new float[8], PP5_630nm = new float[8], PP6_630nm = new float[8], PP7_630nm = new float[8], PP8_630nm = new float[8];
    float[] Pi_450nm = new float[8]; float[] PiN_450nm = new float[8];
    float[] Pi_630nm = new float[8]; float[] PiN_630nm = new float[8];
    TextView ipadd;
    Button openFiles;
    TextView outputView;
    TextView pathDir;
    String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_WIFI_STATE", "android.permission.INTERNET"};
    List<String> fileList;
    List<String> filePathList;
    ListView listView;

    public Uri selectedFile;
    public static ServerSocket serverSocket;
    Thread serverThread = null;
    ArrayAdapter<String> directoryList;
    SharedPreferences sharedpreferences;
    Handler updateConversationHandler;
    boolean serverStatus = false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, this.permissions, 1);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, 0);
//        listView = findViewById(R.id.pdfListView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                viewPdf(fileList.get(i), filePathList.get(i));
//            }
//        });
        this.sharedpreferences = sharedPreferences;
//        this.pathDir.setText(sharedPreferences.getString(Path, "/storage/sdcard0/RightBiotic"));
//        this.openFiles = findViewById(R.id.showFiles);
//        openFiles.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick (View v){
//                performSearch();
//            }
//        });
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }
        if(!isWifiConn){
            this.ipadd = findViewById(R.id.serverAddress);
            ipadd.setText("Please Connect to a Wifi and restart the App");
        } else {
            this.ipadd = findViewById(R.id.serverAddress);
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            this.ipadd.setText(ip);
            this.outputView = findViewById(R.id.outputTextView);
            outputView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    void refreshList(){
        fileList = new ArrayList<>();
        filePathList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/RightBiotic";
        System.out.println("Path: " + path);
        File rbFolder = new File(path);
        List<File> allFiles = getListFiles(rbFolder);
        for(File f: allFiles){
            System.out.println(f.getName());
            fileList.add(f.getName());
            filePathList.add(f.getParent());
        }
        directoryList = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_expandable_list_item_1, fileList);
//        listView.setAdapter(directoryList);
    }

    List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    void viewPdf(String filename, String filePath){
        File f = new File(filePath, filename);
        Uri path = Uri.fromFile(f);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pdfIntent);
    }

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
                        //MainActivity.this.pathDir.setText(MainActivity.this.finalPath);
                    }
                });
                int y = 60;
                new File(MainActivity.this.finalPath);
                String[] pid = ((String) this.Datalist.get(0)).split(":");
                System.out.println(Datalist);
                if (pid.length > 0 && pid[1].length() > 1) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(Environment.getExternalStorageDirectory());
                    sb3.append(MainActivity.this.finalPath);

                    PdfDocument myPdfDocument = new PdfDocument();
                    Paint myPaint = new Paint();
                    myPaint.setTextSize(12f);
                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(595,842,1).create();
                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                    Canvas canvas  = myPage1.getCanvas();
                    canvas.drawLine(30,  22+y, myPageInfo1.getPageWidth()-30, 22+y, myPaint);
                    canvas.drawLine(30,  47+y, myPageInfo1.getPageWidth()-30, 47+y, myPaint);
                    canvas.drawLine(30,  65+y, myPageInfo1.getPageWidth()-30, 65+y, myPaint);
                    canvas.drawLine(30,  85+y, myPageInfo1.getPageWidth()-30, 85+y, myPaint);
                    canvas.drawLine(30,  105+y, myPageInfo1.getPageWidth()-30, 105+y, myPaint);
                    canvas.drawLine(30,  22+y, 30, 105+y, myPaint);
                    canvas.drawLine(myPageInfo1.getPageWidth()-30,  22+y, myPageInfo1.getPageWidth()-30, 105+y, myPaint);
                    canvas.drawLine(345,  47+y, 345, 85+y, myPaint);
                    canvas.drawLine(30,  47+y, myPageInfo1.getPageWidth()-30, 47+y, myPaint);
                    myPaint.setTextSize(18f);
                    myPaint.setFakeBoldText(true);
                    myPaint.setUnderlineText(true);
                    myPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("Laboratory Report", 298, 40+y,myPaint);
                    myPaint.setTextSize(12f);
                    myPaint.setFakeBoldText(false);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setUnderlineText(false);
                    EditText pname = findViewById(R.id.textPatientName), dname = findViewById(R.id.textDoctorName), page = findViewById(R.id.textPatientAge), psex = findViewById(R.id.textPatientGender),sdate = findViewById(R.id.textSampleDate);
                    String patientName =  pname.getText().toString(), age = page.getText().toString(), sex = psex.getText().toString(), sampleDate = sdate.getText().toString(), reportDate, referredBy = "Dr. " + dname.getText().toString();
                    String[] te = Datalist.get(1).split(" ");
                    reportDate = te[0];
                    canvas.drawText("Patient Name : "+patientName, 32, 60+y,myPaint);
                    canvas.drawText("Sample Date : "+sampleDate, 350, 60+y,myPaint);
                    canvas.drawText("Age/Sex           : "+age + " / "+sex, 32, 80+y,myPaint);
                    canvas.drawText("Report Date   : "+reportDate, 350, 80+y,myPaint);
                    canvas.drawText("Referred By      : "+referredBy, 32, 100+y,myPaint);
                    String f = (String) Datalist.get(2);
                    myPaint.setTextSize(18f);
                    myPaint.setTextAlign(Paint.Align.CENTER);
                    myPaint.setUnderlineText(true);
                    myPaint.setFakeBoldText(true);
                    canvas.drawText("CULTURE  REPORT",298, 130+y,myPaint);
                    myPaint.setTextSize(12f);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setUnderlineText(false);
                    myPaint.setFakeBoldText(false);
                    canvas.drawText("Sample                 : "+f, 30, 150+y, myPaint);
                    f = (String) Datalist.get(3);
                    canvas.drawText("Organism Name : "+f, 30, 170+y, myPaint);
                    f = (String) Datalist.get(4);

                    canvas.drawText("Volume                 : "+f, 30, 190+y, myPaint);
                    int startY = 200+y;
                    canvas.drawLine(30, startY, myPageInfo1.getPageWidth()-30, startY, myPaint);
                    myPaint.setFakeBoldText(true);
                    canvas.drawText("S.no", 32, startY + 15, myPaint);
                    canvas.drawText("AntiBiotic Name",  70, startY+15, myPaint);
                    canvas.drawText("Result",  315, startY+15, myPaint);
                    myPaint.setFakeBoldText(false);
                    canvas.drawLine(30, startY + 20, myPageInfo1.getPageWidth()-30, startY+20, myPaint);
                    for(int i = 5; i+1<Datalist.size(); i++){
                        startY += 20;
                        String[] k1 = Datalist.get(i).split(",");
                        k1[0] = k1[0].trim();
                        canvas.drawText(Integer.toString(i-4), 40, startY + 15, myPaint);
                        canvas.drawText(k1[0], 70, startY + 15, myPaint);
                        if(k1[1].equals("S")){
                            myPaint.setColor(Color.rgb(34,139,34));
                        } else if(k1[1].equals("R")){
                            myPaint.setColor(Color.rgb(255,0,0));
                        }
                        myPaint.setFakeBoldText(true);
                        canvas.drawText(k1[1],  315, startY+15, myPaint);
                        myPaint.setFakeBoldText(false);
                        myPaint.setColor(Color.BLACK);
                        canvas.drawLine(30, startY + 20, myPageInfo1.getPageWidth()-30, startY+20, myPaint);
                    }
                    startY += 20;
                    canvas.drawLine(310, 200+y, 310, startY, myPaint);
                    canvas.drawLine(30, 200+y, 30, startY, myPaint);
                    canvas.drawLine(565, 200+y, 565, startY, myPaint);
                    canvas.drawLine(60, 200+y, 60, startY, myPaint);
                    startY += 20;
                    canvas.drawText("R: RESISTANT ", 30, startY,myPaint);
                    canvas.drawText("S: SENSITIVE ", 290, startY,myPaint);
                    canvas.drawText("I: INTERMEDIATE ", 470, startY,myPaint);
                    canvas.drawText("Only for Office Use", 32, startY+30,myPaint);
                    canvas.drawText("DEPARTMENT OF PATHOLOGY", 315, startY+30,myPaint);
                    canvas.drawText("RIGHTBIOTIC REPORT", 32, startY+50,myPaint);
                    canvas.drawText(referredBy, 315, startY+50,myPaint);
                    canvas.drawText("TECHNICIAN : ", 32, startY+80,myPaint);
                    canvas.drawText("Sign : ", 315, startY+80,myPaint);
                    canvas.drawLine(310, startY+15, 310, startY+85, myPaint);
                    canvas.drawLine(30, startY+15, 30, startY+85, myPaint);
                    canvas.drawLine(280, startY+15, 280, startY+85, myPaint);
                    canvas.drawLine(550, startY+15, 550, startY+85, myPaint);
                    canvas.drawLine(310, startY+15, 550, startY+15, myPaint);
                    canvas.drawLine(30, startY+15, 280, startY+15, myPaint);
                    canvas.drawLine(310, startY+85, 550, startY+85, myPaint);
                    canvas.drawLine(30, startY+85, 280, startY+85, myPaint);
                    myPdfDocument.finishPage(myPage1);
                    String fname = pid[1];
                    fname += ".pdf";
                    File pdfFile = new File(sb3.toString(), fname);
                    try {
                        myPdfDocument.writeTo(new FileOutputStream(pdfFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myPdfDocument.close();
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

    public void StartServer(View view) {
        if(serverStatus){
            Toast.makeText(this, "Server Already Running", Toast.LENGTH_SHORT).show();
            return;
        }
        serverStatus = true;
        Button temp = findViewById(R.id.startServerButton);
        temp.setText("Server Active");
        this.updateConversationHandler = new Handler();
        Thread thread = new Thread(new ServerThread());
        this.serverThread = thread;
        thread.start();
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

    public void goToFiles(View view){
        Intent i = new Intent(getApplicationContext(),FilesActivity.class);
        startActivity(i);
    }

    void calcFunction() throws IOException {
        String pathToCsv = "";
        BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if(data[0].charAt(2)=='f' && data[0].charAt(3) == 'r'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    Pi_450nm[i] = Float.parseFloat(lol[0]);
                    Pi_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='f' && data[0].charAt(3)=='i'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PiN_450nm[i] = Float.parseFloat(lol[0]);
                    PiN_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='1'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP1_450nm[i] = Float.parseFloat(lol[0]);
                    PP1_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='2'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP2_450nm[i] = Float.parseFloat(lol[0]);
                    PP2_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='3'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP3_450nm[i] = Float.parseFloat(lol[0]);
                    PP3_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='4'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP4_450nm[i] = Float.parseFloat(lol[0]);
                    PP4_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='5'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP5_450nm[i] = Float.parseFloat(lol[0]);
                    PP5_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='6'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP6_450nm[i] = Float.parseFloat(lol[0]);
                    PP6_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='7'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP7_450nm[i] = Float.parseFloat(lol[0]);
                    PP7_630nm[i] = Float.parseFloat(lol[1]);
                }
            } else if(data[0].charAt(2)=='8'){
                row = csvReader.readLine();
                row = csvReader.readLine();
                for(int i = 0; i<8; i++){
                    row = csvReader.readLine();
                    String[] lol = row.split(",");
                    PP1_450nm[i] = Float.parseFloat(lol[0]);
                    PP2_630nm[i] = Float.parseFloat(lol[1]);
                }
            }
        }
        csvReader.close();
        calculation = new Calculation(PP1_450nm,  PP2_450nm,  PP3_450nm,  PP4_450nm,  PP5_450nm,  PP6_450nm,  PP7_450nm,  PP8_450nm,
                PP1_630nm,  PP2_630nm,  PP3_630nm,  PP4_630nm,  PP5_630nm,  PP6_630nm,  PP7_630nm,  PP8_630nm,
                Pi_450nm,  PiN_450nm,  Pi_630nm,  PiN_630nm);
    }
}