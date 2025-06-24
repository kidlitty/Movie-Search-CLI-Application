# Movie Search CLI Application

A modern, interactive command-line application for searching movie information, built with Java 21.

<div>
  <img src="https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Maven-3.9-red?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Guice-7.0-brightgreen?logo=google&logoColor=white" alt="Google Guice">
  <img src="https://img.shields.io/badge/Jackson-2.17-orange" alt="Jackson">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License: MIT">
</div>

## Description

This project is a sophisticated, real-world command-line tool for movie enthusiasts. It leverages the power of The Movie Database (TMDB) API to provide a fast and intuitive way to search for movies by name, category, genre, and year. The application is designed with a modern, layered architecture to showcase best practices in software engineering, including dependency injection, separation of concerns, and robust error handling.

## Key Features

- **Interactive Menu:** A user-friendly, menu-driven interface for easy navigation.
- **Multi-Faceted Search:**
    - Search movies by **Title**.
    - Discover movies by **Genre**.
    - Filter movies by **Year**.
- **Categorized Lists:** Browse pre-defined lists of movies:
    - Popular
    - Top Rated
    - Now Playing
    - Upcoming
- **Interactive Pagination:** Easily navigate through pages of search results (`next`, `prev`).
- **Formatted Output:** Clean, readable display of movie details with smart text-wrapping for overviews.

## Tech Stack & Core Concepts Demonstrated

- **Language:** **Java 21** <img src="https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white" alt="Java 21" style="height: 20px;">
    - **Virtual Threads (Project Loom):** For efficient, non-blocking I/O when making API calls.
    - **Records:** For creating concise, immutable Data Transfer Objects (DTOs).
    - **Modern Switch Expressions:** For safe and readable control flow in the CLI menu.
- **Architecture & Design Patterns:**
    - **Layered Architecture:** Strict separation between Presentation, Service, and Data layers.
    - **Dependency Injection:** Using **Google Guice** for a clean, decoupled, and testable codebase. <img src="https://img.shields.io/badge/Guice-7.0-brightgreen?logo=google&logoColor=white" alt="Google Guice" style="height: 20px;">
    - **Separation of Concerns (SoC):** Each component has a single, well-defined responsibility.
- **Build & Dependency Management:** **Apache Maven** <img src="https://img.shields.io/badge/Maven-3.9-red?logo=apachemaven&logoColor=white" alt="Maven" style="height: 20px;">
- **API & Data Handling:**
    - **REST API Consumption:** Interacting with the external TMDB API.
    - **JSON Parsing:** Using the **Jackson** library for deserializing API responses into Java objects. <img src="https://img.shields.io/badge/Jackson-2.17-orange" alt="Jackson" style="height: 20px;">
- **Security:** Externalized API key management using a `.properties` file, excluded from version control via `.gitignore`.

## Getting Started

### Prerequisites

- JDK 21 or later
- Apache Maven
- A valid v3 API Key from [The Movie Database (TMDB)](https://www.themoviedb.org/signup).

### Installation & Configuration

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/movie-cli.git](https://github.com/your-username/movie-cli.git)
    cd movie-cli
    ```

2.  **Create the configuration file:**
    In the `src/main/resources` directory, create a new file named `config.properties`.

3.  **Add your API Key:**
    Open the `config.properties` file and add the following line, replacing `YOUR_KEY_HERE` with your actual v3 API Key from TMDB:
    ```properties
    tmdb.api.key=YOUR_KEY_HERE
    ```

4.  **Build the project:**
    From the root directory of the project, run the Maven `package` command.
    ```bash
    mvn clean package
    ```

5.  **Run the application:**
    ```bash
    java -jar target/movie-cli-1.0-SNAPSHOT.jar
    ```

## Future Improvements

- **Unit Testing:** Implement a full suite of unit tests for the Service layer using JUnit and Mockito.
- **Caching:** Introduce an in-memory cache for the genre list to improve performance.
- **Enhanced Search:** Add more complex filtering options available in the TMDB API.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

https://roadmap.sh/projects/tmdb-cli

_This project uses the TMDB API but is not endorsed or certified by TMDB._
