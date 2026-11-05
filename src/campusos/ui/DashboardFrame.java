
package campusos.ui;

import campusos.model.User;
import campusos.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final User user;
    private final JPanel statsPanel;

    private JLabel studentsValue;
    private JLabel hostelValue;
    private JLabel feesValue;
    private JLabel complaintsValue;

    private final Color sidebarColor = new Color(25, 35, 60);
    private final Color backgroundColor = new Color(242, 245, 250);
    private final Color accentColor = new Color(60, 110, 220);

    public DashboardFrame(User user) {
        this.user = user;

        setTitle("CampusOS | Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("CampusOS");
        logo.setFont(new Font("Arial", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(40));

        addNavButton(sidebar, "Overview");
        
        if (user instanceof Student) {
            addNavButton(sidebar, "My Profile");
            addNavButton(sidebar, "Hostel");
            addNavButton(sidebar, "Fees");
            addNavButton(sidebar, "Complaints");
        } else {
            addNavButton(sidebar, "Students");
            addNavButton(sidebar, "Hostel");
            addNavButton(sidebar, "Fees");
            addNavButton(sidebar, "Complaints");
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = createNavButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebar.add(logoutButton);

        add(sidebar, BorderLayout.WEST);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 40, 65));

        JLabel welcome = new JLabel("Welcome back, " + user.getName());
        welcome.setFont(new Font("Arial", Font.PLAIN, 15));
        welcome.setForeground(Color.GRAY);

        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(8));
        headingPanel.add(welcome);

        JButton refreshButton = new JButton("Refresh Stats");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setBackground(accentColor);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(new EmptyBorder(12, 18, 12, 18));
        refreshButton.addActionListener(e -> refreshStats());

        header.add(headingPanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Statistics cards
        statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);

        studentsValue = new JLabel("0");
        hostelValue = new JLabel("0 / 0");
        feesValue = new JLabel("₹0");
        complaintsValue = new JLabel("0");

        statsPanel.add(createStatCard("Total Students", studentsValue));
        statsPanel.add(createStatCard("Hostel Occupancy", hostelValue));
        statsPanel.add(createStatCard("Pending Fees", feesValue));
        statsPanel.add(createStatCard("Unresolved Complaints", complaintsValue));

        mainPanel.add(statsPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        refreshStats();
    }

    private void addNavButton(JPanel sidebar, String text) {
        JButton button = createNavButton(text);
        button.addActionListener(e -> handleNavigation(text));
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(10));
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(190, 45));
        button.setPreferredSize(new Dimension(190, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(sidebarColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 15, 10, 10));
        return button;
    }

    private JPanel createStatCard(String title, JLabel value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 240)),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.GRAY);

        value.setFont(new Font("Arial", Font.BOLD, 30));
        value.setForeground(new Color(35, 50, 85));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(value);

        return card;
    }

    private void refreshStats() {
        try {
            List<String> students = readData("data/students.txt");
            List<String> rooms = readData("data/rooms.txt");
            List<String> fees = readData("data/fees.txt");
            List<String> complaints = readData("data/complaints.txt");

            int studentCount = 0;
            for (String line : students) {
                if (!line.trim().isEmpty()) {
                    studentCount++;
                }
            }

            int totalCapacity = 0;
            int occupiedBeds = 0;

            for (String line : rooms) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    try {
                        totalCapacity += Integer.parseInt(parts[1].trim());
                        occupiedBeds += Integer.parseInt(parts[2].trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            double pendingFees = 0;

            for (String line : fees) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    try {
                        double total = Double.parseDouble(parts[1].trim());
                        double paid = Double.parseDouble(parts[2].trim());
                        pendingFees += Math.max(0, total - paid);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            int unresolvedCount = 0;

            for (String line : complaints) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    String status = parts[4].trim();
                    if (!status.equalsIgnoreCase("Resolved")) {
                        unresolvedCount++;
                    }
                }
            }

            studentsValue.setText(String.valueOf(studentCount));
            hostelValue.setText(occupiedBeds + " / " + totalCapacity);
            feesValue.setText(String.format("₹%.2f", pendingFees));
            complaintsValue.setText(String.valueOf(unresolvedCount));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load dashboard statistics: " + ex.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private List<String> readData(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return List.of();
        }

        return Files.readAllLines(path);
    }

    private void handleNavigation(String option) {
        if (option.equals("Students") && !(user instanceof Student)) {
            new StudentManagementFrame().setVisible(true);

        } else if (option.equals("Hostel")) {
            if (user instanceof Student) {
                JOptionPane.showMessageDialog(
                        this,
                        "Student hostel details can be viewed through the hostel module."
                );
            } else {
                new HostelManagementFrame().setVisible(true);
            }

        } else if (option.equals("Fees")) {
            if (user instanceof Student) {
                JOptionPane.showMessageDialog(
                        this,
                        "Fee details are available in the fee module."
                );
            } else {
                new FeeManagementFrame().setVisible(true);
            }

        } else if (option.equals("Complaints")) {
            new ComplaintManagementFrame(user).setVisible(true);

        } else if (option.equals("My Profile") && user instanceof Student) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name: " + user.getName()
                            + "\nID: " + user.getUserId()
                            + "\nEmail: " + user.getEmail()
                            + "\nRole: " + user.getRole(),
                    "My Profile",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else if (option.equals("Overview")) {
            refreshStats();

        } else {
            JOptionPane.showMessageDialog(this, "Module coming soon.");
        }
    }
}
