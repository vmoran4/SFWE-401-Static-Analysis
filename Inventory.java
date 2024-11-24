import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;


public class Inventory {
    private ArrayList<Medication> medicationList = new ArrayList<>();
    
    

    public ArrayList<Medication> loadMedicationsFromCSV(String filePath) throws FileNotFoundException, IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) { 
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) { 
                String[] values = line.split(","); 
                String type = values[0].trim(); 
                String name = values[1].trim(); 
                String costPerGram = values[2].trim();
                String quantity = values[3].trim();
                String description = values[4].trim();
                String restricted = values[5].trim();
            
            //public Medication (String medType, String medName, double medCost, int medQuantity, String medDesc, boolean restriction)
            this.medicationList.add(new Medication(type, name, Double.parseDouble(costPerGram), Integer.parseInt(quantity), description, Boolean.parseBoolean(restricted) )); //add when a constructor is made
            }
        }
        return new ArrayList<>(this.medicationList);
    }

    // Update inventory after an order is placed
    public void updateInventory(Order order) {
        for (Medication medication : medicationList) {
            if (medication.getName().equals(order.getMedicationName())) {
                medication.setQuantity(medication.getQuantity() + order.getQuantity());
            }
            break; //Assuming there's only one of each type of medication in the arraylist
        }
        //Log transaction? Maybe save that for main?
    }

    public Medication getMedication(String medicationName){
        for (Medication medication : medicationList) {
            if (medication.getName().equals(medicationName)) {
                return medication;
            }
        }
        return null;
    }
}
