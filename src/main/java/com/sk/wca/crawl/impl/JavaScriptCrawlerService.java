/*
 * JavaScriptCrawlerService.java
 * com.sk.wca.crawl.impl
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.crawl.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.sk.wca.crawl.CralwlerService;
import com.sk.wca.util.AppUtils;
import com.sk.wca.util.Progress;

public class JavaScriptCrawlerService implements CralwlerService {

    private static final Pattern PATTERN = Pattern.compile(AppUtils.SCRIPT_SRC_PATTERN);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sk.wca.crawl.CralwlerService#getIncludedLibsFromUrl(java.lang.String)
     */
    @Override
    public final List<String> getIncludedLibsFromUrl(final String urlString) {
        if (urlString.isEmpty()) {
            return null;
        }
        final List<String> jsLibs = new ArrayList<>();
        final Map<String, Integer> traversedUrls = new HashMap<>();
        URL url = null;
        HttpURLConnection connection = null;
        BufferedReader br = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15 * 1000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return jsLibs;
            }
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(AppUtils.SCRIPT_TAG)) {
                    final Matcher match = PATTERN.matcher(line);
                    if (match.find()) {
                        final String matchedGroup = match.group(1);
                        if (traversedUrls.containsKey(matchedGroup)) {
                            continue;
                        }
                        traversedUrls.put(matchedGroup, 1);

                        if (matchedGroup.startsWith(AppUtils.DOUBLE_SLASH)) {
                            jsLibs.add(new StringBuffer(url.getProtocol()).append(AppUtils.COLON).append(matchedGroup)
                                    .toString());
                            continue;
                        } else if (!matchedGroup.startsWith(AppUtils.WWW) && !matchedGroup.startsWith(AppUtils.HTTP)
                                && !matchedGroup.startsWith(AppUtils.HTTPS)) {
                            jsLibs.add(new StringBuffer(url.getProtocol()).append(AppUtils.COLON)
                                    .append(AppUtils.DOUBLE_SLASH).append(url.getHost()).append(AppUtils.SLASH)
                                    .append(matchedGroup).toString());
                        } else {
                            jsLibs.add(matchedGroup);
                        }
                    }
                }

            }
        } catch (final IOException e) {
            return null;
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
            if (null != br) {
                try {
                    br.close();
                } catch (final IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return jsLibs;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sk.wca.crawl.CralwlerService#groupByAndCountLibraries(java.util.List)
     */
    @Override
    public final Map<String, Map<String, Integer>> groupByAndCountLibraries(final List<String> javascriptLibrariesUrls) {
        final Map<String, Map<String, Integer>> entries = new HashMap<>();
        final Progress searchProgress = new Progress(javascriptLibrariesUrls.size());
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Processing & grouping libraries included in URLs from search results...");
        javascriptLibrariesUrls.parallelStream().forEach(js -> {
            searchProgress.setCompleted(searchProgress.getCompleted() + 1);
            searchProgress.setProgressMade(
                    AppUtils.updateProgress(searchProgress.getCompleted(), searchProgress.getTotal(),
                            searchProgress.getProgressMade()));
            if (null != js && !js.isEmpty()) {
                try {
                    final URL url = new URL(js);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(15 * 1000);
                    connection.setRequestMethod(AppUtils.REQUEST_GET);
                    connection.setRequestProperty(AppUtils.REQUEST_ACCEPT, AppUtils.REQUEST_ACCPET_JAVASCRIPT);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return;
                    }
                    final byte[] content = readContentFromUrlInputStream(connection.getInputStream());
                    final String checksum = calculateCheckSumForByteArray(content);
                    if (!entries.containsKey(checksum)) {
                        final Map<String, Integer> m = new HashMap<>();
                        m.put(getFileNameFromURLPath(url.getPath()), 1);
                        entries.put(checksum, m);
                    }
                    connection.disconnect();
                } catch (final Exception e) {
                    return;
                }
            }
        });

        return entries;
    }

    /**
     * Read url content.
     *
     * @param is
     *            the is
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private final byte[] readContentFromUrlInputStream(final InputStream is) throws IOException {
        final byte[] chunk = new byte[4096];
        int length;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((length = is.read(chunk)) != -1) {
            out.write(chunk, 0, length);
        }
        final byte[] content = out.toByteArray();
        out.close();
        return content;
    }

    /**
     * Gets the file name from URL path.
     *
     * @param path
     *            the path
     * @return the file name from URL path
     */
    private final String getFileNameFromURLPath(final String path) {
        return path.substring(path.lastIndexOf(AppUtils.SLASH) + 1, path.length());
    }

    /**
     * Calculate check sum.
     *
     * @param content
     *            the content
     * @return the string
     */
    private final String calculateCheckSumForByteArray(final byte[] content) {
        String data = null;
        try {
            final MessageDigest md = MessageDigest.getInstance(AppUtils.SHA_256);
            md.update(content);
            final byte[] checksum = md.digest();
            data = DatatypeConverter.printHexBinary(checksum);
        } catch (final NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return data;
    }
}
