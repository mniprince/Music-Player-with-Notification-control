package com.jovialway.comblogspotswarosrutiporan.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AudioService extends Service {
    private IBinder mBinder =new MyBinder();
    public static final String ACTION_STOP="STOP";
    public static final String ACTION_PLAY="PLAY";
    Actionplayer actionplayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;

    }
    public class MyBinder extends Binder{
        public AudioService getService(){
            return AudioService.this;
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName=intent.getStringExtra("myactionName");
        if (actionName!=null){
            switch (actionName)
            {

                case ACTION_PLAY:
                    if (actionplayer!=null){
                        actionplayer.playClicked();
                    }
                    break;
                case ACTION_STOP:
                    if (actionplayer!=null){
                        actionplayer.stopClicked();
                    }
                    break;
            }
        }


        return START_STICKY;
    }

      public void setCallback(Actionplayer actionplayer){
        this.actionplayer=actionplayer;
    }
}
