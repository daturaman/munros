package org.example.marilyn;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

public class DataLoader {

    private static final int CATEGORY = 28;
    private static final String SEPARATOR = ",";
    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private static final String POSITIVE_INTEGER_REGEX = "[0-9]+";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile(POSITIVE_INTEGER_REGEX);

    static List<Munro> loadMunroData() {
        final URL resource = MunroFinderService.class.getResource(MUNRO_CSV);
        try (Stream<String> lines = Files.lines(Paths.get(resource.toURI()), StandardCharsets.ISO_8859_1)) {
            return lines.map(s -> s.split(SEPARATOR, -1))
                        .filter(DataLoader::isMunroEntry)
                        .filter(DataLoader::hasCategory)
                        .map(Munro::new)
                        .collect(Collectors.toUnmodifiableList());
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to load data: ", e);
        }
    }

    private static boolean isMunroEntry(String[] entry) {
        return entry.length > 0 && POSITIVE_INTEGER_PATTERN.matcher(entry[0]).matches();
    }

    private static boolean hasCategory(String[] entry) {
        return isNotBlank(entry[CATEGORY]);
    }
}