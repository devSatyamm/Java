
package campusos.ui;

import campusos.model.Fee;
import campusos.service.FeeService;
import campusos.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FeeManagementFrame extends JFrame {

    private final FeeService feeService = new FeeService();
    private final StudentService studentService = new StudentService();
    private final DefaultTableModel model;

    public FeeManagementFrame() {
        setTitle("CampusOS | Fee Management");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Fee Management");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        top.add(title);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{
                "Student ID", "Total Fee", "Paid Amount",
                "Pending Amount", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton createButton = new JButton("Create Fee Record");
        JButton paymentButton = new JButton("Record Payment");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttons.add(createButton);
        buttons.add(paymentButton);
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createFeeRecord());
        paymentButton.addActionListener(e -> recordPayment());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Fee fee : feeService.getAllFees()) {
            model.addRow(new Object[]{
                fee.getStudentId(),
                String.format("%.2f", fee.getTotalFee()),
                String.format("%.2f", fee.getPaidAmount()),
                String.format("%.2f", fee.getPendingAmount()),
                fee.getStatus()
            });
        }
    }

    private void createFeeRecord() {
        JTextField idField = new JTextField();
        JTextField amountField = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Total Fee (₹):"));
        form.add(amountField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Create Fee Record",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String studentId = idField.getText().trim();
        String amountText = amountField.getText().trim();

        if (studentId.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        if (studentService.findStudent(studentId) == null) {
            JOptionPane.showMessageDialog(
                this, "Student ID does not exist."
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                JOptionPane.showMessageDialog(
                    this, "Enter a valid positive fee amount."
                );
                return;
            }

            if (feeService.createFeeRecord(studentId, amount)) {
                refreshTable();
                JOptionPane.showMessageDialog(
                    this, "Fee record created successfully."
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "A fee record already exists for this student."
                );
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Fee must be a valid number."
            );
        }
    }

    private void recordPayment() {
        JTextField idField = new JTextField();
        JTextField amountField = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Payment Amount (₹):"));
        form.add(amountField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Record Payment",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String studentId = idField.getText().trim();
        String amountText = amountField.getText().trim();

        if (studentId.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                JOptionPane.showMessageDialog(
                    this, "Enter a valid positive payment amount."
                );
                return;
            }

            Fee fee = feeService.findFee(studentId);

            if (fee == null) {
                JOptionPane.showMessageDialog(
                    this, "No fee record found for this student."
                );
                return;
            }

            if (feeService.recordPayment(studentId, amount)) {
                refreshTable();
                JOptionPane.showMessageDialog(
                    this, "Payment recorded successfully."
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Payment exceeds the pending amount."
                );
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Payment must be a valid number."
            );
        }
    }
}
