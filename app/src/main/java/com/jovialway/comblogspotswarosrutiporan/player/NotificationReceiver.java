package com.jovialway.comblogspotswarosrutiporan.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP="STOP";
    public static final String ACTION_PLAY="PLAY";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,AudioService.class);

       if (intent.getAction()!=null){
           switch (intent.getAction())
           {

               case ACTION_PLAY:
               case ACTION_STOP:

                   intent1.putExtra("myactionName",intent.getAction());
                   context.startService(intent1);
                   break;
           }
       }

    }
}
