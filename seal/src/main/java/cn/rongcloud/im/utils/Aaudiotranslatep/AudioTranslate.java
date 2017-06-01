package cn.rongcloud.im.utils.Aaudiotranslatep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import cn.rongcloud.im.R;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.utilities.PermissionCheckUtil;

/**
 * Created by DELL on 2017/5/24.
 */

public class AudioTranslate implements IPluginModule {
    private Context context;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.selector_translate);
    }

    @Override
    public String obtainTitle(Context context) {
        return "语音翻译";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        if (!PermissionCheckUtil.requestPermissions(fragment, permissions)) {
            return;
        }
        context = fragment.getActivity().getApplicationContext();
        Toast.makeText(context, "开始语音翻译", Toast.LENGTH_SHORT).show();

        startVoiceRecognitionActivity();
        View view= LayoutInflater.from(context).inflate(R.layout.rc_view_recognizer,null);
        rongExtension.addPluginPager(view);

    }

    private void startVoiceRecognitionActivity() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请对着麦克风说话！");
            ((Activity) context).startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(context, "找不到语音助手,请先安装谷歌语音助手", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        if (i == VOICE_RECOGNITION_REQUEST_CODE
                && i1 == Activity.RESULT_OK) {
            ArrayList<String> matchResults = intent
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String voice_str = " ";
            // for (int i = 0; i < matchresults.size(); i++) {
            // voice_str += matchresults.get(i).toString();
            // }//数组中是匹配到的字符串数组
            voice_str = matchResults.get(0).toString();// 只要最相似的就行，去第一个，
            Log.e("123","语音识别");
        }

    }
}
