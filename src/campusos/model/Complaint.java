
package campusos.model;

public class Complaint {
    private String complaintId;
    private String studentId;
    private String category;
    private String description;
    private String status;

    public Complaint(String complaintId, String studentId,
                     String category, String description) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.category = category;
        this.description = description;
        this.status = "Pending";
    }

    public String getComplaintId() {
        return complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status.equals("Pending")
                || status.equals("In Progress")
                || status.equals("Resolved")) {
            this.status = status;
        }
    }
}
