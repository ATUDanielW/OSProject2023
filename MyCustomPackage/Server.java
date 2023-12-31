package MyCustomPackage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Transaction {
    String date;
    double amount;
    String type; // "LODGE" or "TRANSFER"
    String senderId;
    String recipientId;
}

public class Server {
    private static final int SERVER_PORT = 4000;
    private static HashMap<String, String[]> accounts = new HashMap<>();
    private static HashSet<String> emailSet = new HashSet<>();
    private static HashSet<String> idSet = new HashSet<>();
    private static HashMap<Socket, String> loggedInUsers = new HashMap<>();
    private static HashMap<String, ArrayList<Transaction>> accountTransactions = new HashMap<>();
    private static final String DATABASE_FILE = "MyCustomPackage/Database.txt";

    public static void main(String[] args) {
        loadAccounts(); // Load user accounts from a file
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started. Listening on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming connections
                System.out.println("Client connected: " + clientSocket);

                // Handle each client in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            while (true) {
                String option = (String) in.readObject(); // Read client option

                switch (option) {
                    case "REGISTER":
                        handleRegistration(in, out);
                        break;
                    case "LOGIN":
                        handleLogin(clientSocket, in, out);
                        break;
                    case "LODGE":
                        handleLodgment(in, out);
                        break;
                    case "UPDATE_PASSWORD":
                        handlePasswordUpdate(in, out);
                        break;
                    case "RETRIEVE_USERS":
                        retrieveUsers(out);
                        break;
                    case "TRANSFER":
                        handleTransfer(in, out);
                        break;
                    case "VIEW_TRANSACTIONS":
                        viewTransactions(in, out);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeResources(clientSocket);
        }
    }

    private static void handleRegistration(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Handle user registration
        String id = (String) in.readObject();
        String name = (String) in.readObject();
        String email = (String) in.readObject();
        String password = (String) in.readObject();
        String address = (String) in.readObject();
        String balance = (String) in.readObject();

        if (idSet.contains(id)) {
            out.writeObject("DUPLICATE_ID");
        } else if (checkDuplicateEmail(email)) {
            out.writeObject("DUPLICATE_EMAIL");
        } else {
            idSet.add(id);
            emailSet.add(email);
            String[] userDetails = {name, email, password, address, balance};
            accounts.put(id, userDetails);
            out.writeObject("Registration successful!");
            saveAccounts();
        }
    }

    private static void handleLogin(Socket clientSocket, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Handle user login
        String id = (String) in.readObject();
        String password = (String) in.readObject();

        if (accounts.containsKey(id) && accounts.get(id)[2].equals(password)) {
            loggedInUsers.put(clientSocket, id);
            out.writeObject("LOGIN_SUCCESS");
        } else {
            out.writeObject("LOGIN_FAILURE");
        }
    }

    private static void handleLodgment(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Handle user lodgment
        String userId = (String) in.readObject();
        double amount = Double.parseDouble((String) in.readObject());

        if (accounts.containsKey(userId)) {
            String[] userDetails = accounts.get(userId);
            double currentBalance = Double.parseDouble(userDetails[userDetails.length - 1]);
            double newBalance = currentBalance + amount;
            userDetails[userDetails.length - 1] = String.valueOf(newBalance);
            accounts.put(userId, userDetails);

            // Send the updated balance back to the client
            out.writeObject("UPDATED_BALANCE");
            out.writeDouble(newBalance);
            out.writeObject("LODGE_SUCCESS");
            saveAccounts(); // Save the updated account details
        } else {
            out.writeObject("USER_NOT_FOUND");
        }
    }

    private static void handlePasswordUpdate(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Handle password update
        String userId = loggedInUsers.get(Thread.currentThread());
        String newPassword = (String) in.readObject();
        String confirmPassword = (String) in.readObject();

        if (newPassword.equals(confirmPassword)) {
            String[] userDetails = accounts.get(userId);
            userDetails[2] = newPassword; // Update password in the account details
            accounts.put(userId, userDetails);
            out.writeObject("PASSWORD_UPDATED");
            saveAccounts(); // Save the updated account details
        } else {
            out.writeObject("PASSWORD_MISMATCH");
        }
    }

