# Java Number Properties Analyzer

## Description

This project is a simple web application that analyzes a given non-negative integer and determines several of its mathematical properties:

* Parity (Even/Odd)
* Prime Status
* Perfect Square Status

It serves as a demonstration of building a full-stack application using:

* **Backend:** Java 17+ with Spring Boot 3.x (specifically `spring-boot-starter-web`)
* **Frontend:** HTML, Tailwind CSS, and vanilla JavaScript
* **Build Tool:** Apache Maven

The Java backend showcases several modern Java features and best practices:

* **Spring Boot:** For rapid setup, REST controller (`@RestController`), service layer (`@Service`), and dependency injection.
* **Java Records:** For concise Data Transfer Objects (`NumberProperties`).
* **`CompletableFuture`:** To demonstrate handling potentially asynchronous operations (simulated with `Thread.sleep`).
* **Java Streams API:** Used in the prime number checking logic (`LongStream`).
* **Modern `switch` Expressions:** For determining parity.
* **Asynchronous API Endpoint:** The controller returns `CompletableFuture<ResponseEntity<?>>`.
* **Exception Handling:** Custom exception (`AnalysisException`) and handling in the controller (`exceptionally` block).
* **Clear Separation of Concerns:** Controller -> Service structure.

## Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK):** Version 17 or later.
* **Apache Maven:** Version 3.6 or later (for building the project).
* **Web Browser:** Any modern browser (Chrome, Firefox, Edge, Safari) to access the frontend.

## Building the Application

1.  **Clone the repository or download the source code.**
2.  **Navigate to the project's root directory** (where the `pom.xml` file is located) in your terminal or command prompt.
3.  **Run the Maven build command:**

    ```bash
    mvn clean package
    ```

    This command will compile the code, run tests (if any), and package the application into an executable JAR file located in the `target/` directory (e.g., `target/number-analyzer-0.0.1-SNAPSHOT.jar`).

## Running the Application

Once the application is built, you can run it using the following command:

```bash
java -jar target/number-analyzer-0.0.1-SNAPSHOT.jar
(Replace number-analyzer-0.0.1-SNAPSHOT.jar with the actual name of the generated JAR file if it differs.)The Spring Boot application will start, typically on port 8080. You should see console output indicating that the application has started successfully.Using the ApplicationFrontend: Open your web browser and navigate to:http://localhost:8080Enter a non-negative integer into the input field and click "Analyze Number". The results will be displayed below the button.API Endpoint: You can also interact with the backend API directly. The primary endpoint is:GET /api/number/analyze/{number}Replace {number} with the integer you want to analyze. For example:http://localhost:8080/api/number/analyze/42This will return a JSON response containing the analysis results, like:{
    "number": 42,
    "isEven": true,
    "isPrime": false,
    "isPerfectSquare": false,
    "parity": "Even"
}
Error responses (e.g., for invalid input or server errors) will also be returned in JSON format with appropriate HTTP status codes.Project Structure.
├── pom.xml                   # Maven build configuration
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── numberanalyzer
│   │   │               ├── NumberAnalyzerApplication.java  # Main Spring Boot application class
│   │   │               ├── NumberProperties.java         # Record for results DTO
│   │   │               ├── NumberAnalysisService.java    # Service layer (business logic)
│   │   │               └── NumberController.java         # REST controller (API endpoints)
│   │   └── resources
│   │       ├── static
│   │       │   └── index.html        # Frontend HTML/CSS/JS
│   │       └── application.properties # Spring Boot configuration (optional)
│   └── test                     # Unit/integration tests (if added)
└── target                       # Build output (JAR file)
Showcased Java Features SummarySpring Boot (@SpringBootApplication, @RestController, @Service, @Autowired)RESTful Web Services (@GetMapping, @PathVariable, ResponseEntity)Java Records (`record
