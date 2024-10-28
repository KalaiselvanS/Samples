package com.mycom.java8.samples.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class LockSample {

	private static final int NUM_INCREMENTS = 10000;

	private static ReentrantLock lock = new ReentrantLock();

	private static int count = 0;

	public static void main(String[] args) {
		testWithoutReentrantLock();
		testReentrantLock();
		tryLockInReentrantLock();
		writeLockInReadWriteLock();
		stampedLock();
		stampedLock_tryOptimisticRead();
		stampedLock_tryConvertToWriteLock();
	}

	private static void increment() {
		lock.lock();
		try {
			count++;
		} finally {
			lock.unlock();
		}
	}

	private static void increment1() {
		count++;
	}

	private static void testWithoutReentrantLock() {
		count = 0;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(LockSample::increment1));
		ConcurrentUtils.stop(executor);
		System.out.println("Without lock: " + count);
	}

	private static void testReentrantLock() {
		count = 0;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(LockSample::increment));
		ConcurrentUtils.stop(executor);
		System.out.println("With lock: " + count);
	}

	public static void tryLockInReentrantLock() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(() -> {
			lock.lock();
			try {
				ConcurrentUtils.sleep(1);
			} finally {
				lock.unlock();
			}
		});

		executor.submit(() -> {
			System.out.println("Locked: " + lock.isLocked());
			System.out.println("Held by me: " + lock.isHeldByCurrentThread());
			boolean locked = lock.tryLock();
			System.out.println("Lock acquired: " + locked);
		});

		executor.submit(() -> {
			System.out.println("Locked: " + lock.isLocked());
			System.out.println("Held by me: " + lock.isHeldByCurrentThread());
			try {
				boolean locked = lock.tryLock(2, TimeUnit.SECONDS);
				System.out.println("Lock acquired: " + locked);
				System.out.println("Held by me: " + lock.isHeldByCurrentThread());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		ConcurrentUtils.stop(executor);
	}

	public static void writeLockInReadWriteLock() {
		ExecutorService executor = Executors.newFixedThreadPool(2);

		Map<String, String> map = new HashMap<>();

		ReadWriteLock lock = new ReentrantReadWriteLock();

		executor.submit(() -> {
//            lock.writeLock().lock();
			try {
				ConcurrentUtils.sleep(1);
				map.put("foo", "bar");
			} finally {
				lock.writeLock().unlock();
			}
		});

		Runnable readTask = () -> {
			lock.readLock().lock();
			try {
				System.out.println(map.get("foo"));
//                ConcurrentUtils.sleep(1);
			} finally {
				lock.readLock().unlock();
			}
		};
		executor.submit(readTask);
		executor.submit(readTask);

		ConcurrentUtils.stop(executor);
	}
	
	public static void stampedLock() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Map<String, String> map = new HashMap<>();
		StampedLock lock = new StampedLock();
		executor.submit(() -> {
            long stamp = lock.writeLock();
			try {
				ConcurrentUtils.sleep(1);
				map.put("foo", "bar");
			} finally {
                lock.unlockWrite(stamp);
			}
		});

		Runnable readTask = () -> {
			long stamp = lock.readLock();
			try {
				System.out.println(map.get("foo"));
				ConcurrentUtils.sleep(1);
			} finally {
				lock.unlockRead(stamp);
			}
		};
		executor.submit(readTask);
		ConcurrentUtils.stop(executor);
	}
	public static void stampedLock_tryOptimisticRead() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		StampedLock lock = new StampedLock();
		executor.submit(() -> {
			long stamp = lock.tryOptimisticRead();
			try {
				System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
				ConcurrentUtils.sleep(1);
				System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
				ConcurrentUtils.sleep(2);
				System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
				stamp = lock.tryOptimisticRead();
				System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
			} finally {
				lock.unlock(stamp);
			}
		});
		executor.submit(() -> {
			long stamp = lock.writeLock();
			try {
				System.out.println("Write Lock acquired");
				ConcurrentUtils.sleep(2);
			} finally {
				lock.unlock(stamp);
				System.out.println("Write done");
			}
		});
		ConcurrentUtils.stop(executor);
	}
	public static void stampedLock_tryConvertToWriteLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();
        executor.submit(() -> {
            long stamp = lock.readLock();
            try {
                if (count == 0) {
                    stamp = lock.tryConvertToWriteLock(stamp);
                    if (stamp == 0L) {
                        System.out.println("Could not convert to write lock");
                        stamp = lock.writeLock();
                    }
                    count = 23;
                }
                System.out.println(count);
            } finally {
                lock.unlock(stamp);
            }
        });
        ConcurrentUtils.stop(executor);
    }
}
