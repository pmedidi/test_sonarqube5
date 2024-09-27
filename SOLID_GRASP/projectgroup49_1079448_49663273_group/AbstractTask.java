import java.util.Date;

public abstract class AbstractTask {
    protected String title;
    protected String descrip;
    protected Date duedat;
    protected String priority;
    protected String status;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescrip() {
        return descrip;
    }
    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
    public Date getDuedat() {
        return duedat;
    }
    public void setDuedat(Date duedat) {
        this.duedat = duedat;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
