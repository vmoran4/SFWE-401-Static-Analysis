import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    // Read in a csv file of orders to inventory
    public static void readOrders(String filename, Inventory inventory){
        try {
            File file = new File(filename);
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine(); // Skip the header
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] values = line.split(",");
                    String medicationName = values[0].trim();
                    int quantity = Integer.parseInt(values[1].trim());
                    String expDate = values[2].trim();
                    int batchNumber = Integer.parseInt(values[3].trim());
                    String supplier = values[4].trim();
                    Order order = new Order(medicationName, quantity, expDate, batchNumber, supplier);
                    inventory.updateInventory(order);
                    // FIXME: Log the order here?
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
    
    public static void main(String[] args) {
        // Variables

        // ToDo: 1 CSV file for all types of medications
        // ToDo: 1 CSV file for all orders

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
    }
}
