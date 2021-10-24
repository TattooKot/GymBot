package com.example.demo.model;

public enum Fields {
    NAME("Name"),
    COUNT("Count"),
    PHONE("Phone"),
    PAYMENT("Add pay");

    private final String name;

    Fields(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
