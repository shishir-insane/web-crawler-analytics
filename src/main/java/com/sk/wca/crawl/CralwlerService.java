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
     * Gets the libraries from URL.
     *
     * @param urlString
     *            the url string
     * @return the libraries from URL
     */
    List<String> getLibrariesFromURL(String urlString);

    /**
     * Count libraries.
     *
     * @param javascriptLibrariesUrls
     *            the javascript libraries urls
     * @return the map
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Map<String, Map<String, Integer>> countLibraries(List<String> javascriptLibrariesUrls) throws IOException;

}
