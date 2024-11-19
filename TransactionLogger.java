import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class TransactionLogger{
    private String logsDirectoryPath;

    public TransactionLogger(String logsDirectoryPath){
        this.logsDirectoryPath = logsDirectoryPath;
    }

    public TransactionLogger(){
        this.logsDirectoryPath = "logs/";
    }

    
    public void logOrder(Order order){
        //FIXME: Implement this function
    }

    public void logLowStock(Medication medication){
        //FIXME: Implement this function
    }

    public void logExpiration(Medication medication){
        //FIXME: Implement this function
    }

    //Helper function
    public String getCurrentFilename(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + ".txt";
    }
}
