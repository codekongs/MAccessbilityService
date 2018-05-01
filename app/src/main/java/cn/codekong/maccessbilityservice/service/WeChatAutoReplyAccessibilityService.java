package cn.codekong.maccessbilityservice.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import cn.codekong.maccessbilityservice.R;
import cn.codekong.maccessbilityservice.bean.TuLingReq;
import cn.codekong.maccessbilityservice.bean.TuLingRsp;
import cn.codekong.maccessbilityservice.config.Config;
import cn.codekong.maccessbilityservice.net.HttpCallBackListener;
import cn.codekong.maccessbilityservice.net.HttpMethod;
import cn.codekong.maccessbilityservice.net.NetConnection;
import cn.codekong.maccessbilityservice.service.base.BaseAccessibilityService;
import cn.codekong.maccessbilityservice.util.EncryptUtil;
import cn.codekong.maccessbilityservice.util.SfUtil;

/**
 * Created by shangzhenhong on 04/26/2018
 * Email: szh@codekong.cn
 */
public class WeChatAutoReplyAccessibilityService extends BaseAccessibilityService {
    //微信聊天主页面
    private static final String WE_CHAT_LAUNCHER_UI_NAME = "com.tencent.mm.ui.LauncherUI";
    //微信输入框id
    private static final String WE_CHAT_MSG_INPUT_EDIT_TEXT_ID = "com.tencent.mm:id/aaa";

    private Map<String, String> mAutoReplyUserMsgMap;

    //聊天用户名
    private String mChatUserName;
    //聊天内容
    private String mChatContent;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        List<CharSequence> textList;
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                textList = event.getText();
                if (!textList.isEmpty()) {
                    for (CharSequence text : textList) {
                        if (!TextUtils.isEmpty(text)) {
                            notifyWeChat(event);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                textList = event.getText();
                if (!textList.isEmpty()) {
                    for (CharSequence text : textList) {
                        if (!TextUtils.isEmpty(text)) {
                            notifyWeChat(event);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals(WE_CHAT_LAUNCHER_UI_NAME)) {
                    final String replyMsg = mAutoReplyUserMsgMap.get(mChatUserName);
                    if (getString(R.string.str_robot).equals(replyMsg)){
                        String paramsStr = buildReqParams();
                        //网路请求图灵接口
                        new NetConnection(Config.TU_LING_BASE_URL, HttpMethod.POST, new HttpCallBackListener() {
                            @Override
                            public void onFinish(String response) {
                                Gson gson = new Gson();
                                TuLingRsp tuLingRsp = gson.fromJson(response, TuLingRsp.class);
                                if (tuLingRsp.getResults() != null){
                                    List<TuLingRsp.Results> resultsList = tuLingRsp.getResults();
                                    StringBuilder lines = new StringBuilder();
                                    for(TuLingRsp.Results results : resultsList){
                                        if (results.getValues().getText() != null){
                                            lines.append(results.getValues().getText());
                                        }
                                        if (results.getValues().getUrl() != null){
                                            lines.append(results.getValues().getUrl());
                                        }
                                    }
                                    if (fillReplyMsg(lines.toString())){
                                        sendMsg();
                                    }
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }, null, paramsStr);
                    }else{
                        if (fillReplyMsg(mAutoReplyUserMsgMap.get(mChatUserName))){
                            sendMsg();
                        }
                    }
                }
                break;
        }
    }

    /**
     * 组装请求参数
     * @return
     */
    private String buildReqParams() {
        if (TextUtils.isEmpty(mChatUserName) || TextUtils.isEmpty(mChatContent)){
            return "";
        }
        TuLingReq tuLingReq = new TuLingReq();

        tuLingReq.setReqType(0);

        TuLingReq.Perception perception = new TuLingReq.Perception();
        TuLingReq.Perception.InputText inputText = new TuLingReq.Perception.InputText();
        inputText.setText(mChatContent);
        perception.setInputText(inputText);
        tuLingReq.setPerception(perception);

        TuLingReq.UserInfo userInfo = new TuLingReq.UserInfo();
        userInfo.setApiKey(Config.TU_LING_API_KEY);
        userInfo.setUserId(EncryptUtil.md5Encrypt(mChatUserName));
        tuLingReq.setUserInfo(userInfo);

        Gson gson = new Gson();
        return gson.toJson(tuLingReq, TuLingReq.class);
    }

    /**
     * 唤起微信界面
     */
    private void notifyWeChat(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            String content = notification.tickerText.toString();

            //获取通知内容并分割
            String[] c = content.split(":");
            mChatUserName = c[0].trim();
            mChatContent = c[1].trim();
            if (!mAutoReplyUserMsgMap.containsKey(mChatUserName)) {
                return;
            }
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 填充回复信息
     */
    private boolean fillReplyMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        AccessibilityNodeInfo replyEditTextNodeInfo = findViewById(WE_CHAT_MSG_INPUT_EDIT_TEXT_ID);
        if ("android.widget.EditText".equals(replyEditTextNodeInfo.getClassName().toString())) {
            //消息填充
            Bundle arguments = new Bundle();
            arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
            arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
            replyEditTextNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
            replyEditTextNodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

            //剪切板粘贴
            ClipData clipData = ClipData.newPlainText("label", msg);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                replyEditTextNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }
        }
        return false;
    }

    /**
     * 发送消息
     */
    private void sendMsg() {
        AccessibilityNodeInfo sendBtnNodeInfo = findViewByText("发送", true);
        if (sendBtnNodeInfo == null) {
            sendBtnNodeInfo = findViewByText("Send");
        }
        if (sendBtnNodeInfo != null && "android.widget.Button".equals(sendBtnNodeInfo.getClassName().toString())) {
            sendBtnNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        //返回到桌面
        performHomeClick();
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //从SharePreferences中读取所有的配置
        mAutoReplyUserMsgMap = SfUtil.getDataList(this, Config.WE_CHAT_AUTO_REPLY_SF_NAME, Config.WE_CHAT_AUTO_REPLY_USER_MSG_MAP_KEY);
    }
}










