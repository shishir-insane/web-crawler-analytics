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

    private GoogleSearchService() {
        // Hidden constructor
    }

    public enum GoogleSearchServiceFactory {
        INSTANCE;

        public SearchService getInstance() {
            return new GoogleSearchService();
        }
    }

    public final static synchronized SearchService getInstance() {
        return INSTANCE;
    }

    @Override
    public final List<String> getResultURLsForSearchQuery(final String searchQuery, final int noOfResults) {
        List<String> resultUrls = null;
        try {
            final Document doc = Jsoup.connect(generateSearchUrl(searchQuery, noOfResults + 1))
                    .userAgent(AppUtils.USER_AGENT).get();
            final Elements results = doc.select(AppUtils.GOOGLE_RESULTS_ELEMENT);
            if (null != results) {
                resultUrls = new ArrayList<>();
                for (final Element result : results) {
                    final String linkHref = result.attr(AppUtils.HREF_ATTR);
                    if (!linkHref.isEmpty() && !linkHref.startsWith("/search")) {
                        resultUrls.add(linkHref.replaceAll(AppUtils.GOOGLE_RESULT_URL_REPLACE_STR,
                                AppUtils.EMPTY_STR));
                    }
                }
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
            return resultUrls;
        }
    }

    private String generateSearchUrl(final String searchQuery, final int noOfResults) {
        return new StringBuffer(AppUtils.GOOGLE_SEARCH_URL).append("?q=").append(searchQuery).append("&num=")
                .append(noOfResults).toString();
    }
}
