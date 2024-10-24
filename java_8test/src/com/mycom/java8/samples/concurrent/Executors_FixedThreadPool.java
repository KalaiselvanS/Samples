package com.mycom.java8.samples.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Executors_FixedThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        testSubmit1();
//        testSubmit2();
        testSubmit3();
    }

    private static void testSubmit1() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Future<Integer> future = executor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        });

        future.get(1, TimeUnit.SECONDS);
    }

    private static void testSubmit2() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Future<Integer> future = executor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        });

        executor.shutdownNow();
        future.get();
    }

    private static void testSubmit3() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Future<Integer> future = executor.submit(() -> {
            try {
                TimeUnit.MICROSECONDS.sleep(1000);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        });
        Future<Integer> future2 = executor.submit(() -> {
            try {
                TimeUnit.MICROSECONDS.sleep(10000);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        });
        
        System.out.println("future done: " + future.isDone());
        System.out.println("future2 done: " + future2.isDone());

//        Integer result = future.get(1100, TimeUnit.MICROSECONDS);
//        System.out.println("future done: " + future.isDone());
//        System.out.println("result: " + result);
        Integer result2 = 0;
        while (!future2.isDone()) {
        	try {
        		result2 = future2.get(1, TimeUnit.MICROSECONDS);
        	}catch (TimeoutException e) {
        		System.out.print(".");
			}
        }
        result2 = future2.get(10, TimeUnit.MICROSECONDS);
        System.out.println("future2 done: " + future2.isDone());
        System.out.print("result2: " + result2);

        executor.shutdownNow();
    }

}
