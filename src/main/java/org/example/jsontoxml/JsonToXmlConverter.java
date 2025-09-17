package org.example.jsontoxml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JsonToXmlConverter {

    public static String convert(String jsonString) {
        try {
            // Check if the top level is an object or an array as per the document.
            if (jsonString.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonString);
                StringBuilder xmlBuilder = new StringBuilder("<object>");
                convertJsonObject(jsonObject, xmlBuilder, false);
                xmlBuilder.append("</object>");
                return xmlBuilder.toString();
            } else if (jsonString.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonString);
                StringBuilder xmlBuilder = new StringBuilder("<array>");
                convertJsonArray(jsonArray, xmlBuilder, false);
                xmlBuilder.append("</array>");
                return xmlBuilder.toString();
            } else {
                return "Unsupported JSON format. Only objects and arrays are supported as top-level values.";
            }
        } catch (JSONException e) {
            return "Invalid JSON format: " + e.getMessage();
        }
    }

    private static void convertJsonObject(JSONObject jsonObject, StringBuilder xmlBuilder, boolean isNested) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String tagName = getXmlTagName(value);
            String nameAttribute = " name=\"" + key + "\"";

            if (value instanceof JSONObject) {
                xmlBuilder.append("<").append(tagName).append(nameAttribute).append(">");
                convertJsonObject((JSONObject) value, xmlBuilder, true);
                xmlBuilder.append("</").append(tagName).append(">");
            } else if (value instanceof JSONArray) {
                xmlBuilder.append("<").append(tagName).append(nameAttribute).append(">");
                convertJsonArray((JSONArray) value, xmlBuilder, true);
                xmlBuilder.append("</").append(tagName).append(">");
            } else if (JSONObject.NULL.equals(value)) {
                xmlBuilder.append("<null").append(nameAttribute).append("/>");
            } else {
                xmlBuilder.append("<").append(tagName).append(nameAttribute).append(">");
                xmlBuilder.append(value.toString());
                xmlBuilder.append("</").append(tagName).append(">");
            }
        }
    }

    private static void convertJsonArray(JSONArray jsonArray, StringBuilder xmlBuilder, boolean isNested) {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            String tagName = getXmlTagName(value);

            if (value instanceof JSONObject) {
                xmlBuilder.append("<").append(tagName).append(">");
                convertJsonObject((JSONObject) value, xmlBuilder, true);
                xmlBuilder.append("</").append(tagName).append(">");
            } else if (value instanceof JSONArray) {
                xmlBuilder.append("<").append(tagName).append(">");
                convertJsonArray((JSONArray) value, xmlBuilder, true);
                xmlBuilder.append("</").append(tagName).append(">");
            } else if (JSONObject.NULL.equals(value)) {
                xmlBuilder.append("<null/>");
            } else {
                xmlBuilder.append("<").append(tagName).append(">");
                xmlBuilder.append(value.toString());
                xmlBuilder.append("</").append(tagName).append(">");
            }
        }
    }

    private static String getXmlTagName(Object value) {
        if (value instanceof String) {
            return "string";
        } else if (value instanceof Integer || value instanceof Double || value instanceof Long) {
            return "number";
        } else if (value instanceof Boolean) {
            return "boolean";
        } else if (value instanceof JSONArray) {
            return "array";
        } else if (value instanceof JSONObject) {
            return "object";
        } else if (JSONObject.NULL.equals(value)) {
            return "null";
        } else {
            return "unknown";
        }
    }
}