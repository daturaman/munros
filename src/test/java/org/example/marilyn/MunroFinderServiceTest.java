package org.example.marilyn;

import static org.example.marilyn.Munro.Category.MUN;
import static org.example.marilyn.Munro.Category.TOP;
import static org.example.marilyn.api.MunroFinderService.Query.query;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.example.marilyn.Munro.Category;
import org.example.marilyn.api.MunroFinderService;
import org.example.marilyn.api.MunroFinderService.Query;
import org.example.marilyn.data.MunroLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class MunroFinderServiceTest {

    private final static Predicate<Munro> CATEGORY_NOT_NULL = munro -> munro.getCategory() == null;
    private final static Predicate<Munro> GRID_REF_NOT_NULL = munro -> munro.getGridReference() == null;
    private final static Predicate<Munro> HEIGHT_NOT_NULL = munro -> munro.getHeight() == null;
    private final static Predicate<Munro> NAME_NOT_NULL = munro -> munro.getName() == null;
    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MunroFinderService service;
    private MunroLoader munroLoader;

    @BeforeEach
    void setUp() {
        munroLoader = new MunroLoader(MunroFinderServiceTest.class.getResource(MUNRO_CSV));
        service = new MunroFinderService(munroLoader);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void shouldReturnAllDataWhenNoSearchQueriesProvided() throws IOException {
        final List<Munro> munros = searchAndSerialise();
        assertTrue(munros.stream().noneMatch(CATEGORY_NOT_NULL.and(GRID_REF_NOT_NULL).and(HEIGHT_NOT_NULL).and(
                NAME_NOT_NULL)));
        assertTrue(munros.stream().anyMatch(munro -> munro.getCategory() == MUN));
        assertTrue(munros.stream().anyMatch(munro -> munro.getCategory() == TOP));
    }

    @ParameterizedTest
    @ValueSource(floats = {900.0f, 1100.5f, 1012.2f, 984.2f})
    public void shouldConstrainResultsWithMinimumHeight(Float minimum) throws IOException {
        Query searchQuery = query().minHeight(minimum);
        final List<Munro> munros = searchAndSerialise(searchQuery);
        assertTrue(munros.stream().noneMatch(munro -> munro.getHeight() < minimum));
    }

    @ParameterizedTest
    @ValueSource(floats = {900.0f, 1100.5f, 1012.2f, 984.2f})
    public void shouldConstrainResultsWithMaximumHeight(Float maximum) throws IOException {
        Query searchQuery = new Query().maxHeight(maximum);
        final List<Munro> munros = searchAndSerialise(searchQuery);
        assertTrue(munros.stream().noneMatch(munro -> munro.getHeight() > maximum));
    }

    @ParameterizedTest
    @MethodSource("filterByCategory")
    public void shouldFilterResultsByCategory(Query searchQuery, Category excluded) throws IOException {
        final List<Munro> munros = searchAndSerialise(searchQuery);
        assertTrue(munros.stream().noneMatch(munro -> munro.getCategory() == excluded));
    }

    private List<Munro> searchAndSerialise() throws IOException {
        final String search = service.search();
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {});
    }

    private List<Munro> searchAndSerialise(Query searchQuery) throws IOException {
        final String search = service.search(searchQuery);
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {});
    }

    private static Stream<Arguments> filterByCategory() {
        return Stream.of(
                arguments(query().category(MUN), TOP),
                arguments(query().category(TOP), MUN)
        );
    }
}