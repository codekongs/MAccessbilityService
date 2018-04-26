package cn.codekong.maccessbilityservice.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SharePreferences工具类
 * Created by shangzhenhong on 04/26/2018
 * Email: szh@codekong.cn
 */
public class SfUtil {

    /**
     * 保存单条数据
     * @param context
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static void saveData(Context context, String fileName, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 可选追加保存一组数据，并且单独存储了key
     * @param context
     * @param fileName
     * @param dataMap
     * @param allKeysOfKey
     */
    public static void saveData(Context context, String fileName, Map<String, String> dataMap, String allKeysOfKey, boolean append){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (append){
            Map<String, String> data = getDataList(context, fileName, allKeysOfKey);
            dataMap.putAll(data);
        }
        for (Map.Entry<String, String> entry : dataMap.entrySet()){
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.putStringSet(allKeysOfKey, dataMap.keySet());
        editor.apply();
    }

    /**
     * 获得指定key的值
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static String getData(Context context, String fileName, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 一次获取所有的key-map数据
     * @param context
     * @param fileName
     * @param allKeysOfKey
     * @return
     */
    public static Map<String, String> getDataList(Context context, String fileName, String allKeysOfKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Map<String, String> result = new HashMap<>();
        if (sharedPreferences.contains(allKeysOfKey)){
            Set<String> keySet = sharedPreferences.getStringSet(allKeysOfKey, null);
            if (keySet != null){
                for (String key : keySet){
                    result.put(key, sharedPreferences.getString(key, ""));
                }
            }
        }
        return result;
    }
}
