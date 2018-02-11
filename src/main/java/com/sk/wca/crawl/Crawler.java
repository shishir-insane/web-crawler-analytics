package com.sk.wca.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

    private static final int NUMBER_OF_SEARCH_RESULT = 20;
    private final List<String> resultLinks;
    private final Map<String, Integer> javaScriptLibrariesCounterMap;
    private final Set<String> uniqueURLs;

    public Crawler(final String searchQuery) {

        resultLinks = new LinkedList<>();
        javaScriptLibrariesCounterMap = new TreeMap<>();
        uniqueURLs = new HashSet<>();
        crawlLinks(searchQuery);
    }

    public void getTopFiveCrawledJavaScriptLibraries() {

        if (resultLinks.size() == 0) {
            return;
        }

        // crawl the libraries in each found link, and PRINT the top used
        // libraries.
        for (final String link : resultLinks) {
            getJavaScriptLibrariesFromURL(link);
        }

        final List<Map.Entry<String, Integer>> topFiveLibraries = getTopFiveLibrariesFromMapCounter(
                javaScriptLibrariesCounterMap);

        for (final Map.Entry<String, Integer> entry : topFiveLibraries) {

            System.out.println("Librayr is : " + entry.getKey() + ", with number of occurrence :" + entry.getValue());
        }

    }

    private void crawlLinks(String query) {

        if (query == null) {
            throw new NullPointerException("Search String can not be NULL");
        }

        if (query == "") {
            throw new IllegalArgumentException("Search String can not be empty");
        }

        try {

            // Parse the query, and extract the absolute urls or relevant result
            // only, before adding them to the list for later access.
            query = query.replace(" ", "+");
            final String url = "https://www.google.com/search?q=" + query + "&num=" + NUMBER_OF_SEARCH_RESULT;
            final Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
            final Elements links = doc.select("h3.r > a");

            // adding the UNIQUE elements to the links.
            for (final Element link : links) {
                if (!uniqueURLs.contains(link.absUrl("href"))) {
                    ;
                }
                {
                    resultLinks.add(link.absUrl("href"));
                }
            }

        } catch (final IOException ex) {

            System.err.println("Attempt to search throws IOException " + ex.getMessage());
        }

    }

    private void getJavaScriptLibrariesFromURL(final String url) {

        if (url == null) {
            throw new NullPointerException("URL String can not be NULL");
        }

        if (url == "") {
            throw new IllegalArgumentException("URL String can not be empty");
        }

        try {

            // parse the url and iterate over the parsed document. Search for
            // Script tags, and extract the library name in case it has Src
            // attribute.
            // Each identified library name updates the HashTable.
            final Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
            final Elements libraries = doc.getElementsByTag("script");

            for (final Element library : libraries) {
                if (library.hasAttr("src")) {
                    final String libraryName = extractJavaScriptLibraryOriginalName(library.attr("src"));

                    if (javaScriptLibrariesCounterMap.containsKey(libraryName)) {
                        javaScriptLibrariesCounterMap.put(libraryName,
                                javaScriptLibrariesCounterMap.get(libraryName) + 1);
                    } else {
                        javaScriptLibrariesCounterMap.put(libraryName, new Integer(1));
                    }
                }
            }

        } catch (final IOException ex) {

            System.err.println("Attempt to access one of the URLs throws IOException " + ex.getMessage());
        }
    }

    private String extractJavaScriptLibraryOriginalName(final String url) {

        if (url == null) {
            throw new NullPointerException();
        }

        if (url == "") {
            throw new IllegalArgumentException();
        }

        final String[] urlSplits = url.split("/");
        final String[] nameSplits = urlSplits[urlSplits.length - 1].split("\\?");

        return nameSplits[0];
    }

    private List<Map.Entry<String, Integer>> getTopFiveLibrariesFromMapCounter(final Map<String, Integer> unsortedMap) {

        // Convert the Map into a List<Entries>, before sorting over the values
        // descending, and resturn only the top five elements.
        final List<Map.Entry<String, Integer>> mapAsList = new ArrayList<>(
                unsortedMap.entrySet());

        Collections.sort(mapAsList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        return mapAsList.subList(0, 5);
    }

    public List<String> getResultLinks() {
        return resultLinks;
    }

}
