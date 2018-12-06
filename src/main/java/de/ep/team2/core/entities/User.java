package de.ep.team2.core.entities;

/**
 * Class which represents a User of the Database table 'users'.
 */
public class User {
    private long id;
    private String email, firstName, lastName, password;

    public User(long id,  String email, String firstName, String lastName, String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public User(){}

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, email='%s', firstName='%s', lastName='%s']",
                id, email, firstName, lastName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {this.password = password; }
}
