package cn.rongcloud.im.message.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.rongcloud.im.App;
import cn.rongcloud.im.R;
import cn.rongcloud.im.utils.SPUtils;
import cn.rongcloud.im.utils.TextTranslateTask;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.ILinkClickListener;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by DELL on 2017/5/27.
 */
@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true
)

public class MyTextMessageItemProvider extends IContainerItemProvider.MessageProvider<TextMessage> {
    private static final String TAG = "MyTextMessageItemProvider";

    public MyTextMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_text_message, (ViewGroup) null);
        ViewHolder holder = new ViewHolder();
        holder.message = (AutoLinkTextView) view.findViewById(android.R.id.text1);
        view.setTag(holder);
        return view;
    }

    public Spannable getContentSummary(TextMessage data) {
        if (data == null) {
            return null;
        } else {
            String content = data.getContent();
            if (content != null) {
                if (content.length() > 100) {
                    content = content.substring(0, 100);
                }

                return new SpannableString(AndroidEmoji.ensure(content));
            } else {
                return null;
            }
        }
    }

    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
    }

    public void onItemLongClick(final View view, int position, final TextMessage content, final UIMessage message) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.longClick = true;
        if (view instanceof TextView) {
            CharSequence items = ((TextView) view).getText();
            if (items != null && items instanceof Spannable) {
                Selection.removeSelection((Spannable) items);
            }
        }

        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;
        boolean hasSent = !message.getSentStatus().equals(Message.SentStatus.SENDING) && !message.getSentStatus().equals(Message.SentStatus.FAILED);

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException var15) {
            RLog.e("TextMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
            var15.printStackTrace();
        }

        String[] items1;
        if (hasSent && enableMessageRecall && normalTime - message.getSentTime() <= (long) (messageRecallInterval * 1000) && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && !message.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && !message.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE) && !message.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE) && !message.getConversationType().equals(Conversation.ConversationType.SYSTEM) && !message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
            items1 = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_recall)};
        } else {
            items1 = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items1).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(content.getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, (RongIMClient.ResultCallback) null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), MyTextMessageItemProvider.this.getPushContent(view.getContext(), message));
                }

            }
        }).show();
    }

    public void bindView(final View v, int position, TextMessage content, final UIMessage data) {
        Log.e("123", "MyTextMessageItemProvider——bindView  " + data.getTextMessageContent() + "   position= " + position);
        ViewHolder holder = (ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
        } else {
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }

        final AutoLinkTextView textView = holder.message;
        textView.setTag(R.id.tag_second, position);
        if (data.getTextMessageContent() != null) {
            textView.setText(data.getTextMessageContent().toString());
            String var = App.content.get(data.getTextMessageContent().toString());
            if (!TextUtils.isEmpty(var)) {
                setText(v,textView,var,data.getTextMessageContent().toString());
            }else{
                new TextTranslateTask(v,textView, position, new TextTranslateTask.OnTranslateListener() {
                    @Override
                    public void setData(View viewGroup, AutoLinkTextView tv, int position, String translateVar, String src) {
                        int tag = (Integer) tv.getTag(R.id.tag_second);
                        Log.e("123", "tag= " + tag + "  position=" + position);
                        if (tag != position) return;
                        setText(viewGroup, tv, translateVar,src);
                    }

                }).execute(data.getTextMessageContent().toString());
            }


        }

        holder.message.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener() {
            public boolean onLinkClick(String link) {
                RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
                boolean result = false;
                if (listener != null) {
                    result = listener.onMessageLinkClick(v.getContext(), link);
                }

                if (listener == null || !result) {
                    String str = link.toLowerCase();
                    if (str.startsWith("http") || str.startsWith("https")) {
                        Intent intent = new Intent("io.rong.imkit.intent.action.webview");
                        intent.setPackage(v.getContext().getPackageName());
                        intent.putExtra("url", link);
                        v.getContext().startActivity(intent);
                        result = true;
                    }
                }

                return result;
            }
        }));
    }

    private void setText(final View v, final AutoLinkTextView tv, final String translateVar,String src) {
        final String show=getShowText(src,translateVar);
        int len = show.length();
        if (v.getHandler() != null && len > 500) {
            v.getHandler().postDelayed(new Runnable() {
                public void run() {
                    tv.setText(show);
                }
            }, 50L);
        } else {
            tv.setText(show);
        }

    }

    private String getShowText(String src,String translate){
        boolean flag= SPUtils.findBoolean("showModel");
        StringBuilder sb=new StringBuilder();
        if(flag){
            sb.append("原文: "+src);
            sb.append("\r\n");
            sb.append("译文: ");
        }
        sb.append(translate);
        return  sb.toString();

    }

    private static class ViewHolder {
        AutoLinkTextView message;
        boolean longClick;

        private ViewHolder() {
        }
    }
}
