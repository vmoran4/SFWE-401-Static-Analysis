import java.io.IOException;
import java.util.Scanner;

public class Main {

    
    public static void printMenu(){
        System.out.println("Welcome to the Pharmacy Inventory System");
        System.out.println("Please select an option:");
        System.out.println("1.) Sell Medication");
        System.out.println("2.) Restock Medication");
        System.out.println("3.) Retrieve Medication Information");
        System.out.println("4.) Retrieve All Medication Information");
        System.out.println("5.) Manually Generate Report");
        System.out.println("q.) Exit");
    }

    public static void printReportOptionMenu(){
        System.out.println("Please select a report type to generate:");
        System.out.println("1.) Financial Report");
        System.out.println("2.) Inventory Report");
    }
    

    public static void main(String[] args) {

        // Initialize and read medication CSV file
        Inventory inventory = new Inventory();
        
        try {
            inventory.loadMedicationsFromCSV("CSVFiles/InitialMedication.csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Sort medications
        inventory.sortMedicationsByName();

        //Load orders from CSV
        try {
            inventory.loadOrdersFromCSV("CSVFiles/Orders.csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Print curr Inventory
        inventory.printAllMedications();

        

        //Main menu
        Scanner scanner = new Scanner(System.in);
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
                    printReportOptionMenu();
                    String reportOption = scanner.nextLine();
                    switch(reportOption){
                        case "1":
                            //FIXME: implement financial report
                            break;
                        case "2":
                            //FIXME: implement inventory report
                            break;
                    }

                case "q":
                    System.out.println("Exiting...");
                    running = false;
                    break;
            }
        }

        scanner.close();

        //Run end of day tasks after exiting main loop
        EndOfDay.endOfDay();
    }
}
