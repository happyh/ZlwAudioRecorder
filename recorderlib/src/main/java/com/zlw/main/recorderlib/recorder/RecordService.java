package com.zlw.main.recorderlib.recorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import com.zlw.main.recorderlib.recorder.listener.RecordDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.recorder.listener.RecordStateListener;
import com.zlw.main.recorderlib.utils.FileUtils;
import com.zlw.main.recorderlib.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 录音服务
 *
 * @author zhaolewei
 */
public class RecordService extends Service {
    private static final String TAG = RecordService.class.getSimpleName();

    /**
     * 录音配置
     */
    private static RecordConfig currentConfig = new RecordConfig();

    private final static String ACTION_NAME = "action_type";

    private final static int ACTION_INVALID = 0;

    private final static int ACTION_START_RECORD = 1;

    private final static int ACTION_STOP_RECORD = 2;

    private final static int ACTION_RESUME_RECORD = 3;

    private final static int ACTION_PAUSE_RECORD = 4;

    private final static String PARAM_PATH = "path";

    public static final String CHANNEL_ID = "ForegroundServiceChannel";


    public RecordService() throws FileNotFoundException {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        /*
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, RecordService.class);
        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                //.setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("要显示的内容") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        startForeground(110, notification);// 开始前台服务
        */
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_START_RECORD:
                    doStartRecording(bundle.getString(PARAM_PATH));
                    break;
                case ACTION_STOP_RECORD:
                    doStopRecording();
                    break;
                case ACTION_RESUME_RECORD:
                    doResumeRecording();
                    break;
                case ACTION_PAUSE_RECORD:
                    doPauseRecording();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void startRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORD);
        intent.putExtra(PARAM_PATH, getFilePath());
        context.startService(intent);
    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORD);
        context.startService(intent);
    }

    public static void resumeRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RESUME_RECORD);
        context.startService(intent);
    }

    public static void pauseRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_PAUSE_RECORD);
        context.startService(intent);
    }

    /**
     * 改变录音格式
     */
    public static boolean changeFormat(RecordConfig.RecordFormat recordFormat) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig.setFormat(recordFormat);
            return true;
        }
        return false;
    }

    /**
     * 改变录音配置
     */
    public static boolean changeRecordConfig(RecordConfig recordConfig) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig = recordConfig;
            return true;
        }
        return false;
    }

    /**
     * 获取录音配置参数
     */
    public static RecordConfig getRecordConfig() {
        return currentConfig;
    }

    public static void changeRecordDir(String recordDir) {
        currentConfig.setRecordDir(recordDir);
    }

    /**
     * 获取当前的录音状态
     */
    public static RecordHelper.RecordState getState() {
        return RecordHelper.getInstance().getState();
    }

    public static void setRecordStateListener(RecordStateListener recordStateListener) {
        RecordHelper.getInstance().setRecordStateListener(recordStateListener);
    }

    public static void setRecordDataListener(RecordDataListener recordDataListener) {
        RecordHelper.getInstance().setRecordDataListener(recordDataListener);
    }

    public static void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
        RecordHelper.getInstance().setRecordSoundSizeListener(recordSoundSizeListener);
    }

    public static void setRecordResultListener(RecordResultListener recordResultListener) {
        RecordHelper.getInstance().setRecordResultListener(recordResultListener);
    }

    public static void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        RecordHelper.getInstance().setRecordFftDataListener(recordFftDataListener);
    }

    private void doStartRecording(String path) {
        Logger.v(TAG, "doStartRecording path: %s", path);
        RecordHelper.getInstance().start(path, currentConfig);
    }

    private void doResumeRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().resume();
    }

    private void doPauseRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().pause();
    }

    private void doStopRecording() {
        Logger.v(TAG, "doStopRecording");
        RecordHelper.getInstance().stop();
        stopSelf();
    }

    public static RecordConfig getCurrentConfig() {
        return currentConfig;
    }

    public static void setCurrentConfig(RecordConfig currentConfig) {
        RecordService.currentConfig = currentConfig;
    }

    /**
     * 根据当前的时间生成相应的文件名
     * 实例 record_20160101_13_15_12
     */
    private static String getFilePath() {

        String fileDir =
                currentConfig.getRecordDir();
        if (!FileUtils.createOrExistsDir(fileDir)) {
            Logger.w(TAG, "文件夹创建失败：%s", fileDir);
            return null;
        }
        String fileName = String.format(Locale.getDefault(), "record_%s", FileUtils.getNowString(new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.SIMPLIFIED_CHINESE)));
        return String.format(Locale.getDefault(), "%s%s%s", fileDir, fileName, currentConfig.getFormat().getExtension());
    }


}
