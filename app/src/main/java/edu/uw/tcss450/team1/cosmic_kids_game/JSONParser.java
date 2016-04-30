package edu.uw.tcss450.team1.cosmic_kids_game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.List;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

/**
 * Created by Brandon on 4/28/2016.
 */
public class JSONParser {

    static InputStream is = null;
    static JSONObject jsonObject = null;
    static String jsonData = "";


    //empty constructor
    public JSONParser() {}

    public JSONObject getJSONFromUrl(String url) {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //TODO: should it be "HttpPost" rather than "HttpGet"?
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Store retrieved content into StringBuilder-->String
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line =reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonData = sb.toString();
        } catch (Exception e) {
            Log.e("BufferReader Error", "Error converting the json data " + e.toString());
        }

        //Attempt parsing the String jsonData into a JSON Object
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e("JSONParser", "Error parsing the data " + e.toString());
        }

        return jsonObject;
    }


    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if(method == "POST") {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                //TODO: HttpPost or HttpGet?
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else if(method == "GET") {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonData = sb.toString();
        } catch (Exception e) {
            Log.e("BufferReader Error", "Error converting json data " + e.toString());
        }

        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e("JSON Parser Error", "Error parsing the data " + e.toString());
        }

        return jsonObject;
    }
}
