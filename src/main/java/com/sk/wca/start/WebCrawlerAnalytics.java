/*
 * WebCrawlerAnalytics.java
 * com.sk.wca.start
 * web-crawler-analytics
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.start;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.sk.wca.crawl.CralwlerService;
import com.sk.wca.crawl.impl.JavaScriptCrawlerService;
import com.sk.wca.search.SearchService;
import com.sk.wca.search.impl.GoogleSearchService.GoogleSearchServiceFactory;
import com.sk.wca.util.AppUtils;
import com.sk.wca.util.Progress;

public class WebCrawlerAnalytics {
    private static final int TOP_X_NUMBER = 5;
    private static final int TOP_SEARCH_RESULTS = 30;

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        final Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the search term: ");
        final String query = scanner.nextLine();
        final WebCrawlerAnalytics webCrawlerAnalytics = new WebCrawlerAnalytics();
        int topX = TOP_X_NUMBER;
        try {
            final List<Map.Entry<String, Integer>> results = webCrawlerAnalytics.getAnalyticsResults(query,
                    TOP_SEARCH_RESULTS);
            if (null != results && !results.isEmpty()) {
                if (results.size() < topX) {
                    topX = results.size();
                }
                System.out.println();
                System.out.println("------------------------------------------------------------------------------");
                System.out.println("Top " + topX + " out of total " + results.size() + " Javascript Libraries used:");
                System.out.println("------------------------------------------------------------------------------");
                System.out.printf("%-5s%-60s%-16s", "#", "LIBRARY", "COUNT");
                System.out.println();
                System.out.println("------------------------------------------------------------------------------");
                for (int i = 0; i < topX; i++) {
                    System.out.format("%-5s%-60s%-16s", i + 1, results.get(i).getKey(), results.get(i).getValue());
                    System.out.println();
                }
                System.out.println("------------------------------------------------------------------------------");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the analytics results.
     *
     * @param query
     *            the query
     * @param noOfResults
     *            the no of results
     * @return the analytics results
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<Map.Entry<String, Integer>> getAnalyticsResults(final String query, final int noOfResults)
            throws IOException {
        final List<Map.Entry<String, Integer>> finalResults = new ArrayList<>();
        Map<String, Map<String, Integer>> countLibMap;
        final List<String> allLibraryUrls = new ArrayList<>();
        final SearchService searchService = GoogleSearchServiceFactory.INSTANCE.getInstance();
        final CralwlerService cralwlerService = new JavaScriptCrawlerService();
        final List<String> resultUrls = searchService.getResultURLsForSearchQuery(query, noOfResults);
        List<String> libUrls;
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Processing search result URLs...");
        final Progress searchProgress = new Progress(resultUrls.size());
        for (final String url : resultUrls) {
            searchProgress.setCompleted(searchProgress.getCompleted() + 1);
            searchProgress.setProgressMade(AppUtils.updateProgress(searchProgress.getCompleted(),
                    searchProgress.getTotal(), searchProgress.getProgressMade()));
            libUrls = cralwlerService.getIncludedLibsFromUrl(url);
            if (null != libUrls && !libUrls.isEmpty()) {
                allLibraryUrls.addAll(libUrls);
            }
        }
        countLibMap = cralwlerService.groupByAndCountLibraries(allLibraryUrls);

        for (final Entry<String, Map<String, Integer>> entry : countLibMap.entrySet()) {
            for (final Entry<String, Integer> entry2 : countLibMap.get(entry.getKey()).entrySet()) {
                finalResults.add(new AbstractMap.SimpleEntry<>(entry2.getKey(), entry2.getValue()));
            }
        }

        Collections.sort(finalResults, (o1, o2) -> o2.getValue() - o1.getValue());
        return finalResults;
    }

}
