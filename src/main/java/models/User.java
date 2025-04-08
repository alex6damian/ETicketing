package models;

import java.util.Objects;

public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected double balance;
    static int userCount;

    public User(String name, String email, String password, double balance) {
        this.id = userCount;
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    // Static and non-static blocks
    static
    {
        userCount = 0;
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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