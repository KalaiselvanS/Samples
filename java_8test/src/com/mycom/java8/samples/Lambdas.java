package com.mycom.java8.samples;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambdas {
	public static void main(String[] args) {
		lambda1();
		lambda2();
	}

//    lambda1
	public static void lambda1() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return b.compareTo(a);
			}
		});

		Collections.sort(names, (String a, String b) -> {
			return b.compareTo(a);
		});

		Collections.sort(names, (String a, String b) -> b.compareTo(a));

		Collections.sort(names, (a, b) -> b.compareTo(a));

		System.out.println(names);

		names.sort(Collections.reverseOrder());

		System.out.println(names);

		List<String> names2 = Arrays.asList("peter", null, "anna", "mike", "xenia");
		names2.sort(Comparator.nullsLast(String::compareTo));
		System.out.println(names2);

		List<String> names3 = null;

		Optional.ofNullable(names3).ifPresent(list -> list.sort(Comparator.naturalOrder()));

		System.out.println(names3);
	}

//  lambda2
	@FunctionalInterface
	public static interface Converter<F, T> {
		T convert(F from);
	}

	static class Something {
		String startsWith(String s) {
			return String.valueOf(s.charAt(0));
		}
	}

	interface PersonFactory<P extends Person> {
		P create(String firstName, String lastName);
	}

	public static void lambda2() {
		Converter<String, Integer> integerConverter1 = (from) -> Integer.valueOf(from);
		Integer converted1 = integerConverter1.convert("123");
		System.out.println(converted1); // result: 123
		// method reference
		Converter<String, Integer> integerConverter2 = Integer::valueOf;
		Integer converted2 = integerConverter2.convert("123");
		System.out.println(converted2); // result: 123

		Something something = new Something();

		Converter<String, String> stringConverter = something::startsWith;
		String converted3 = stringConverter.convert("Java");
		System.out.println(converted3); // result J

		// constructor reference
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
	}

//  lambda3
	@FunctionalInterface
	interface Fun {
		void foo();
	}
	public static void lambda3() throws Exception {
		// Predicates
		Predicate<String> predicate = (s) -> s.length() > 0;
		predicate.test("foo"); // true
		predicate.negate().test("foo"); // false
		Predicate<Boolean> nonNull = Objects::nonNull;
		Predicate<Boolean> isNull = Objects::isNull;
		Predicate<String> isEmpty = String::isEmpty;
		Predicate<String> isNotEmpty = isEmpty.negate();
		// Functions
		Function<String, Integer> toInteger = Integer::valueOf;
		Function<String, String> backToString = toInteger.andThen(String::valueOf);

		backToString.apply("123"); // "123"
		// Suppliers
		Supplier<Person> personSupplier = Person::new;
		personSupplier.get(); // new Person
		// Consumers
		Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
		greeter.accept(new Person("Luke", "Skywalker"));
		// Comparators
		Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
		Person p1 = new Person("John", "Doe");
		Person p2 = new Person("Alice", "Wonderland");
		comparator.compare(p1, p2); // > 0
		comparator.reversed().compare(p1, p2); // < 0
		// Runnables
		Runnable runnable = () -> System.out.println(UUID.randomUUID());
		runnable.run();
		// Callables
		Callable<UUID> callable = UUID::randomUUID;
		callable.call();
	}
//  lambda4
    static int outerStaticNum;
    int outerNum;
    void testScopes() {
        int num = 1;
        Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);
        String convert = stringConverter.convert(2);
        System.out.println(convert);    // 3
        Converter<Integer, String> stringConverter2 = (from) -> {
            outerNum = 13;
            return String.valueOf(from);
        };

        String[] array = new String[1];
        Converter<Integer, String> stringConverter3 = (from) -> {
            array[0] = "Hi there";
            return String.valueOf(from);
        };

        stringConverter3.convert(23);
        System.out.println(array[0]);
    }
//  lambda5
	// Pre-Defined Functional Interfaces
	public static void lambda5() {
		// BiConsumer Example
		BiConsumer<String, Integer> printKeyAndValue = (key, value) -> System.out.println(key + "-" + value);
		printKeyAndValue.accept("One", 1);
		printKeyAndValue.accept("Two", 2);
		System.out.println("##################");
		// Java Hash-Map foreach supports BiConsumer
		HashMap<String, Integer> dummyValues = new HashMap<>();
		dummyValues.put("One", 1);
		dummyValues.put("Two", 2);
		dummyValues.put("Three", 3);
		dummyValues.forEach((key, value) -> System.out.println(key + "-" + value));
	}
	
	public static class Person {
	    public String firstName;
	    public String lastName;
	    public Person() {}
	    public Person(String firstName, String lastName) {
	        this.firstName = firstName;
	        this.lastName = lastName;
	    }
	}
}