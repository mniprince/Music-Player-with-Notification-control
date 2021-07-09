package com.jovialway.comblogspotswarosrutiporan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.jovialway.comblogspotswarosrutiporan.player.Actionplayer;
import com.jovialway.comblogspotswarosrutiporan.player.AudioService;
import com.jovialway.comblogspotswarosrutiporan.player.NotificationReceiver;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;
import static com.jovialway.comblogspotswarosrutiporan.player.PlayerNotification.ACTION_PLAY;
import static com.jovialway.comblogspotswarosrutiporan.player.PlayerNotification.ACTION_STOP;
import static com.jovialway.comblogspotswarosrutiporan.player.PlayerNotification.CHANNEL_ID_2;


public class MainActivity extends AppCompatActivity implements Actionplayer, ServiceConnection {
    ImageView play,stop;
    TextView startTime, endTime,title;
    Notification notification;
    MediaSessionCompat mediaSession;
    AudioService mservice;
    MediaPlayer player;
    SeekBar seekBar;
    AudioManager audioManager;
    int maxvolume,currentvolume;
    ArcSeekBar arcSeekBar;
    int totalTime,curpos;
    SharedPreferences sharedpreferences;
    Handler handler;
    Runnable runnable;
    boolean notificationrun=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title=findViewById(R.id.title);
        startTime=findViewById(R.id.starttime);
        endTime=findViewById(R.id.endTime);


        sharedpreferences = getSharedPreferences("prince", Context.MODE_PRIVATE);
        play=findViewById(R.id.play);
        arcSeekBar=(ArcSeekBar) findViewById(R.id.arcseek);
        stop=findViewById(R.id.stop);
        seekBar=findViewById(R.id.seek);
        mediaSession=new MediaSessionCompat(this,"PlayerAudio");
        audioManager= (AudioManager) getSystemService(AUDIO_SERVICE);
        maxvolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentvolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        arcSeekBar.setMaxProgress(maxvolume);
        arcSeekBar.setProgress(currentvolume);


        //arcseekbar
        int[] colorarray=getResources().getIntArray(R.array.gradient);
        arcSeekBar.setProgressGradient(colorarray);

        handler=new Handler();
        player = MediaPlayer.create(this, R.raw.ahirbhairav);
        player.start();
        totalTime = player.getDuration();

        seekBar.setMax(totalTime);
        seekBarupdate();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked();
            }
        });


        arcSeekBar.setOnProgressChangedListener(new ProgressListener() {
            @Override
            public void invoke(int i) {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putInt("prince", i);
                editor.apply();


            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                player.seekTo(progress);
                seekBar.setProgress(progress);
            }}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopClicked();
            }
        });


        title.setText("Prince Player");



    }

    public String createTime(int time) {
        String timelevel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timelevel = min + ":";
        if (sec < 10)
            timelevel += "0";
        timelevel += sec;
        return timelevel;
    }

    @Override
    protected void onResume() {
        super.onResume();
       
        Intent intent=new Intent(MainActivity.this, AudioService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //notification start here
        if (player.isPlaying()){
            shownotification(R.drawable.ic_baseline_pause);
        }
    }


    @Override
    protected void onDestroy() {
        if (player != null) player.release();
        Intent intent=new Intent(MainActivity.this, AudioService.class);
        stopService(intent);
        super.onDestroy();
    }





    @Override
    public void playClicked() {
        if (!player.isPlaying()) {
            player.start();
            play.setImageResource(R.drawable.ic_baseline_pause);
            if (notificationrun){
                shownotification(R.drawable.ic_baseline_pause);
            }
        } else {
            if (player!=null){
                player.pause();
            }
            play.setImageResource(R.drawable.ic_baseline_play);
if (notificationrun){
    shownotification(R.drawable.ic_baseline_play);
}


        }


    }

    @Override
    public void stopClicked() {
        play.setImageResource(R.drawable.ic_baseline_play);
        try {
            player.stop();
            player.release();
            seekBar.setProgress(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void seekBarupdate(){
        try {
            curpos=player.getCurrentPosition();
            seekBar.setProgress(curpos);
    }catch (Exception e){e.printStackTrace();}

        String time = createTime(curpos);
        startTime.setText(time);
        String endtime = createTime(totalTime - curpos);
        endTime.setText("-" + endtime);
        int data = sharedpreferences.getInt("prince", 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,data,0);
    runnable=new Runnable() {
        @Override
        public void run() {
            if (player!=null){
            seekBarupdate();}
        }
    };
    handler.postDelayed(runnable,100);

    }

//service
    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
    AudioService.MyBinder binder = (AudioService.MyBinder)iBinder;
    mservice=binder.getService();
    mservice.setCallback(MainActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    mservice = null;
    }


    public void shownotification(int playpausebtn){
        notificationrun=true;
        Intent intentc = new Intent(this,MainActivity.class);
        PendingIntent contentintent=PendingIntent.getActivity(this,
                0, intentc,0);


        Intent playIntent= new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingintent = PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent stopIntent= new Intent(this, NotificationReceiver.class).setAction(ACTION_STOP);
        PendingIntent stopPendingintent = PendingIntent.getBroadcast(this,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.prince);

        notification=new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_baseline_pause).setLargeIcon(picture)
                .setContentTitle("Prince").setContentText("Ahir Bhairav")
                .addAction(playpausebtn,"Play",playPendingintent)
                .addAction(R.drawable.ic_stop,"Stop",stopPendingintent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentintent)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }


}