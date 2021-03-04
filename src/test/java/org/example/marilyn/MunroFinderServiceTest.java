package org.example.marilyn;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MunroFinderServiceTest {

    private MunroFinderService service;

    @BeforeEach
    void setUp() {
        service = new MunroFinderService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void shouldReturnAllDataWhenNoSearchQueriesProvided() {
        final String search = service.search();
        System.out.println(search);
        //assert that entries with no category are not included
        //assert no null properites
    }
}