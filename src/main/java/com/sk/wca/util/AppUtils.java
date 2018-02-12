package com.sk.wca.util;

public final class AppUtils {

    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    public static final String GOOGLE_RESULTS_ELEMENT = "h3.r > a";
    public static final String USER_AGENT = "Mozilla/5.0";
    public static final String HREF_ATTR = "href";
    public static final String EMPTY_STR = "";
    public static final String GOOGLE_RESULT_URL_REPLACE_STR = "/url\\?q=";
    public static final String SCRIPT_SRC_PATTERN = "src=\"(.*?.js)\"";
    public static final String SCRIPT_TAG = "<script";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String WWW = "www";
    public static final char COLON = ':';
    public static final String DOUBLE_SLASH = "//";
    public static final char SLASH = '/';
    public static final String REQUEST_GET = "GET";
    public static final String REQUEST_ACCEPT = "Accept";
    public static final String REQUEST_ACCPET_JAVASCRIPT = "application/javascript";
    public static final String SHA_256 = "SHA-256";
    public static final String HTTP_GET = "GET";

    public static double updateProgress(final int completed, final int total, final double lastProgress) {
        final int width = 50;
        final double progress = calculateProgress(completed, total);
        if (progress - lastProgress > 0.1) {
            System.out.print('[');
            int i = 0;
            for (; i <= (int) (progress * width); i++) {
                System.out.print('.');
            }
            for (; i < width; i++) {
                System.out.print(' ');
            }
            System.out.println(']');
            return progress;
        } else {
            return lastProgress;
        }

    }

    public static double calculateProgress(final int completed, final int total) {
        return (double) completed / total;
    }

    private AppUtils() {
        // Hidden constructor
    }

}
