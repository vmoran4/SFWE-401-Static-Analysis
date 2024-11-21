import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;


//Log Types: ORDER, LOW_STOCK, EXPIRATION
//Format:
// Log Type | [Timestamp] | Description
// Example:
// LOW STOCK | [2024-11-21 15:33:08] | LOW STOCK | Item: Medication1, Threshold: 5, Remaining Quantity: 3
public class TransactionLogger{
    private String logsDirectoryPath;

    public TransactionLogger(String logsDirectoryPath){
        this.logsDirectoryPath = logsDirectoryPath;
    }

    public TransactionLogger(){
        this.logsDirectoryPath = "logs/";
        //Create logs directory if it doesn't exist
        if(!new File(logsDirectoryPath).exists()){
            new File(logsDirectoryPath).mkdir();
        }
    }
    //General log message function
    private void logMessage(String logType, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("%-18s | [%-19s] | %s", logType, timestamp, message);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logsDirectoryPath + getCurrentFilename(),
             true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    //Logs order
    public void logOrder(Order order){
        String message = String.format(
            "Batch Number: %s, Customer: %s, Item: %s, Quantity: %d",
           order.getBatchNumber(), order.getSupplier(), order.getMedicationName(), order.getQuantity()
        );
        logMessage("ORDER", message);
    }

    //Logs low stock notification
    public void logLowStock(Medication medication){
        String message = String.format(
            "Item: %s, Threshold: %s, Remaining Quantity: %d",
            medication.getName(), medication.getLowStockThreshold(), medication.getQuantity()
        );
        logMessage("LOW STOCK", message);
    }

    //Logs expired medication
    public void logExpiration(Order order){
        String message = String.format(
            "Medication: %s, Expiry Date: %s, Amount Expired: %d",
            order.getMedicationName(), order.getExpDate(), order.getQuantity()
        );
        logMessage("EXPIRED MEDICATION", message);
    }

    //Helper function
    public String getCurrentFilename(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + "LOG.txt";
    }

    //Test the logger
    public static void main(String[] args){
        TransactionLogger logger = new TransactionLogger();
        Order order = new Order();
        order.setBatchNumber(1234);
        order.setSupplier("Supplier1");
        order.setMedicationName("Medication1");
        order.setQuantity(10);
        order.setExpDate("2021-03-01");
        logger.logOrder(order);

        Medication medication = new Medication();
        medication.setName("Medication1");
        medication.setLowStockThreshold(5);
        medication.setQuantity(3);
        logger.logLowStock(medication);

        Order expiredOrder = new Order();
        expiredOrder.setMedicationName("Medication1");
        expiredOrder.setExpDate("2021-02-01");
        expiredOrder.setQuantity(5);
        logger.logExpiration(expiredOrder);
    }
}
