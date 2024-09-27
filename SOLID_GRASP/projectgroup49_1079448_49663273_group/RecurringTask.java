import java.util.Date;

public class RecurringTask extends AbstractTask {
    private Date schedule;
    
    public RecurringTask() {
        
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

}
