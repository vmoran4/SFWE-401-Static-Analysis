import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class Order{

    private String medicationName;
    private int quantity;
    private String expDate; // formatted YYYY-MM-DD
    private int batchNumber;
    private String supplier;

    // Getters and setters
    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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


    public boolean isExpired(){
        //Check if date is past expiration date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.parse(this.expDate, dtf);
        //Make sure that day of expiration counts as expiration
        return currentDate.isAfter(expirationDate.minusDays(1)); 
    }


}
