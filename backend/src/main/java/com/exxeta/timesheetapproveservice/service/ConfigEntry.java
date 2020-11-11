package com.exxeta.timesheetapproveservice.service;

public enum ConfigEntry {
    USE_PROXY("useProxy", "false"),
    HTTP_PROXY_HOST("http.proxyHost", "webproxy01.gisa.dmz"),
    HTTP_PROXY_PORT("http.proxyPort", "8080"),
    HTTPS_PROXY_HOST("https.proxyHost", "webproxy01.gisa.dmz"),
    HTTPS_PROXY_PORT("https.proxyPort", "8080");
    protected String key;
    protected String value;

    ConfigEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }


}
