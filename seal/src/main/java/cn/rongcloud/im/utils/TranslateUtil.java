package cn.rongcloud.im.utils;

import android.util.Log;

import java.util.List;

import cn.rongcloud.im.utils.baiduapi.TransApi;
import io.rong.imlib.model.Message;

/**
 * Created by DELL on 2017/5/25.
 */

public class TranslateUtil {
    public static String baidufanyi(String query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String to = SPUtils.find("language");
        Log.e("123", "to=" + to);
        String content = api.getTransResult(query, "auto", to);
        Log.e("123", "翻译=" + content);
        return content;
    }

    private static final String APP_ID = "20170524000048971";
    private static final String SECURITY_KEY = "jHQWFLr1Lk5KO2nQzYfn";

}
