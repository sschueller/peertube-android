package net.schueller.peertube.provider;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = SearchSuggestionsProvider.class.getName();
    public static final int MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider()
    {
        setupSuggestions(AUTHORITY, MODE);
    }

    // TODO: add search suggestions once they become available in peertube server

}
