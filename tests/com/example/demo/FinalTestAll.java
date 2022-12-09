package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FinalTestAll {

    @Test
    void test1() {
        Final test = new Final();
        String actual = "Three Three Three !";
        var output = test.cleanTheData(actual);
        assertEquals("1. three 3", output.toString());
}
    @Test
    public void test2() {
        Final test = new Final();
        String actual = "two two";
        String output = test.cleanTheData(actual);
        assertEquals("1. two 2", output.toString());
    }
    @Test
    public void test3() {
        Final test = new Final();
        String actual = "two two";
        String output = test.cleanTheData(actual);
        assertNotEquals("1. three 2", output.toString());
    }
}