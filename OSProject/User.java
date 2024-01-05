package OSProject;

public class User {
    // Fields to store user information
    private String name;
    private int id;
    private String email;
    private String password;
    private String address;
    private double balance;

    // Constructor to initialize user object with provided information
    public User(String name, int id, String email, String password, String address, double balance) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.address = address;
        this.balance = balance;
    }

    // Getter methods to access the private fields

    /**
     * Get the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the ID of the user.
     *
     * @return The user's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the email of the user.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the address of the user.
     *
     * @return The user's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the balance of the user.
     *
     * @return The user's balance.
     */
    public double getBalance() {
        return balance;
    }

    // Setter methods can be added if needed

    // toString method to provide a string representation of the user object

    /**
     * Convert the user object to a string for easy representation.
     *
     * @return A string representation of the user object.
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", balance=" + balance +
                '}';
    }
}
