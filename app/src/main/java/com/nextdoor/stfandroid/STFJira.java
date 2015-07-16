package com.nextdoor.stfandroid;

import org.json.JSONException;
import org.json.JSONObject;

public class STFJira {
    private static final String JIRA_PROJECT = "project";
    private static final String JIRA_PROJECT_KEY = "key";
    private static final String JIRA_SUMMARY = "summary";
    private static final String JIRA_DESCRIPTION = "description";
    private static final String JIRA_ISSUETYPE = "issuetype";
    private static final String JIRA_ISSUETYPE_NAME = "name";
    private static final String JIRA_ATTACHMENT = "image";
    private static final String TYPE = "Bug";

    public static JSONObject getRequest(String email, String summary, String encodedImage) {
        JSONObject requestJson = new JSONObject();
        JSONObject projectJson = new JSONObject();
        JSONObject issueTypeJson = new JSONObject();
        try {
            projectJson.put(JIRA_PROJECT_KEY, STFConfig.JIRA_PROJECT);
            issueTypeJson.put(JIRA_ISSUETYPE_NAME, TYPE);
            requestJson.put(JIRA_PROJECT, projectJson);
            requestJson.put(JIRA_SUMMARY, summary);
            requestJson.put(JIRA_DESCRIPTION, email);
            requestJson.put(JIRA_ISSUETYPE, issueTypeJson);
            requestJson.put(JIRA_ATTACHMENT, encodedImage);
        } catch (JSONException e) {
        }
        return requestJson;
    }
}
