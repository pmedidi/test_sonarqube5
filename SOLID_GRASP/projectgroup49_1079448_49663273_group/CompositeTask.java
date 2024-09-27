import java.util.List;

public class CompositeTask extends AbstractTask {
    private List<AbstractTask> subtasks;
    private CompositeTask() {
        
    }
    
    public List<AbstractTask> getSubtasks() {
        return subtasks;
    }
    public void setSubtasks(List<AbstractTask> subtasks) {
        this.subtasks = subtasks;
    }
}
