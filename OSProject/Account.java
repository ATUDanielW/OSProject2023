package OSProject;


public class Account {
    private String userId;
    private String[] userDetails; // Other details: name, email, password, address, etc.
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
        this.userDetails = userDetails;
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
     * Gets an array containing additional user details.
     *
     * @return an array of user details
     */
    public String[] getUserDetails() {
        return userDetails;
    }

    /**
     * Gets the current balance of the account.
     *
     * @return the current balance
     */
    public double getCurrentBalance() {
        return currentBalance;
    }

    /**
     * Sets the current balance of the account to the specified value.
     *
     * @param newBalance the new balance to set
     */
    public void setCurrentBalance(double newBalance) {
        this.currentBalance = newBalance;
    }
}
