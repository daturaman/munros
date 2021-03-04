package org.example.munros;

import static org.junit.jupiter.api.Assertions.*;

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

    }
}