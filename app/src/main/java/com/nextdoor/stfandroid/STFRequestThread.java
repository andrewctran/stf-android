package com.nextdoor.stfandroid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class STFRequestThread extends Thread {
    List<STFItem> stfQueue;

    public STFRequestThread(List<STFItem> stfQueue) {
        this.stfQueue = stfQueue;
    }

    @Override
    public void run() {
        android.os.Debug.waitForDebugger();
        while (true) {
            while (!stfQueue.isEmpty()) {
                STFItem stfItem = stfQueue.get(0);
                JSONObject requestJson = STFJira.getRequest(stfItem.getSummary(), stfItem.getEmailAddr(), stfItem.getBase64Screenshot());
                HttpPost post = new HttpPost(STFConfig.API_SERVER);
                try {
                    StringEntity jsonString = new StringEntity(requestJson.toString());
                    jsonString.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(jsonString);
                    post.setHeader("Content-Type", "application/json");
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(post);
                    Log.d("HTTP", response.getStatusLine().getStatusCode() + "");
                    if (response.getStatusLine().getStatusCode() == 200) {
                        stfQueue.remove(0);
                    }
                } catch (UnsupportedEncodingException e) {
                } catch (IOException e) {
                }
            }
            try {
                this.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }
}
