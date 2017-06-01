package cn.rongcloud.im.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import cn.rongcloud.im.App;
import io.rong.imkit.widget.AutoLinkTextView;

/**
 * Created by DELL on 2017/5/27.
 */

public class TextTranslateTask extends AsyncTask<String, Void, String> {
    private AutoLinkTextView textView;
    private int position_;
    private OnTranslateListener onTranslateListener;
    private View viewGroup;
    private String src;
    public TextTranslateTask(View viewGroup, AutoLinkTextView textView, int position_, OnTranslateListener onTranslateListener) {
        this.textView = textView;
        this.position_ = position_;
        this.onTranslateListener = onTranslateListener;
        this.viewGroup = viewGroup;
    }

    @Override
    protected String doInBackground(String... params) {
        src=params[0];
        String translateVar = TranslateUtil.baidufanyi(params[0]);
        Log.e("123", "翻译原文=  " + params[0] + "   译文=  " + translateVar);
        App.content.put(params[0], translateVar);
        return translateVar;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (onTranslateListener != null && !TextUtils.isEmpty(s) && textView != null) {
            onTranslateListener.setData(viewGroup, textView, position_, s,src);
        }

    }

    public interface OnTranslateListener {
        void setData(View viewGroup, AutoLinkTextView tv, int position, String translateVar, String src);
    }
}
