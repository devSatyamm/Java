
package campusos.ui;

import campusos.model.Student;
import campusos.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class StudentManagementFrame extends JFrame {

    private final StudentService service = new StudentService();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField;

    public StudentManagementFrame() {
        setTitle("CampusOS | Student Management");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Student Management");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        topPanel.add(title, BorderLayout.NORTH);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setToolTipText("Search students");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Course", "Semester", "Hostel Room"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                private void filter() {
                    String query = searchField.getText().trim();

                    if (query.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(
                            RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query))
                        );
                    }
                }

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    filter();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    filter();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    filter();
                }
            }
        );

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showStudentDialog(null));
        updateButton.addActionListener(e -> updateSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Student s : service.getAllStudents()) {
            model.addRow(new Object[]{
                s.getUserId(),
                s.getName(),
                s.getEmail(),
                s.getCourse(),
                s.getSemester(),
                s.getHostelRoom()
            });
        }
    }

    private void showStudentDialog(Student existing) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField courseField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField roomField = new JTextField();

        if (existing != null) {
            idField.setText(existing.getUserId());
            idField.setEditable(false);
            nameField.setText(existing.getName());
            emailField.setText(existing.getEmail());
            courseField.setText(existing.getCourse());
            semesterField.setText(String.valueOf(existing.getSemester()));
            roomField.setText(existing.getHostelRoom());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Course:"));
        form.add(courseField);
        form.add(new JLabel("Semester:"));
        form.add(semesterField);
        form.add(new JLabel("Hostel Room:"));
        form.add(roomField);

        int result = JOptionPane.showConfirmDialog(
            this, form,
            existing == null ? "Add Student" : "Update Student",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseField.getText().trim();
        String semesterText = semesterField.getText().trim();
        String room = roomField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()
                || course.isEmpty() || semesterText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        try {
            int semester = Integer.parseInt(semesterText);

            if (semester < 1 || semester > 12) {
                JOptionPane.showMessageDialog(this, "Enter a valid semester.");
                return;
            }

            Student student = new Student(
                id, name, email, "not-used", course, semester
            );
            student.assignHostelRoom(room.isEmpty() ? "Not Assigned" : room);

            boolean success;

            if (existing == null) {
                success = service.addStudent(student);
            } else {
                success = service.updateStudent(existing.getUserId(), student);
            }

            if (!success) {
                JOptionPane.showMessageDialog(
                    this, "Student ID already exists or record was not found."
                );
                return;
            }

            refreshTable();
            JOptionPane.showMessageDialog(this, "Student record saved.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Semester must be a number.");
        }
    }

    private void updateSelectedStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String id = model.getValueAt(modelRow, 0).toString();

        Student student = service.findStudent(id);

        if (student != null) {
            showStudentDialog(student);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String id = model.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete student " + id + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteStudent(id)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Student deleted.");
            }
        }
    }
}
