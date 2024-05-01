package io.github.slangerosuna;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    public void intentionallyFailingTest() {
        fail("TDD Dictates you must have a failing test before you write any new code");
    }
}