package com.wisesoft.android.kotlinlib.utils;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import com.blankj.utilcode.util.ImageUtils;
import net.wisesoft.android.kotlinlib.R;

import java.io.IOException;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by yangmin
 * on 2019/6/25.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
public class NotifiationUtil {
    private static NotificationManager mManager;

    private static NotificationManager getManager(Context context) {
        if (mManager == null) {
            mManager = (NotificationManager) context.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }


    public static void showNotification(Context mContext, String title, String ContentTitle, String message, int id) {
        showNotification(mContext, title, ContentTitle, message, id, 0, null);
    }

    public static void showNotification(Context mContext, String title, String ContentTitle, String message, int id, Intent intent) {
        showNotification(mContext, title, ContentTitle, message, id, 0, intent);
    }

    public static void showNotification(Context mContext, String title, String ContentTitle, String message, int id, int smallIcon, Intent intent) {
        showNotification(mContext, null, title, ContentTitle, message, id, smallIcon, intent, Notification.DEFAULT_ALL);
    }

    /**
     * @param mContext
     * @param views        自定义view
     * @param title        标题
     * @param ContentTitle 标题
     * @param message      内容
     * @param id           通知id
     * @param smallIcon    小图标资源id
     * @param intent
     * @param noticeType   通知类型（0:响铃+震动,1响铃，2震动，3）
     */
    public static void showNotification(Context mContext, RemoteViews views, String title, String ContentTitle, String message, int id, int smallIcon, Intent intent, int noticeType) {
        Bitmap largeIcon = getIconBitmap(mContext);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//TODO 兼容android 8.0
            String channelId = mContext.getPackageName();
            NotificationChannel channel = new NotificationChannel(channelId, mContext.getPackageName(), NotificationManager.IMPORTANCE_HIGH);
            getManager(mContext).createNotificationChannel(channel);
            Notification notification = new Notification.Builder(mContext)
                    .setTicker(title) //通知首次出现在通知栏，带上升动画效果的
                    .setOngoing(true)//设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐) 或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(smallIcon == 0 ? R.mipmap.logo : smallIcon)//设置通知小ICON
                    .setAutoCancel(true)
                    .setLargeIcon(largeIcon != null ? largeIcon : BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo))
                    .setContentTitle(ContentTitle)//设置通知栏标题
                    .setContentText(message)//设置通知栏显示内容
                    .setContentIntent(resultPendingIntent)//设置通知栏点击意图
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setDefaults(noticeType)
                    .setWhen(System.currentTimeMillis())
                    .setChannelId(channelId)
                    .build();
            getManager(mContext).notify(id, notification);
        } else {
            Notification.Builder notifyBuilder = new Notification.Builder(mContext).setContentTitle(title)
                    .setContentText(message)
                    // 点击消失
                    .setAutoCancel(true)
                    .setContentTitle(ContentTitle)
                    // 设置该通知优先级
                    .setPriority(Notification.PRIORITY_MAX)
                    .setLargeIcon(largeIcon != null ? largeIcon : BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo))
                    .setSmallIcon(smallIcon == 0 ? R.mipmap.logo : smallIcon)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(noticeType)
                    .setTicker(title)// 通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis());
            getManager(mContext).notify(id, notifyBuilder.build());
        }
    }
    
    /**
     * 关闭某个通知
     *
     * @param mContext
     * @param id
     */
    public static void cancelNotification(Context mContext, int id) {
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        }
        mManager.cancel(id);
    }


    /**
     * 关闭应用所有通知
     *
     * @param mContext
     */
    public static void cancelAllNotify(Context mContext) {
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        }
        mManager.cancelAll();
    }


    /**
     * 从asset目录下读取配置图片
     *
     * @param mContext
     * @return
     */
    public static Bitmap getIconBitmap(Context mContext) {
        Bitmap bitmap = null;
        try {
            bitmap = ImageUtils.getBitmap(mContext.getAssets().open("ic_launcher.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        return bitmap;
    }


    /**
     * 兼容8.0+ service必须前台服务启动
     *
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setForegroundNotification26(Service context, String channeId, int notifyID) {
        setForegroundNotification26(context, channeId, notifyID, null);
    }

    /**
     * 兼容8.0+ service必须前台服务启动
     *
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setForegroundNotification26(Service context, String channeId, int notifyID, String desc) {
        try {
            Bitmap largeIcon = NotifiationUtil.getIconBitmap(context);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            // 用户可以看到的通知渠道的名字.
            CharSequence name = context.getString(R.string.app_name);
//         用户可以看到的通知渠道的描述
            String description = (desc != null ? desc : "正在为您服务");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channeId, name, importance);
//         配置通知渠道的属性
            mChannel.setDescription(description);
            mChannel.setSound(null, null);
//         最后在notificationmanager中创建该通知渠道 //
            mNotificationManager.createNotificationChannel(mChannel);
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(name).setContentText(description)
                    .setSmallIcon(R.mipmap.logo)
                    .setChannelId(channeId)
                    .setLargeIcon(largeIcon)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .build();
            context.startForeground(notifyID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 8.0以下版本开启前台服务
     *
     * @param context
     * @param notifyID
     * @param desc
     */
    public static void setForegroundNotification(Service context, int notifyID, String desc) {
        Bitmap largeIcon = NotifiationUtil.getIconBitmap(context);
        CharSequence name = context.getString(R.string.app_name);
        Notification.Builder builder = new Notification.Builder
                (context.getApplicationContext()); //获取一个Notification构造器
        builder.setLargeIcon(largeIcon)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentTitle(name) // 设置下拉列表里的标题
                .setContentText(desc) // 设置上下文内容
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.logo) // 设置状态栏内的小图标
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        context.startForeground(notifyID, notification);// 开始前台服务
    }
}
