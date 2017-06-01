package cn.rongcloud.im.ui.fragment;

import android.content.Intent;

import cn.rongcloud.im.ui.activity.ReadReceiptDetailActivity;
import io.rong.imlib.model.Conversation;

/**
 * Created by DELL on 2017/5/25.
 */

public class MyConversationFragmentEx extends MyConversationFragment {

    @Override
    public boolean onResendItemClick(io.rong.imlib.model.Message message) {
        return false;
    }

    @Override
    public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {
        if (message.getConversationType() == Conversation.ConversationType.GROUP) { //目前只适配了群组会话
            Intent intent = new Intent(getActivity(), ReadReceiptDetailActivity.class);
            intent.putExtra("message", message);
            getActivity().startActivity(intent);
        }
    }

    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }
}
