import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Iterator;

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
    public void updateInventoryQuantity(String medicationName, double newQuantityGrams){
        for (Medication medication : medicationList) {
            if (medication.getName().equals(medicationName)) {
                medication.setQuantityGrams(newQuantityGrams);
                break;
            }
        }
    }


//Importing stuff
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
    //isInitial indicates whether medicationList should be updated based on orders
    public void loadOrdersFromCSV(String filePath, boolean isInitial) throws FileNotFoundException, IOException {
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
                //If isInitial, update medications based on orders
                if(isInitial){
                    updateInventoryOrder(order);
                }
                else{
                    orders.add(order);
                }

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
    //FIXME: Might be a logic error here in terms of remainingQuantity
    public void updateOrdersSale(String medicationName, double quantityGrams){
        double remainingQuantity = quantityGrams;
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getMedicationName().equals(medicationName)) {
                order.setQuantityGrams(order.getQuantityGrams() - quantityGrams);
                if(order.getQuantityGrams() <= 0){
                    remainingQuantity = -order.getQuantityGrams();
                    iterator.remove();
                } else {
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
        if(medication.isLowStock()){
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

    public void checkAllLowStock(){
        for (Medication medication : medicationList) {
            //Check if medication is low stock
            if(medication.isLowStock()){
                TransactionLogger logger = new TransactionLogger();
                System.out.println(logger.logLowStock(medication));
                System.out.println("Automatically Reordering 1000 stock");
                //Automatic Restock
                Order order = new Order(medication.getName(), 1000, "2022-12-31", 0, "Automatic Restock");
                updateInventoryOrder(order);
            }
        }
    }
    //Returns true if an invalid medication type is found
    //FIXME: should this return a list of medication names that are invalid?
    public boolean checkMedicationTypes(){
        boolean invalidFound = false;
        String[] validTypes = { "Analgesics (Pain Relievers)", "Antibiotics", "Antivirals", "Antifungals", 
                        "Antihistamines", "Antacids and Anti-Ulcer Medications", "Antidepressants", 
                        "Antipsychotics", "Mood Stabilizers", "Hormone Therapy Drugs", 
                        "Cardiovascular Drugs", "Diuretics", "Immunosuppressants", 
                        "Vitamins and Supplements", "Other" };
        for (Medication medication : medicationList) {
            boolean validType = false;
            for (String type : validTypes) {
                if (medication.getType().equals(type)) {
                    validType = true;
                    break;
                }
            }
            if (!validType) {
                System.out.println("Invalid medication type: " + medication.getType());
                invalidFound = true;
                continue;
            }
        }
        return invalidFound;
    }
    //Removes medications from inventory that expire today
    //Returns true if expired medications are found
    public boolean removeExpiredMedications(){
        boolean expiredFound = false;
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getExpDate().equals(TransactionLogger.getCurrDate())) {
                System.out.println("Expired medication: " + order.getMedicationName());

                //Log Expiration
                TransactionLogger logger = new TransactionLogger();
                logger.logExpiration(order);

                //Get previous quantity and decrement by expired quantity
                double newQuantityGrams = getMedication(order.getMedicationName()).getQuantityGrams() - order.getQuantityGrams();
                updateInventoryQuantity(order.getMedicationName(), newQuantityGrams);
                
                //Remove expired order
                iterator.remove();
                expiredFound = true;
            }
        }
        return expiredFound;
    }

    //Returns true if order is found and removed from orders. False otherwise
    public boolean removeOrder(Order order){
        return orders.remove(order);
    }


//Exporting stuff

    //Export the current medicationList to csv. Meant to happen when Main.java exits
    public void exportCurrInventory(String filenameToWrite){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filenameToWrite, false))) {
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
        exportCurrInventory("medications/" + TransactionLogger.getCurrDate() + "Medications.csv");
    }

    //Export the current orders to csv. Meant to happen when Main.java exits
    public void exportCurrOrders(String filenameToWrite){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders/" + filenameToWrite, false))) {
            writer.write("MedicationName,QuantityGrams,ExpDate,BatchNumber,Supplier\n");
            for (Order order : orders) {
            writer.write(order.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    //Default exportCurrOrders constructor for end of day
    public void exportCurrOrders(){
        exportCurrOrders(TransactionLogger.getCurrDate() + "Orders.csv");
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
