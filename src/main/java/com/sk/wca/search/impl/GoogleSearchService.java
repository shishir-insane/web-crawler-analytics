/*
 * GoogleSearchService.java
 * com.sk.wca.search.impl
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.wca.search.SearchService;
import com.sk.wca.util.AppUtils;

public class GoogleSearchService implements SearchService {

    private static final GoogleSearchService INSTANCE = new GoogleSearchService();

    /**
     * Instantiates a new google search service.
     */
    private GoogleSearchService() {
        // Hidden constructor
    }

    public enum GoogleSearchServiceFactory {
        INSTANCE;

        /**
         * Gets the single instance of GoogleSearchServiceFactory.
         *
         * @return single instance of GoogleSearchServiceFactory
         */
        public SearchService getInstance() {
            return new GoogleSearchService();
        }
    }

    /**
     * Gets the single instance of GoogleSearchService.
     *
     * @return single instance of GoogleSearchService
     */
    public final static synchronized SearchService getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sk.wca.search.SearchService#getResultURLsForSearchQuery(java.lang.
     * String, int)
     */
    @Override
    public final List<String> getResultURLsForSearchQuery(final String searchQuery, final int noOfResults) {
        List<String> resultUrls = null;
        try {
            final Document doc = getHtmlDocumentFromUrl(generateSearchUrl(searchQuery, noOfResults));
            final Elements results = doc.select(AppUtils.GOOGLE_RESULTS_ELEMENT);
            if (null != results) {
                resultUrls = new ArrayList<>();
                for (final Element result : results) {
                    final String linkHref = result.attr(AppUtils.HREF_ATTR);
                    if (!linkHref.isEmpty() && !linkHref.startsWith("/search")) {
                        resultUrls.add(linkHref.replaceAll(AppUtils.GOOGLE_RESULT_URL_REPLACE_STR, AppUtils.EMPTY_STR));
                    }
                }
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
        return resultUrls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sk.wca.search.SearchService#getHtmlDocumentFromUrl(java.lang.String)
     */
    @Override
    public final Document getHtmlDocumentFromUrl(final String url) throws IOException {
        return Jsoup.connect(url).userAgent(AppUtils.USER_AGENT).get();
    }

    /**
     * Generate search url.
     *
     * @param searchQuery
     *            the search query
     * @param noOfResults
     *            the no of results
     * @return the string
     */
    private String generateSearchUrl(final String searchQuery, final int noOfResults) {
        return new StringBuffer(AppUtils.GOOGLE_SEARCH_URL).append("?q=").append(searchQuery).append("&num=")
                .append(noOfResults).toString();
    }
}
