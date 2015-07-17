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
    private static final String JIRA_REPORTER = "reporter";
    private static final String JIRA_ISSUE = "issue";
    private static final String TYPE = "Bug";

    public static JSONObject getRequest(String email, String summary, String encodedImage) {
        JSONObject requestJson = new JSONObject();
        JSONObject issueJson = new JSONObject();
        JSONObject projectJson = new JSONObject();
        JSONObject issueTypeJson = new JSONObject();
        try {
            projectJson.put(JIRA_PROJECT_KEY, STFConfig.JIRA_PROJECT);
            issueTypeJson.put(JIRA_ISSUETYPE_NAME, TYPE);
            issueJson.put(JIRA_PROJECT, projectJson);
            issueJson.put(JIRA_SUMMARY, summary);
            issueJson.put(JIRA_DESCRIPTION, "");
            issueJson.put(JIRA_ISSUETYPE, issueTypeJson);
            requestJson.put(JIRA_ISSUE, issueJson);
            requestJson.put(JIRA_REPORTER, email);
            requestJson.put(JIRA_ATTACHMENT, encodedImage);
        } catch (JSONException e) {
        }
        return requestJson;
    }
}
