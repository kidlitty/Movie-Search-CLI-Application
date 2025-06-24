package com.moviecliapplication;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.moviecliapplication.config.AppModule;
import com.moviecliapplication.domain.Movie;
import com.moviecliapplication.domain.Genre;
import com.moviecliapplication.domain.Page;
import com.moviecliapplication.service.MovieService;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

public class MovieCliApp {

    private final MovieService movieService;
    private final Scanner scanner;

    @Inject
    public MovieCliApp(MovieService movieService) {
        this.movieService = movieService;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        MovieCliApp app = injector.getInstance(MovieCliApp.class);
        app.run();
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleCategorySearch();
                case "2" -> handleNameSearch();
                case "3" -> handleGenreSearch();
                case "4" -> handleYearSearch();
                case "5", "exit" -> running = false;
                case null, default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Exiting application. Goodbye!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n--- Movie Search CLI ---");
        System.out.println("1. Search movies by Category");
        System.out.println("2. Search movies by Name");
        System.out.println("3. Search movies by Genre");
        System.out.println("4. Search movies by Year");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private void handleNameSearch() {
        System.out.println("Enter movie name to search: ");
        String name = scanner.nextLine();
        Function<Integer, Optional<Page<Movie>>> pageFetcher = (page) -> movieService.searchByName(name, page);
        managePaginatedSession(pageFetcher);
    }

    private void handleYearSearch() {
        System.out.println("Enter year to search: ");
        try {
            int year = Integer.parseInt(scanner.nextLine());
            Function<Integer, Optional<Page<Movie>>> pageFetcher = (page) -> movieService.getMoviesByYear(year, page);
            managePaginatedSession(pageFetcher);
        } catch (NumberFormatException e) {
            System.out.println("Invalid year format. Please enter a year.");
        }
    }

    private void handleGenreSearch() {
        List<Genre> genres = movieService.getGenres();
        if (genres.isEmpty()) {
            System.out.println("Could not fetch genres at this time.");
            return;
        }

        System.out.println("\n--- Available Genres ---");
        for (int i = 0; i < genres.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, genres.get(i).name());
        }

        System.out.print("Select a genre by number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= genres.size()) {
                int genreId = genres.get(choice - 1) .id();
                Function<Integer, Optional<Page<Movie>>> pageFetcher = (page) -> movieService.getMoviesByYear(genreId, page);
                managePaginatedSession(pageFetcher);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number!");
        }

    }

    private void handleCategorySearch() {
        System.out.println("\nSelect a category:");
        System.out.println("1. Popular");
        System.out.println("2. Top Rated");
        System.out.println("3. Now Playing");
        System.out.println("4. Upcoming");
        System.out.println("Enter your choice: ");
        String choice = scanner.nextLine().trim();

        Function<Integer, Optional<Page<Movie>>> pageFetcher;

        switch (choice) {
            case "1" -> pageFetcher = movieService::getPopularMovies;
            case "2" -> pageFetcher = movieService::getTopRatedMovies;
            case "3" -> pageFetcher = movieService::getNowPlayingMovies;
            case "4" -> pageFetcher = movieService::getUpcomingMovies;
            default -> {
                System.out.println("Invalid category choice.");
                return;
            }
        }
        managePaginatedSession(pageFetcher);
    }

    private void displayMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            System.out.println("No movies found matching your criteria.");
            return;
        }

        System.out.println("\n--- Search Results (10 items per page) ---");

        movies.stream()
                .limit(10)
                .forEach(movie -> {
                    System.out.println("--------------------------");
                    System.out.printf("Title: %s (%s)\n", movie.title(), movie.releaseDate());
                    System.out.printf("Rating: %.1f/10\n", movie.voteAverage());
                    System.out.printf("Genres: %s\n", String.join(", ", movie.genres()));
                    System.out.printf("\nOverview:\n%s\n", wrapText(movie.overview(), 80));

                });
        System.out.println("--------------------------");
    }

    private String wrapText(String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String [] words = text.split(" ");
        StringBuilder wrappedText = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                wrappedText.append(currentLine).append("\n");
                currentLine = new StringBuilder();
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        wrappedText.append(currentLine);
        return wrappedText.toString();
    }

    private void managePaginatedSession(Function<Integer, Optional<Page<Movie>>> pageFetcher) {
        int currentPageNumber = 1;
        Optional<Page<Movie>> currentPageOpt = pageFetcher.apply(currentPageNumber);

        while (currentPageOpt.isPresent()) {
            Page<Movie> currentPage = currentPageOpt.get();
            displayMovies(currentPage.content());

            System.out.printf("\n--- Page %d of %d ---\n", currentPage.pageNumber(), currentPage.totalPages());
            System.out.print("Enter 'next, 'prev', or 'exit' to return to menu: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "next":
                    if (currentPage.pageNumber() < currentPage.totalPages()) {
                        currentPageNumber++;
                    } else {
                        System.out.println("You are on the last page");
                        continue;
                    }
                    break;
                case "prev":
                    if (currentPage.pageNumber() > 1) {
                        currentPageNumber--;
                    } else {
                        System.out.println("You are on the first page");
                        continue;
                    }
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Invalid command.");
                    continue;
            }
            currentPageOpt = pageFetcher.apply(currentPageNumber);
        }
        if (currentPageOpt.isEmpty()) {
            System.out.println("No movies found for this search.");
        }
    }
}