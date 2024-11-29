import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Inventory {
    private ArrayList<Medication> medicationList = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    
    public ArrayList<Medication> loadMedicationsFromCSV(String filePath) throws FileNotFoundException, IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) { 
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) { 
                String[] values = line.split(","); 
                String type = values[0].trim(); 
                String name = values[1].trim(); 
                String costPerGram = values[2].trim();
                String quantityGrams = values[3].trim();
                String description = values[4].trim();
                String restricted = values[5].trim();
            
                //public Medication (String medType, String medName, double medCost, double medQuantityGrams, String medDesc, boolean restriction)
                this.medicationList.add(new Medication(type, name, Double.parseDouble(costPerGram), Double.parseDouble(quantityGrams), description, Boolean.parseBoolean(restricted) ));
            }
        }
        return new ArrayList<>(this.medicationList);
    }

    // Update inventory after an order is placed
    public void updateInventory(Order order) {
        //Update medication quantity in grams
        for (Medication medication : medicationList) {
            if (medication.getName().equals(order.getMedicationName())) {
                medication.setQuantityGrams(medication.getQuantityGrams() + order.getQuantityGrams());
                break; //Assuming there's only one of each type of medication in the arraylist
            }
        }
        //Add to orders
        orders.add(order);
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

    //Sort Medications alphabetically
    public void sortMedicationsByName(){
        medicationList.sort((Medication m1, Medication m2) -> m1.getName().compareTo(m2.getName()));
    }

    public void printAllMedications(){
        for (Medication medication : medicationList) {
            System.out.println(medication);
        }
    }
}
