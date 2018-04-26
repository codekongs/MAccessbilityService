package cn.codekong.maccessbilityservice.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.codekong.maccessbilityservice.R;
import cn.codekong.maccessbilityservice.config.Config;
import cn.codekong.maccessbilityservice.service.WeChatAutoReplyAccessibilityService;
import cn.codekong.maccessbilityservice.util.MsgUtil;
import cn.codekong.maccessbilityservice.util.SfUtil;

public class WeChatAutoReplyActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //开启关闭自动回复
    private Button mStartAutoReplyBtn;
    private Button mCloseAutoReplyBtn;
    private Button mAddAutoReplyBtn;

    //用户消息映射
    private ListView mUserMsgListView;
    private ArrayAdapter<String> mUserMsgAdapter;
    private List<String> mUserMsgListData = new ArrayList<>();

    /**
     * 启动当前Activity
     *
     * @param activity
     */
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, WeChatAutoReplyActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_auto_reply);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mStartAutoReplyBtn = findViewById(R.id.id_start_auto_reply_btn);
        mCloseAutoReplyBtn = findViewById(R.id.id_close_auto_reply_btn);
        mAddAutoReplyBtn = findViewById(R.id.id_add_auto_reply_btn);
        mUserMsgListView = findViewById(R.id.id_user_msg_lv);

        mStartAutoReplyBtn.setOnClickListener(this);
        mCloseAutoReplyBtn.setOnClickListener(this);
        mAddAutoReplyBtn.setOnClickListener(this);

        mUserMsgListData.clear();
        mUserMsgListData.addAll(getListData());
        mUserMsgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, mUserMsgListData);
        mUserMsgListView.setAdapter(mUserMsgAdapter);
        mUserMsgListView.setOnItemClickListener(this);
    }

    /**
     * 从SharedPreferences中读取数据并拼接
     */
    private List<String> getListData() {
        List<String> list = new ArrayList<>();
        Map<String, String> map = SfUtil.getDataList(this, Config.WE_CHAT_AUTO_REPLY_SF_NAME, Config.WE_CHAT_AUTO_REPLY_USER_MSG_MAP_KEY);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list.add(entry.getKey() + ":" + entry.getValue());
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        boolean isEnabled = false;
        switch (v.getId()) {
            case R.id.id_start_auto_reply_btn:
                WeChatAutoReplyAccessibilityService.getInstance().init(this);
                isEnabled = WeChatAutoReplyAccessibilityService.getInstance().checkAccessibilityEnabled(Config.WE_CHAT_AUTO_REPLY_SERVICE_NAME);
                if (!isEnabled) {
                    WeChatAutoReplyAccessibilityService.getInstance().goAccess();
                }
                break;
            case R.id.id_close_auto_reply_btn:
                WeChatAutoReplyAccessibilityService.getInstance().init(this);
                isEnabled = WeChatAutoReplyAccessibilityService.getInstance().checkAccessibilityEnabled(Config.WE_CHAT_AUTO_REPLY_SERVICE_NAME);
                if (isEnabled) {
                    WeChatAutoReplyAccessibilityService.getInstance().goAccess();
                }
                break;
            case R.id.id_add_auto_reply_btn:
                addAutoReplyUserMsg();
                break;
        }
    }

    /**
     * 添加自动回复用户信息
     */
    private void addAutoReplyUserMsg() {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.wechat_auto_reply_input_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder1 = builder.setTitle(R.string.str_set_auto_reply_msg)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.str_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText inputUserNameEd = dialogView.findViewById(R.id.id_input_username_ed);
                        EditText inputMsgEd = dialogView.findViewById(R.id.id_input_msg_ed);
                        if (TextUtils.isEmpty(inputUserNameEd.getText())
                                || TextUtils.isEmpty(inputMsgEd.getText())) {
                            MsgUtil.showToast(WeChatAutoReplyActivity.this, R.string.str_input_dont_is_empty);
                        }
                        Map<String, String> map = new HashMap<>();
                        map.put(inputUserNameEd.getText().toString(), inputMsgEd.getText().toString());
                        SfUtil.saveData(WeChatAutoReplyActivity.this, Config.WE_CHAT_AUTO_REPLY_SF_NAME, map, Config.WE_CHAT_AUTO_REPLY_USER_MSG_MAP_KEY, true);
                        //刷新列表
                        refreshList();
                    }
                })
                .setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 刷新数据列表
     */
    private void refreshList() {
        mUserMsgAdapter.clear();
        mUserMsgListData.addAll(getListData());
        mUserMsgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
