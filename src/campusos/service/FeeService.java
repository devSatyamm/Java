
package campusos.service;

import campusos.model.Fee;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FeeService {
    private final List<Fee> fees = new ArrayList<>();
    private final String filePath = "data/fees.txt";

    public FeeService() {
        loadFees();
    }

    public List<Fee> getAllFees() {
        return new ArrayList<>(fees);
    }

    public Fee findFee(String studentId) {
        for (Fee fee : fees) {
            if (fee.getStudentId().equalsIgnoreCase(studentId)) {
                return fee;
            }
        }
        return null;
    }

    public boolean createFeeRecord(String studentId, double totalFee) {
        if (studentId == null || studentId.trim().isEmpty()
                || totalFee <= 0 || findFee(studentId) != null) {
            return false;
        }

        Fee fee = new Fee(studentId, totalFee);
        fees.add(fee);
        saveFees();
        return true;
    }

    public boolean recordPayment(String studentId, double amount) {
        Fee fee = findFee(studentId);

        if (fee == null || !fee.addPayment(amount)) {
            return false;
        }

        saveFees();
        return true;
    }

    private void saveFees() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Fee fee : fees) {
                writer.println(
                    fee.getStudentId() + "|" +
                    fee.getTotalFee() + "|" +
                    fee.getPaidAmount()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFees() {
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

                String studentId = data[0];
                double total = Double.parseDouble(data[1]);
                double paid = Double.parseDouble(data[2]);

                if (total <= 0 || paid < 0 || paid > total
                        || findFee(studentId) != null) {
                    continue;
                }

                Fee fee = new Fee(studentId, total);
                fee.setPaidAmount(paid);
                fees.add(fee);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
