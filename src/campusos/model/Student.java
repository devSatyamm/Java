
package campusos.model;

public class Student extends User {
    private String course;
    private int semester;
    private String hostelRoom;

    public Student(String userId, String name, String email,
                   String password, String course, int semester) {
        super(userId, name, email, password);
        this.course = course;
        this.semester = semester;
        this.hostelRoom = "Not Assigned";
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public String getCourse() {
        return course;
    }

    public int getSemester() {
        return semester;
    }

    public String getHostelRoom() {
        return hostelRoom;
    }

    public void assignHostelRoom(String hostelRoom) {
        this.hostelRoom = hostelRoom;
    }

    @Override
    public void displayProfile() {
        super.displayProfile();
        System.out.println("Course: " + course);
        System.out.println("Semester: " + semester);
        System.out.println("Hostel Room: " + hostelRoom);
    }
}
