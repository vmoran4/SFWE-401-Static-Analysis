import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Report {
    

    //Calculate total day's sales using log from that day for Financial Report.
    //FIXME: This may need to be moved to financial report class if that is created.
    public double calculateTotalSales(String logFilePath){
        double totalSales = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(logFilePath))) { 
            String line;

            while ((line = br.readLine()) != null) { 
                String[] values = line.split(","); 
                String logType = values[0].trim(); 
                String message = values[1].trim(); 
                String[] messageValues = message.split(","); 
                String medicationName = messageValues[1].trim(); 
                double quantityGrams = Double.parseDouble(messageValues[3].trim()); 
                double totalPrice = Double.parseDouble(messageValues[4].trim());
                if(logType.equals("SALES")){
                    totalSales += totalPrice;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        return totalSales;
    }

    //Calculate total day's sales using log from that day.
    public double calculateTotalSales() {
        return calculateTotalSales(TransactionLogger.getCurrentFilename());
    }
    
}
