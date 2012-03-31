
package com.eee168.appmonitor.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class JSONUtils {
    public static String getString(String jsonData, String key) {
        String value = null;
        try {
            JSONObject json = new JSONObject(jsonData);
            value = json.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getJson(Map<String, String> params) {
        String jsonString = null;
        try {
            if (params != null && params.size() > 0) {
                JSONObject json = new JSONObject();
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    json.put(key, value);
                }
                jsonString = json.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;

    }

}
