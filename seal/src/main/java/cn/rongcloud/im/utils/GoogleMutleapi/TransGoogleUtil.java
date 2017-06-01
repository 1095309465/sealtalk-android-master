package cn.rongcloud.im.utils.GoogleMutleapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import cn.rongcloud.im.utils.SPUtils;
import io.rong.imkit.model.UIMessage;

/**
 * Created by DELL on 2017/5/26.
 */

public class TransGoogleUtil {
    private List<UIMessage> mList;
    private String host = "https://www.googleapis.com/language/translate/v2";
    private String securityKey = "AIzaSyCegO8LjPujwaTtxijzowN3kCUQTop8tRA";


    public TransGoogleUtil(List<UIMessage> mList) {
        this.mList = mList;
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
       // sb.append(query);
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
