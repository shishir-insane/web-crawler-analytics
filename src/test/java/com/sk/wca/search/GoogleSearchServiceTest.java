package com.sk.wca.search;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sk.wca.search.impl.GoogleSearchService;

public class GoogleSearchServiceTest {

    private GoogleSearchService searchService;

    private final String[] fetchedResultsExpected = new String[] { "https://nl.wikipedia.org/wiki/Kunststof",
            "https://en.wikipedia.org/wiki/Plastic", "https://nl.wiktionary.org/wiki/plastic",
            "https://www.plasticsoupfoundation.org/",
            "https://www.scientias.nl/zo-kunnen-we-99-plastic-oceanen-verdwenen-is-opsporen/",
            "https://www.volkskrant.nl/wetenschap/achteraf-scheiden-van-plastic-in-afvalfabrieken-is-goedkoper-en-efficienter-dan-vooraf-klopt-dit-wel~a4532556/",
            "https://nos.nl/artikel/2193860-plastic-afval-recyclen-heeft-weinig-effect-op-milieu.html",
            "https://www.schooltv.nl/video/het-klokhuis-plastic-2/", "http://www.plasticheroes.nl/wat" };

    private Document getHtmlData() throws IOException, URISyntaxException {
        return Jsoup.parse(new String(Files.readAllBytes(Paths.get(getClass().getResource("/results.html").toURI()))),
                StandardCharsets.UTF_8.name());
    }

    @Before
    public void init() throws IOException, URISyntaxException {
        searchService = Mockito.mock(GoogleSearchService.class);
        when(searchService.getHtmlDocumentFromUrl(any())).thenReturn(getHtmlData());
    }

    @Test
    public void testGetResultURLsForSearchQuery() {
        final List<String> resultUrls = searchService.getResultURLsForSearchQuery("java", 30);
        System.out.println(resultUrls);
    }

}