    private static void retrieveUsers(ObjectOutputStream out) throws IOException {
        // Retrieve and send user list to the client
        out.writeObject("USERS_LIST");
        out.writeObject(accounts.size()); // Sending the number of users

        for (String userId : accounts.keySet()) {
            String[] userDetails = accounts.get(userId);
            out.writeObject(userId); // Sending user ID
            for (String detail : userDetails) {
                out.writeObject(detail); // Sending user details
            }
        }
    }

    private static void handleTransfer(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Handle fund transfer between users
        String senderId = loggedInUsers.get(Thread.currentThread());
        String recipientDetails = (String) in.readObject(); // Email or ID
        double amountToTransfer = Double.parseDouble((String) in.readObject());

        if (accounts.containsKey(senderId)) {
            String[] senderDetails = accounts.get(senderId);
            double senderBalance = Double.parseDouble(senderDetails[senderDetails.length - 1]);

            // Check if sender has sufficient balance
            if (senderBalance >= amountToTransfer) {
                String recipientId = findRecipient(recipientDetails); // Find recipient ID from email or ID provided
                if (recipientId != null) {
                    // Update sender's balance
                    double newSenderBalance = senderBalance - amountToTransfer;
                    senderDetails[senderDetails.length - 1] = String.valueOf(newSenderBalance);
                    accounts.put(senderId, senderDetails);

                    // Update recipient's balance
                    String[] recipientAccount = accounts.get(recipientId);
                    double recipientBalance = Double.parseDouble(recipientAccount[recipientAccount.length - 1]);
                    double newRecipientBalance = recipientBalance + amountToTransfer;
                    recipientAccount[recipientAccount.length - 1] = String.valueOf(newRecipientBalance);
                    accounts.put(recipientId, recipientAccount);

                    out.writeObject("TRANSFER_SUCCESS");
                    saveAccounts(); // Save the updated account details
                } else {
                    out.writeObject("RECIPIENT_NOT_FOUND");
                }
            } else {
                out.writeObject("INSUFFICIENT_BALANCE");
            }
        } else {
            out.writeObject("SENDER_NOT_FOUND");
        }
    }

    private static void viewTransactions(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Retrieve and send user transactions to the client
        String userId = loggedInUsers.get(Thread.currentThread());
        ArrayList<Transaction> transactions = accountTransactions.get(userId);

        if (transactions != null) {
            out.writeObject("TRANSACTIONS_FOUND");
            out.writeObject(transactions.size()); // Sending the number of transactions

            for (Transaction transaction : transactions) {
                // Send transaction details to the client
                out.writeObject(transaction.date);
                out.writeObject(transaction.amount);
                out.writeObject(transaction.type);
                out.writeObject(transaction.senderId);
                out.writeObject(transaction.recipientId);
            }
        } else {
            out.writeObject("NO_TRANSACTIONS");
        }
    }

    private static String findRecipient(String recipientDetails) {
        // Find recipient ID from email or ID provided
        for (String userId : accounts.keySet()) {
            String[] userDetails = accounts.get(userId);
            String userEmail = userDetails[1]; // Assuming email is at index 1, adjust as needed

            if (userEmail.equals(recipientDetails)) {
                return userId;
            }
        }
        return null;
    }

    private static void closeResources(Socket clientSocket) {
        // Close socket
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkDuplicateEmail(String email) {
        // Check for duplicate email
        return emailSet.contains(email);
    }

    private static void loadAccounts() {
        // Loading user accounts from a saved file
        try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Reading each line of user info
                String[] userInfo = line.split(",");
                String userId = userInfo[0]; // Extracting user ID
                String[] userDetails = new String[userInfo.length - 1]; // Getting user details
                System.arraycopy(userInfo, 1, userDetails, 0, userDetails.length);
                idSet.add(userId);
                emailSet.add(userDetails[1]); // Assuming email is at index 1, adjust as needed
                accounts.put(userId, userDetails); // Putting info into the accounts map
            }
        } catch (IOException e) {
            e.printStackTrace(); // Ouch! Something went wrong while loading accounts
        }
    }

    private static void saveAccounts() {
        // Saving user accounts to a file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (String userId : accounts.keySet()) {
                String[] userDetails = accounts.get(userId); // Fetching user details
                StringBuilder line = new StringBuilder(userId); // Preparing line to save
                for (String detail : userDetails) {
                    line.append(",").append(detail); // Appending user details to the line
                }
                bw.write(line.toString()); // Writing user info to the file
                bw.newLine(); // Moving to the next line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
