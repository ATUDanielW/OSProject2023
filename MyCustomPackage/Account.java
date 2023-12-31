package MyCustomPackage;

public class Account {
    private final String userId;
    private final String[] userDetails; // Other details: address, email, name, etc.
    private double currentBalance;

    /**
     * Constructs an Account object with the specified user ID, user details, and initial balance.
     *
     * @param userId         the unique identifier for the user
     * @param userDetails    an array containing additional user details (e.g., name, email, password)
     * @param initialBalance the initial balance of the account
     */
    public Account(String userId, String[] userDetails, double initialBalance) {
        this.userId = userId;
        this.userDetails = userDetails.clone(); // Defensive copy
        this.currentBalance = initialBalance;
    }

    /**
     * Gets the user ID associated with the account.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets a defensive copy of the array containing additional user details.
     *
     * @return a defensive copy of the user details array
     */
    public String[] getUserDetails() {
        return userDetails.clone(); // Defensive copy
    }

    /**
     * Gets the current balance of the account.
     *
     * @return the current balance
     */
    public double getCurrentBalance() {
        return currentBalance;
    }

    // No setter for currentBalance to make the class immutable

    // Other methods as needed...
}
