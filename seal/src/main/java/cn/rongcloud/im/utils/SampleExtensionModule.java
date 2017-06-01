package cn.rongcloud.im.utils;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.im.utils.Aaudiotranslatep.AudioTranslate;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by DELL on 2017/5/2.
 */

public class SampleExtensionModule extends DefaultExtensionModule {

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules = new ArrayList<>();
       // pluginModules.add(new AudioTranslate());
        return pluginModules;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }
}
