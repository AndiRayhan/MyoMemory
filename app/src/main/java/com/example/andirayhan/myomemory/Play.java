package com.example.andirayhan.myomemory;

/**
 * Created by AndiRayhan on 6/7/2015.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.v7.app.AppCompatActivity;


public class Play extends ActionBarActivity {
    private static final String TAG="Connors Debug";
    private TextView unlock;
    private ImageView imageSequence;
    private int count = 0;
    private boolean readyForGesture;
    private String[] userAnswer = new String[5];
    private String[] randomHolder = new String[5];
    private Hashtable<String, Runnable> myHash = new Hashtable<String, Runnable>();
    private TextView score;
    private boolean isReady = false;

    private DeviceListener listener=new DeviceListener() {
        @Override
        public void onAttach(Myo myo, long l) {
            Log.i(TAG, "On Attach");
        }

        @Override
        public void onDetach(Myo myo, long l) {
            Log.i(TAG,"On Detach");
        }

        @Override
        public void onConnect(Myo myo, long l) {
            Log.i(TAG,"On Connect");
        }

        @Override
        public void onDisconnect(Myo myo, long l) {
            Log.i(TAG,"On Disonnect");
        }

        @Override
        public void onArmSync(Myo myo, long l, Arm arm, XDirection xDirection) {
            Log.i(TAG,"On ArmSync");
        }

        @Override
        public void onArmUnsync(Myo myo, long l) {
            Log.i(TAG, "On ArmUnsync");
        }

        @Override
        public void onUnlock(Myo myo, long l) {
            Log.i(TAG,"Unlock");
            unlock.setTextColor(Color.BLACK);
            unlock.setText("Memorize this sequence of gestures!");
            myo.unlock(Myo.UnlockType.HOLD);
            imageSequence = (ImageView)findViewById(R.id.imageSequence);
            imageSequence.setVisibility(View.VISIBLE);
            //imageSequence.setImageDrawable(Play.this.getResources().getDrawable(R.drawable.spread));

            int i;
            for(i=1;i<6;i++){
                final int j=i;
                Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Calling timer method");
                        TimerMethod(j);
                    }
                }, 1000*j);
            }
        }

        private int calculateScore(String[] userAnswer, String[] randomHolder){
            int score = 0;
            for(int i =0;i<userAnswer.length;i++){
                if(userAnswer[i].equals(randomHolder[i])) score++;
            }
            Log.i(TAG,"Score: "+score);
            MainActivity.MyoDB.execSQL("INSERT INTO MYODATA (SCORE) VALUES "+"("+score+")"+";");

            Date date=new Date(System.currentTimeMillis());
            MainActivity.MyoDB.execSQL("INSERT INTO MYODATA (DATE) VALUES ("+date+");");
            return score;
        }



        @Override
        public void onLock(Myo myo, long l) {
            unlock.setTextColor(Color.GRAY);
            onUnlock(myo,l);
            Log.i(TAG, "On lock");
        }

        @Override
        public void onPose(Myo myo, long l, Pose pose) {
            //Log.i(TAG,""+readyForUsersGesture)
            Log.i(TAG,"Pose called");
            if(isReady && count <5){
                score.setVisibility(View.VISIBLE);
                score.setText("Gesture count:"+(count));
            }
            if(count>=5){
                score.setText("Your score is"+calculateScore(userAnswer,randomHolder));
                return;
            }
            switch(pose){
                case DOUBLE_TAP:
                    //Log.i(TAG,"Double Tap");
                    break;
                case FIST:
                   // Log.i(TAG, "Fist");
                    /*switch(Play.this.count){
                        case 0:
                            imageSequence.setImageDrawable(Play.this.getResources().getDrawable(R.drawable.rock));
                            break;
                        case 1:
                            imageSequence.setImageDrawable(Play.this.getResources().getDrawable(R.drawable.wavein));
                            break;
                        case 2:
                            imageSequence.setImageDrawable(Play.this.getResources().getDrawable(R.drawable.waveout));
                            break;
                    }*/
                    if(isReady) {
                        userAnswer[count++] = "rock";
                        imageSequence.setImageDrawable(Play.this.getResources().getDrawable(R.drawable.rock));
                    }
                    break;
                case WAVE_IN:
                    Log.i(TAG, "Wave in");
                    if(isReady) userAnswer[count++] = "wavein";
                    break;
                case WAVE_OUT:
                    //Log.i(TAG,"Wave out");
                    if(isReady) userAnswer[count++] = "waveout";
                    break;
                case FINGERS_SPREAD:
                    //Paper
                    //Log.i(TAG,"Spread");
                    if(isReady) userAnswer[count++] = "spread"; // the user is always one groove behind we need to change this
                    break;
                case UNKNOWN:
                    //Log.i(TAG,"UNKOWN");
                    break;
                case REST:
                    //Log.i(TAG,"REST");
                    break;

            }
            Log.i(TAG, Arrays.toString(userAnswer));
            Log.i(TAG, Arrays.toString(randomHolder));
            Log.i(TAG, ""+count);

        }

        @Override
        public void onOrientationData(Myo myo, long l, Quaternion quaternion) {
            //Log.i(TAG,"On orient");
        }

        @Override
        public void onAccelerometerData(Myo myo, long l, Vector3 vector3) {
            //Log.i(TAG,"On Accelerometer");
        }

        @Override
        public void onGyroscopeData(Myo myo, long l, Vector3 vector3) {
            //Log.i(TAG,"On gyro");
        }

        @Override
        public void onRssi(Myo myo, long l, int i) {
            Log.i(TAG,"On rssi");
        }
    };

    private Runnable rockExecute = new Runnable(){
        public void run(){
            Log.i(TAG, "Showing Rock");
            imageSequence.setImageDrawable((Play.this.getResources().getDrawable(R.drawable.rock)));
        }
    };

    private Runnable outExecute = new Runnable(){
        public void run(){
            Log.i(TAG, "Showing wave out");
            imageSequence.setImageDrawable((Play.this.getResources().getDrawable(R.drawable.waveout)));
        }
    };

    private Runnable inExecute = new Runnable(){
        public void run(){
            Log.i(TAG, "Showing wave in");
            imageSequence.setImageDrawable((Play.this.getResources().getDrawable(R.drawable.wavein)));
        }
    };

    private Runnable spreadExecute = new Runnable(){
        public void run(){
            Log.i(TAG, "Showing spread");
            imageSequence.setImageDrawable((Play.this.getResources().getDrawable(R.drawable.spread)));
        }
    };

    private Runnable ready = new Runnable() {
        @Override
        public void run() {
            unlock.setText("Your turn now.");
            score.setText("Gesture count: "+count);

            imageSequence.setVisibility(View.INVISIBLE);
        }
    };

    private void TimerMethod(int i){
        if(i==5) {
            isReady = true;
            Play.this.runOnUiThread(ready);
            return;
        }
        Play.this.runOnUiThread(myHash.get(randomHolder[i-1]));

        /*switch(i){
            case 1:
                Play.this.runOnUiThread(rockExecute);
                break;
            case 2:
                Play.this.runOnUiThread(outExecute);
                break;
            case 3:
                Play.this.runOnUiThread(spreadExecute);
                break;
            case 4:
                Play.this.runOnUiThread(inExecute);
                break;

        }*/
    }


    private String getRandomComputerThrow(){
        Random generator= new Random();
        long time = Calendar.getInstance().getTimeInMillis();

        generator.setSeed(100+(int)time);
        switch(generator.nextInt(4)){
            case 0:
                return "rock";
            case 1:
                return "spread";
            case 2:
                return "wavein";
            case 3:
                return "waveout";
            default:
                return "ERROR";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        unlock=(TextView)findViewById(R.id.unlockText);
        unlock.setText("Double tap to start playing.");
        unlock.setTextColor(Color.GRAY);
        score = (TextView) findViewById(R.id.scoreCount);
        score.setVisibility(View.INVISIBLE);
        MainActivity.hub.addListener(listener);
        myHash.put("rock",rockExecute);
        myHash.put("spread",spreadExecute);
        myHash.put("wavein",inExecute);
        myHash.put("waveout",outExecute);
        /*for(int i=0;i<5;i++){
            randomHolder[i]=getRandomComputerThrow();
        }*/
        randomHolder[0] = "rock";
        randomHolder[1] = "spread";
        randomHolder[2] = "wavein";
        randomHolder[3] = "waveout";
        randomHolder[4] = "rock";




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
