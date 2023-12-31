package MyCustomPackage;

/**
 * Represents a user in the banking system.
 */
public class BankUser {
    private final String fullName;
    private final int userId;
    private final String emailAddress;
    private final String userPassword; // Consider using a more secure method (hashing and salting)
    private final String userAddress;
    private final double accountBalance;

    /**
     * Constructs a BankUser object with the specified details.
     *
     * @param fullName      the full name of the user
     * @param userId        the unique identifier for the user
     * @param emailAddress  the email address of the user
     * @param userPassword  the password associated with the user
     * @param userAddress   the address of the user
     * @param accountBalance the initial balance of the user's account
     */
    public BankUser(String fullName, int userId, String emailAddress, String userPassword, String userAddress, double accountBalance) {
        this.fullName = fullName;
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.userPassword = userPassword; // You may want to handle password security here
        this.userAddress = userAddress;
        this.accountBalance = accountBalance;
    }

    /**
     * Gets the full name of the user.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Gets the password associated with the user.
     *
     * @return the user password
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Gets the address of the user.
     *
     * @return the user address
     */
    public String getUserAddress() {
        return userAddress;
    }

    /**
     * Gets the balance of the user's account.
     *
     * @return the account balance
     */
    public double getAccountBalance() {
        return accountBalance;
    }

    // No setter methods to make the class immutable

    /**
     * Provides a string representation of the BankUser object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "BankUser{" +
                "fullName='" + fullName + '\'' +
                ", userId=" + userId +
                ", emailAddress='" + emailAddress + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
