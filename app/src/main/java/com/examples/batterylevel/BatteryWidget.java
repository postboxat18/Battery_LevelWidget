package com.examples.batterylevel;

import static androidx.core.app.ServiceCompat.stopForeground;

import android.app.ApplicationErrorReport;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/**
 * Define a simple widget that shows a battery meter. To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class BatteryWidget extends AppWidgetProvider {

    public static final String LOG_TAG = BatteryWidget.class.getSimpleName();
    //public static final int SDK_VERSION = Integer.parseInt(android.os.Build.VERSION.SDK); //1.5 version
    //public static final int SDK_VERSION = android.os.Build.VERSION.SDK_INT;
    public static final String KEY_SCALE = "KEY_SCALE";
    public static String PREFS_NAME="BATWIDG_PREFS";
    public static String KEY_LEVEL = "BATWIDG_LEVEL";
    public static String KEY_CHARGING = "BATWIDG_CHARGING";
    public static String KEY_VOLTAGE = "BATWIDG_VOLTAGE";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        BatteryWidget.clearSettings(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        try
        {
            context.stopService(new Intent(context, UpdateService.class));//unregisterReceiver(mBI);
        }catch(Exception e){Log.d("BatteryWidget","Exception on disable: ",e);}
        BatteryWidget.clearSettings(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        try
        {
            context.stopService(new Intent(context, UpdateService.class));//if(mBI != null) context.unregisterReceiver(mBI);
        }catch(Exception e){Log.d("BatteryWidget","Exception on delete: ",e);}
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        // To prevent any ANR timeouts, we perform the update in a service

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*context.startForegroundService(new Intent(context, UpdateService.class));*/
            Intent serviceIntent = new Intent(context, UpdateService.class);
            ContextCompat.startForegroundService(context, serviceIntent );

        }
        else
        {
            /*context.startService(new Intent(context, UpdateService.class));*/
            Intent serviceIntent = new Intent(context, UpdateService.class);
            context.startService(serviceIntent);
        }
    }

    private static void clearSettings(Context context) {
        if (context != null)
        {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Editor editor = settings.edit();
            editor.remove(KEY_LEVEL);
            editor.remove(KEY_CHARGING);
            editor.remove(KEY_SCALE);
            editor.commit();
        }
    }

    public static class UpdateService extends Service {

        BatteryInfo mBI = null;
        public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
        public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
        private int level=0;

        @Override
        public int onStartCommand(Intent intents, int flags, int startId) {
            if (intents != null) {
                String action = intents.getAction();
                if(action!=null)
                    switch (action) {
                        case ACTION_START_FOREGROUND_SERVICE:
                            if (Build.VERSION.SDK_INT >= 26) {

                            }
                            Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();

                            break;
                        case ACTION_STOP_FOREGROUND_SERVICE:
                            //stop the service, clear up memory, can't do this, need the Broadcast Receiver running
                            stopSelf();
                            Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                            break;
                    }
            }

            return super.onStartCommand(intents, flags, startId);
            //return START_STICKY;

        }
        @Override
        public void onStart(Intent intent, int startId) {
            if(mBI == null)
            {
                mBI = new BatteryInfo();
                IntentFilter mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(mBI, mIntentFilter);
            }

            // Build the widget update for today
            RemoteViews updateViews = buildUpdate(this);
            if(updateViews != null)
            {
                try
                {
                    // Push update for this widget to the home screen
                    ComponentName thisWidget = new ComponentName(this, BatteryWidget.class);
                    if(thisWidget != null)
                    {
                        AppWidgetManager manager = AppWidgetManager.getInstance(this);
                        if(manager != null && updateViews != null)
                        {
                            manager.updateAppWidget(thisWidget, updateViews);
                        }
                    }

                    //stop the service, clear up memory, can't do this, need the Broadcast Receiver running
                    //stopSelf();
                }catch(Exception e)
                {
                    Log.e("Widget", "Update Service Failed to Start", e);
                }
            }
        }
        @Override
        public void onCreate() {
            super.onCreate();
            if (Build.VERSION.SDK_INT >= 26) {
              /*  String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("").build();
                startForeground(1, notification);*/

                ///////////
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                // Create notification builder.
                /*------------------------------------------------------------------*/
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                /*------------------------------------------------------------------*/
                //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
                // Make notification show big text.
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle("Phone Percentage ");
                Log.e("Battery", "Batterypercentage" + level);
                bigTextStyle.bigText(level + " %");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                builder.setLargeIcon(largeIconBitmap);
                // Make the notification max priority.
                builder.setPriority(Notification.FLAG_ONLY_ALERT_ONCE);
                // Make head-up notification.
                builder.setFullScreenIntent(pendingIntent, true);

                // Build the notification.
                Notification notification = builder.build();

                // Start foreground service.
                startForeground(1, notification);
                //////////////


            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            try{
                if(mBI != null) unregisterReceiver(mBI);
            }catch(Exception e)
            {Log.e("Widget", "Failed to unregister", e);}
        }

        /**
         * Build a widget update to show the current Wiktionary
         * "Word of the day." Will block until the online API returns.
         */
        public RemoteViews buildUpdate(Context context) {

            // Build an update that holds the updated widget contents
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.battery_circle);
            try
            {
                //Log.d("BatteryWidget","Updating Views");
                boolean charging = false;
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                if(settings !=null)
                {
                    level = settings.getInt(KEY_LEVEL, 0);

                    //update level based on scale
                    int scale = settings.getInt(KEY_SCALE, 100);
                    if(scale != 100)
                    {
                        if (scale <= 0) scale = 100;
                        level = (100 * level) / scale;
                    }

                    charging = (settings.getInt(KEY_CHARGING, BatteryManager.BATTERY_STATUS_UNKNOWN)==BatteryManager.BATTERY_STATUS_CHARGING);
                }
                if(level==100)
                {
                    /*updateViews.setViewVisibility(R.id.bar100, level>80?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar80, level>60?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar60, level>40?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar40, level>20?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar20, View.VISIBLE);
                    updateViews.setImageViewResource(R.id.bar20, R.drawable.bar_green);*/
                    updateViews.setViewVisibility(R.id.per100,level==100?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.batterytext, View.VISIBLE);
                }else if(level>=75 && level<100)
                {
                    /*updateViews.setViewVisibility(R.id.bar100, View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar80, View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar60, View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar40, View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.bar20, View.VISIBLE);
                    updateViews.setImageViewResource(R.id.bar20, R.drawable.bar_red);*/
                    updateViews.setViewVisibility(R.id.per75,level>=75?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.batterytext, View.VISIBLE);
                }
                else if (level>=50 && level<=75)
                {
                    updateViews.setViewVisibility(R.id.per50,level>=50?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.batterytext, View.VISIBLE);
                }
                else if (level>=25 && level<=50)
                {
                    updateViews.setViewVisibility(R.id.per25,level>=25?View.VISIBLE:View.INVISIBLE);
                    updateViews.setViewVisibility(R.id.batterytext, View.VISIBLE);
                }

                updateViews.setViewVisibility(R.id.charging, charging?View.VISIBLE:View.INVISIBLE);
                batterlevel(level);
                Log.e("BatteryWidget","levels"+level);
                String levelText = level==100?"100":level+"%"; //100% too wide
                if(level == 0) levelText=" 0%";
                updateViews.setTextViewText(R.id.batterytext, levelText);
            }catch(Exception e)
            {
                Log.e("BatteryWidget","Error Updating Views",e);
            }

            /*try
            {
                Intent defineIntent2 = new Intent(context,TranslucentBlurActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(context,0,defineIntent2, 0 );
                updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent2);
            }catch(Exception e)
            {
                Log.e("BatteryWidget","Error Settings Intents",e);
            }*/

            return updateViews;
        }

        private void batterlevel(int level) {
            if (Build.VERSION.SDK_INT >= 26) {
              /*  String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("").build();
                startForeground(1, notification);*/

                ///////////
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                // Create notification builder.
                /*------------------------------------------------------------------*/
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                /*------------------------------------------------------------------*/
                //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
                // Make notification show big text.
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle("Phone Percentage ");
                Log.e("Battery", "Batterypercentage" + level);
                bigTextStyle.bigText(level + " %");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                builder.setLargeIcon(largeIconBitmap);
                // Make the notification max priority.
                builder.setPriority(Notification.DEFAULT_ALL);
                // Make head-up notification.
                builder.setFullScreenIntent(pendingIntent, true);

                // Build the notification.
                Notification notification = builder.build();

                // Start foreground service.
                startForeground(1, notification);
                //////////////


            }
        }

        @Override public IBinder onBind(Intent intent) {return null;}
        /*private void runAsForeground(){
            // Create notification default intent.
            if (Build.VERSION.SDK_INT >= 26) {
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                // Create notification builder.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                // Make notification show big text.
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle("Phone Percentage ");
                Log.e("Battery", "Batterypercentage" + level);
                bigTextStyle.bigText(level + " %");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                builder.setLargeIcon(largeIconBitmap);
                // Make the notification max priority.
                builder.setPriority(Notification.FLAG_ONLY_ALERT_ONCE);
                // Make head-up notification.
                builder.setFullScreenIntent(pendingIntent, true);

                // Build the notification.
                Notification notification = builder.build();

                // Start foreground service.
                startForeground(1, notification);
            }

        }*/
    }

}
