package com.yashi.passwordmanager.ui;

import com.yashi.passwordmanager.model.Credential;
import com.yashi.passwordmanager.model.User;
import com.yashi.passwordmanager.service.AuthService;
import com.yashi.passwordmanager.service.CredentialService;
import com.yashi.passwordmanager.util.AppException;

import java.util.List;
import java.util.Scanner;

/**
 * Simple text-based menu that drives the app. Kept deliberately separate
 * from the service layer so the same services could later be reused by a
 * Swing GUI or a REST controller without any changes.
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService = new AuthService();
    private final CredentialService credentialService = new CredentialService();
    private User currentUser;

    public void start() {
        System.out.println("=========================================");
        System.out.println("   SECURE PASSWORD MANAGER");
        System.out.println("=========================================");

        while (currentUser == null) {
            System.out.println("\n1. Login\n2. Register\n3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }

        mainMenu();
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Master password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(username, password);
            if (user == null) {
                System.out.println("Invalid username or password.");
            } else {
                currentUser = user;
                System.out.println("Welcome back, " + user.getUsername() + "!");
            }
        } catch (AppException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a master password (min 6 chars): ");
        String password = scanner.nextLine();

        try {
            User user = authService.register(username, password);
            System.out.println("Account created for '" + user.getUsername() + "'. Please log in.");
        } catch (AppException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n----------- MAIN MENU -----------");
            System.out.println("1. Add new credential");
            System.out.println("2. View all my credentials");
            System.out.println("3. Search by site name");
            System.out.println("4. Update a credential");
            System.out.println("5. Delete a credential");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        addCredential();
                        break;
                    case "2":
                        viewAll();
                        break;
                    case "3":
                        searchCredentials();
                        break;
                    case "4":
                        updateCredential();
                        break;
                    case "5":
                        deleteCredential();
                        break;
                    case "6":
                        currentUser = null;
                        running = false;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (AppException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        // Return to login/register screen after logout
        start();
    }

    private void addCredential() throws AppException {
        System.out.print("Site name (e.g. gmail.com): ");
        String site = scanner.nextLine().trim();
        System.out.print("Username/email for that site: ");
        String siteUser = scanner.nextLine().trim();
        System.out.print("Password for that site: ");
        String sitePass = scanner.nextLine();
        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine();

        String strength = credentialService.checkStrength(sitePass);
        System.out.println("Password strength: " + strength);

        Credential c = credentialService.saveCredential(currentUser.getUserId(), site, siteUser, sitePass, notes);
        System.out.println("Saved! (id=" + c.getCredentialId() + ")");
    }

    private void viewAll() throws AppException {
        List<Credential> list = credentialService.listCredentials(currentUser.getUserId());
        printCredentials(list);
    }

    private void searchCredentials() throws AppException {
        System.out.print("Search keyword: ");
        String keyword = scanner.nextLine().trim();
        List<Credential> list = credentialService.search(currentUser.getUserId(), keyword);
        printCredentials(list);
    }

    private void printCredentials(List<Credential> list) {
        if (list.isEmpty()) {
            System.out.println("No credentials found.");
            return;
        }
        System.out.println("\nID   SITE                 SITE-USERNAME        PASSWORD");
        System.out.println("------------------------------------------------------------");
        for (Credential c : list) {
            String revealed = credentialService.revealPassword(c);
            System.out.printf("%-4d %-20s %-20s %s%n",
                    c.getCredentialId(), c.getSiteName(), c.getSiteUsername(), revealed);
        }
    }

    private void updateCredential() throws AppException {
        System.out.print("Credential ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("New site name: ");
        String site = scanner.nextLine().trim();
        System.out.print("New site username: ");
        String siteUser = scanner.nextLine().trim();
        System.out.print("New site password: ");
        String sitePass = scanner.nextLine();
        System.out.print("New notes: ");
        String notes = scanner.nextLine();

        credentialService.updateCredential(currentUser.getUserId(), id, site, siteUser, sitePass, notes);
        System.out.println("Credential updated.");
    }

    private void deleteCredential() throws AppException {
        System.out.print("Credential ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        credentialService.deleteCredential(currentUser.getUserId(), id);
        System.out.println("Credential deleted.");
    }
}
