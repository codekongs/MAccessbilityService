package cn.codekong.maccessbilityservice.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 消息提示相关工具类
 * Created by shangzhenhong on 04/26/2018
 * Email: szh@codekong.cn
 */
public class MsgUtil {
    private static Toast toast;

    /**
     * 显示Toast
     * @param context 上下文
     * @param content 要显示的内容
     */
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /**
     * 显示Toast
     * @param context 上下文
     * @param resId 要显示的资源id
     */
    public static void showToast(Context context, int resId) {
        showToast(context, (String) context.getResources().getText(resId));
    }
}
