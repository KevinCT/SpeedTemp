package com.zweigbergk.speedswede.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Translation {
    public static final String TAG = Translation.class.getSimpleName().toUpperCase();

    private final ProductBuilder<String> mBuilder;

    public Translation(String url) {
        mBuilder = new ProductBuilder<>(translationBlueprint);
        mBuilder.attachLocks(ProductLock.TRANSLATION);
        new JsonReadTask(url, mBuilder);
    }

    public static Translation

    public void then(Client<String> client) {
        mBuilder.addClient(client);
    }

    private static final ProductBuilder.Blueprint<String> translationBlueprint = items ->
            items.getString(ProductLock.TRANSLATION);

    private static class JsonReadTask extends AsyncTask<Void, Void, JSONArray> {

        private final String mUrl;
        private final ProductBuilder<String> mBuilder;

        private JsonReadTask(String url, ProductBuilder<String> builder) {
            mUrl = url;
            mBuilder = builder;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            Log.d(TAG, "doInBackground!");
            return fetchJson(mUrl);
        }

        protected void onPostExecute(JSONArray object) {
            System.out.println("We got this stuff: " + object);
            try {
                JSONObject jsonObject = new JSONObject(mUrl);

                //Get the translation string
                String translation = jsonObject.getJSONObject("data").getJSONArray("translations").getString(0);

                mBuilder.addItem(ProductLock.TRANSLATION, translation);
            } catch (JSONException e) {
                Log.d(TAG, "RIP.");
            }
        }

        private JSONArray fetchJson(String url) {
            try {
                InputStream inputStream = new URL(url).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                String jsonText = readAll(reader);
                Log.d(TAG, "Before JSONArray: " + jsonText);
                JSONArray json = new JSONArray(jsonText);
                inputStream.close();
                return json;
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


}
