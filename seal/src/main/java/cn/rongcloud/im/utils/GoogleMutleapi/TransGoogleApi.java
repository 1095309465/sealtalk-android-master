package cn.rongcloud.im.utils.GoogleMutleapi;

import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by DELL on 2017/5/26.
 */

public class TransGoogleApi {

    private static final String TRANS_API_HOST = "https://www.googleapis.com/language/translate/v2";
    private static StringBuilder sb = null;

    private String securityKey;

    public TransGoogleApi(String securityKey) {
        this.securityKey = securityKey;
        sb = new StringBuilder();
    }

    public String getTransResult(List<Message> list, String to) {
        String afterUrl = buildParams(list, to);
        return HttpGet.get(TRANS_API_HOST, afterUrl);
    }

    ;

    private String buildParams(List<Message> list, String to) {
        if (list == null) return "";
        if (list.size() <= 0) return "";
        sb.append("?key=");
        sb.append(securityKey);
        sb.append("&target=");
        sb.append(to);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getContent() instanceof TextMessage) {
                TextMessage msg = (TextMessage) list.get(i).getContent();
                String q = msg.getContent();
                sb.append("&q=");
                sb.append(q);
            }
        }
        return sb.toString();
    }
}
