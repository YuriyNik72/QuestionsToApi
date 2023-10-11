package com.nikitin;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String json = null;
        Object doc = new Object();
        try {
            CrptApi.createDocument(doc, json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
