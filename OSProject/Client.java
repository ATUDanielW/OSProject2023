package OSProject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "192.168.0.1";
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        boolean continueSession = true; // Flag to control the main loop

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            boolean loggedIn = false; // Flag to check if the user is logged in
            String userId = ""; // Variable to store the user ID after login

            while (continueSession) {
                if (!loggedIn) {
                    // User not logged in
                    System.out.println("Welcome! Choose an option:");
                    System.out.println("1. Register an account");
                    System.out.println("2. Login");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    if (choice == 1) {
                        // User Registration
                        System.out.print("Enter PPSN: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter Address: ");
                        String address = scanner.nextLine();
                        System.out.print("Enter Balance: ");
                        String balance = scanner.nextLine();

                        // Send registration details to the server
                        out.writeObject("REGISTER");
                        out.writeObject(id);
                        out.writeObject(name);
                        out.writeObject(email);
                        out.writeObject(password);
                        out.writeObject(address);
                        out.writeObject(balance);

                        // Receive and display the server's response
                        String response = (String) in.readObject();
                        System.out.println("Server: " + response);
                    } else if (choice == 2) {
                        // User Login
                        System.out.print("Enter PPSN: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();

                        // Send login details to the server
                        out.writeObject("LOGIN");
                        out.writeObject(id);
                        out.writeObject(password);

                        // Receive and process the server's response
                        String response = (String) in.readObject();
                        if (response.equals("LOGIN_SUCCESS")) {
                            loggedIn = true;
                            System.out.println("Login successful!");
                            userId = id;
                        } else {
                            System.out.println("Login failed. Try again.");
                        }
                    }
                } else {
                    // User logged in
                    System.out.println("Logged in! Options:");
                    System.out.println("1. Lodge money to the user account.");
                    System.out.println("2. Retrieve all registered users listing.");
                    System.out.println("3. Transfer money to another account.");
                    System.out.println("4. View all transactions on your bank account.");
                    System.out.println("5. Update your password.");
                    System.out.println("0. Log out.");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    switch (choice) {
                        case 1:
                            // Lodging Money
                            System.out.print("Enter the amount you want to lodge: ");
                            double amountToLodge = scanner.nextDouble();
                            scanner.nextLine(); // Consume the newline character

                            // Send lodgment details to the server
                            out.writeObject("LODGE");
                            out.writeObject(userId);
                            out.writeObject(String.valueOf(amountToLodge));

                            // Receive and process the server's response
                            String lodgeResponse = (String) in.readObject();
                            if (lodgeResponse.equals("UPDATED_BALANCE")) {
                                double updatedBalance = in.readDouble();
                                System.out.println("Updated Balance: " + updatedBalance);
                            }

                            String lodgeSuccessResponse = (String) in.readObject();
                            System.out.println("Server: " + lodgeSuccessResponse);
                            break;
                        case 2:
                            // Retrieve Users Listing
                            out.writeObject("RETRIEVE_USERS");

                            // Receive and process the server's response
                            String usersListResponse = (String) in.readObject();
                            if (usersListResponse.equals("USERS_LIST")) {
                                int numUsers = (int) in.readObject(); // Number of registered users
                                System.out.println("Registered Users:");

                                for (int i = 0; i < numUsers; i++) {
                                    String retrievedUserId = (String) in.readObject(); // User ID
                                    String name = (String) in.readObject(); // User Name
                                    String email = (String) in.readObject(); // User Email
                                    String password = (String) in.readObject(); // User Password
                                    String address = (String) in.readObject(); // User Address
                                    String balance = (String) in.readObject(); // User Balance

                                    // Display user information
                                    System.out.println("User ID: " + retrievedUserId);
                                    System.out.println("Name: " + name);
                                    System.out.println("Email: " + email);
                                    System.out.println("Address: " + address);
                                    System.out.println("Balance: " + balance);
                                    System.out.println("------------------------");
                                }
                            } else {
                                System.out.println("Failed to retrieve user listing.");
                            }
                            break;
                        case 3:
                            // Transfer Money
                            System.out.print("Enter recipient's email or ID: ");
                            String recipient = scanner.nextLine();
                            System.out.print("Enter the amount to transfer: ");
                            double amountToTransfer = scanner.nextDouble();
                            scanner.nextLine(); // Consume the newline character

                            // Send transfer details to the server
                            out.writeObject("TRANSFER");
                            out.writeObject(userId);
                            out.writeObject(recipient);
                            out.writeObject(String.valueOf(amountToTransfer));

                            // Receive and display the server's response
                            String transferResponse = (String) in.readObject();
                            System.out.println("Server: " + transferResponse);
                            break;
                        case 4:
                            // View Transactions
                            out.writeObject("VIEW_TRANSACTIONS");

                            // Receive and process the server's response
                            String transactionResponse = (String) in.readObject();
                            if (transactionResponse.equals("TRANSACTIONS_FOUND")) {
                                int numTransactions = (int) in.readObject(); // Number of transactions
                                System.out.println("Transactions:");

                                for (int i = 0; i < numTransactions; i++) {
                                    String date = (String) in.readObject(); // Transaction Date
                                    double amount = (double) in.readObject(); // Transaction Amount
                                    String type = (String) in.readObject(); // Transaction Type
                                    String senderId = (String) in.readObject(); // Sender ID
                                    String recipientId = (String) in.readObject(); // Recipient ID

                                    // Display transaction details
                                    System.out.println("Date: " + date);
                                    System.out.println("Amount: " + amount);
                                    System.out.println("Type: " + type);
                                    System.out.println("Sender: " + senderId);
                                    System.out.println("Recipient: " + recipientId);
                                    System.out.println("------------------------");
                                }
                            } else {
                                System.out.println("No transactions found.");
                            }
                            break;
                        case 5:
                            // Update Password
                            System.out.print("Enter new password: ");
                            String newPassword = scanner.nextLine();
                            System.out.print("Confirm new password: ");
                            String confirmNewPassword = scanner.nextLine();

                            // Send password update details to the server
                            out.writeObject("UPDATE_PASSWORD");
                            out.writeObject(newPassword);
                            out.writeObject(confirmNewPassword);

                            // Receive and process the server's response
                            String updateResponse = (String) in.readObject();
                            if (updateResponse.equals("PASSWORD_UPDATED")) {
                                System.out.println("Password updated successfully!");
                            } else if (updateResponse.equals("PASSWORD_MISMATCH")) {
                                System.out.println("Passwords don't match. Please try again.");
                            }
                            break;
                        case 0:
                            // Logout
                            System.out.println("Logged out...");
                            out.writeObject("LOGOUT"); // Notify the server about the logout
                            loggedIn = false; // Update the login status
                            continueSession = false; // Exit the main loop
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
