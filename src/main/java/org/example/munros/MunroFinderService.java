package org.example.munros;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lookup service for munros that can sort and filter the results based on height and category.
 */
public class MunroFinderService {

    private static final String SEPARATOR = ",";
    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private static final String POSITIVE_INTEGER_REGEX = "[0-9]+";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile(POSITIVE_INTEGER_REGEX);

    public MunroFinderService() {
        loadMunroData();
    }

    private void loadMunroData() {
        final URL resource = MunroFinderService.class.getResource(MUNRO_CSV);
        final List<String[]> collect;
        try (Stream<String> lines = Files.lines(Paths.get(resource.toURI()), StandardCharsets.ISO_8859_1)){
            collect = lines.map(s -> s.split(SEPARATOR)).filter(this::isMunroEntry).collect(Collectors.toList());
System.out.println(collect);
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



    private boolean isMunroEntry(String [] entry) {
        return entry.length > 0 && POSITIVE_INTEGER_PATTERN.matcher(entry[0]).matches();
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
