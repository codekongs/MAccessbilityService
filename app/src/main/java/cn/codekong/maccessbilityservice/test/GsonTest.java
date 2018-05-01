package cn.codekong.maccessbilityservice.test;

import com.google.gson.Gson;

import cn.codekong.maccessbilityservice.bean.TuLingReq;
import cn.codekong.maccessbilityservice.config.Config;
import cn.codekong.maccessbilityservice.util.EncryptUtil;

/**
 * Created by shangzhenhong on 05/01/2018
 * Email: szh@codekong.cn
 */
public class GsonTest {
    public static void main(String[] args) {
        TuLingReq tuLingReq = new TuLingReq();

        tuLingReq.setReqType(0);

        TuLingReq.Perception perception = new TuLingReq.Perception();
        TuLingReq.Perception.InputText inputText = new TuLingReq.Perception.InputText();
        inputText.setText("123");
        perception.setInputText(inputText);
        tuLingReq.setPerception(perception);

        TuLingReq.UserInfo userInfo = new TuLingReq.UserInfo();
        userInfo.setApiKey(Config.TU_LING_API_KEY);
        userInfo.setUserId(EncryptUtil.md5Encrypt("hjh"));
        tuLingReq.setUserInfo(userInfo);

        Gson gson = new Gson();
        System.out.println(gson.toJson(tuLingReq, TuLingReq.class));
    }
}
