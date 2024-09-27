import java.util.Date;

public class Engineer extends AbstractTeamMember {
    private double salary;
    private Date startDate;
    
    public void designCode() {
        
    }

    public Engineer(double salary, Date startDate) {
        super();
        this.salary = salary;
        this.startDate = startDate;
    }
    
}
