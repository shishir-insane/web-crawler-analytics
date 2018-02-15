/*
 * CralwlerService.java
 * com.sk.wca.crawl
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.crawl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CralwlerService {

    /**
     * Gets the included libs from url.
     *
     * @param urlString
     *            the url string
     * @return the included libs from url
     */
    List<String> getIncludedLibsFromUrl(String urlString);

    /**
     * Group by and count libraries.
     *
     * @param javascriptLibrariesUrls
     *            the javascript libraries urls
     * @return the map
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Map<String, Map<String, Integer>> groupByAndCountLibraries(List<String> javascriptLibrariesUrls) throws IOException;

}
