package cn.codekong.maccessbilityservice.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

import cn.codekong.maccessbilityservice.config.Config;
import cn.codekong.maccessbilityservice.util.SfUtil;

/**
 * Created by shangzhenhong on 04/26/2018
 * Email: szh@codekong.cn
 */
public class WeChatNotificationListenerService extends NotificationListenerService {
    private static final String WE_CHAT_PACKAGE_NAME = "com.tencent.mm";

    private Map<String, String> mAutoReplyUserMsgMap;
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        //从SharePreferences中读取所有的配置
        mAutoReplyUserMsgMap = SfUtil.getDataList(this, Config.WE_CHAT_AUTO_REPLY_SF_NAME, Config.WE_CHAT_AUTO_REPLY_USER_MSG_MAP_KEY);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!WE_CHAT_PACKAGE_NAME.equals(sbn.getPackageName())){
            return;
        }

        Notification notification = sbn.getNotification();
        if (notification == null){
            return;
        }

        PendingIntent pendingIntent = null;
        //API > 18使用extras
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            Bundle extras = notification.extras;
            if (extras != null){
                String title = extras.getString(Notification.EXTRA_TITLE, "");
                String content = extras.getString(Notification.EXTRA_TEXT, "");
                if (!TextUtils.isEmpty(title) && mAutoReplyUserMsgMap.containsKey(title)){
                    pendingIntent = notification.contentIntent;
                }
            }
        }

        try {
            if (pendingIntent != null){
                pendingIntent.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    /**
     * 取消通知
     * @param sbn
     */
    public void  cancelNotification(StatusBarNotification sbn){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            cancelNotification(sbn.getKey());
        }else{
            cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
        }
    }

    /**
     * 检查通知监听服务是否被授权
     * @param context
     * @return
     */
    public boolean isNotificationListenerEnabled(Context context){
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 打开通知监听设置界面
     */
    public static void openNotificationListenSettings(Context context){
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 被杀死后再次启动,监听不生效问题
     */
    private void toggleNotificationListenerService(){
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, cn.codekong.maccessbilityservice.service.WeChatNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, cn.codekong.maccessbilityservice.service.WeChatNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}



























