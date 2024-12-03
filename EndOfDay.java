import java.time.LocalTime;

public class EndOfDay{

    public static void endOfDay(){
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

    }

    //FIXME need to do automatic report generation, experation report, what else?
    public static void runEndOfDayTasks(){
        
        //Export the current inventory to csv. 

    }

    public static void main(String[] args) {
        //In reality this would run from task scheduler, but for testing it will run after main loop ends
        endOfDay();
    }

}