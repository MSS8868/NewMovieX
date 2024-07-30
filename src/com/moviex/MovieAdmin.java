package com.moviex;

import java.util.Scanner;

public class MovieAdmin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter movie title (or 'exit' to quit):");
            String title = scanner.nextLine();
            if (title.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println("Enter movie description:");
            String description = scanner.nextLine();

            System.out.println("Enter image URL:");
            String imageUrl = scanner.nextLine();

            boolean success = DatabaseManager.addOrUpdateMovie(title, description, imageUrl);
            if (success) {
                System.out.println("Movie added/updated successfully!");
            } else {
                System.out.println("Failed to add/update movie.");
            }
        }

        scanner.close();
    }
}