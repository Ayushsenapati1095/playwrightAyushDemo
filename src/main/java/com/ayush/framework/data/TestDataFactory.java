package com.ayush.framework.data;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.UUID;

/**
 * Generates unique, throw-away test data so tests never depend on each other
 * and can safely run in parallel.
 */
public final class TestDataFactory {

    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(() -> new Faker(Locale.UK));

    private TestDataFactory() {
    }

    public static String uniqueEmail() {
        return "qa+" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String randomPassword() {
        return FAKER.get().internet().password(10, 16, true, true, true);
    }

    public static String firstName() {
        return FAKER.get().name().firstName();
    }

    public static String lastName() {
        return FAKER.get().name().lastName();
    }
}
