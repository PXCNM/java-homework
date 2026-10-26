package com.homework;

import com.homework.sort.Bubblesort;
import com.homework.sort.Quicksort;
import com.homework.sort.SortAlgorithm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[\\w+-]+(\\.[\\w+-]+)*@([A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,}"
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<User> users = new ArrayList<>();
        Map<String, SortAlgorithm<User>> algorithms = Map.of(
            "quicksort", new Quicksort<>(),
            "bubblesort", new Bubblesort<>()
        );

        while (true) {
            printMenu();

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            switch (parts[0]) {
                case "add" -> add(parts, users);
                case "print" -> print(users);
                case "filter" -> filter(parts, users);
                case "map" -> map(parts, users);
                case "sort" -> sort(parts, users, algorithms);
                default -> System.out.println("Unknown command");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("add <id> <name> <email>");
        System.out.println("add <file-path>");
        System.out.println("print");
        System.out.println("filter <text>");
        System.out.println("map <name|email>");
        System.out.println("sort <id|name|email> [asc|desc] [quick|bubble]");
        System.out.println("Exit");
        System.out.print("> ");
    }

    private static void add(String[] parts, List<User> users) {
        if (parts.length == 4) {
            addUser(users, parts[1] + " " + parts[2] + " " + parts[3]);
        } else if (parts.length == 2) {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(parts[1]))) {
                String line;
                while ( (line = reader.readLine()) != null ) {
                    addUser(users, line);
                }
            } catch (IOException | InvalidPathException e) {
                System.out.println("Error reading file");
            }
        } else {
            System.out.println("Incorrect arguments");
        }
    }

    private static void print(List<User> users) {
        users.forEach(System.out::println);
    }

    private static void addUser(List<User> users, String line) {
        User user = buildFromString(line);
        if (user == null) {
            return;
        }
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            System.out.println("User with id " + user.getId() + "already exists");
            return;
        }
        users.add(user);
    }

    private static void filter(String[] parts, List<User> users) {
        if (parts.length != 2) {
            System.out.println("Incorrect arguments");
            return;
        }
        String filter = parts[1].toLowerCase();
        users.stream()
            .filter(u -> u.getName().toLowerCase().contains(filter) || u.getEmail().toLowerCase().contains(filter))
            .forEach(System.out::println);
    }

    private static void map(String[] parts, List<User> users) {
        if (parts.length != 2) {
            System.out.println("Incorrect arguments");
            return;
        }
        switch (parts[1]) {
            case "name" -> users.stream().map(User::getName).forEach(System.out::println);
            case "email" -> users.stream().map(User::getEmail).forEach(System.out::println);
            default -> System.out.println("Unknown field: " + parts[1]);
        }
    }

    private static void sort(String[] parts, List<User> users, Map<String, SortAlgorithm<User>> algorithms) {
        if (parts.length < 2 || parts.length > 4) {
            System.out.println("Incorrect arguments");
            return;
        }

        Comparator<User> comparator = switch (parts[1]) {
            case "id" -> Comparator.comparing(User::getId);
            case "name" -> Comparator.comparing(User::getName);
            case "email" -> Comparator.comparing(User::getEmail);
            default -> null;
        };
        if (comparator == null) {
            System.out.println("Unknown sort field: " + parts[1]);
            return;
        }

        SortAlgorithm<User> algorithm = null;
        boolean descending = false;
        boolean directionSet = false;

        for (int i = 2; i < parts.length; i++) {
            String token = parts[i].toLowerCase();

            if (token.equals("asc") || token.equals("desc")) {
                if (directionSet) {
                    System.out.println("Sort direction specified more than once");
                    return;
                }
                directionSet = true;
                descending = token.equals("desc");
            } else if (algorithms.containsKey(token)) {
                if (algorithm != null) {
                    System.out.println("Sort algorithm specified more than once");
                    return;
                }
                algorithm = algorithms.get(token);
            } else {
                System.out.println("Unknown sort or algorithm option: " + parts[i]);
                return;
            }
        }

        if (algorithm == null) {
            algorithm = algorithms.get("quicksort");
        }
        if (descending) {
            comparator = comparator.reversed();
        }

        algorithm.sort(users, comparator).forEach(System.out::println);
    }

    private static User buildFromString(String userString) {
        String[] parts = userString.trim().split("\\s+");
        if (parts.length != 3) {
            System.out.println("User string should contain 3 params");
            return null;
        }

        long id;
        try {
            id = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect id value");
            return null;
        }
        if (id <= 0) {
            System.out.println("Id must be positive");
            return null;
        }
        if (!EMAIL_PATTERN.matcher(parts[2]).matches()) {
            System.out.println("Incorrect email value");
            return null;
        }

        return new User(id, parts[1], parts[2]);
    }

}
