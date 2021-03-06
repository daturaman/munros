package org.example.marilyn.api;

import static org.example.marilyn.Munro.Category.MUN;
import static org.example.marilyn.Munro.Category.TOP;
import static org.example.marilyn.api.MunroFinderService.Query.query;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.example.marilyn.Munro;
import org.example.marilyn.Munro.Category;
import org.example.marilyn.api.MunroFinderService.Query;
import org.example.marilyn.data.MunroLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MunroFinderServiceTest {

    private final static Predicate<Munro> CATEGORY_NOT_NULL = munro -> munro.getCategory() == null;
    private final static Predicate<Munro> GRID_REF_NOT_NULL = munro -> munro.getGridReference() == null;
    private final static Predicate<Munro> HEIGHT_NOT_NULL = munro -> munro.getHeight() == null;
    private final static Predicate<Munro> NAME_NOT_NULL = munro -> munro.getName() == null;
    private static final String MUNRO_CSV = "/munrotab_v6.2.csv";
    private static final String SORT_TEST_CSV = "/sort_test.csv";
    private static final String SORT_HEIGHT_ASC = "/sortHeightAsc.json";
    private static final String SORT_HEIGHT_DESC = "/sortHeightDesc.json";
    private static final String SORT_NAME_ASC = "/sortNameAsc.json";
    private static final String SORT_NAME_DESC = "/sortNameDesc.json";
    private static final String COMBO_QUERY_1 = "/comboQuery1.json";
    private static final String COMBO_QUERY_2 = "/comboQuery2.json";
    private static final String COMBO_QUERY_3 = "/comboQuery3.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MunroFinderService service;

    @BeforeEach
    void setUp() {
        service = createService(MUNRO_CSV);
    }

    @Test
    public void shouldReturnAllDataWhenNoSearchQueriesProvided() throws IOException {
        final List<Munro> munros = searchAndSerialise();
        assertTrue(munros.stream().noneMatch(CATEGORY_NOT_NULL
                .and(GRID_REF_NOT_NULL)
                .and(HEIGHT_NOT_NULL)
                .and(NAME_NOT_NULL)));
        assertTrue(munros.stream().anyMatch(munro -> munro.getCategory() == MUN));
        assertTrue(munros.stream().anyMatch(munro -> munro.getCategory() == TOP));
    }

    @ParameterizedTest
    @ValueSource(ints = {300, 10, 20, 1, 5})
    public void shouldLimitNumberOfResultsReturned(int limit) throws IOException {
        final List<Munro> munros = searchAndSerialise(query().limitResults(limit), service);
        assertEquals(limit, munros.size());
    }

    @ParameterizedTest
    @MethodSource("filterByHeight")
    public void shouldConstrainResultsByHeight(float minimum, float maximum) throws IOException {
        final List<Munro> munros = searchAndSerialise(query().minHeight(minimum).maxHeight(maximum), service);
        assertTrue(munros.stream().noneMatch(munro -> munro.getHeight() < minimum));
        assertTrue(munros.stream().noneMatch(munro -> munro.getHeight() > maximum));
    }

    @ParameterizedTest
    @MethodSource("invalidFilterByHeight")
    public void shouldThrowExceptionWhenPassedInvalidHeights(float minimum, float maximum) {
        assertThrows(IllegalArgumentException.class,
                () -> searchAndSerialise(query().minHeight(minimum).maxHeight(maximum), service));
    }

    @ParameterizedTest
    @MethodSource("filterByCategory")
    public void shouldFilterResultsByCategory(Query searchQuery, Category excluded) throws IOException {
        final List<Munro> munros = searchAndSerialise(searchQuery, service);
        assertTrue(munros.stream().noneMatch(munro -> munro.getCategory() == excluded));
    }

    @ParameterizedTest
    @MethodSource("queries")
    public void shouldSearchUsingOneOrMoreQueryCriteria(Query query, String afterSortFile) throws IOException {
        MunroFinderService finderService = createService(SORT_TEST_CSV);
        final List<Munro> actual = searchAndSerialise(query, finderService);
        final List<Munro> expected = objectMapper.readValue(
                getClass().getResource(afterSortFile),
                new TypeReference<>() {});
        assertIterableEquals(actual, expected);
    }

    private MunroFinderService createService(String file) {
        MunroLoader munroLoader = new MunroLoader(getClass().getResource(file));
        return new MunroFinderService(munroLoader);
    }

    private List<Munro> searchAndSerialise() throws IOException {
        final String search = service.search();
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {
        });
    }

    private List<Munro> searchAndSerialise(Query searchQuery, MunroFinderService service) throws IOException {
        final String search = service.search(searchQuery);
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {});
    }

    private static Stream<Arguments> filterByHeight() {
        return Stream.of(
                arguments(900.0f, 1000.0f),
                arguments(951.0f, 1100.2f),
                arguments(1001.1f, 1201.3f),
                arguments(1111.1f, 1299.0f)
        );
    }

    private static Stream<Arguments> invalidFilterByHeight() {
        return Stream.of(
                arguments(1000.0f, 900.0f),
                arguments(1100.2f, 951.0f),
                arguments(1201.3f, 1001.1f),
                arguments(1299.0f, 1111.1f)
        );
    }

    private static Stream<Arguments> filterByCategory() {
        return Stream.of(
                arguments(query().category(MUN), TOP),
                arguments(query().category(TOP), MUN)
        );
    }

    private static Stream<Arguments> queries() {
        return Stream.of(
                arguments(query().sortHeightAsc(), SORT_HEIGHT_ASC),
                arguments(query().sortHeightDesc(), SORT_HEIGHT_DESC),
                arguments(query().sortNameAsc(), SORT_NAME_ASC),
                arguments(query().sortNameDesc(), SORT_NAME_DESC),
                arguments(query().minHeight(1000.0f).sortHeightDesc(), COMBO_QUERY_1),
                arguments(query().sortNameAsc().maxHeight(950.0f), COMBO_QUERY_2),
                arguments(query().maxHeight(1070.0f).sortHeightAsc().minHeight(975.0f), COMBO_QUERY_3)
        );
    }
}