package com.example.rightbiotic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1570;
    @SuppressLint("StaticFieldLeak")
    public static EditText doctorNameEditText, patientNameEditText, patientAgeEditText, patientSexEditText, sampleDateEditText;
    public static String doctorName = "", patientName = "", patientAge ="", patientSex = "", sampleDate = "", logoPath = "";
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        doctorNameEditText = findViewById(R.id.doctorName);
        patientNameEditText = findViewById(R.id.patientName);
        patientAgeEditText = findViewById(R.id.patientAge);
        patientSexEditText = findViewById(R.id.patientSex);
        sampleDateEditText = findViewById(R.id.sampleDate);
    }

    public void SaveClicked(View view){
        doctorName = doctorNameEditText.getText().toString();
        patientName = patientNameEditText.getText().toString();
        patientAge = patientAgeEditText.getText().toString();
        patientSex = patientSexEditText.getText().toString();
        sampleDate = sampleDateEditText.getText().toString();
        Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    public void SelectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public String getImageFilePath(Uri uri) {

        File file = new File(Objects.requireNonNull(uri.getPath()));
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            cursor.close();
            return imagePath;
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            logoPath = getImageFilePath(Objects.requireNonNull(data.getData()));

            Toast.makeText(getApplicationContext(), "Logo Set", Toast.LENGTH_SHORT).show();
        }
    }
}
