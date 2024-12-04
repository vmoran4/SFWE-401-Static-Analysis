import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;


public class Main {

    
    public static void printMenu(){
        System.out.println("Welcome to the Pharmacy Inventory System");
        System.out.println("Please select an option:");
        System.out.println("1.) Sell Medication");
        System.out.println("2.) Restock Medication");
        System.out.println("3.) Retrieve Medication Information");
        System.out.println("4.) Retrieve All Medication Information");
        System.out.println("5.) Manually Generate Report");
        System.out.println("6.) Retrieve Batch Information");
        System.out.println("7.) Report Discepancies b/w Physical and Digital Inventory");
        System.out.println("q.) Exit");
    }

    public static void printReportOptionMenu(){
        System.out.println("Please select a report type to generate:");
        System.out.println("1.) Financial Report");
        System.out.println("2.) Inventory Report");
    }

    //Finds valid dates, gets user input for date selection and returns that date
    public static String requestDateSelection(){
        //FIXME: Current implementation is only for a single target date, not range
        ArrayList<String> validDates = getValidDates();
        System.out.println("Please select a date to generate a report for:");
        for(int i = 0; i < validDates.size(); i++){
            System.out.println((i+1) + ".) " + validDates.get(i));
        }
        Scanner scanner = new Scanner(System.in);
        int selection = Integer.parseInt(scanner.nextLine());
        return validDates.get(selection - 1);
        
    }

    public static ArrayList<String> getValidDates(){
        //Based on existing files in orders directory
        ArrayList<String> validDates = new ArrayList<String>();
        File ordersDir = new File("orders/");
        File[] orderFiles = ordersDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (orderFiles != null && orderFiles.length > 0) {
            for(File file : orderFiles){
                String date = file.getName().substring(0, 10);
                validDates.add(date);
            }
        }
        return validDates;
    }



    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        //Prepare for input
        Scanner scanner = new Scanner(System.in);

