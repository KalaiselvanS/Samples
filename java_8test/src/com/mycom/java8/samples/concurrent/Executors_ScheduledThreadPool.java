package com.mycom.java8.samples.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Executors_ScheduledThreadPool {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
//        testSchedule();
//		testScheduleAtFixedRate();
		testScheduleWithFixedDelay();
	}

	private static void testSchedule() throws InterruptedException {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable task = () -> System.out.println("Scheduling: " + System.currentTimeMillis());
		int delay = 4;
		ScheduledFuture<?> future = executor.schedule(task, delay, TimeUnit.SECONDS);
		TimeUnit.MILLISECONDS.sleep(2001);
		long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
		System.out.printf("Remaining Delay: %sms\n", remainingDelay);
		executor.shutdown();
	}

	private static void testScheduleAtFixedRate() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable task = () -> System.out.println("Scheduling: " + System.currentTimeMillis());
		int initialDelay = 0;
		int period = 1;
		executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
		shut(executor, 5);
	}

	private static void testScheduleWithFixedDelay() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		int initialDelay = 0;
		int longDelay = 2;
		executor.scheduleWithFixedDelay(()->System.out.println("Scheduling: " + System.currentTimeMillis()),
				initialDelay, longDelay, TimeUnit.SECONDS);
		shut(executor, 10);
	}

	private static void shut(ScheduledExecutorService executor, int t) {
		try {
			TimeUnit.SECONDS.sleep(t);
		} catch (InterruptedException e) {
			throw new IllegalStateException("task interrupted", e);
		}
		executor.shutdown();
	}
}
