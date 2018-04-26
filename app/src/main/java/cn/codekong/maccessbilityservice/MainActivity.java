package cn.codekong.maccessbilityservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.codekong.maccessbilityservice.activity.WeChatAutoReplyActivity;

/**
 * Created by shangzhenhong on 04/26/2018
 * Email: szh@codekong.cn
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Button weChatAutoReplyBtn = findViewById(R.id.id_we_chat_auto_reply_btn);
        weChatAutoReplyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_we_chat_auto_reply_btn:
                WeChatAutoReplyActivity.start(this);
                break;
        }
    }
}
