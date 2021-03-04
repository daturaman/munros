package org.example.munros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    public MunroFinderService() {
        loadMunroData();
    }

    private void loadMunroData() {
        try {
            final URL resource = MunroFinderService.class.getResource("/munrotab_v6.2.csv");
            final List<String> strings = Files.readAllLines(Paths.get(resource.toURI()), StandardCharsets.ISO_8859_1);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to load data: ", e);
        }
    }

    /**
     * Performs a search without any queries, i.e. will return unfiltered and unsorted data.
     * @return the search results as a JSON array formatted string.
     */
    public String search() {
        return "";
    }

    public static class Query {
        private float minHeight;
        private float maxHeight;
        private int limitResults;

        public Query minHeight(float minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Query maxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Query limitResults(int limitResults) {
            this.limitResults = limitResults;
            return this;
        }
    }
}
