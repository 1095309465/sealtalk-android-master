package cn.rongcloud.im.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.util.List;

import cn.rongcloud.im.model.TranslateGoogleBean;
import cn.rongcloud.im.utils.googleApi.TransGoogleApi;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by DELL on 2017/5/26.
 */

public class TranslateGoogleUtil {
    public static String googleTranslate(List<Message> list) {
        if (list == null) return "";
        if (list.size() <= 0) return "";
        TransGoogleApi api = new TransGoogleApi(googleKey);
        String to = SPUtils.find("language");
        to = "ko";
        String content = api.getTransResult(list, to);
        operate(list, content);
        return content;


    }

    private static int index = 0;

    public static void operate(List<Message> list, String str) {
        try {
            JSONObject object = JSON.parseObject(str);
            JSONObject data = object.getJSONObject("data");
            JSONArray array = data.getJSONArray("translations");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object1 = (JSONObject) array.get(i);
                String translatedText = object1.getString("translatedText");
                Log.e("123", "translatedText=" + translatedText);

                ok:
                for (int j = index; j < list.size(); j++) {

                    Message msg = list.get(j);
                    if (msg.getContent() instanceof TextMessage) {
                        TextMessage textMsg = (TextMessage) msg.getContent();
                        textMsg.setContent(translatedText);
                        index = j + 1;

                        break ok;
                    }

                }


            }
        } catch (Exception e) {
            Log.e("123", "json解析错误" + e.getMessage());
        }

    }

    ;

    private static final String googleKey = "AIzaSyCegO8LjPujwaTtxijzowN3kCUQTop8tRA";
}
