package org.example.marilyn;

import static java.nio.file.Files.lines;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    private static final String SEPARATOR = ",";
    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private static final String POSITIVE_INTEGER_REGEX = "[0-9]+";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile(POSITIVE_INTEGER_REGEX);
    private static final int CATEGORY = 28;
    private final List<Munro> munros;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MunroFinderService() {
        munros = loadMunroData();
    }

    /**
     * Performs a search without any queries, i.e. will return unfiltered and unsorted data.
     *
     * @return the search results as a JSON array formatted string.
     */
    public String search() throws JsonProcessingException {
        final String s = objectMapper.writeValueAsString(munros);
        return s;
    }

    /**
     * Performs a search, using the provided query to manipulate the result.
     *
     * @return the search results as a JSON array formatted string.
     */
    public String search(Query query) {
        return "";
    }

    private List<Munro> loadMunroData() {
        final URL resource = MunroFinderService.class.getResource(MUNRO_CSV);
        try (Stream<String> lines = lines(Paths.get(resource.toURI()), StandardCharsets.ISO_8859_1)) {
            return lines.map(s -> s.split(SEPARATOR, -1))
                        .filter(this::isMunroEntry)
                        .filter(this::hasCategory)
                        .map(Munro::new)
                        .collect(toUnmodifiableList());
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to load data: ", e);
        }
    }

    private boolean isMunroEntry(String[] entry) {
        return entry.length > 0 && POSITIVE_INTEGER_PATTERN.matcher(entry[0]).matches();
    }

    private boolean hasCategory(String[] entry) {
        return isNotBlank(entry[CATEGORY]);
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
