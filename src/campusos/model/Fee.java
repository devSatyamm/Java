
package campusos.model;

public class Fee {
    private String studentId;
    private double totalFee;
    private double paidAmount;

    public Fee(String studentId, double totalFee) {
        this.studentId = studentId;
        this.totalFee = totalFee;
        this.paidAmount = 0;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getPendingAmount() {
        return totalFee - paidAmount;
    }

    public String getStatus() {
        if (paidAmount == 0) {
            return "Pending";
        } else if (paidAmount >= totalFee) {
            return "Paid";
        }
        return "Partial";
    }

    public boolean addPayment(double amount) {
        if (amount <= 0 || amount > getPendingAmount()) {
            return false;
        }

        paidAmount += amount;
        return true;
    }

    public void setPaidAmount(double paidAmount) {
        if (paidAmount >= 0 && paidAmount <= totalFee) {
            this.paidAmount = paidAmount;
        }
    }
}
