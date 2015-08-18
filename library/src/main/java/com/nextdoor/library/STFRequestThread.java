package com.nextdoor.library;

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
    private List<STFItem> stfQueue;
    private boolean isRunning;

    public STFRequestThread(List<STFItem> stfQueue) {
        this.stfQueue = stfQueue;
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            while (!stfQueue.isEmpty()) {
                STFItem stfItem = stfQueue.get(0);
                JSONObject requestJson = STFJira.getRequest(stfItem.getSummary(), stfItem.getEmailAddr(), stfItem.getBase64Screenshot());
                HttpPost post = new HttpPost(STFConfig.getApiServer());
                try {
                    StringEntity jsonString = new StringEntity(requestJson.toString());
                    jsonString.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(jsonString);
                    post.setHeader("Content-Type", "application/json");
                    post.addHeader("x-api-get", STFConfig.getApiKey());
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(post);
                    Log.d("HTTP", response.getStatusLine().getStatusCode() + "");
                    if (response.getStatusLine().getStatusCode() == 200) {
                        STFManager.dequeue();
                        STFManager.updateQueue();
                        STFManager.persist();
                    }
                } catch (UnsupportedEncodingException e) {
                } catch (IOException e) {
                }
            }

        }
    }

    public void requestStop() {
        isRunning = false;
    }

    public void setQueue(List<STFItem> queue) {
        this.stfQueue = queue;
    }
}
