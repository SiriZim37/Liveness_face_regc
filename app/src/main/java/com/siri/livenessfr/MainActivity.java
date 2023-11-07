package com.siri.livenessfr;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
public class MainActivity extends AppCompatActivity implements CameraSource.PictureCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LivenessManualDetection face_detection = new LivenessManualDetection(MainActivity.this);
        face_detection.start();//start Face detection and facial expression recognition

    }

    @Override
    public void onPictureTaken(byte[] data) {
        Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length); //get the taken picture
    }



}
