package com.sk.wca.crawl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CralwlerService {

    List<String> getLibrariesFromURL(String urlString);

    Map<String, Map<String, Integer>> countLibraries(List<String> javascriptLibrariesUrls) throws IOException;

}
