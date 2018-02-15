/**
 *
 * JavaScriptCrawlerServiceTest.java
 * com.sk.wca.crawl
 * web-crawler-analytics
 *
 * Copyright 2018 - Shishir Kumar
 */
package com.sk.wca.crawl;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.sk.wca.crawl.impl.JavaScriptCrawlerService;

public class JavaScriptCrawlerServiceTest {

    private JavaScriptCrawlerService javaScriptCrawlerService;

    private List<String> getHtmlData(final URI path) throws IOException {
        return Files.lines(Paths.get(path)).collect(toList());
    }

    @Before
    public void prepare() throws URISyntaxException, IOException {
        javaScriptCrawlerService = spy(new JavaScriptCrawlerService());
        doReturn(ImmutableList.of("angular.js", "chart.js", "jquery.js", "moment.js", "prototype.js", "react.js",
                "test.js", "video.js")).when(javaScriptCrawlerService).getIncludedLibsFromUrl(any());
    }

    @Test
    public void testGetIncludedLibsFromUrl() throws Exception {
        final List<String> result = javaScriptCrawlerService.getIncludedLibsFromUrl("test");
        assertEquals(8, result.size());
        assertEquals("jquery-3.2.1.min.js", result.get(0));
        assertEquals("moment.js", result.get(1));
        assertEquals("react.js", result.get(2));
        assertEquals("chart.bundle.min.js", result.get(3));
        assertEquals("angular.js", result.get(4));
        assertEquals("test.js", result.get(5));
        assertEquals("prototype.js", result.get(6));
        assertEquals("video.min.js", result.get(7));
    }
}
