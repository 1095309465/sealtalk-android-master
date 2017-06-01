package cn.rongcloud.im.utils.googleApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rongcloud.im.utils.SPUtils;

/**
 * Created by DELL on 2017/5/26.
 */

public class TransGoogleUtil {
    private String query;
    private String host = "https://www.googleapis.com/language/translate/v2";
    private String securityKey = "AIzaSyCegO8LjPujwaTtxijzowN3kCUQTop8tRA";


    public TransGoogleUtil(String query) {
        this.query = query;
    }

    public String startGoogleTranslate() {

        String bean = HttpGet.get(host, getAfterUrl());
        return getContent(bean);
    }

    private String getAfterUrl() {
        StringBuilder sb = new StringBuilder();

        String to = SPUtils.find("language");
        sb.append("?key=");
        sb.append(securityKey);
        sb.append("&target=");
        sb.append(to);
        sb.append("&q=");
        sb.append(query);
        return sb.toString();
    }

    private String getContent(String json) {
        try {
            JSONObject object = JSON.parseObject(json);
            JSONObject data = object.getJSONObject("data");
            JSONArray array = data.getJSONArray("translations");

            JSONObject object1 = (JSONObject) array.get(0);
            String translatedText = object1.getString("translatedText");
            return translatedText;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        // return "解析error=" + e.getMessage();
    }


}
