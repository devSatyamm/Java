
package campusos.ui;

import campusos.model.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField userIdField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private Student student;
    private Admin admin;

    public LoginFrame() {
        student = new Student(
            "STU101", "Satyam Mishra",
            "satyam@example.com", "student123",
            "B.Tech CSE", 2
        );

        admin = new Admin(
            "ADM101", "Campus Admin",
            "admin@campusos.com", "admin123"
        );

        setTitle("CampusOS | Login");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(30, 41, 59));
        loginPanel.setPreferredSize(new Dimension(340, 380));
        loginPanel.setLayout(null);

        JLabel title = new JLabel("CampusOS");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(90, 25, 200, 40);

        JLabel subtitle = new JLabel("Smart Campus Management");
        subtitle.setForeground(new Color(148, 163, 184));
        subtitle.setBounds(75, 65, 220, 25);

        JLabel roleLabel = new JLabel("Login as");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBounds(35, 110, 100, 25);

        roleBox = new JComboBox<>(new String[]{"Student", "Admin"});
        roleBox.setBounds(35, 140, 270, 35);

        JLabel userLabel = new JLabel("User ID");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(35, 185, 100, 25);

        userIdField = new JTextField();
        userIdField.setBounds(35, 215, 270, 35);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(35, 260, 100, 25);

        passwordField = new JPasswordField();
        passwordField.setBounds(35, 290, 270, 35);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(35, 340, 270, 35);
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        loginButton.addActionListener(e -> login());

        loginPanel.add(title);
        loginPanel.add(subtitle);
        loginPanel.add(roleLabel);
        loginPanel.add(roleBox);
        loginPanel.add(userLabel);
        loginPanel.add(userIdField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        mainPanel.add(loginPanel);
        add(mainPanel);
    }

    private void login() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (role.equals("Student")
                && userId.equals(student.getUserId())
                && student.authenticate(password)) {

            JOptionPane.showMessageDialog(this, "Student login successful!");
            dispose();
            new DashboardFrame(student).setVisible(true);

        } else if (role.equals("Admin")
                && userId.equals(admin.getUserId())
                && admin.authenticate(password)) {

            JOptionPane.showMessageDialog(this, "Admin login successful!");
            dispose();
            new DashboardFrame(admin).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(
                this,
                "Invalid User ID or Password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
