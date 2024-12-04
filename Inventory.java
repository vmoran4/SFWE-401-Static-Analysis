import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;

public class Inventory {
    private ArrayList<Medication> medicationList = new ArrayList<>();
    //FIXME: handling for removing and modifying orders after sales and sell by
    //DO NOT SORT. Append order matters for sell by date and usage
    private ArrayList<Order> orders = new ArrayList<>(); //Shall be retained until sell by date
    
    public ArrayList<Medication> getMedicationList() {
        return new ArrayList<>(medicationList);
    }
    public ArrayList<Order> getOrders() {
        return new ArrayList<>(orders);
    }
    
    public ArrayList<Medication> loadMedicationsFromCSV(String filePath) throws FileNotFoundException, IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) { 
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) { 
                String[] values = line.split(","); 
                String type = values[0].trim(); 
                String name = values[1].trim(); 
                double costPerGram = Double.parseDouble(values[2].trim());
                double quantityGrams = Double.parseDouble(values[3].trim());
                String description = values[4].trim();
                boolean restricted = Boolean.parseBoolean(values[5].trim());
                
                boolean found = false;
                for (Medication medication : medicationList) {
                    if (medication.getName().equals(name)) {
                        medication.setQuantityGrams(medication.getQuantityGrams() + quantityGrams);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    //public Medication (String medType, String medName, double medCost, double medQuantityGrams, String medDesc, boolean restriction)
                    this.medicationList.add(new Medication(type, name, costPerGram, quantityGrams, description, restricted));
                }
            }
        }
        return new ArrayList<>(this.medicationList);
    }

    //PRECONDITION: medications loaded from CSV
    public void loadOrdersFromCSV(String filePath) throws FileNotFoundException, IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) { 
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) { 
                String[] values = line.split(","); 
                String medicationName = values[0].trim(); 
                double quantityGrams = Double.parseDouble(values[1].trim()); 
                String expDate = values[2].trim();
                int batchNumber = Integer.parseInt(values[3].trim());
                String supplier = values[4].trim();
                //public Order(String medicationName, double quantityGrams, String expDate, int batchNumber, String supplier)
                Order order = new Order(medicationName, quantityGrams, expDate, batchNumber, supplier);
                updateInventoryOrder(order);

            }
        }
    }

    // Update inventory medications and orders after an order is placed
    public void updateInventoryOrder(Order order) {
        //Update medication quantity in grams
        double costPerGram = 0.0;
        for (Medication medication : medicationList) {
            if (medication.getName().equals(order.getMedicationName())) {
                medication.setQuantityGrams(medication.getQuantityGrams() + order.getQuantityGrams());
                costPerGram = medication.getCostPerGram();
                break; //Assuming there's only one of each type of medication in the arraylist
            }
        }
        //Add to orders
        orders.add(order);
        //Log transaction
        TransactionLogger logger = new TransactionLogger();
        logger.logOrder(order, costPerGram);
    }

    // Update orders arraylist after a sale is made by removing quantity from orders based on sale.
    // Purpose: keep track of medications sell by date
    public void updateOrdersSale(String medicationName, double quantityGrams){
        double remainingQuantity = quantityGrams;
        for (Order order : orders) {
            if (order.getMedicationName().equals(medicationName)) {
                order.setQuantityGrams(order.getQuantityGrams() - quantityGrams);
                if(order.getQuantityGrams() <= 0){
                    remainingQuantity = -order.getQuantityGrams();
                    orders.remove(order);
                }
                else{
                    // remainingQuantity = 0;
                    break;
                }
            }
        }
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

    // Returns true if purchase is successful, false otherwise
    // update inventory after a purchase is made
    public boolean makePurchase(String medicationName, double quantityGrams){
        Medication medication = getMedication(medicationName);
        if (medication == null) {
            System.out.println("Medication not found");
            return false;
        }
        if (medication.getQuantityGrams() < quantityGrams) {
            System.out.println("Not enough stock");
            return false;
        }

        medication.setQuantityGrams(medication.getQuantityGrams() - quantityGrams);
        //Log Transaction
        double totalCost = quantityGrams * medication.getCostPerGram();
        TransactionLogger logger = new TransactionLogger();
        System.out.println(logger.logSale(quantityGrams, totalCost, medicationName));

        //Check if medication is low stock
        if(medication.checkLowStock()){
            System.out.println(logger.logLowStock(medication));
            System.out.println("Automatically Reordering 1000 stock");
            //Automatic Restock
            Order order = new Order(medication.getName(), 1000, "2022-12-31", 0, "Automatic Restock");
            updateInventoryOrder(order);
        }

        //Remove orders that have been sold out
        updateOrdersSale(medicationName, quantityGrams);
        return true;
    }

    //Export the current inventory to csv. Meant to happen at the end of the day
    public void exportCurrInventory(String filenameToWrite){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventories/" + filenameToWrite))) {
            writer.write("Type,Name,CostPerGram,QuantityGrams,Description,Restricted\n");
            for (Medication medication : medicationList) {
                writer.write(medication.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    //Default exportCurrInventory constructor for end of day
    public void exportCurrInventory(){
        exportCurrInventory("inventories/" + TransactionLogger.getCurrDate() + "Inventory.csv");
    }

    public void getBatchInfo(int batchNum){
        boolean batchFound = false;
        for(int w = 0; w < orders.size(); w++){
            if( orders.get(w).getBatchNumber() == batchNum){
                System.out.println("Medication Name: " + orders.get(w).getMedicationName());
                System.out.println("Batch Number: " + orders.get(w).getBatchNumber());
                System.out.println("Expiration Date: " + orders.get(w).getExpDate());
                System.out.println("Medication Name: " + orders.get(w).getQuantityGrams());
                System.out.println("Medication Name: " + orders.get(w).getSupplier());
                batchFound = true;
            }
        }
        if (!batchFound) {
            System.out.println("Batch not found");
        }
    }
}
