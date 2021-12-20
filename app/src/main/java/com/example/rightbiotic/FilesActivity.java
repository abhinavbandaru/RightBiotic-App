package com.example.rightbiotic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilesActivity extends AppCompatActivity {
    ListView listView;
    List<String> fileList;
    List<String> filePathList;
    ArrayAdapter<String> directoryList;
    private static final String AUTHORITY="com.example.rightbiotic";
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_view);
        listView = findViewById(R.id.pdfListView);
        refreshList();
        listView = findViewById(R.id.pdfListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewPdf(fileList.get(i), filePathList.get(i));
            }
        });
    }

    void refreshList(){ //refresh the list
        fileList = new ArrayList<>();
        filePathList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/Data/com.example.rightbiotic/files/RightBiotic";
        System.out.println("Path: " + path);
        File rbFolder = new File(path);
        List<File> allFiles = getListFiles(rbFolder);
        for(File f: allFiles){
            System.out.println(f.getName());
            fileList.add(f.getName());
            filePathList.add(f.getParent());
        }
        directoryList = new ArrayAdapter<>(FilesActivity.this,android.R.layout.simple_expandable_list_item_1, fileList);
        listView.setAdapter(directoryList);
    }

    List<File> getListFiles(File parentDir) { //get list of files from RightBiotic folder
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if(files == null){
            return inFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    void viewPdf(String filename, String filePath){ //opens pdf file
        Log.d("filename", filename);
        File f = new File(filePath, filename);
//        Uri path = Uri.fromFile(f);


        // Setting the intent for pdf reader
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        Log.d("Path", path.toString());
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(pdfIntent);

        Intent i=
                new Intent(Intent.ACTION_VIEW,
                        FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                                BuildConfig.APPLICATION_ID + ".provider", f));

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(i);
        finish();
    }
}
