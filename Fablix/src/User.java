/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String email;
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String ccId;
    private final String address;

    public User(String id, String email, String firstName, String lastName, String ccId, String address) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
    }
    public String getUsername() {
        return this.firstName + " " + this.lastName;
    }
    public String getId() {
        return this.id;
    }
    public String getCcId() {
        return this.ccId;
    }
    public String getEmail() {
        return this.email;
    }
    public String getAddress() {
        return this.address;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
}
