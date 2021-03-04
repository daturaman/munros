package org.example.marilyn;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class MunroFinderServiceTest {

    private MunroFinderService service;
    private ObjectMapper objectMapper = new ObjectMapper();
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
        final String search = service.search();
        System.out.println(search);
        final List<Munro> munros = objectMapper.readValue(search.getBytes(), new TypeReference<List<Munro>>() {
        });
        assertTrue(munros.stream().noneMatch(categoryNotNull.and(gridRefNotNull).and(heightNotNull).and(nameNotNull)));
    }
}