package com.example.numberanalyzer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller: Handles incoming HTTP requests related to number analysis.
 * Maps HTTP endpoints (URLs) to specific handler methods in this class.
 * Annotated with @RestController, which combines @Controller and @ResponseBody.
 */
@RestController
@RequestMapping("/api/number") // Base URL path for all endpoints defined in this controller
public class NumberController {

    private final NumberAnalysisService analysisService;

    /**
     * Constructor-based dependency injection.
     * Spring Boot automatically finds the NumberAnalysisService bean and injects it here.
     * This is the recommended way to inject dependencies.
     * @param analysisService The service bean responsible for number analysis logic.
     */
    public NumberController(NumberAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Handles GET requests to /api/number/analyze/{number}.
     * Analyzes the number provided as a path variable.
     * Returns the analysis results asynchronously.
     *
     * Example Usage: GET http://localhost:8080/api/number/analyze/16
     *
     * @param number The number extracted from the URL path. Spring automatically converts it to long.
     * If the conversion fails (e.g., non-numeric input), Spring returns a 400 Bad Request by default.
     * @return A CompletableFuture containing a ResponseEntity.
     * - On success: ResponseEntity with HTTP status 200 (OK) and NumberProperties in the body.
     * - On failure (e.g., analysis error, invalid input handled by service): ResponseEntity with an appropriate
     * error status (e.g., 500 Internal Server Error, 400 Bad Request) and an error message body.
     */
    @GetMapping("/analyze/{number}")
    public CompletableFuture<ResponseEntity<?>> analyzeNumber(@PathVariable long number) {
        // Delegate the analysis task to the service layer.
        // This returns a CompletableFuture<NumberProperties>.
        return analysisService.analyzeNumber(number)
            .thenApply(properties -> {
                // This block executes when the CompletableFuture from the service completes successfully.
                // Wrap the NumberProperties result in a ResponseEntity with status 200 OK.
                System.out.println("Successfully analyzed: " + number); // Simple logging
                return ResponseEntity.ok(properties);
            })
            .exceptionally(ex -> {
                // This block executes if the CompletableFuture from the service completes exceptionally.
                // Log the error for debugging purposes.
                System.err.println("Analysis failed for " + number + ": " + ex.getMessage());
                // ex.getCause() often holds the original exception (e.g., IllegalArgumentException, AnalysisException)
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                // Create a structured error response body.
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Analysis failed");
                errorResponse.put("details", cause.getMessage()); // Provide the underlying error message
                errorResponse.put("input", String.valueOf(number));

                // Determine the appropriate HTTP status code based on the exception type.
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500
                if (cause instanceof IllegalArgumentException) {
                    status = HttpStatus.BAD_REQUEST; // 400 for bad input detected by the service
                } else if (cause instanceof NumberAnalysisService.AnalysisException) {
                    // Could potentially map different AnalysisException subtypes to different statuses
                    status = HttpStatus.INTERNAL_SERVER_ERROR; // Treat general analysis errors as 500
                }

                // Return a ResponseEntity with the determined error status and the error map as the body.
                return ResponseEntity.status(status).body(errorResponse);
            });
    }

    /*
     * Note on the "/analyze-safe/{numberStr}" endpoint previously shown:
     * While explicit string parsing and validation in the controller is possible,
     * it's often cleaner to let Spring handle basic type conversion (@PathVariable long number).
     * For more complex validation (e.g., ranges, specific formats), using Bean Validation (@Valid annotation
     * with constraints on a request object) or custom validation logic within the service layer is preferred.
     * The primary `/analyze/{number}` endpoint relies on Spring's default conversion and
     * the service layer's validation for negative numbers.
     */
}
