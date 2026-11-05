
package campusos.service;

import campusos.model.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final List<Student> students = new ArrayList<>();
    private final String filePath = "data/students.txt";

    public StudentService() {
        loadStudents();
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public Student findStudent(String id) {
        for (Student student : students) {
            if (student.getUserId().equalsIgnoreCase(id)) {
                return student;
            }
        }
        return null;
    }

    public boolean addStudent(Student student) {
        if (findStudent(student.getUserId()) != null) {
            return false;
        }

        students.add(student);
        saveStudents();
        return true;
    }

    public boolean updateStudent(String id, Student updatedStudent) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getUserId().equalsIgnoreCase(id)) {
                students.set(i, updatedStudent);
                saveStudents();
                return true;
            }
        }
        return false;
    }

    public boolean deleteStudent(String id) {
        boolean removed = students.removeIf(
                student -> student.getUserId().equalsIgnoreCase(id)
        );

        if (removed) {
            saveStudents();
        }
        return removed;
    }

    private void saveStudents() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Student s : students) {
                writer.println(
                    s.getUserId() + "|" +
                    s.getName() + "|" +
                    s.getEmail() + "|" +
                    s.getCourse() + "|" +
                    s.getSemester() + "|" +
                    s.getHostelRoom()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length != 6) {
                    continue;
                }

                Student student = new Student(
                    data[0], data[1], data[2],
                    "not-used", data[3],
                    Integer.parseInt(data[4])
                );

                student.assignHostelRoom(data[5]);
                students.add(student);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
