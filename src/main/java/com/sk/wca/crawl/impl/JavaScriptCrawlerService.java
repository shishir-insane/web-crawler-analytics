package com.sk.wca.crawl.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class JavaScriptCrawlerService implements CralwlerService {

    Pattern p = Pattern.compile(AppUtils.SCRIPT_SRC_PATTERN);

    @Override
    public final List<String> getLibrariesFromURL(final String urlString) {
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
                // System.err.println(connection.getResponseCode() + " returned
                // by " + urlString);
                return jsLibs;
            }
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(AppUtils.SCRIPT_TAG)) {
                    final Matcher match = p.matcher(line);
                    if (match.find()) {
                        final String matchedGroup = match.group(1);
                        if (traversedUrls.containsKey(matchedGroup)) {
                            continue;
                        }
                        traversedUrls.put(matchedGroup, 1);

                        if (matchedGroup.startsWith(AppUtils.DOUBLE_SLASH)) {
                            jsLibs.add(new StringBuffer(url.getProtocol()).append(AppUtils.COLON)
                                    .append(matchedGroup).toString());
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
        } catch (final Exception e) {
            System.err.println(e.getMessage() + " - " + urlString);
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

    @Override
    public final Map<String, Map<String, Integer>> countLibraries(final List<String> javascriptLibrariesUrls) {
        final Map<String, Map<String, Integer>> entries = new HashMap<>();
        int i = 1;
        double searchProgress = 0.0;
        System.out.println("Processing & grouping libraries included in URLS from search results");
        for (final String jsurl : javascriptLibrariesUrls) {
            searchProgress = AppUtils.updateProgress(i++, javascriptLibrariesUrls.size(), searchProgress);
            if (null != jsurl && !jsurl.isEmpty()) {
                try {
                    final URL url = new URL(jsurl);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(15 * 1000);
                    connection.setRequestMethod(AppUtils.REQUEST_GET);
                    connection.setRequestProperty(AppUtils.REQUEST_ACCEPT, AppUtils.REQUEST_ACCPET_JAVASCRIPT);
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        continue;
                    }
                    final byte[] content = readUrlContent(connection.getInputStream());
                    final String checksum = calculateCheckSum(content);
                    if (!entries.containsKey(checksum)) {
                        final Map<String, Integer> m = new HashMap<>();
                        m.put(getFileNameFromURLPath(url.getPath()), 1);
                        entries.put(checksum, m);
                    }
                    connection.disconnect();
                } catch (final Exception e) {
                    continue;
                }

            }
        }

        return entries;
    }

    private final byte[] readUrlContent(final InputStream is) throws IOException {
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

    private final String getFileNameFromURLPath(final String path) {
        return path.substring(path.lastIndexOf(AppUtils.SLASH) + 1, path.length());
    }

    private final String calculateCheckSum(final byte[] content) {
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
