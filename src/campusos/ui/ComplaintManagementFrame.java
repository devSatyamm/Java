
package campusos.ui;

import campusos.model.*;
import campusos.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ComplaintManagementFrame extends JFrame {

    private final User user;
    private final ComplaintService service = new ComplaintService();
    private final DefaultTableModel model;
    private final JTable table;

    public ComplaintManagementFrame(User user) {
        this.user = user;

        setTitle("CampusOS | Complaint Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Complaint Management");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        top.add(title);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{
                "Complaint ID", "Student ID", "Category",
                "Description", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));

        if (user instanceof Student) {
            JButton submitButton = new JButton("Submit Complaint");
            submitButton.addActionListener(e -> submitComplaint());
            buttons.add(submitButton);
        }

        if (user instanceof Admin) {
            JButton statusButton = new JButton("Update Status");
            statusButton.addActionListener(e -> updateStatus());
            buttons.add(statusButton);
        }

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());
        buttons.add(refreshButton);

        add(buttons, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        List<Complaint> complaints;

        if (user instanceof Admin) {
            complaints = service.getAllComplaints();
        } else {
            complaints = service.getStudentComplaints(user.getUserId());
        }

        for (Complaint c : complaints) {
            model.addRow(new Object[]{
                c.getComplaintId(),
                c.getStudentId(),
                c.getCategory(),
                c.getDescription(),
                c.getStatus()
            });
        }
    }

    private void submitComplaint() {
        JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{
                "Hostel",
                "Mess",
                "Academics",
                "Infrastructure",
                "Other"
            }
        );

        JTextArea descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new BorderLayout(8, 8));
        JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
        categoryPanel.add(new JLabel("Category:"), BorderLayout.NORTH);
        categoryPanel.add(categoryBox, BorderLayout.CENTER);

        JPanel descriptionPanel = new JPanel(new BorderLayout(5, 5));
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        form.add(categoryPanel, BorderLayout.NORTH);
        form.add(descriptionPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Submit Complaint",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String category = (String) categoryBox.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please enter a complaint description."
            );
            return;
        }

        Complaint complaint = service.addComplaint(
            user.getUserId(), category, description
        );

        if (complaint != null) {
            refreshTable();
            JOptionPane.showMessageDialog(
                this,
                "Complaint submitted successfully.\nComplaint ID: "
                    + complaint.getComplaintId()
            );
        } else {
            JOptionPane.showMessageDialog(
                this, "Unable to submit complaint."
            );
        }
    }

    private void updateStatus() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this, "Select a complaint first."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String complaintId = model.getValueAt(modelRow, 0).toString();

        String[] statuses = {"Pending", "In Progress", "Resolved"};

        String currentStatus = model.getValueAt(modelRow, 4).toString();

        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setSelectedItem(currentStatus);

        int result = JOptionPane.showConfirmDialog(
            this,
            statusBox,
            "Update Complaint Status",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newStatus = (String) statusBox.getSelectedItem();

        if (service.updateStatus(complaintId, newStatus)) {
            refreshTable();
            JOptionPane.showMessageDialog(
                this, "Complaint status updated."
            );
        } else {
            JOptionPane.showMessageDialog(
                this, "Unable to update complaint status."
            );
        }
    }
}
