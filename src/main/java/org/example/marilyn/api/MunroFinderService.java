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
     *
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
        long limit = query.limitResults == 0 ? munros.size() : query.limitResults;
        final List<Munro> searchResult = munros.stream()
                                               .filter(query.filters)
                                               .sorted(query.sorts)
                                               .limit(limit)
                                               .collect(toUnmodifiableList());
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
        private long limitResults;
        private Float minHeight;
        private Float maxHeight;
        private String description = "Search for entries where ";

        /**
         * Factory method to make query initialisation slightly more fluent.
         *
         * @return a new {@link Query} instance.
         */
        public static Query query() {
            return new Query();
        }

        /**
         * Adds a minimum height search criterion to the query.
         *
         * @param minHeight floating point value representing the minimum height, in metres, of munros to find.
         * @return this {@link Query}.
         */
        public Query minHeight(float minHeight) {
            this.minHeight = minHeight;
            if (maxHeight != null && minHeight > maxHeight) {
                throw new IllegalArgumentException("Minimum height must be less than maximum.");
            }
            this.filters = filters.and(munro -> munro.getHeight() >= minHeight);
            description += String.format("minimum height is %f; ", minHeight);
            return this;
        }

        /**
         * Adds a maximum height search criterion to the query.
         *
         * @param maxHeight floating point value representing the maximum height, in metres, of munros to find.
         * @return this {@link Query}.
         */
        public Query maxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
            if (minHeight != null && maxHeight < minHeight) {
                throw new IllegalArgumentException("Maximum height must be greater than minimum");
            }
            this.filters = filters.and(munro -> munro.getHeight() <= maxHeight);
            description += String.format("maximum height is %f; ", maxHeight);
            return this;
        }

        /**
         * Filters the results based on munro category.
         *
         * @param category the munro category.
         * @return this {@link Query}.
         */
        public Query category(Category category) {
            this.filters = category == MUN ? filters.and(MUNRO_FILTER) : filters.and(MUNRO_TOP_FILTER);
            description += String.format("category is %s; ", category);
            return this;
        }

        /**
         * Sorts the results by height in ascending order.
         *
         * @return this {@link Query}.
         */
        public Query sortHeightAsc() {
            this.sorts = sorts.thenComparing(Munro::getHeight);
            description += "sorting by height ascending; ";
            return this;
        }

        /**
         * Sorts the results by height in descending order.
         *
         * @return this {@link Query}.
         */
        public Query sortHeightDesc() {
            this.sorts = sorts.thenComparing(Munro::getHeight).reversed();
            description += "sorting by height descending; ";
            return this;
        }

        /**
         * Sorts the results by name in ascending order.
         *
         * @return this {@link Query}.
         */
        public Query sortNameAsc() {
            this.sorts = sorts.thenComparing(Munro::getName);
            description += "sorting by name ascending; ";
            return this;
        }

        /**
         * Sorts the results by name in descending order.
         *
         * @return this {@link Query}.
         */
        public Query sortNameDesc() {
            this.sorts = sorts.thenComparing(Munro::getName).reversed();
            description += "sorting by name descending; ";
            return this;
        }

        /**
         * Restricts the results by the provided limit.
         *
         * @param limitResults the maximum number of results to return.
         * @return this {@link Query}.
         */

        public Query limitResults(long limitResults) {
            this.limitResults = limitResults;
            description += String.format("limiting results to %d entries", limitResults);
            return this;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
