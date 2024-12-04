import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;

public class Report {

    // Calculate total day's sales using log from that day for Financial Report.
    // FIXME: This may need to be moved to financial report class if that is created.
    public static double calculateTotalSales(String logFilePath) {
        double totalSales = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String logType = values[0].trim();
                String message = values[1].trim();
                String[] messageValues = message.split(",");
                String medicationName = messageValues[1].trim();
                double quantityGrams = Double.parseDouble(messageValues[3].trim());
                double totalPrice = Double.parseDouble(messageValues[4].trim());
                if (logType.equals("SALES")) {
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

    public static void generateFinanciaReport(String date) {

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
                Scanner scnrSales = new Scanner(new File("logs/" + date + "LOG.txt"));
                while (scnrSales.hasNextLine()) {
                    String saleString = scnrSales.nextLine();
                    if (saleString.startsWith("S")) {
                        dailyFinancialReport.write(scnrSales.nextLine() + "\n");
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
                Scanner scnrOrders = new Scanner(new File("logs/" + date + "LOG.txt"));
                while (scnrOrders.hasNextLine()) {
                    String orderString = scnrOrders.nextLine();
                    if (orderString.startsWith("O")) {
                        dailyFinancialReport.write(scnrOrders.nextLine()+ "\n");
                    }
                }
                scnrOrders.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            dailyFinancialReport.write("__________________________________________________________\n");
            dailyFinancialReport.write("Profit " + calculateTotalSales()); // calculate the profit
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

    public static void generateInventoryReport(Inventory currntInventory, String date) {

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

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 30 days\n");

            // Write code for writing expenses onto the file

            dailyInventoryReport.write("__________________________________________________________\n");
            dailyInventoryReport.write("Medications with expiration date soon in the next 1 days\n");

            // Write code for writing expenses onto the file

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
