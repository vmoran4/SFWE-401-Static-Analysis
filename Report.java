import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

// Stuff for comparing times
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


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

    // Function to compare time and return true if they are 31 days apart
    public static boolean expiresIn30DaysorLess(String date1, String date2) {
        // Define the date format (YY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");

        // Convert the strings to LocalDate objects
        LocalDate date1Obj = LocalDate.parse(date1, formatter);
        LocalDate date2Obj = LocalDate.parse(date2, formatter);

        // Calculate the absolute difference in days
        long daysBetween = ChronoUnit.DAYS.between(date1Obj, date2Obj);

        // Return true if the difference is less than 31 days
        return Math.abs(daysBetween) < 31;
    }

    // Function to compare time and return true if they are 1 day apart
    public static boolean expiresIn1Day(String date1, String date2) {
        // Define the date format (YY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");

        // Convert the strings to LocalDate objects
        LocalDate date1Obj = LocalDate.parse(date1, formatter);
        LocalDate date2Obj = LocalDate.parse(date2, formatter);

        // Calculate the absolute difference in days
        long daysBetween = ChronoUnit.DAYS.between(date1Obj, date2Obj);

        // Return true if the difference is exactly 1 day
        return Math.abs(daysBetween) == 1;
    }

    public void generateFinanciaReport() {

        // Generate the file name
        String todaysDate = TransactionLogger.getCurrDate();

        // Open the file with correct filename
        try {
            FileWriter dailyFinancialReport = new FileWriter("reports/" + todaysDate + "FinancialReport.txt");

            // Write to the file
            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Pharmacy Name\n");
            dailyFinancialReport.write("Pharmacy Address\n");
            dailyFinancialReport.write("Pharmacy eMail Address\n");
            dailyFinancialReport.write("Pharmacy Phone Number");
            dailyFinancialReport.write("Financial Report");
            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Sales\n");

            // Write code for writing sales onto the file
            // open the log of the day
            try {
                Scanner scnrSales = new Scanner(new File("logs/" + todaysDate + "LOG.txt"));
                while (scnrSales.hasNextLine()) {
                    String saleString = scnrSales.nextLine();
                    if (saleString.startsWith("S")) {
                        dailyFinancialReport.write(scnrSales.nextLine());
                    }
                }
    
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Expenses\n");

            // Write code for writing expenses onto the file
            // open the log of the day
            try {
                Scanner scnrOrders = new Scanner(new File("logs/" + todaysDate + "LOG.txt"));
                while (scnrOrders.hasNextLine()) {
                    String orderString = scnrOrders.nextLine();
                    if (!orderString.startsWith("S")) {
                        dailyFinancialReport.write(scnrOrders.nextLine());
                    }
                }
    
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Profit " + calculateTotalSales()); // calculate the profit
            dailyFinancialReport.write("Date " + todaysDate);

            // close the file
            dailyFinancialReport.close();


        } catch (IOException e) {
            System.out.println("Error making the file occured");
            e.printStackTrace();
        }
    }

    public void generateInventoryReport(Inventory currentInventory) {
        // Generate the file name
        String todaysDate = TransactionLogger.getCurrDate();

        // Open the file with correct filename
        try {
            FileWriter dailyInventoryReport = new FileWriter("reports/" + todaysDate + "InventoryReport.txt");

            // Write to the file
            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Pharmacy Name\n");
            dailyInventoryReport.write("Pharmacy Address\n");
            dailyInventoryReport.write("Pharmacy eMail Address\n");
            dailyInventoryReport.write("Pharmacy Phone Number");
            dailyInventoryReport.write("Inventory Report");
            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Available Medications\n");

            // Write all available medication into the file

            ArrayList<Medication> listOfMedications = currentInventory.getMedicationList();
            for (Medication medication : listOfMedications) {                
                dailyInventoryReport.write(medication.toString() + "\n");
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 30 days\n");

            // Write all available medication that will expire in the next 30 days
            ArrayList<Order> listofOrders = currentInventory.getOrders();
            for (Order order : listofOrders) {
                String expDate = order.getExpDate();
                if (expiresIn30DaysorLess(expDate, todaysDate)) {
                    dailyInventoryReport.write(order.toString() + "\n");
                }
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 1 days\n");

            // Write code for writing expenses onto the file
            for (Order order : listofOrders) {
                String expDate = order.getExpDate();
                if (expiresIn1Day(expDate, todaysDate)) {
                    dailyInventoryReport.write(order.toString() + "\n");
                }
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Date " + todaysDate);

            // close the file
            dailyInventoryReport.close();


        } catch (IOException e) {
            System.out.println("Error making the file occured");
            e.printStackTrace();
        }
    }

}
