package com.siri.livenessfr;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class CameraSourceDetect extends AppCompatActivity implements SurfaceHolder.Callback{

    CameraSource cameraSource;
     SurfaceView surfaceView;
    SurfaceView transparentView;
    final static int CameraID = 1001;
    Activity activity;

    public CameraSourceDetect(Activity activit, FaceDetector faceDetector) {
        activity = activit;
        surfaceView = activity.findViewById(R.id.surfaceview);
        transparentView = activity.findViewById(R.id.transparentview);
        cameraSource = new CameraSource.Builder(activity,faceDetector)
                .setRequestedPreviewSize(640,480) // size
                .setFacing(CameraSource.CAMERA_FACING_FRONT)//selfie
                .setRequestedFps(30)
                .build();

        StartCamera();
    }

    public void StartCamera() {
        surfaceView.getHolder().addCallback(this);//implement it's methods
        transparentView.getHolder().addCallback(this);//implement it's methods

        transparentView.getHolder().setFormat(PixelFormat.TRANSLUCENT);//System chooses a format that supports translucency (many alpha bits)
        transparentView.setZOrderMediaOverlay(true); //it will be overly
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try
        {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){//if camera permission doesn't exist request it
                ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.CAMERA},CameraID);//request permission
                return;
            }

            cameraSource.start(surfaceView.getHolder());//start camera
        } catch (IOException e) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraSource.stop(); // stop camera source
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) { //after requesting the permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) { //the request code 1001
            case CameraID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted

                    if (ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    try
                    {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {

                    }

                }
            }

        }
    }


    public void DrawRectangle(float x,float y,float width,float height) {

        Canvas canvas = transparentView.getHolder().lockCanvas(null); //creates a surface area that you will write to
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);//clear any past drawn shapes
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(5);
//        Rect rec=new Rect((int) x,(int)y,(int) x+(int)width,(int) y+(int)height); //draw rectangle
//        canvas.drawRect(rec,paint);

        transparentView.getHolder().unlockCanvasAndPost(canvas);//apply the drawing and unlock canvas so that no other thread write to it
    }


}
