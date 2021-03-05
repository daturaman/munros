package org.example.marilyn;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    private final List<Munro> munros;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MunroFinderService() {
        munros = DataLoader.loadMunroData();
    }

    /**
     * Performs a search without any queries, i.e. will return unfiltered and unsorted data.
     *
     * @return the search results as a JSON array formatted string.
     */
    public String search() throws JsonProcessingException {
        return objectMapper.writeValueAsString(munros);
    }

    /**
     * Performs a search, using the provided query to manipulate the result.
     *
     * @return the search results as a JSON array formatted string.
     */
    public String search(Query query) throws JsonProcessingException {
        return objectMapper.writeValueAsString(munros);
    }

    /**
     * Builds queries for the {@link MunroFinderService}.
     */
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
