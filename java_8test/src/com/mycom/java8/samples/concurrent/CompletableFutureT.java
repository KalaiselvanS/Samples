package com.mycom.java8.samples.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureT{

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("42");
        future.thenAccept(System.out::println)
                .thenAccept(v -> System.out.println("done"+v));

    }
}
