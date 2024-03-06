package com.zlw.audio_recorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = "DaemonService";
    public static final int NOTICE_ID = 100;


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("KeepAppAlive");
            builder.setContentText("DaemonService is runing...");
            startForeground(NOTICE_ID, builder.build());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }

         */
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("KeepAppAlive");
            builder.setContentText("DaemonService is runing...");

            /*
            Intent nfIntent = new Intent(this, MainActivity.class);
            builder.setContentIntent(PendingIntent.
                            getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                    //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                    .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                    //.setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("要显示的内容") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

             */

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            // 参数一：唯一的通知标识；参数二：通知消息。
            startForeground(NOTICE_ID, notification);// 开始前台服务

            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            //Intent cnsintent = new Intent(this, CancelNoticeService.class);
            //startService(cnsintent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }

}