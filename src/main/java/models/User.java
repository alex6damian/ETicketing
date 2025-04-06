package models;

import java.util.Objects;

public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    static int userCount;

    public User(String name, String email, String password) {
        this.id = userCount;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Static and non-static blocks
    static
    {
        UserDAO userDAO = new CustomerDAO();
        userCount = userDAO.getUserCount();
    }

    {
        userCount++;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getId(){
        return id;
    }

    // Abstract methods
    public abstract void displayInfo();

    public abstract String getUserType();


    // HashCode and Equals
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password);
    }
}

// clasa utilitara = toate metodele/membrele statice si nu poate fi instantiata