        //Ask User if this is initial run (medication quantities and orders should be populating from CSVFiles)
        System.out.println("Is this the first time running the program? (y/n) -- ");
        System.out.println("(In other words should medication quantities and orders be initialized?)");
        System.out.println("NOTE: Selecting 'y' will delete any of today's current orders and medications.");
        String firstRun = scanner.nextLine();
        //Initial run
        if(firstRun.equals("y")){

            //Admin password check
            System.out.println("Enter the admin password:");
            String password = scanner.nextLine();
            if(!password.equals("admin")){
                System.out.println("Incorrect password.");
                System.exit(0);
            }
            
            // Initialize and read medication CSV file
            try {
                inventory.loadMedicationsFromCSV("CSVFiles/InitialMedication.csv");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Sort medications
            inventory.sortMedicationsByName();

            //Load initial orders from CSV
            try {
                inventory.loadOrdersFromCSV("CSVFiles/Orders.csv", true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else{
            //Read in most recent orders and medications file based on date in filename
            String mostRecentDate = "";
            try {
                //Find most recent date from orders directory files
                File ordersDir = new File("orders/");
                File[] orderFiles = ordersDir.listFiles((dir, name) -> name.endsWith(".csv"));
                if (orderFiles != null && orderFiles.length > 0) {
                    Arrays.sort(orderFiles, (f1, f2) -> {
                        String date1 = f1.getName().replaceAll("[^0-9]", "");
                        String date2 = f2.getName().replaceAll("[^0-9]", "");
                        return date2.compareTo(date1);
                    });

                    // Load the most recent order file
                    String mostRecentOrderFile = orderFiles[0].getName();
                    mostRecentDate = mostRecentOrderFile.substring(0, 10);
                    System.out.println("Loading orders from the most recent file: " + mostRecentOrderFile);
                    inventory.loadOrdersFromCSV("orders/" + mostRecentOrderFile, false);

                    // Load the most recent medication file
                    System.out.println("Loading medications from the most recent file: " + mostRecentDate + "Medications.csv");
                    inventory.loadMedicationsFromCSV("medications/" + mostRecentDate + "Medications.csv");
                } else {
                    System.out.println("No order files found in the orders directory.");
                }
            
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //Print curr Inventory
        inventory.printAllMedications();
        //Main menu
        
        boolean running = true;
        while(running){
            System.out.println();
            printMenu();
            String input = scanner.nextLine();
            switch(input){
                //Selling 
                case "1":
                    System.out.println("Enter the name of the medication you would like to sell:");
                    String medicationName = scanner.nextLine();
                    //Check if medication name exists
                    if(inventory.getMedication(medicationName) == null){
                        System.out.println("Medication not found.");
                        break;
                    }
                    System.out.println("Enter the quantity in grams:");
                    double quantity = Double.parseDouble(scanner.nextLine());
                    inventory.makePurchase(medicationName, quantity);
                    break;
                
                //Manual Restock
                case "2":
                    //Admin password check
                    System.out.println("Enter the admin password:");
                    String password = scanner.nextLine();
                    if(!password.equals("admin")){
                        System.out.println("Incorrect password.");
                        break;
                    }
                    System.out.println("Enter the name of the medication you would like to restock:");
                    String medName = scanner.nextLine();
                    Medication medication = inventory.getMedication(medName);
                    if(medication == null){
                        System.out.println("Medication not found.");
                        break;
                    }
                    System.out.println("Enter the quantity in grams:");
                    double quantityGrams = Double.parseDouble(scanner.nextLine());
                    Order order = new Order(medName, quantityGrams, "12/31/2021", 1, "Manual Restock");
                    inventory.updateInventoryOrder(order);
                    break;

                //Retrieve Individual Medication Info -- More specific
                case "3":
                    System.out.println("Enter the name of the medication you would like to retrieve information for:");
                    String medName1 = scanner.nextLine();
                    Medication medication1 = inventory.getMedication(medName1);
                    System.out.println();
                    if(medication1 == null){
                        System.out.println("Medication not found.");
                        break;
                    }
                    System.out.println(medication1.toString());
                    System.out.println("Restriction Status: " + medication1.getRestrictionStatus() + ", Description: " + medication1.getDescription());
                    break;

                //Retrieve All Medication Info -- Less specific
                case "4":
                    inventory.printAllMedications();
                    break;
                case "5":
                    //Check for admin password
                    System.out.println("Enter the admin password:");
                    String password1 = scanner.nextLine();
                    if(!password1.equals("admin")){
                        System.out.println("Incorrect password.");
                        break;
                    }
                    //Select Report Type
                    printReportOptionMenu();
                    String reportOption = scanner.nextLine();
                    //Check for invalid input
                    if(!reportOption.equals("1") && !reportOption.equals("2")){
                        System.out.println("Invalid input.");
                        break;
                    }

                    //Select  Date
                    String targetDate = requestDateSelection();

                    //Generate Report
                    switch(reportOption){
                        case "1":
                            //FIXME: implement financial report
                            
                            break;
                        case "2":
                            //FIXME: implement inventory report
                            break;
                    }
                    break;
                case "6": 
                    System.out.println("Input Batch Number: ");

                    int batchNum = Integer.parseInt(scanner.nextLine());
                    inventory.getBatchInfo(batchNum);

                    break;

                case "7":
                    System.out.println("Enter the admin password:");
                    String password2 = scanner.nextLine();
                    if(!password2.equals("admin")){
                        System.out.println("Incorrect password.");
                        break;
                    }
                    System.out.println("Enter medication name of medication with discrepancy");
                    String medName2 = scanner.nextLine();
                    Medication medication2 = inventory.getMedication(medName2);
                    if(medication2 == null){
                        System.out.println("Medication not found.");
                        break;
                    }
                    System.out.println("Enter the new quantity in grams:");
                    double quantityGrams2 = Double.parseDouble(scanner.nextLine());
                    inventory.updateInventoryQuantity(medName2, quantityGrams2);
                    break;

                case "q":
                    System.out.println("Exiting...");
                    running = false;
                    break;
            }
        }

        scanner.close();

        //Export current inventory medications and orders to CSV
        inventory.exportCurrInventory();
        inventory.exportCurrOrders();

        //Run end of day tasks after exiting main loop
        EndOfDay.endOfDay();
    }
}
