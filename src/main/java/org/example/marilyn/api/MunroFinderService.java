package org.example.marilyn.api;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.example.marilyn.data.MunroLoader;
import org.example.marilyn.Munro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    private final List<Munro> munros;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MunroFinderService() {
        munros = MunroLoader.loadMunroData();
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
        final List<Munro> searchResult = munros.stream().filter(query.minHeight).collect(Collectors.toUnmodifiableList());
        return objectMapper.writeValueAsString(searchResult);
    }

    /**
     * Builds queries for the {@link MunroFinderService}.
     */
    public static class Query {
        private Predicate<Munro> minHeight;
        private float maxHeight;
        private int limitResults;

        public Query minHeight(float minHeight) {
            this.minHeight = munro -> munro.getHeight() >= minHeight;
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
