
package campusos.service;

import campusos.model.Room;
import campusos.model.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HostelService {
    private final List<Room> rooms = new ArrayList<>();
    private final String filePath = "data/rooms.txt";
    private final StudentService studentService = new StudentService();

    public HostelService() {
        loadRooms();
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public Room findRoom(String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public boolean addRoom(Room room) {
        if (findRoom(room.getRoomNumber()) != null) {
            return false;
        }

        rooms.add(room);
        saveRooms();
        return true;
    }

    public boolean allocateStudent(String studentId, String roomNumber) {
        Student student = studentService.findStudent(studentId);
        Room room = findRoom(roomNumber);

        if (student == null || room == null || !room.hasVacancy()) {
            return false;
        }

        if (!student.getHostelRoom().equalsIgnoreCase("Not Assigned")) {
            return false;
        }

        if (!room.allocateSeat()) {
            return false;
        }

        student.assignHostelRoom(roomNumber);

        if (!studentService.updateStudent(studentId, student)) {
            room.releaseSeat();
            student.assignHostelRoom("Not Assigned");
            return false;
        }

        saveRooms();
        return true;
    }

    private void saveRooms() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Room room : rooms) {
                writer.println(
                    room.getRoomNumber() + "|" +
                    room.getCapacity() + "|" +
                    room.getOccupied()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRooms() {
        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length != 3) {
                    continue;
                }

                int capacity = Integer.parseInt(data[1]);
                int occupied = Integer.parseInt(data[2]);

                if (capacity <= 0 || occupied < 0 || occupied > capacity) {
                    continue;
                }

                Room room = new Room(data[0], capacity);
                room.setOccupied(occupied);
                rooms.add(room);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
