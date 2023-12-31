package MyCustomPackage;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BankingAppClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 4000;

    public static void main(String[] args) {
        boolean keepSession = true; // Flag to control the session

        try (Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
             Scanner inputScanner = new Scanner(System.in)) {

            boolean loggedIn = false;
            String userIdentifier = ""; // Store logged-in user identifier

            while (keepSession) {
                if (!loggedIn) {
                    // User not logged in
                    System.out.println("Welcome to BankingApp! Choose an option:");
                    System.out.println("1. Create a new account");
                    System.out.println("2. Login");

                    int userChoice = inputScanner.nextInt();
                    inputScanner.nextLine(); // Consume newline

                    if (userChoice == 1) {
                        // Registration
                        System.out.print("Enter Social Security Number: ");
                        String id = inputScanner.nextLine();
                        // ... (Other registration details)

                        // Send registration data to server
                        outputStream.writeObject("REGISTER");
                        outputStream.writeObject(id);
                        // ... (Send other registration details)

                        String serverResponse = (String) inputStream.readObject();
                        System.out.println("Server: " + serverResponse);
                    } else if (userChoice == 2) {
                        // Login
                        System.out.print("Enter Social Security Number: ");
                        String id = inputScanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = inputScanner.nextLine();

                        // Send login data to server
                        outputStream.writeObject("LOGIN");
                        outputStream.writeObject(id);
                        outputStream.writeObject(password);

                        String serverResponse = (String) inputStream.readObject();
                        if (serverResponse.equals("LOGIN_SUCCESS")) {
                            loggedIn = true;
                            System.out.println("Login successful!");
                            userIdentifier = id;
                        } else {
                            System.out.println("Login failed. Try again.");
                        }
                    }
                } else {
                    // User logged in
                    System.out.println("Logged in! Choose an option:");
                    System.out.println("1. Deposit money to your account");
                    System.out.println("2. List all registered users");
                    System.out.println("3. Transfer money to another account");
                    System.out.println("4. View all transactions");
                    System.out.println("5. Change your password");
                    System.out.println("0. Log out");

                    int userChoice = inputScanner.nextInt();
                    inputScanner.nextLine(); // Consume newline

                    switch (userChoice) {
                        case 1:
                            // Deposit money
                            System.out.print("Enter the amount you want to deposit: ");
                            double amountToDeposit = inputScanner.nextDouble();
                            inputScanner.nextLine(); // Consume newline

                            // Send deposit details to the server
                            outputStream.writeObject("DEPOSIT");
                            outputStream.writeObject(userIdentifier); // Send user identifier
                            outputStream.writeObject(String.valueOf(amountToDeposit));

                            // Receive the updated balance from the server
                            String depositResponse = (String) inputStream.readObject();
                            if (depositResponse.equals("UPDATED_BALANCE")) {
                                double updatedBalance = inputStream.readDouble();
                                System.out.println("Updated Balance: " + updatedBalance);
                            }

                            // Receive and process server response for deposit success
                            String depositSuccessResponse = (String) inputStream.readObject();
                            System.out.println("Server: " + depositSuccessResponse);
                            break;
                        case 2:
                            // List all registered users
                            outputStream.writeObject("LIST_USERS");

                            String usersListResponse = (String) inputStream.readObject();
                            if (usersListResponse.equals("USERS_LIST")) {
                                int numUsers = (int) inputStream.readObject(); // Reading the number of users
                                System.out.println("Registered Users:");

                                for (int i = 0; i < numUsers; i++) {
                                    String retrievedUserId = (String) inputStream.readObject(); // Reading user identifier
                                    String fullName = (String) inputStream.readObject(); // Reading user details
                                    String emailAddress = (String) inputStream.readObject();
                                    String userAddress = (String) inputStream.readObject();
                                    String accountBalance = (String) inputStream.readObject();

                                    // Display user details
                                    System.out.println("User ID: " + retrievedUserId);
                                    // ... (Display other user details)
                                    System.out.println("------------------------");
                                }
                            } else {
                                System.out.println("Failed to retrieve user listing.");
                            }
                            break;
                        // ... (Cases for other user choices)
                        case 0:
                            // Log out
                            System.out.println("Logging out...");
                            outputStream.writeObject("LOGOUT"); // Inform server about logout
                            loggedIn = false; // Reset logged-in state
                            keepSession = false; // Exit the loop
                            break;
                        default:
                            System.out.println("Invalid choice. Please choose a valid option.");
                            break;
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
