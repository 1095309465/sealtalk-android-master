package cn.rongcloud.im.model;

import java.util.List;

/**
 * Created by DELL on 2017/5/26.
 */

public class TranslateGoogleBean {


    /**
     * data : {"translations":[{"translatedText":"好吧","detectedSourceLanguage":"zh-CN"},{"translatedText":"好吧","detectedSourceLanguage":"zh-CN"},{"translatedText":"不太好","detectedSourceLanguage":"zh-CN"},{"translatedText":"不好","detectedSourceLanguage":"zh-CN"},{"translatedText":"你好","detectedSourceLanguage":"zh-CN"},{"translatedText":"年","detectedSourceLanguage":"zh-CN"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<TranslationsBean> translations;

        public List<TranslationsBean> getTranslations() {
            return translations;
        }

        public void setTranslations(List<TranslationsBean> translations) {
            this.translations = translations;
        }

        public static class TranslationsBean {
            /**
             * translatedText : 好吧
             * detectedSourceLanguage : zh-CN
             */

            private String translatedText;
            private String detectedSourceLanguage;

            public String getTranslatedText() {
                return translatedText;
            }

            public void setTranslatedText(String translatedText) {
                this.translatedText = translatedText;
            }

            public String getDetectedSourceLanguage() {
                return detectedSourceLanguage;
            }

            public void setDetectedSourceLanguage(String detectedSourceLanguage) {
                this.detectedSourceLanguage = detectedSourceLanguage;
            }
        }
    }
}
