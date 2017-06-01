package cn.rongcloud.im.model;

import java.util.List;

import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public class HistoryMsgBean {
    private List<Message> messages;
    private int scrollMode;
    private int reqCount;

    public HistoryMsgBean(List<Message> messages, int scrollMode, int reqCount) {
        this.messages = messages;
        this.scrollMode = scrollMode;
        this.reqCount = reqCount;
    }

    @Override
    public String toString() {
        return "HistoryMsgBean{" +
                "messages=" + messages +
                ", scrollMode=" + scrollMode +
                ", reqCount=" + reqCount +
                '}';
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getScrollMode() {
        return scrollMode;
    }

    public void setScrollMode(int scrollMode) {
        this.scrollMode = scrollMode;
    }

    public int getReqCount() {
        return reqCount;
    }

    public void setReqCount(int reqCount) {
        this.reqCount = reqCount;
    }
}
