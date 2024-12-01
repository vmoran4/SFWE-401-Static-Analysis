import java.util.Scanner;
import java.io.IOException;

public class Main {

    
    public static void printMenu(){
        System.out.println("Welcome to the Pharmacy Inventory System");
        System.out.println("Please select an option:");
        System.out.println("1.) Sell Medication");
        System.out.println("2.) Restock Medication");
        System.out.println("3.) Retrieve Mecication Information");
        System.out.println("q.) Exit");
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

        
        //Main menu
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        //FIXME: implement main loop
        while(running){
            printMenu();
            String input = scanner.nextLine();
            switch(input){
                
                case "q":
                    System.out.println("Exiting...");
                    running = false;
                    break;
            }
        }


        scanner.close();
    }
}
