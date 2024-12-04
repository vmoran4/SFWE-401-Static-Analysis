import java.time.LocalTime;
import java.io.FileNotFoundException;
import java.io.IOException;

//Made to run be run as script independent of Main.java, while Main.java is not running
public class EndOfDay{


    public static void endOfDay(){
        //Prob don't need this, but it's here for reference
        /* 
        LocalTime current;
        boolean testing =true;
        LocalTime testTime = LocalTime.of(17,0);   //sets test time to 5:00pm

        while (true) { 
            if (testing){
                current=testTime;
            }
            else{
                current = LocalTime.now();
            }


            if (current.getHour()==17 && current.getMinute()==0){
                runEndOfDayTasks();

                try {
                    Thread.sleep(60*1000);     //makes sure it doesn't execute more than once at 5:00pm
                } catch (InterruptedException e){
                    System.out.println("End of Day script interrupted");
                    Thread.currentThread().interrupt();
                    break;
            }


            }

            try {
                Thread.sleep(1000); //reduce cpu usage
            } catch (InterruptedException e) {
                System.out.println("End of Day script interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        */
        runEndOfDayTasks();
    }



    //FIXME need to do automatic report generation, experation report, what else?
    public static void runEndOfDayTasks() {
        
        //Read current medication inventory from csv file
        Inventory inventory = new Inventory();
        try {
            inventory.loadMedicationsFromCSV("medications/" + TransactionLogger.getCurrDate() + "Medications.csv");
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }

        //Read current orders from csv file
        try {
            inventory.loadOrdersFromCSV("orders/" + TransactionLogger.getCurrDate() + "Orders.csv", false);
        } catch (FileNotFoundException e) {
            System.out.println("Orders file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading orders file: " + e.getMessage());
        }

        //Check for invalid medication types (see requirement for types)
            //Right now just returns true if there are any invalid types and prints invalid medications?
        inventory.checkMedicationTypes();
        

        //Remove medications that expire today
        inventory.removeExpiredMedications();

        //Check for any automatic restock needed when expired medications are removed.
        inventory.checkAllLowStock();

        //Export new inventory information to csv files
        inventory.exportCurrInventory();
        inventory.exportCurrOrders();


        //Generate reports
        Report.generateFinanciaReport();
        Report.generateInventoryReport(inventory);
        //Remove reports logs and data that are over five years old
        TransactionLogger logger = new TransactionLogger();
        logger.removeOldLogs();
    }

    public static void main(String[] args) {
        //In reality this would run from task scheduler, but for testing it will run after main loop ends
        endOfDay();
    }

}