package com.mycom.java8.samples.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Executors_WorkStealingPool {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		testInvokeAll();
		System.out.println("=============");
		testInvokeAny();
	}

	private static void testInvokeAll() throws InterruptedException {
		ExecutorService executor = Executors.newWorkStealingPool();
		List<Callable<String>> callableTasks = Arrays.asList(() -> "task1", () -> "task2", () -> "task3");
		callableTasks = Arrays.asList(callableTask("task1", 1), callableTask("task2", 2), callableTask("task3", 3));
		executor.invokeAll(callableTasks).stream().map(future -> {
			try {
				return future.get();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}).forEach(System.out::println);
//		executor.shutdown(); // not required
	}

	private static void testInvokeAny() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newWorkStealingPool();
		List<Callable<String>> callables = Arrays.asList(callableTask("task1", 2), callableTask("task2", 1), callableTask("task3", 3));
		String result = executor.invokeAny(callables);
		System.out.println(result);
	}

	private static Callable<String> callableTask(String result, long sleepSeconds) {
		return () -> {
			System.out.println(result +">>>> by "+ Thread.currentThread().getName());
			TimeUnit.SECONDS.sleep(sleepSeconds);
			System.out.println(result +"<<<< by "+ Thread.currentThread().getName());
			return result;
		};
	}
}
