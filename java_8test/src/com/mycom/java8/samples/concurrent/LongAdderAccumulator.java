package com.mycom.java8.samples.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

public class LongAdderAccumulator {

    public static void main(String[] args) {
    	testAccumulate();
    	testAdd();
        testIncrement();
    }

    private static void testAccumulate() {
        LongBinaryOperator op = (x, y) -> 2 * x + y;
        LongAccumulator accumulator = new LongAccumulator(op, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 5)
                .forEach(i -> executor.submit(() -> accumulator.accumulate(i)));

        ConcurrentUtils.stop(executor);

        System.out.format("Add: %d\n", accumulator.getThenReset());
    }

    private static void testAdd() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        LongAdder adder = new LongAdder();
        IntStream.range(0, 100)
                .forEach(i -> executor.submit(() -> adder.add(2)));

        ConcurrentUtils.stop(executor);

        System.out.format("Add: %d\n", adder.sumThenReset());
    }

    private static void testIncrement() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        LongAdder adder = new LongAdder();
        IntStream.range(0, 100)
                .forEach(i -> executor.submit(adder::increment));

        ConcurrentUtils.stop(executor);

        System.out.format("Increment: Expected=%d; Is=%d\n", 100, adder.sumThenReset());
    }
}
