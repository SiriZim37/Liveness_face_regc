package com.siri.livenessfr;


import static com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LivenessManualDetection implements Detector.Processor  {

    FaceDetector faceDetector;
    boolean rightHead ,leftHead;
    boolean closed_eyes,captured;
    long timeLeft ,timeRight;
    Activity activity;
    CameraSourceDetect camera_source;
    TextView txt_res_shake_head , tv_res_shake_head_left , tv_res_shake_head_right ,txt_res_smile,txt_res_blink_eye , tv_command_authen , tvTime ;
    ArrayList<Boolean> arrayAuthen , arraySoundAuthen;
    ArrayList<String> arrayDetectList;
    RelativeLayout layout_capture;
    public LivenessManualDetection(Activity activity1) {
        activity = activity1;
        rightHead = false;
        leftHead = false;
        closed_eyes = false;
        captured = true;
        txt_res_blink_eye  = activity.findViewById(R.id.txt_res_blink_eye);
        txt_res_shake_head = activity.findViewById(R.id.txt_res_shake_head);
        tv_res_shake_head_right = activity.findViewById(R.id.txt_res_shake_head_right);
        tv_res_shake_head_left = activity.findViewById(R.id.txt_res_shake_head_left);
        txt_res_smile = activity.findViewById(R.id.txt_res_smile);
        tv_command_authen = activity.findViewById(R.id.tv_command_authen);
        layout_capture = activity.findViewById(R.id.relativeLayout);
        tvTime = activity.findViewById(R.id.tv_time);

        arrayAuthen = new ArrayList<>();
        arrayAuthen.add(false); // blink eye
        arrayAuthen.add(false); // shake
        arrayAuthen.add(false); // smile
        arrayAuthen.add(false); // shake head right
        arrayAuthen.add(false); // shake head left

        arrayDetectList = new ArrayList<>();
        arrayDetectList.add("Please Blink your eyes!"); // blink eye
        arrayDetectList.add("Please shake your head!"); // shake
        arrayDetectList.add("Please smile!"); // smile
        arrayDetectList.add("Please shake your head to the right!"); // shake
        arrayDetectList.add("Please shake your head to the left!"); // shake

        arraySoundAuthen = new ArrayList<>();
        arraySoundAuthen.add(false); // blink eye
        arraySoundAuthen.add(false); // shake
        arraySoundAuthen.add(false); // smile
        arraySoundAuthen.add(false); // shake head right
        arraySoundAuthen.add(false); // shake head left



        faceDetector = new FaceDetector.Builder(activity1)
                .setTrackingEnabled(true) //for tracking in live video
                .setLandmarkType(FaceDetector.ALL_LANDMARKS) //get all face landmarks
                .setMode(FaceDetector.ACCURATE_MODE) //for orientation
                .setClassificationType(ALL_CLASSIFICATIONS) // for smile and blink
                .build();
        camera_source = new CameraSourceDetect(activity,faceDetector);
        timer.start();
    }

    public void start()
    {
        faceDetector.setProcessor(this); // implement it's methods
    }

    public void playSound(int audio) {
        MediaPlayer mediaPlayer = MediaPlayer.create(activity,audio); //play the audio
        mediaPlayer.start();
    }

    private CountDownTimer timer = new CountDownTimer(20000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tvTime.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            playSound(R.raw.time_out);//play the desired detection sound
            showErrorDialog();//time out

        }
    };

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections)  {

        SparseArray detectedFaces = detections.getDetectedItems();//get all detected face (sparseArray it map integer to object and it uses binary tree in search)
        if (detectedFaces.size()!=0){

            //for (int i=0;i<detectedFaces.size();i++){
            final Face face = (Face)detectedFaces.valueAt(0); // get only first face that appear on the camera
            try {
                synchronized (camera_source.surfaceView.getHolder()){ //only one thread that executes at the time (stop main thread to draw)
                    camera_source.DrawRectangle(face.getPosition().x,face.getPosition().y +50,face.getWidth(),face.getHeight());// Draw rectangle on face
                }
            }catch (Exception e){
            }

            Callable<Object> ShakeHead = new Callable<Object>() {//define region for shaking head
                public Object call() throws Exception {

                    if (face.getEulerY() < -12.0) { //get the orientation of face
                        leftHead = true;
                        timeLeft = (System.currentTimeMillis() / 1000);
                    }
                    if (face.getEulerY() > 12.0) { //get the orientation of face
                        rightHead = true;
                        timeRight = (System.currentTimeMillis() / 1000);
                    }

                    if (leftHead && rightHead) { //check if shake his head
                        if (Math.abs(timeLeft - timeRight) <= 4) //check the time between the left and the right move
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, "\n" + "You moved your head right and left", Toast.LENGTH_SHORT).show();
                                }
                            });
                        leftHead = false;
                        rightHead = false;
                        return true;
                    }
                    return false;
                }
            };

            Callable<Object> ShakeHeadRight = new Callable<Object>() {//define region for shaking head
                public Object call() throws Exception {

                    if (face.getEulerY() < -12.0) { //get the orientation of face
                        leftHead = true;
                        timeLeft = (System.currentTimeMillis() / 5000);
                    }

                    if (leftHead) { //check if shake his head
                        if (Math.abs(timeLeft) >=4)
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, "\n" + "You moved your head to the right.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        return true;
                    }
                    return false;
                }
            };

            Callable<Object> ShakeHeadLeft= new Callable<Object>() {//define region for shaking head
                public Object call() throws Exception {

                    if (face.getEulerY() > 12.0) { //get the orientation of face
                        rightHead = true;
                        timeRight = (System.currentTimeMillis() / 5000);
                    }

                    if (rightHead) { //check if shake his head
                        if (Math.abs(timeRight) >=4)
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, "\n" + "You moved your head to the left.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        return true;
                    }
                    return false;
                }
            };

            Callable<Object> Smile = new Callable<Object>() {
                public Object call() throws Exception {

                    if (face.getIsSmilingProbability() >= 0.5) { //get the probability of smile
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, "\n" + "I smiled correctly", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    }
                    return false;
                }
            };

            Callable<Object> Blink = new Callable<Object>() {
                public Object call() throws Exception {

                    if ((face.getIsLeftEyeOpenProbability() + face.getIsRightEyeOpenProbability()) / 2.0f < 0.3 && face.getIsLeftEyeOpenProbability() > 0 && face.getIsRightEyeOpenProbability() > 0) { //closing eye (0's to check if eye option is already working in detection)
                        closed_eyes = true;
                    }
                    if (closed_eyes) {
                        if ((face.getIsLeftEyeOpenProbability() + face.getIsRightEyeOpenProbability()) / 2.0f >= 0.6 && face.getIsLeftEyeOpenProbability() > 0 && face.getIsRightEyeOpenProbability() > 0) {//open eye after closing it
                            closed_eyes = false;
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(activity, "\n" + "You closed your eyes properly", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return true;
                        }
                    }
                    return false;
                }
            };

            // 1st please blink your eye
            // 2st please smile
            // 3st please shake
            // 4st please shake head right
            // 5st please shake head left

            if (face.getId() == 0) {


                    if (!arrayAuthen.get(0)) {  // 1st please blink your eye
                        if (!arraySoundAuthen.get(0)) { //check if sound doesn't played before
                            tv_command_authen.setText(arrayDetectList.get(0));
                            playSound(R.raw.please_blink_eye);//play the desired detection sound

                            arraySoundAuthen.set(0, true);
                        }

                        try {
                            Boolean isSuccess = (boolean) Blink.call(); //blink
                            if (isSuccess) {
                                txt_res_blink_eye.setText("success");
                                arrayAuthen.set(0, isSuccess);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (arrayAuthen.get(0) && !arrayAuthen.get(1)  ) {   // 2st please shake
                        if (!arraySoundAuthen.get(1)) { //check if sound doesn't played before
                            tv_command_authen.setText(arrayDetectList.get(1));
                            playSound(R.raw.please_shake_head);//play the desired detection sound
                            arraySoundAuthen.set(1, true);
                        }
                        try {
                            Boolean isSuccess = (boolean) ShakeHead.call(); //shake

                            if (isSuccess) {
                                txt_res_shake_head.setText("success");
                                arrayAuthen.set(1, isSuccess);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                if (arrayAuthen.get(0) && arrayAuthen.get(1) && !arrayAuthen.get(2)) {  // 3st please smile
                    if (!arraySoundAuthen.get(2)) { //check if sound doesn't played before
                        tv_command_authen.setText(arrayDetectList.get(2));
//                        playSound(R.raw.please_blink_eye);//play the desired detection sound
                        arraySoundAuthen.set(2, true);
                    }
                    try {
                        Boolean isSuccess = (boolean) Smile.call(); //smile
                        if (isSuccess) {
                            txt_res_smile.setText("success");
                            arrayAuthen.set(2, isSuccess);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (arrayAuthen.get(0) && arrayAuthen.get(1)  && arrayAuthen.get(2) && !arrayAuthen.get(3) ) {   // 4st please shake head right
                    if (!arraySoundAuthen.get(3)) { //check if sound doesn't played before
                        tv_command_authen.setText(arrayDetectList.get(3));
                        playSound(R.raw.please_right_head);//play the desired detection sound
                        arraySoundAuthen.set(3, true);
                    }
                    try {
                        Boolean isSuccess = (boolean) ShakeHeadRight.call(); //shake

                        if (isSuccess) {
                            tv_res_shake_head_right.setText("success");
                            arrayAuthen.set(3, isSuccess);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (arrayAuthen.get(0) && arrayAuthen.get(1) && arrayAuthen.get(2) && arrayAuthen.get(3) && !arrayAuthen.get(4)) {   // 5st please shake head left
                    if (!arraySoundAuthen.get(4)) { //check if sound doesn't played before
                        tv_command_authen.setText(arrayDetectList.get(4));
                        playSound(R.raw.please_left_head);//play the desired detection sound
                        arraySoundAuthen.set(4, true);
                    }
                    try {
                        Boolean isSuccess = (boolean) ShakeHeadLeft.call(); //shake

                        if (isSuccess) {
                            tv_res_shake_head_left.setText("success");
                            arrayAuthen.set(4, isSuccess);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (captured && arrayAuthen.get(0) && arrayAuthen.get(1)
                        && arrayAuthen.get(2)  && arrayAuthen.get(3)  && arrayAuthen.get(4) ) { //check if no pictures is taken before and he passed both detections successfully
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                timer.cancel();
                                playSound(R.raw.please_wait_clarify);//play the desired detection sound

                                try {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(activity, ResultActivity.class);
                                            activity.startActivity(intent);
                                        }
                                    }, 3000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        camera_source.cameraSource.takePicture(null, (CameraSource.PictureCallback) activity);//take picture
                        captured = false;
                    }

            }

        }
    }

    private void showErrorDialog() {
        //－－－－－－－－－－Stop detecting first－－－－－－－－－－
//        detector.stopFaceDetect();
        tv_command_authen.setText("");
        tvTime.setText("0");
        timer.cancel();//Cancel countdown
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage( "Time Out");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, MainMenuActivity.class);
                        activity.startActivity(intent);
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



}
