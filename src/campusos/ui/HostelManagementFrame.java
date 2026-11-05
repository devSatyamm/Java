
package campusos.ui;

import campusos.model.Room;
import campusos.service.HostelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HostelManagementFrame extends JFrame {
    private final HostelService service = new HostelService();
    private final DefaultTableModel model;

    public HostelManagementFrame() {
        setTitle("CampusOS | Hostel Management");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Hostel Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        top.add(title);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Room Number", "Capacity", "Occupied", "Vacancy"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addRoomButton = new JButton("Add Room");
        JButton allocateButton = new JButton("Allocate Student");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttons.add(addRoomButton);
        buttons.add(allocateButton);
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);

        addRoomButton.addActionListener(e -> addRoom());
        allocateButton.addActionListener(e -> allocateStudent());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Room room : service.getAllRooms()) {
            model.addRow(new Object[]{
                room.getRoomNumber(),
                room.getCapacity(),
                room.getOccupied(),
                room.getVacancy()
            });
        }
    }

    private void addRoom() {
        JTextField roomField = new JTextField();
        JTextField capacityField = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Room Number:"));
        form.add(roomField);
        form.add(new JLabel("Capacity:"));
        form.add(capacityField);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Hostel Room",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String roomNumber = roomField.getText().trim();
        String capacityText = capacityField.getText().trim();

        if (roomNumber.isEmpty() || capacityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityText);

            if (capacity <= 0) {
                JOptionPane.showMessageDialog(
                    this, "Capacity must be greater than zero."
                );
                return;
            }

            boolean added = service.addRoom(new Room(roomNumber, capacity));

            if (!added) {
                JOptionPane.showMessageDialog(
                    this, "Room already exists."
                );
                return;
            }

            refreshTable();
            JOptionPane.showMessageDialog(this, "Room added successfully.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Capacity must be a valid number."
            );
        }
    }

    private void allocateStudent() {
        JTextField studentIdField = new JTextField();
        JTextField roomField = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Student ID:"));
        form.add(studentIdField);
        form.add(new JLabel("Room Number:"));
        form.add(roomField);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Allocate Hostel Room",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String studentId = studentIdField.getText().trim();
        String roomNumber = roomField.getText().trim();

        if (studentId.isEmpty() || roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        boolean allocated = service.allocateStudent(studentId, roomNumber);

        if (allocated) {
            refreshTable();
            JOptionPane.showMessageDialog(
                this, "Room allocated successfully."
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Allocation failed. Check the student ID, room number, vacancy, and existing allocation.",
                "Allocation Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
