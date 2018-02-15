/**
 * SearchService.java
 * com.sk.wca.search
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.search;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;

public interface SearchService {

    /**
     * Gets the result UR ls for search query.
     *
     * @param searchQuery
     *            the search query
     * @param noOfResults
     *            the no of results
     * @return the result UR ls for search query
     */
    List<String> getResultURLsForSearchQuery(String searchQuery, int noOfResults);

    /**
     * Gets the html document from url.
     *
     * @param url
     *            the url
     * @return the html document from url
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Document getHtmlDocumentFromUrl(String url) throws IOException;

}
