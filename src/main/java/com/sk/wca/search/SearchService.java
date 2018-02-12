/**
 * SearchService.java
 * com.sk.wca.search
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.search;

import java.util.List;

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

}
