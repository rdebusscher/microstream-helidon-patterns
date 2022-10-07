package be.rubus.microstream.helidon.demo.dto;

public class CreateUser {

    private final String name;
    private String email;

    public CreateUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
