package cn.rongcloud.im.model;

import android.view.View;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class TranslateBean {
    private IContainerItemProvider finalProvider;
    private View  view;
    private int position;
    private UIMessage data;

    public TranslateBean(IContainerItemProvider finalProvider, View view, int position, UIMessage data) {
        this.finalProvider = finalProvider;
        this.view = view;
        this.position = position;
        this.data = data;
    }

    @Override
    public String toString() {
        return "TranslateBean{" +
                "finalProvider=" + finalProvider +
                ", view=" + view +
                ", position=" + position +
                ", data=" + data +
                '}';
    }

    public IContainerItemProvider getFinalProvider() {
        return finalProvider;
    }

    public void setFinalProvider(IContainerItemProvider finalProvider) {
        this.finalProvider = finalProvider;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public UIMessage getData() {
        return data;
    }

    public void setData(UIMessage data) {
        this.data = data;
    }
}
