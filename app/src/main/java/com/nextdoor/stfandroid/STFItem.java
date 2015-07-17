package com.nextdoor.stfandroid;

import org.json.JSONObject;

import android.graphics.Bitmap;

import java.io.Serializable;

public class STFItem implements Serializable {
    public String getBase64Screenshot() {
        return base64Screenshot;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    private String base64Screenshot;
    private String summary;
    private String description;
    private String emailAddr;

    public STFItem(String base64Screenshot, String summary, String description, String emailAddr) {
        this.base64Screenshot = base64Screenshot;
        this.summary = summary;
        this.description = description;
        this.emailAddr = emailAddr;
    }


}
