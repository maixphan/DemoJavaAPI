package com.example.demo.util;

public class NotFoundException extends RuntimeException{
    public NotFoundException() {
        super("no data");
    }
}
