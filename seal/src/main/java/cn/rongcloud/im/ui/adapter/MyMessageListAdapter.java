package cn.rongcloud.im.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.rongcloud.im.App;
import cn.rongcloud.im.message.provider.MyTextMessageItemProvider;
import cn.rongcloud.im.model.TranslateBean;
import cn.rongcloud.im.utils.TranslateUtil;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.DebouncedOnClickListener;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.*;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class MyMessageListAdapter extends io.rong.imkit.widget.adapter.BaseAdapter<UIMessage> {
    private static final String TAG = "MyMessageListAdapter";
    private static final long READ_RECEIPT_REQUEST_INTERVAL = 120L;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemHandlerListener mOnItemHandlerListener;
    boolean evaForRobot = false;
    boolean robotMode = true;
    private boolean timeGone = false;

    public MyMessageListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }


    public void onDestory() {
    }


    public void setOnItemHandlerListener(OnItemHandlerListener onItemHandlerListener) {
        this.mOnItemHandlerListener = onItemHandlerListener;
    }

    public long getItemId(int position) {
        UIMessage message = (UIMessage) this.getItem(position);
        return message == null ? -1L : (long) message.getMessageId();
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(io.rong.imkit.R.layout.rc_item_message, (ViewGroup) null);
        ViewHolder holder = new ViewHolder();
        holder.leftIconView = (AsyncImageView) this.findViewById(result, io.rong.imkit.R.id.rc_left);
        holder.rightIconView = (AsyncImageView) this.findViewById(result, io.rong.imkit.R.id.rc_right);
        holder.nameView = (TextView) this.findViewById(result, io.rong.imkit.R.id.rc_title);
        holder.contentView = (ProviderContainerView) this.findViewById(result, io.rong.imkit.R.id.rc_content);
        holder.layout = (ViewGroup) this.findViewById(result, io.rong.imkit.R.id.rc_layout);
        holder.progressBar = (ProgressBar) this.findViewById(result, io.rong.imkit.R.id.rc_progress);
        holder.warning = (ImageView) this.findViewById(result, io.rong.imkit.R.id.rc_warning);
        holder.readReceipt = (ImageView) this.findViewById(result, io.rong.imkit.R.id.rc_read_receipt);
        holder.readReceiptRequest = (ImageView) this.findViewById(result, io.rong.imkit.R.id.rc_read_receipt_request);
        holder.readReceiptStatus = (TextView) this.findViewById(result, io.rong.imkit.R.id.rc_read_receipt_status);
        holder.time = (TextView) this.findViewById(result, io.rong.imkit.R.id.rc_time);
        holder.sentStatus = (TextView) this.findViewById(result, io.rong.imkit.R.id.rc_sent_status);
        holder.layoutItem = (RelativeLayout) this.findViewById(result, io.rong.imkit.R.id.rc_layout_item_message);
        if (holder.time.getVisibility() == View.GONE) {
            this.timeGone = true;
        } else {
            this.timeGone = false;
        }

        result.setTag(holder);
        return result;
    }

    private boolean getNeedEvaluate(UIMessage data) {
        String extra = "";
        String robotEva = "";
        String sid = "";
        if (data != null && data.getConversationType() != null && data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            if (data.getContent() instanceof TextMessage) {
                extra = ((TextMessage) data.getContent()).getExtra();
                if (TextUtils.isEmpty(extra)) {
                    return false;
                }

                try {
                    JSONObject e = new JSONObject(extra);
                    robotEva = e.optString("robotEva");
                    sid = e.optString("sid");
                } catch (JSONException var6) {
                    ;
                }
            }

            if (data.getMessageDirection() == Message.MessageDirection.RECEIVE && data.getContent() instanceof TextMessage && this.evaForRobot && this.robotMode && !TextUtils.isEmpty(robotEva) && !TextUtils.isEmpty(sid) && !data.getIsHistoryMessage()) {
                return true;
            }
        }

        return false;
    }

   /* private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (isRunning) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isRunning = true;

                        int count = getCount();
                        if (count <= 0) {


                        }else{


                        }


                        handler.removeMessages(1);
                        isRunning = false;
                    }
                }).start();
                handler.sendEmptyMessageDelayed(1, 5000);
            } else if (msg.what == 1) {
                isRunning = false;
            }

        }
    };*/

    //   private boolean isRunning = false;


    protected void bindView(final View v, final int position, final UIMessage data) {
        if (data.getContent() instanceof TextMessage) {
            Log.e("123", "data.getContent() instanceof TextMessage");
        }
        Log.e("123", "執行bindView" + position);
        // handler.sendEmptyMessage(0);
     /*   Log.e("123", "138bindView  " + Thread.currentThread().getName() + "  " + position);
        boolean flag = data.getContent() instanceof TextMessage;
        if (flag) {
            TextMessage msg = (TextMessage) data.getContent();
            final String srcVat = msg.getContent();
            String var = App.content.get(srcVat);
            if (!TextUtils.isEmpty(var)) {
                msg.setContent(var);
                Log.e("123", "150TextMessage,Map不为空  " + var + "   原文=" + srcVat);
            } else {
                Log.e("123", "161TextMessage,Map为空  " + var + "    原文=" + srcVat);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String transLateStr = TranslateUtil.baidufanyi(srcVat);
                        App.content.put(srcVat, transLateStr);
                        //  handler.sendEmptyMessage(0);
                    }
                }).start();
                //  msg.setContent("这是韩语");

            }

        } else {
            Log.e("123", "142ClassName=" + data.getContent().getClass().getName());
        }*/
        if (data != null) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            if (holder == null) {
                RLog.e("MessageListAdapter", "view holder is null !");
            } else {
                Object provider;
                ProviderTag tag;
                if (this.getNeedEvaluate(data)) {
                    provider = RongContext.getInstance().getEvaluateProvider();
                    tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                } else {
                    if (RongContext.getInstance() == null || data == null || data.getContent() == null) {
                        RLog.e("MessageListAdapter", "Message is null !");
                        return;
                    }

                    provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (provider == null) {
                        provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
                        tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
                    } else {
                        tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                    }

                    if (provider == null) {
                        RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
                        return;
                    }
                }
                if (data.getContent() instanceof TextMessage) {
                    provider = new MyTextMessageItemProvider();
                }
                //  Log.e("123", "173TextMessage bindView=" + flag);
                final View view = holder.contentView.inflate((IContainerItemProvider) provider);
                ((IContainerItemProvider) provider).bindView(view, position, data);

                if (tag == null) {
                    RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
                } else {
                    if (tag.hide()) {
                        holder.contentView.setVisibility(View.GONE);
                        holder.time.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.GONE);
                        holder.leftIconView.setVisibility(View.GONE);
                        holder.rightIconView.setVisibility(View.GONE);
                        holder.layoutItem.setVisibility(View.GONE);
                        holder.layoutItem.setPadding(0, 0, 0, 0);
                    } else {
                        holder.contentView.setVisibility(View.VISIBLE);
                        holder.layoutItem.setVisibility(View.VISIBLE);
                        holder.layoutItem.setPadding(RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F), RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F));
                    }

                    UserInfo var13;
                    GroupUserInfo var14;
                    if (data.getMessageDirection() == Message.MessageDirection.SEND) {
                        if (tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.VISIBLE);
                            holder.leftIconView.setVisibility(View.GONE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }

                        if (!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 5);
                            holder.contentView.containerViewRight();
                            holder.nameView.setGravity(5);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }

                        boolean time = false;

                        try {
                            time = this.mContext.getResources().getBoolean(io.rong.imkit.R.bool.rc_read_receipt);
                        } catch (Resources.NotFoundException var12) {
                            RLog.e("MessageListAdapter", "rc_read_receipt not configure in rc_config.xml");
                            var12.printStackTrace();
                        }

                        if (data.getSentStatus() == Message.SentStatus.SENDING) {
                            if (tag.showProgress()) {
                                holder.progressBar.setVisibility(View.VISIBLE);
                            } else {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == Message.SentStatus.FAILED) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.VISIBLE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == Message.SentStatus.SENT) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (time && data.getSentStatus() == Message.SentStatus.READ) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            if (data.getConversationType().equals(Conversation.ConversationType.PRIVATE) && tag.showReadState()) {
                                holder.readReceipt.setVisibility(View.VISIBLE);
                            } else {
                                holder.readReceipt.setVisibility(View.GONE);
                            }
                        } else {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        }

                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        if (time && RongContext.getInstance().isReadReceiptConversationType(data.getConversationType()) && (data.getConversationType().equals(Conversation.ConversationType.GROUP) || data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
                            if (data.getContent() instanceof TextMessage && !TextUtils.isEmpty(data.getUId())) {
                                boolean pre = true;

                                for (int publicServiceProfile = position + 1; publicServiceProfile < this.getCount(); ++publicServiceProfile) {
                                    if (((UIMessage) this.getItem(publicServiceProfile)).getMessageDirection() == Message.MessageDirection.SEND) {
                                        pre = false;
                                        break;
                                    }
                                }

                                long var16 = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
                                if (var16 - data.getSentTime() < 120000L && pre && (data.getReadReceiptInfo() == null || !data.getReadReceiptInfo().isReadReceiptMessage())) {
                                    holder.readReceiptRequest.setVisibility(View.VISIBLE);
                                }
                            }

                            if (data.getContent() instanceof TextMessage && data.getReadReceiptInfo() != null && data.getReadReceiptInfo().isReadReceiptMessage()) {
                                if (data.getReadReceiptInfo().getRespondUserIdList() != null) {
                                    holder.readReceiptStatus.setText(String.format(view.getResources().getString(io.rong.imkit.R.string.rc_read_receipt_status), new Object[]{Integer.valueOf(data.getReadReceiptInfo().getRespondUserIdList().size())}));
                                } else {
                                    holder.readReceiptStatus.setText(String.format(view.getResources().getString(io.rong.imkit.R.string.rc_read_receipt_status), new Object[]{Integer.valueOf(0)}));
                                }

                                holder.readReceiptStatus.setVisibility(View.VISIBLE);
                            }
                        }

                        holder.nameView.setVisibility(View.GONE);
                        holder.readReceiptRequest.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                RongIMClient.getInstance().sendReadReceiptRequest(data.getMessage(), new RongIMClient.OperationCallback() {
                                    public void onSuccess() {
                                        ReadReceiptInfo readReceiptInfo = data.getReadReceiptInfo();
                                        if (readReceiptInfo == null) {
                                            readReceiptInfo = new ReadReceiptInfo();
                                            data.setReadReceiptInfo(readReceiptInfo);
                                        }

                                        readReceiptInfo.setIsReadReceiptMessage(true);
                                        holder.readReceiptStatus.setText(String.format(view.getResources().getString(io.rong.imkit.R.string.rc_read_receipt_status), new Object[]{Integer.valueOf(0)}));
                                        holder.readReceiptRequest.setVisibility(View.GONE);
                                        holder.readReceiptStatus.setVisibility(View.VISIBLE);
                                    }

                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        RLog.e("MessageListAdapter", "sendReadReceiptRequest failed, errorCode = " + errorCode);
                                    }
                                });
                            }
                        });
                        holder.readReceiptStatus.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (MyMessageListAdapter.this.mOnItemHandlerListener != null) {
                                    MyMessageListAdapter.this.mOnItemHandlerListener.onReadReceiptStateClick(data.getMessage());
                                }

                            }
                        });
                        holder.rightIconView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MyMessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                                }

                            }
                        });
                        holder.rightIconView.setOnLongClickListener(new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MyMessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                                } else {
                                    return true;
                                }
                            }
                        });
                        if (!tag.showWarning()) {
                            holder.warning.setVisibility(View.GONE);
                        }
                    } else {
                        if (tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.GONE);
                            holder.leftIconView.setVisibility(View.VISIBLE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }

                        if (!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 3);
                            holder.contentView.containerViewLeft();
                            holder.nameView.setGravity(3);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }

                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.VISIBLE);
                        if (data.getConversationType() != Conversation.ConversationType.PRIVATE && tag.showSummaryWithName() && data.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE && data.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE) {
                            var13 = null;
                            if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                                if (data.getUserInfo() != null) {
                                    var13 = data.getUserInfo();
                                } else if (data.getMessage() != null && data.getMessage().getContent() != null) {
                                    var13 = data.getMessage().getContent().getUserInfo();
                                }

                                if (var13 != null) {
                                    holder.nameView.setText(var13.getName());
                                } else {
                                    holder.nameView.setText(data.getSenderUserId());
                                }
                            } else if (data.getConversationType() == Conversation.ConversationType.GROUP) {
                                var14 = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
                                if (var14 != null) {
                                    holder.nameView.setText(var14.getNickname());
                                } else {
                                    var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                    if (var13 == null) {
                                        holder.nameView.setText(data.getSenderUserId());
                                    } else {
                                        holder.nameView.setText(var13.getName());
                                    }
                                }
                            } else {
                                var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                if (var13 == null) {
                                    holder.nameView.setText(data.getSenderUserId());
                                } else {
                                    holder.nameView.setText(var13.getName());
                                }
                            }
                        } else {
                            holder.nameView.setVisibility(View.GONE);
                        }

                        holder.leftIconView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MyMessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                                }

                            }
                        });
                    }

                    holder.leftIconView.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            UserInfo userInfo = null;
                            if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                            }

                            if (RongContext.getInstance().getConversationBehaviorListener() != null && RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MyMessageListAdapter.this.mContext, data.getConversationType(), userInfo)) {
                                return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MyMessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                            } else if (!RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_mentioned_message) || !data.getConversationType().equals(Conversation.ConversationType.GROUP) && !data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                                return false;
                            } else {
                                RongMentionManager.getInstance().mentionMember(data.getConversationType(), data.getTargetId(), data.getSenderUserId());
                                return true;
                            }
                        }
                    });
                    ConversationKey mKey;
                    Uri var15;
                    PublicServiceProfile var17;
                    if (holder.rightIconView.getVisibility() == View.VISIBLE) {
                        if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            var15 = var13.getPortraitUri();
                            if (var15 != null) {
                                holder.rightIconView.setAvatar(var15.toString(), 0);
                            }
                        } else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            } else {
                                mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                                var17 = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                                var15 = var17.getPortraitUri();
                                if (var15 != null) {
                                    holder.rightIconView.setAvatar(var15.toString(), 0);
                                }
                            }
                        } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            if (var13 != null && var13.getPortraitUri() != null) {
                                holder.rightIconView.setAvatar(var13.getPortraitUri().toString(), 0);
                            }
                        }
                    } else if (holder.leftIconView.getVisibility() == View.VISIBLE) {
                        var13 = null;
                        var14 = null;
                        if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                            if (data.getUserInfo() != null) {
                                var13 = data.getUserInfo();
                            } else if (data.getMessage() != null && data.getMessage().getContent() != null) {
                                var13 = data.getMessage().getContent().getUserInfo();
                            }

                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            }
                        } else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            } else {
                                mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                                var17 = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                                if (var17 != null && var17.getPortraitUri() != null) {
                                    holder.leftIconView.setAvatar(var17.getPortraitUri().toString(), 0);
                                }
                            }
                        } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            if (var13 != null && var13.getPortraitUri() != null) {
                                holder.leftIconView.setAvatar(var13.getPortraitUri().toString(), 0);
                            }
                        }
                    }

                    if (view != null) {
                        view.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() == null || !RongContext.getInstance().getConversationBehaviorListener().onMessageClick(MyMessageListAdapter.this.mContext, v, data.getMessage())) {
                                    Object provider;
                                    if (MyMessageListAdapter.this.getNeedEvaluate(data)) {
                                        provider = RongContext.getInstance().getEvaluateProvider();
                                    } else {
                                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                    }

                                    if (provider != null) {
                                        ((IContainerItemProvider.MessageProvider) provider).onItemClick(v, position, data.getContent(), data);
                                    }

                                }
                            }
                        });
                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null && RongContext.getInstance().getConversationBehaviorListener().onMessageLongClick(MyMessageListAdapter.this.mContext, v, data.getMessage())) {
                                    return true;
                                } else {
                                    Object provider;
                                    if (MyMessageListAdapter.this.getNeedEvaluate(data)) {
                                        provider = RongContext.getInstance().getEvaluateProvider();
                                    } else {
                                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                    }

                                    if (provider != null && data.getContent() instanceof TextMessage) {
                                        onItemLongClick(v, position, (TextMessage) data.getContent(), data);
                                        //   ((IContainerItemProvider.MessageProvider) provider).onItemLongClick(v, position, data.getContent(), data);
                                    }

                                    return true;
                                }
                            }
                        });
                    }

                    holder.warning.setOnClickListener(new DebouncedOnClickListener() {
                        public void onDebouncedClick(View view) {
                            if (MyMessageListAdapter.this.mOnItemHandlerListener != null) {
                                MyMessageListAdapter.this.mOnItemHandlerListener.onWarningViewClick(position, data.getMessage(), view);
                            }

                        }
                    });
                    if (tag.hide()) {
                        holder.time.setVisibility(View.GONE);
                    } else {
                        if (!this.timeGone) {
                            String var19 = RongDateUtils.getConversationFormatDate(data.getSentTime(), view.getContext());
                            holder.time.setText(var19);
                            if (position == 0) {
                                holder.time.setVisibility(View.VISIBLE);
                            } else {
                                UIMessage var18 = (UIMessage) this.getItem(position - 1);
                                if (RongDateUtils.isShowChatTime(data.getSentTime(), var18.getSentTime(), 180)) {
                                    holder.time.setVisibility(View.VISIBLE);
                                } else {
                                    holder.time.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    public void onItemLongClick(final View view, int position, final TextMessage content, final UIMessage message) {
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
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }

            }
        }).show();
    }

    public String getPushContent(Context context, UIMessage message) {
        String userName = "";
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
        if (userInfo != null) {
            userName = userInfo.getName();
        }

        return context.getString(io.rong.imkit.R.string.rc_user_recalled_message, new Object[]{userName});
    }


    private void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    public void setEvaluateForRobot(boolean needEvaluate) {
        this.evaForRobot = needEvaluate;
    }

    public void setRobotMode(boolean robotMode) {
        this.robotMode = robotMode;
    }

    public interface OnItemHandlerListener {
        boolean onWarningViewClick(int var1, Message var2, View var3);

        void onReadReceiptStateClick(Message var1);
    }

    class ViewHolder {
        AsyncImageView leftIconView;
        AsyncImageView rightIconView;
        TextView nameView;
        ProviderContainerView contentView;
        ProgressBar progressBar;
        ImageView warning;
        ImageView readReceipt;
        ImageView readReceiptRequest;
        TextView readReceiptStatus;
        ViewGroup layout;
        TextView time;
        TextView sentStatus;
        RelativeLayout layoutItem;

        ViewHolder() {
        }
    }
}
