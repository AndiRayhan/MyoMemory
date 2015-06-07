package com.example.andirayhan.myomemory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;


public class MainActivity extends ActionBarActivity {
    private static final String TAG="Connors Debug";
    private Button playButton;
    private Button sendButton;
    public static Hub hub;
    public static SQLiteDatabase MyoDB=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyoDB=this.openOrCreateDatabase("MyoDB",MODE_APPEND,null);
        try{
            MyoDB.execSQL("CREATE TABLE IF NOT EXISTS MYODATA (DATE DATETIME, SCORE INT(10));");

        }
        catch(Exception e){
            Log.i(TAG,"Exception "+ e.getMessage());
        }
        hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return;
        }

        playButton=(Button)findViewById(R.id.playButton);
        sendButton=(Button)findViewById(R.id.sendButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hub.getConnectedDevices() ==null || hub.getConnectedDevices().size()==0){
                    Toast.makeText(MainActivity.this, "No myo devices connected", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent playIntent=new Intent(MainActivity.this,Play.class);
                    startActivity(playIntent);
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///
                //
                String date=null;
                int score=0;
                try{
                    Cursor c=MyoDB.rawQuery("SELECT * FROM MYODATA",null);
                    int index1=c.getColumnIndex("DATE");
                    int index2=c.getColumnIndex("SCORE");
                    c.moveToFirst();
                    if(c!=null){
                        do{
                            date=c.getString(index1);
                            score=c.getInt(index2);
                            Log.i(TAG,"Date: "+date+" Score: "+score);
                        }
                        while(c.moveToNext());
                    }
                }
                catch(Exception e){
                    Log.i(TAG,"Exception "+e.getMessage());
                }

                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("ajwad605@gmail.com")+"?subject="+
                Uri.encode("Patient Daily Data")+"&body"+Uri.encode("Date: "+date+ " Score: "+score);
                Uri uri=Uri.parse(uriText);
                send.setData(uri);
                startActivity(Intent.createChooser(send,"Send mail..."));

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.scan:
                Intent i=new Intent(this, ScanActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
