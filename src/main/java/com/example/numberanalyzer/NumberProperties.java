package com.example.numberanalyzer;

/**
 * Java Record (introduced in Java 14) for Data Transfer Object (DTO).
 * Automatically generates an immutable class with constructor, getters,
 * equals(), hashCode(), and toString() methods based on its components.
 * Represents the properties calculated for a given number.
 *
 * @param number The number that was analyzed.
 * @param isEven Boolean flag indicating if the number is even.
 * @param isPrime Boolean flag indicating if the number is prime.
 * @param isPerfectSquare Boolean flag indicating if the number is a perfect square.
 * @param parity A string indicating the parity ("Even" or "Odd"), demonstrating switch expression usage.
 */
public record NumberProperties(
    long number,
    boolean isEven,
    boolean isPrime,
    boolean isPerfectSquare,
    String parity // Using String for switch expression demo
) {}
