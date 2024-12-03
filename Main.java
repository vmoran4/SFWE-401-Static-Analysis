import java.io.IOException;
import java.util.Scanner;

public class Main {

    
    public static void printMenu(){
        System.out.println("Welcome to the Pharmacy Inventory System");
        System.out.println("Please select an option:");
        System.out.println("1.) Sell Medication");
        System.out.println("2.) Restock Medication");
        System.out.println("3.) Retrieve Mecication Information");
        System.out.println("4.) Manually Generate Report");
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
        
        inventory.sortMedicationsByName();
        inventory.printAllMedications();

        //Load orders from CSV
        try {
            inventory.loadOrdersFromCSV("CSVFiles/Orders.csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        //Main menu
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        //FIXME: implement main loop
        while(running){
            printMenu();
            String input = scanner.nextLine();
            switch(input){
                

                case "4":
                    printReportOptionMenu();
                    String reportType=scanner.nextLine();

                case "q":
                    System.out.println("Exiting...");
                    running = false;
                    break;
            }
        }


        scanner.close();
    }
}
