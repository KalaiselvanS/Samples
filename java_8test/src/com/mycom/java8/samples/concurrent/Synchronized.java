package com.mycom.java8.samples.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Synchronized {

	private static final int NUM_INCREMENTS = 10000;
	private static int count = 0;

	public static void main(String[] args) {
		testSyncIncrement();
		testNonSyncIncrement();
		testSyncIncrementBlock();
	}

	private static void testSyncIncrement() {
		count = 0;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(Synchronized::incrementSync));
		ConcurrentUtils.stop(executor);
		System.out.println("   Sync: " + count);
	}
	private static void testNonSyncIncrement() {
		count = 0;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(Synchronized::increment));
		ConcurrentUtils.stop(executor);
		System.out.println("NonSync: " + count);
	}
 	private static synchronized void incrementSync() {
		count = count + 1;
	}
	private static void increment() {
		count = count + 1;
	}

	private static void testSyncIncrementBlock() {
		count = 0;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(Synchronized::incrementSyncBlock));
		ConcurrentUtils.stop(executor);
		System.out.println(count);
	}
	private static void incrementSyncBlock() {
		synchronized (Synchronized2.class) {
			count = count + 1;
		}
	}
}
