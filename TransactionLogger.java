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

//FIXME: should this whole class be static?
public class TransactionLogger{
    private final String logsDirectoryPath;

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
            //If file is empty, add header
            if(new File(logsDirectoryPath + getCurrentFilename()).length() == 0){
                writer.write("Log Type          | Timestamp           | Description");
                writer.newLine();
            }
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    //Logs order
    public String logOrder(Order order, double costPerGram){
        String message = String.format(
            //FIXME: change getCostPerGram to get the medication object somehow
            "Batch Number: %s, Supplier: %s, Item: %s, Quantity: %.2f grams, Total Cost: %.2f",
           order.getBatchNumber(), order.getSupplier(), order.getMedicationName(), order.getQuantityGrams(),
           order.getQuantityGrams() * costPerGram
        );
        logMessage("ORDER", message);
        return message;
    }

    //Logs low stock notification
    public String logLowStock(Medication medication){
        String message = String.format(
            "Item: %s is Low Stock, Threshold: %s, Remaining Quantity: %.2f grams",
            medication.getName(), medication.getLowStockThreshold(), medication.getQuantityGrams()
        );
        logMessage("LOW STOCK", message);
        return message;
    }

    //Logs expired medication
    public String logExpiration(Order order){
        String message = String.format(
            "Medication: %s, Expiry Date: %s, Amount Expired: %.2f grams",
            order.getMedicationName(), order.getExpDate(), order.getQuantityGrams()
        );
        logMessage("EXPIRED MEDICATION", message);
        return message;
    }
    
    //Logs sales
    public String logSale(double quantityGrams, double totalPrice, String medicationName){
        String message = String.format(
            "Medication: %s, Quantity Sold: %.2f grams, Gross Income: %.2f",
            medicationName, quantityGrams, totalPrice
        );
        logMessage("SALE", message);
        return message;
    }
    //Returns curr day's date in yyyy-MM-dd format
    public static String getCurrDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    //Helper function to get current day's log file name
    public static String getCurrentFilename(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + "LOG.txt";
    }


    public boolean removeOldLogs(){
        //Get current date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String currDate = dtf.format(now);

        //Get five years ago date
        LocalDateTime fiveYearsAgo = now.minusYears(5);
        String fiveYearsAgoDate = dtf.format(fiveYearsAgo);

        //Get all files in logs directory
        File logsDirectory = new File(logsDirectoryPath);
        File[] logFiles = logsDirectory.listFiles();

        //Delete log files older than five years
        for(File file : logFiles){
            String filename = file.getName();
            if(filename.compareTo(fiveYearsAgoDate + "LOG.txt") < 0){
                file.delete();
            }
        }

        // Delete reports, orders, and medications older than five years
        File reportsDirectory = new File("reports/");
        File[] reportFiles = reportsDirectory.listFiles();
        for (File file : reportFiles) {
            String filename = file.getName();
            if (filename.compareTo(fiveYearsAgoDate + "REPORT.txt") < 0) {
            file.delete();
            }
        }

        File ordersDirectory = new File("orders/");
        File[] orderFiles = ordersDirectory.listFiles();
        for (File file : orderFiles) {
            String filename = file.getName();
            if (filename.compareTo(fiveYearsAgoDate + "ORDER.txt") < 0) {
            file.delete();
            }
        }

        File medicationsDirectory = new File("medications/");
        File[] medicationFiles = medicationsDirectory.listFiles();
        for (File file : medicationFiles) {
            String filename = file.getName();
            if (filename.compareTo(fiveYearsAgoDate + "MEDICATION.txt") < 0) {
            file.delete();
            }
        }

        return true;
    }


    //Test the logger
    public static void main(String[] args){
        TransactionLogger logger = new TransactionLogger();
        Order order = new Order();
        order.setBatchNumber(1234);
        order.setSupplier("Supplier1");
        order.setMedicationName("Medication1");
        order.setQuantityGrams(10.0);
        order.setExpDate("2021-03-01");
        logger.logOrder(order, 100.0);

        Medication medication = new Medication();
        medication.setName("Medication1");
        medication.setLowStockThreshold(5);
        medication.setQuantityGrams(3.0);
        logger.logLowStock(medication);

        Order expiredOrder = new Order();
        expiredOrder.setMedicationName("Medication1");
        expiredOrder.setExpDate("2021-02-01");
        expiredOrder.setQuantityGrams(5.0);
        logger.logExpiration(expiredOrder);

        logger.logSale(5.0, 100.0, "Medication1");
    }
}
