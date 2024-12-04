import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

//Represents an order of medication from a supplier
public class Order {

    private String medicationName;
    private double quantityGrams;
    private String expDate; // formatted YYYY-MM-DD
    private int batchNumber;
    private String supplier;

    private static int currBatchNumber = 0;

    public Order(String medicationName, double quantityGrams, String expDate, int batchNumber, String supplier) {
        this.medicationName = medicationName;
        this.quantityGrams = quantityGrams;
        this.expDate = expDate;
        this.batchNumber = currBatchNumber;
        ++currBatchNumber;
        this.supplier = supplier;
    }

    public Order() {
        this.medicationName = "";
        this.quantityGrams = 0.0;
        this.expDate = "";
        this.batchNumber = 0;
        this.supplier = "";
    }

    // Getters and setters
    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public double getQuantityGrams() {
        return quantityGrams;
    }

    public void setQuantityGrams(double quantityGrams) {
        this.quantityGrams = quantityGrams;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public boolean isExpired() {
        // Check if date is past expiration date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.parse(this.expDate, dtf);
        // Make sure that day of expiration counts as expiration
        return currentDate.isAfter(expirationDate.minusDays(1));
    }

    public String toCSV() {
        return String.format("%s,%f,%s,%d,%s\n", medicationName, quantityGrams, expDate, batchNumber, supplier);
    }
}
