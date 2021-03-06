package org.example.marilyn.api;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.example.marilyn.Munro.Category.MUN;
import static org.example.marilyn.Munro.Category.TOP;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.example.marilyn.Munro;
import org.example.marilyn.Munro.Category;
import org.example.marilyn.data.MunroLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private final List<Munro> munros;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initialises a {@link MunroFinderService} using the default data loader.
     */
    public MunroFinderService() {
        this.munros = new MunroLoader(MunroFinderService.class.getResource(MUNRO_CSV)).loadMunroData();
    }

    /**
     * Initialises the finder service with the provided munro data loader.
     * @param munroLoader the service for loading munro data.
     */
    public MunroFinderService(MunroLoader munroLoader) {
        munros = munroLoader.loadMunroData();
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
        final List<Munro> searchResult = munros.stream().filter(query.filters).sorted(query.sorts).collect(toUnmodifiableList());
        return objectMapper.writeValueAsString(searchResult);
    }

    /**
     * Builds queries for the {@link MunroFinderService}.
     */
    public static class Query {

        private static final Predicate<Munro> MUNRO_FILTER = munro -> munro.getCategory() == MUN;
        private static final Predicate<Munro> MUNRO_TOP_FILTER = munro -> munro.getCategory() == TOP;
        private Predicate<Munro> filters = p -> true;
        private Comparator<Munro> sorts = (o1, o2) -> 0;
        private int limitResults;

        /**
         * Factory method to make query initialisation slightly more fluent.
         * @return a new {@link Query} instance.
         */
        public static Query query() {
            return new Query();
        }

        /**
         * Adds a minimum height search criterion to the query.
         * @param minHeight floating point value representing the minimum height, in metres, of munros to find.
         * @return this {@link Query}.
         */
        public Query minHeight(float minHeight) {
            this.filters = filters.and(munro -> munro.getHeight() >= minHeight);
            return this;
        }

        /**
         * Adds a maximum height search criterion to the query.
         * @param maxHeight floating point value representing the maximum height, in metres, of munros to find.
         * @return this {@link Query}.
         */
        public Query maxHeight(float maxHeight) {
            this.filters = filters.and(munro -> munro.getHeight() <= maxHeight);
            return this;
        }

        /**
         * Filters the results based on munro category.
         * @param category the munro category.
         * @return this {@link Query}.
         */
        public Query category(Category category) {
            this.filters = category == MUN ? filters.and(MUNRO_FILTER) : filters.and(MUNRO_TOP_FILTER);
            return this;
        }

        /**
         * Sorts the results by height in ascending order.
         * @return this {@link Query}.
         */
        public Query sortHeightAsc() {
            this.sorts = sorts.thenComparing(Munro::getHeight);
            return this;
        }

        /**
         * Sorts the results by height in descending order.
         * @return this {@link Query}.
         */
        public Query sortHeightDesc() {
            this.sorts = sorts.thenComparing(Munro::getHeight).reversed();
            return this;
        }

        public Query limitResults(int limitResults) {
            this.limitResults = limitResults;
            return this;
        }
    }
}
