import java.time.LocalTime;

public class EndOfDay{

    public static void endOfDay(){
        while (true) { 
            LocalTime current = LocalTime.now();

            if (current.getHour()==17 && current.getMinute()==0){
                runEndOfDayTasks();



                try {
                    Thread.sleep(60*1000);
                } catch (InterruptedException e){
                    System.out.println("End of Day script interrupted");
                    Thread.currentThread().interrupt();
                    break;
            }


            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("End of Day script interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    public static void runEndOfDayTasks(){
        
    }


}