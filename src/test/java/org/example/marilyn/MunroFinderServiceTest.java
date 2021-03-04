package org.example.marilyn;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import org.example.marilyn.MunroFinderService.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class MunroFinderServiceTest {

    private MunroFinderService service;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Predicate<Munro> categoryNotNull = munro -> munro.getCategory() == null;
    private final static Predicate<Munro> gridRefNotNull = munro -> munro.getGridReference() == null;
    private final static Predicate<Munro> heightNotNull = munro -> munro.getHeight() == null;
    private final static Predicate<Munro> nameNotNull = munro -> munro.getName() == null;

    @BeforeEach
    void setUp() {
        service = new MunroFinderService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void shouldReturnAllDataWhenNoSearchQueriesProvided() throws IOException {
        final List<Munro> munros = searchAndSerialise();
        assertTrue(munros.stream().noneMatch(categoryNotNull.and(gridRefNotNull).and(heightNotNull).and(nameNotNull)));
    }

    @ParameterizedTest
    @ValueSource(floats = {900.0f, 1100.5f, 1012.2f, 984.2f})
    public void shouldRestrictResultsToMinimumHeight(Float minimum) throws IOException {
        Query searchQuery = new Query().minHeight(minimum);
        final List<Munro> munros = searchAndSerialise(searchQuery);
        assertTrue(munros.stream().noneMatch(munro -> munro.getHeight() < minimum));
    }

    private List<Munro> searchAndSerialise() throws IOException {
        final String search = service.search();
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {});
    }

    private List<Munro> searchAndSerialise(Query searchQuery) throws IOException {
        final String search = service.search(searchQuery);
        return objectMapper.readValue(search.getBytes(), new TypeReference<>() {});
    }
}