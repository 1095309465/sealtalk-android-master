package cn.rongcloud.im.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Arrays;

import cn.rongcloud.im.App;
import cn.rongcloud.im.R;
import cn.rongcloud.im.SealConst;
import cn.rongcloud.im.SealUserInfoManager;
import cn.rongcloud.im.server.SealAction;
import cn.rongcloud.im.server.broadcast.BroadcastManager;
import cn.rongcloud.im.server.network.async.AsyncTaskManager;
import cn.rongcloud.im.server.network.async.OnDataListener;
import cn.rongcloud.im.server.network.http.HttpException;
import cn.rongcloud.im.server.response.VersionResponse;
import cn.rongcloud.im.server.widget.SelectableRoundedImageView;
import cn.rongcloud.im.ui.activity.AboutRongCloudActivity;
import cn.rongcloud.im.ui.activity.AccountSettingActivity;
import cn.rongcloud.im.ui.activity.MyAccountActivity;
import cn.rongcloud.im.ui.widget.WheelView;
import cn.rongcloud.im.ui.widget.switchbutton.SwitchButton;
import cn.rongcloud.im.utils.SPUtils;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/21.
 * Company RongCloud
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static final int COMPARE_VERSION = 54;
    public static final String SHOW_RED = "SHOW_RED";
    private SharedPreferences sp;
    private SelectableRoundedImageView imageView;
    private TextView mName;
    private ImageView mNewVersionView;
    private boolean isHasNewVersion;
    private String url;
    private boolean isDebug;
    private String[] languages = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.seal_mine_fragment, container, false);
        isDebug = getContext().getSharedPreferences("config", getContext().MODE_PRIVATE).getBoolean("isDebug", false);
        initViews(mView);
        initData();
        BroadcastManager.getInstance(getActivity()).addAction(SealConst.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserInfo();
            }
        });
        compareVersion();
        return mView;
    }

    private void compareVersion() {
        AsyncTaskManager.getInstance(getActivity()).request(COMPARE_VERSION, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getSealTalkVersion();
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    VersionResponse response = (VersionResponse) result;
                    String[] s = response.getAndroid().getVersion().split("\\.");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < s.length; i++) {
                        sb.append(s[i]);
                    }

                    String[] s2 = SealConst.SEALTALKVERSION.split("\\.");
                    StringBuilder sb2 = new StringBuilder();
                    for (int i = 0; i < s2.length; i++) {
                        sb2.append(s2[i]);
                    }
                    if (Integer.parseInt(sb.toString()) > Integer.parseInt(sb2.toString())) {
                        mNewVersionView.setVisibility(View.VISIBLE);
                        url = response.getAndroid().getUrl();
                        isHasNewVersion = true;
                        BroadcastManager.getInstance(getActivity()).sendBroadcast(SHOW_RED);
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }

    private void initData() {
        languages = getResources().getStringArray(R.array.languages);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        updateUserInfo();
    }

    private void initViews(View mView) {
        mNewVersionView = (ImageView) mView.findViewById(R.id.new_version_icon);
        imageView = (SelectableRoundedImageView) mView.findViewById(R.id.mine_header);
        mName = (TextView) mView.findViewById(R.id.mine_name);
        LinearLayout mUserProfile = (LinearLayout) mView.findViewById(R.id.start_user_profile);
        LinearLayout mMineSetting = (LinearLayout) mView.findViewById(R.id.mine_setting);
        LinearLayout mMineService = (LinearLayout) mView.findViewById(R.id.mine_service);
        LinearLayout mMineXN = (LinearLayout) mView.findViewById(R.id.mine_xiaoneng);
        LinearLayout set_lanaug = (LinearLayout) mView.findViewById(R.id.set_lanaug);
        LinearLayout mMineAbout = (LinearLayout) mView.findViewById(R.id.mine_about);
        boolean flag=SPUtils.findBoolean("showModel");
        SwitchButton toggleButton= (SwitchButton) mView.findViewById(R.id.btn_tog);
        toggleButton.setChecked(flag);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.updateOrSave("showModel", isChecked);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.show_module, null);
                ImageView iv = (ImageView) view.findViewById(R.id.iv);
                iv.setImageResource(isChecked == true ? R.drawable.model_0 : R.drawable.model_1);
                new AlertDialog.Builder(getActivity())
                        .setTitle(isChecked == true ? "会话中同时显示原文和译文！" : "会话中只显示译文!")
                        .setPositiveButton("确认", null)
                        .setView(view)
                        .show();


            }
        });
        if (isDebug) {
            mMineXN.setVisibility(View.VISIBLE);
        } else {
            mMineXN.setVisibility(View.GONE);
        }
        set_lanaug.setOnClickListener(this);
        mUserProfile.setOnClickListener(this);
        mMineSetting.setOnClickListener(this);
        mMineService.setOnClickListener(this);
        mMineAbout.setOnClickListener(this);
        mMineXN.setOnClickListener(this);
        mView.findViewById(R.id.my_wallet).setOnClickListener(this);
    }

    private int selectIndex = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.set_lanaug://语言选择

                String yuyan = SPUtils.find("language");
                Log.e("123", "yuyan=" + yuyan);
                int index = 0;
                if ("zh".equals(yuyan)) {
                    index = 0;
                } else if ("en".equals(yuyan)) {
                    index = 1;
                } else if ("jp".equals(yuyan)) {
                    index = 2;
                } else if ("fra".equals(yuyan)) {
                    index = 3;
                } else if ("kor".equals(yuyan)) {
                    index = 4;
                } else if ("spa".equals(yuyan)) {
                    index = 5;
                } else if ("it".equals(yuyan)) {
                    index = 6;
                } else if ("ru".equals(yuyan)) {
                    index = 7;
                }
                View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.wheel_view, null);
                WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(Arrays.asList(languages));
                wv.setSeletion(index);//2:日文 1，英语,0 中文
                final int finalIndex = index;
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("请选择您接收的语言类型")
                        .setView(outerView)
                        .setPositiveButton("确认", null).create();
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        if (selectIndex == selectedIndex) {
                            dialog.dismiss();
                            return;
                        }
                        selectIndex = selectedIndex;
                        String langue = null;
                        switch (selectedIndex) {
                            case 2://中文
                                langue = "zh";

                                break;
                            case 3://英文
                                langue = "en";
                                break;

                            case 4://日文
                                langue = "jp";
                                break;
                            case 5://法语
                                langue = "fra";

                                break;
                            case 6://韩语
                                langue = "kor";
                                break;

                            case 7://西班牙语
                                langue = "spa";
                                break;
                            case 8://意大利语
                                langue = "it";

                                break;
                            case 9://俄语
                                langue = "ru";
                                break;

                        }
                        SPUtils.updateOrSave("language", langue);
                        if (finalIndex == selectedIndex) {
                            dialog.dismiss();

                        }
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        App.content.clear();
                    }
                });
                dialog.show();

                break;


            case R.id.start_user_profile:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;
            case R.id.mine_setting:
                startActivity(new Intent(getActivity(), AccountSettingActivity.class));
                break;
            case R.id.mine_service:
                CSCustomServiceInfo.Builder builder = new CSCustomServiceInfo.Builder();
                builder.province("北京");
                builder.city("北京");
                RongIM.getInstance().startCustomerServiceChat(getActivity(), "KEFU146001495753714", "在线客服", builder.build());
                // KEFU146001495753714 正式  KEFU145930951497220 测试  小能: zf_1000_1481459114694   zf_1000_1480591492399
                break;
            case R.id.mine_xiaoneng:
                CSCustomServiceInfo.Builder builder1 = new CSCustomServiceInfo.Builder();
                builder1.province("北京");
                builder1.city("北京");
                RongIM.getInstance().startCustomerServiceChat(getActivity(), "zf_1000_1481459114694", "在线客服", builder1.build());
                break;
            case R.id.mine_about:
                mNewVersionView.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), AboutRongCloudActivity.class);
                intent.putExtra("isHasNewVersion", isHasNewVersion);
                if (!TextUtils.isEmpty(url)) {
                    intent.putExtra("url", url);
                }
                startActivity(intent);
                break;
            case R.id.my_wallet:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUserInfo() {
        String userId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "");
        String username = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
        String userPortrait = sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "");
        mName.setText(username);
        if (!TextUtils.isEmpty(userId)) {
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri
                    (new UserInfo(userId, username, Uri.parse(userPortrait)));
            ImageLoader.getInstance().displayImage(portraitUri, imageView, App.getOptions());
        }
    }
}
