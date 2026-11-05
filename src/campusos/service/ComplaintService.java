
package campusos.service;

import campusos.model.Complaint;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintService {
    private final List<Complaint> complaints = new ArrayList<>();
    private final String filePath = "data/complaints.txt";

    public ComplaintService() {
        loadComplaints();
    }

    public List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaints);
    }

    public List<Complaint> getStudentComplaints(String studentId) {
        List<Complaint> result = new ArrayList<>();

        for (Complaint c : complaints) {
            if (c.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(c);
            }
        }

        return result;
    }

    public Complaint findComplaint(String complaintId) {
        for (Complaint c : complaints) {
            if (c.getComplaintId().equalsIgnoreCase(complaintId)) {
                return c;
            }
        }
        return null;
    }

    public Complaint addComplaint(String studentId, String category,
                                  String description) {
        if (studentId == null || studentId.trim().isEmpty()
                || category == null || category.trim().isEmpty()
                || description == null || description.trim().isEmpty()) {
            return null;
        }

        String id = generateComplaintId();

        Complaint complaint = new Complaint(
            id,
            studentId.trim(),
            category.trim(),
            description.trim().replace("|", "/").replace("\n", " ")
        );

        complaints.add(complaint);
        saveComplaints();
        return complaint;
    }

    public boolean updateStatus(String complaintId, String status) {
        Complaint complaint = findComplaint(complaintId);

        if (complaint == null) {
            return false;
        }

        if (!status.equals("Pending")
                && !status.equals("In Progress")
                && !status.equals("Resolved")) {
            return false;
        }

        complaint.setStatus(status);
        saveComplaints();
        return true;
    }

    private String generateComplaintId() {
        int max = 0;

        for (Complaint c : complaints) {
            try {
                String id = c.getComplaintId().replace("CMP", "");
                max = Math.max(max, Integer.parseInt(id));
            } catch (NumberFormatException ignored) {
                // Ignore malformed IDs.
            }
        }

        return String.format("CMP%03d", max + 1);
    }

    private void saveComplaints() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Complaint c : complaints) {
                writer.println(
                    c.getComplaintId() + "|" +
                    c.getStudentId() + "|" +
                    c.getCategory() + "|" +
                    c.getDescription() + "|" +
                    c.getStatus()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadComplaints() {
        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length != 5) {
                    continue;
                }

                Complaint complaint = new Complaint(
                    data[0], data[1], data[2], data[3]
                );

                complaint.setStatus(data[4]);
                complaints.add(complaint);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
