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

    // Calculate total day's Sales using log from that day for Financial Report.
    public static double calculateTotalSales(String logFilePath) {
        double totalSales = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("SALE")) {
                    String[] parts = line.split("\\|");
                    String details = parts[2].trim();
                    String[] detailParts = details.split(",");
                    double totalPrice = Double.parseDouble(detailParts[2].split(":")[1].trim().replace("Gross Income: ", ""));
                    totalSales += totalPrice;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        return totalSales;
    }

    // Calculate total day's sales using log from that day.
    public static double calculateTotalSales() {
        return calculateTotalSales("logs/" + TransactionLogger.getCurrentFilename());
    }

    //Calculate total day's expenses using log from that day for Financial Report.
    public static double calculateTotalExpenses(String logFilePath) {
        double totalExpenses = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("ORDER")) {
                    String[] parts = line.split("Total Cost: ");
                    if (parts.length > 1) {
                        totalExpenses += Double.parseDouble(parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        return totalExpenses;
    }

    // Calculate total day's expenses using log from that day.
    public static double calculateTotalExpenses() {
        return calculateTotalExpenses("logs/" + TransactionLogger.getCurrentFilename());
    }
  
  //Expiration functions
  // Function to compare time and return true if they are 31 days apart
  public static boolean expiresIn30DaysorLess(String date1, String date2) {
        // Define the date format (YY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convert the strings to LocalDate objects
        LocalDate date1Obj = LocalDate.parse(date1, formatter);
        LocalDate date2Obj = LocalDate.parse(date2, formatter);

        // Calculate the absolute difference in days
        long daysBetween = ChronoUnit.DAYS.between(date1Obj, date2Obj);

        // Return true if the difference is exactly 1 day
        return Math.abs(daysBetween) == 1;
    }

  public static void generateFinanciaReport(String date) {

        String targetLogFilename = "logs/" + date + "LOG.txt";
        // Open the file with correct filename
        try {
            FileWriter dailyFinancialReport = new FileWriter("reports/" + date + "FinancialReport.txt");

            // Write to the file
            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Pharmacy Name\n");
            dailyFinancialReport.write("Pharmacy Address\n");
            dailyFinancialReport.write("Pharmacy eMail Address\n");
            dailyFinancialReport.write("Pharmacy Phone Number\n");
            dailyFinancialReport.write("Financial Report\n");
            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Sales\n");

            // Write code for writing sales onto the file
            // open the log of the day
            try {
                Scanner scnrSales = new Scanner(new File(targetLogFilename));
                while (scnrSales.hasNextLine()) {
                    String saleString = scnrSales.nextLine();
                    if (saleString.startsWith("SALE")) {
                        dailyFinancialReport.write(saleString + "\n");
                    }
                }
                scnrSales.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Expenses\n");

            // Write code for writing expenses onto the file
            // open the log of the day
            try {
                Scanner scnrOrders = new Scanner(new File(targetLogFilename));
                while (scnrOrders.hasNextLine()) {
                    String orderString = scnrOrders.nextLine();
                    if (orderString.startsWith("ORDER")) {
                        dailyFinancialReport.write(orderString + "\n");
                    }
                }
                scnrOrders.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            dailyFinancialReport.write("__________________________________________________________\n");
            double totalSales = calculateTotalSales(targetLogFilename);
            double totalExpenses = calculateTotalExpenses(targetLogFilename);
            dailyFinancialReport.write("Total Sales: $" + totalSales + "\n"); // calculate the total sales
            dailyFinancialReport.write("Total Expenses: $" + totalExpenses + "\n"); // calculate the total expenses
            dailyFinancialReport.write("Net Income: $" + (totalSales-totalExpenses) + "\n"); // calculate the net income
            dailyFinancialReport.write("Date " + date);

            // close the file
            dailyFinancialReport.close();

        } catch (IOException e) {
            System.out.println("Error making the file occured");
            e.printStackTrace();
        }
    }

    public static void generateFinanciaReport() {

        // Generate the file name
        String todaysDate = TransactionLogger.getCurrDate();
        generateFinanciaReport(todaysDate);
    }

    public static void generateInventoryReport(Inventory currntInventory) {
        // Generate the file name
        String todaysDate = TransactionLogger.getCurrDate();
        generateInventoryReport(currntInventory, todaysDate);
    }

    public static void generateInventoryReport(Inventory currentInventory, String date) {

        // Open the file with correct filename
        try {
            FileWriter dailyInventoryReport = new FileWriter("reports/" + date + "InventoryReport.txt");

            // Write to the file
            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Pharmacy Name\n");
            dailyInventoryReport.write("Pharmacy Address\n");
            dailyInventoryReport.write("Pharmacy eMail Address\n");
            dailyInventoryReport.write("Pharmacy Phone Number\n");
            dailyInventoryReport.write("Inventory Report\n");
            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Available Medications\n");

            // Write all available medication into the file

            ArrayList<Medication> listOfMedications = currentInventory.getMedicationList();
            for (Medication medication : listOfMedications) {                
                dailyInventoryReport.write(medication.toString() + "\n");
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 30 days:\n\n");

            // Write all available medication that will expire in the next 30 days
            ArrayList<Order> listofOrders = currentInventory.getOrders();
            for (Order order : listofOrders) {
                String expDate = order.getExpDate();
                if (expiresIn30DaysorLess(expDate, date)) {
                    dailyInventoryReport.write(order.toString() + "\n");
                }
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 1 days:\n\n");

            // Write code for writing expenses onto the file
            for (Order order : listofOrders) {
                String expDate = order.getExpDate();
                if (expiresIn1Day(expDate, date)) {
                    dailyInventoryReport.write(order.toString() + "\n");
                }
            }

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Date " + date);

            // close the file
            dailyInventoryReport.close();


        } catch (IOException e) {
            System.out.println("Error making the file occured");
            e.printStackTrace();
        }
    }

}
