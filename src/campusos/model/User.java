package campusos.model;

public abstract class User {
    private String userId;
    private String name;
    private String email;
    private String password;

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public abstract String getRole();

    public void displayProfile() {
        System.out.println("ID: " + userId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Role: " + getRole());
    }
}

// isme abstarction  use ho rha hai : User is an abstract class
// isme Encapsulation  use ho rha hai : fields are private
// isme   inheritence use ho rha hai : Student and Admin will extend User
// isme polymorphism use ho rha hai : getRole() will be implemented differently by subclasses