
package campusos.model;

public class Room {
    private String roomNumber;
    private int capacity;
    private int occupied;

    public Room(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.occupied = 0;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public int getVacancy() {
        return capacity - occupied;
    }

    public boolean hasVacancy() {
        return occupied < capacity;
    }

    public boolean allocateSeat() {
        if (!hasVacancy()) {
            return false;
        }

        occupied++;
        return true;
    }

    public void releaseSeat() {
        if (occupied > 0) {
            occupied--;
        }
    }

    public void setOccupied(int occupied) {
        if (occupied >= 0 && occupied <= capacity) {
            this.occupied = occupied;
        }
    }
}
