package com.zweigbergk.speedswede.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;

public class Translation {
    public static final String TAG = Translation.class.getSimpleName().toUpperCase();

    private static final boolean DISABLED = false;

    private static final String BASE_URL = "https://www.googleapis.com/language/translate/v2?key=";
    private static final String TANSLATE_API_KEY = "AIzaSyCjL04iIPrLYwqCVyCrIvRWwMA60yeMSvE";

    private Client<String> mClient;

    private Translation(String url) {
        if (!DISABLED) {
            new TranslationTask(url).execute();
        }
    }

    private Translation() {

    }

    public static Translation translate(String text, Language from, Language to) {
        try {
        text = URLEncoder.encode(text, "UTF-8");
        String url = Stringify.curlyFormat("{baseUrl}{key}&q={text}&source={source}&target={target}",
                BASE_URL, TANSLATE_API_KEY, text, from.getLanguageCode(), to.getLanguageCode());

        Log.d(TAG, "Formatted URL: " + url);
        return new Translation(url);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "Warning: UnsupportedEncodingException. Stacktrace: ");
            e.printStackTrace();
            return new Translation();
        }
    }

    public void then(Client<String> client) {
        if (!DISABLED) {
            mClient = client;
        } else {
            client.supply("Not translating right now.");
        }
    }

    private class TranslationTask extends AsyncTask<Void, Void, String> {

        private final String mUrl;

        private TranslationTask(String url) {
            mUrl = url;
            Log.d(TAG, "In constructor");
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "Performing a Translation.");
            return fetchJson();
        }

        @Override
        protected void onPostExecute(String translation) {
            if (mClient != null) {
                mClient.supply(translation);
            }
        }

        private String fetchJson() {
            try {
                InputStream inputStream = new URL(mUrl).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                String jsonText = readAll(reader);
                Log.d(TAG, "Before JSONArray: " + jsonText);
                JSONObject jsonObject = new JSONObject(jsonText);
                inputStream.close();

                //Get the translation key & value and extract the value
                String translation = jsonObject.getJSONObject("data")
                        .getJSONArray("translations")
                        .getString(0)
                        .split(":")[1];

                translation = Stringify.removeCurlyBraces(translation);

                Log.d(TAG, "Translation: " + translation);

                return translation;

            } catch (IOException|JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String readAll(Reader reader) throws IOException {
            StringBuilder builder = new StringBuilder();
            int currentChar;
            while ((currentChar = reader.read()) != -1) {
                builder.append((char) currentChar);
            }
            return builder.toString();
        }
    }

    public static class TranslationCache {

        private String translatedText;
        private String locale;

        private TranslationCache() {

        }

        private TranslationCache(String locale, String translatedText) {
            this.translatedText = translatedText;
            this.locale = locale;
        }

        public static TranslationCache cache(String locale, String translatedText) {
            return new TranslationCache(locale, translatedText);
        }

        public String getLocale() {
            return locale;
        }

        public String getTranslatedText() {
            return translatedText;
        }

        @Exclude
        public boolean isFromLocale(Locale locale) {
            return this.locale.equals(locale.getLanguage());
        }
    }
}
