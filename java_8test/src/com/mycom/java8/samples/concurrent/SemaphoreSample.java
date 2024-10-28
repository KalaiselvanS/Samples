package com.mycom.java8.samples.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreSample {

    private static Semaphore semaphore = new Semaphore(5);
	private static int count = 0;

	public static void main(String[] args) {
		testIncrement();
		
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.range(0, 10).forEach(i -> executor.submit(SemaphoreSample::doWork));
        ConcurrentUtils.stop(executor);
	}

	private static void testIncrement() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, 1000).forEach(i -> executor.submit(SemaphoreSample::increment));
		ConcurrentUtils.stop(executor);
		System.out.println("Increment: " + count);
	}

	private static void increment() {
		boolean permit = false;
		try {
			permit = semaphore.tryAcquire(5, TimeUnit.SECONDS);
			count++;
		} catch (InterruptedException e) {
			throw new RuntimeException("could not increment");
		} finally {
			if (permit) {
				semaphore.release();
			}
		}
	}

    private static void doWork() {
        boolean permit = false;
        try {
            permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
            if (permit) {
                System.out.println("Semaphore acquired");
                ConcurrentUtils.sleep(5);
            } else {
                System.out.println("Could not acquire semaphore");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if (permit) {
                semaphore.release();
            }
        }
    }

}
