package com.example.numberanalyzer;

import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;

/**
 * Service Layer: Contains the core business logic for analyzing numbers.
 * This class is annotated with @Service, making it a Spring-managed bean.
 * It demonstrates various Java features like CompletableFuture, Streams, and modern switch.
 */
@Service
public class NumberAnalysisService {

    // Constants for simulated delays (in milliseconds) to mimic work
    private static final long DELAY_EVEN_CHECK = 10;
    private static final long DELAY_PRIME_CHECK = 50;
    private static final long DELAY_SQUARE_CHECK = 20;

    /**
     * Analyzes the given number and returns its properties asynchronously.
     * Uses CompletableFuture to potentially run different property checks concurrently.
     * This pattern is useful for I/O bound tasks or computationally intensive checks
     * that can run in parallel.
     *
     * @param number The non-negative long integer to analyze.
     * @return A CompletableFuture which will eventually contain the NumberProperties record.
     * The future completes exceptionally if any analysis step fails.
     */
    public CompletableFuture<NumberProperties> analyzeNumber(long number) {
        // Input validation at the service boundary
        if (number < 0) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("Input number cannot be negative.")
            );
        }

        // Launch asynchronous computations for each property check.
        // CompletableFuture.supplyAsync runs the task in ForkJoinPool.commonPool() by default.
        CompletableFuture<Boolean> evenCheckFuture = CompletableFuture.supplyAsync(() -> isEven(number));
        CompletableFuture<Boolean> primeCheckFuture = CompletableFuture.supplyAsync(() -> isPrime(number));
        CompletableFuture<Boolean> perfectSquareCheckFuture = CompletableFuture.supplyAsync(() -> isPerfectSquare(number));
        CompletableFuture<String> parityCheckFuture = CompletableFuture.supplyAsync(() -> checkParity(number));

        // Combine the results of all asynchronous checks.
        // CompletableFuture.allOf waits for all futures to complete (normally or exceptionally).
        // thenApplyAsync is used to process the results in another async step,
        // preventing blocking of the thread that completed the last future in the 'allOf'.
        return CompletableFuture.allOf(evenCheckFuture, primeCheckFuture, perfectSquareCheckFuture, parityCheckFuture)
            .thenApplyAsync(ignoredVoid -> {
                try {
                    // Retrieve results from the completed futures.
                    // .get() will throw ExecutionException if the future completed exceptionally.
                    boolean evenResult = evenCheckFuture.get();
                    boolean primeResult = primeCheckFuture.get();
                    boolean perfectSquareResult = perfectSquareCheckFuture.get();
                    String parityResult = parityCheckFuture.get();

                    // Construct the result record using the gathered properties.
                    return new NumberProperties(number, evenResult, primeResult, perfectSquareResult, parityResult);
                } catch (InterruptedException e) {
                    // Handle interruption during Future.get()
                    Thread.currentThread().interrupt(); // Preserve interrupt status
                    // Wrap in a custom runtime exception for consistent error handling upstream.
                    throw new AnalysisException("Analysis interrupted for number: " + number, e);
                } catch (ExecutionException e) {
                    // Handle exceptions thrown by the async tasks themselves.
                    // The cause of ExecutionException is the original exception.
                    Throwable cause = e.getCause();
                    // Log the underlying cause for debugging
                    System.err.println("Async task failed: " + cause.getMessage());
                    // Wrap in a custom runtime exception.
                    throw new AnalysisException("Error during number analysis for " + number, cause);
                }
            });
    }

    /**
     * Checks if a number is even using the modulo operator.
     * Includes a simulated delay.
     * @param n The number to check.
     * @return true if n is even, false otherwise.
     */
    private boolean isEven(long n) {
        simulateWork(DELAY_EVEN_CHECK);
        return n % 2 == 0;
    }

    /**
     * Checks if a number is prime using Java Streams for efficiency.
     * Handles edge cases (<= 1, 2) and optimizes by checking only odd divisors up to sqrt(n).
     * Includes a simulated delay.
     * @param n The number to check.
     * @return true if n is prime, false otherwise.
     */
    private boolean isPrime(long n) {
        simulateWork(DELAY_PRIME_CHECK);
        if (n <= 1) return false; // Numbers less than or equal to 1 are not prime
        if (n == 2) return true;  // 2 is the only even prime number
        if (n % 2 == 0) return false; // Other even numbers are not prime

        // Calculate the limit for checking divisors (square root of n)
        long limit = (long) Math.sqrt(n);

        // Use a LongStream to check for divisibility by odd numbers from 3 up to the limit.
        // LongStream.rangeClosed includes both start and end values.
        // We step by 2 (i -> i + 2) implicitly by filtering i % 2 != 0, but an explicit step
        // using iterate could also be used: LongStream.iterate(3, i -> i <= limit, i -> i + 2)
        return LongStream.rangeClosed(3, limit)
                         .filter(i -> i % 2 != 0) // Consider only odd divisors
                         .noneMatch(divisor -> n % divisor == 0); // Returns true if NO divisor is found
    }

    /**
     * Checks if a number is a perfect square.
     * Calculates the integer part of the square root and checks if squaring it yields the original number.
     * Includes a simulated delay.
     * @param n The number to check. Must be non-negative.
     * @return true if n is a perfect square, false otherwise.
     */
    private boolean isPerfectSquare(long n) {
        simulateWork(DELAY_SQUARE_CHECK);
        if (n < 0) return false; // Negative numbers cannot be perfect squares
        if (n == 0 || n == 1) return true; // 0 and 1 are perfect squares

        // Calculate the square root
        long sqrt = (long) Math.sqrt(n);

        // Check if the square of the calculated root equals the original number
        return sqrt * sqrt == n;
    }

    /**
     * Determines the parity ("Even" or "Odd") of a number using a modern Java switch expression (Java 14+).
     * Switch expressions provide a concise way to return a value based on conditions.
     * @param n The number to check.
     * @return The string "Even" if n is even, "Odd" otherwise.
     */
    private String checkParity(long n) {
        // No simulated delay needed here as it's very fast.
        // Using a switch expression based on the result of the modulo operation.
        return switch ((int)(n % 2)) { // Cast to int is safe here for modulo 2 result (0 or 1/-1)
            case 0 -> "Even"; // If remainder is 0
            // The default case handles any non-zero remainder (which means odd)
            default -> "Odd";
        };
        // Note: For negative numbers, n % 2 can be -1 in Java. The default case correctly handles this.
    }

    /**
     * Helper method to simulate work by pausing the current thread.
     * Handles InterruptedException by restoring the interrupt status.
     * @param durationMillis The duration to sleep in milliseconds.
     */
    private void simulateWork(long durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            // Restore the interrupted status flag on the thread
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted during simulated work.");
        }
    }

    /**
     * Custom runtime exception specific to errors occurring during number analysis.
     * This allows for more specific exception handling in the controller layer.
     */
    public static class AnalysisException extends RuntimeException {
        /**
         * Constructs a new AnalysisException with the specified detail message and cause.
         * @param message The detail message.
         * @param cause The cause (which is saved for later retrieval by the getCause() method).
         */
        public AnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
