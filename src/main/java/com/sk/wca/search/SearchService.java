package com.sk.wca.search;

import java.util.List;

public interface SearchService {

    List<String> getResultURLsForSearchQuery(String searchQuery, int noOfResults);

}
