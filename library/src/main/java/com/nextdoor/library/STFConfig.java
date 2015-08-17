package com.nextdoor.library;

public class STFConfig {
    private static String API_SERVER = "http://stf-web-02.herokuapp.com/bug/";
    private static String DEFAULT_EMAIL = "";
    private static String JIRA_PROJECT = "STF1";
    private static String APP_COLOR = "#1E9E5E";

    public static String getApiServer() {
        return API_SERVER;
    }

    public static String getDefaultEmail() {
        return DEFAULT_EMAIL;
    }

    public static String getJiraProject() {
        return JIRA_PROJECT;
    }

    public static String getAppColor() {
        return APP_COLOR;
    }

    public static void setApiServer(String addr) {
        API_SERVER = addr;
    }

    public static void setDefaultEmail(String email) {
        DEFAULT_EMAIL = email;
    }

    public static void setJiraProject(String projectName) {
        JIRA_PROJECT = projectName;
    }

    public static void setAppColor(String hexColor) {
        APP_COLOR = hexColor;
    }
}
