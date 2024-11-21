import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    
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
        
    }
}
