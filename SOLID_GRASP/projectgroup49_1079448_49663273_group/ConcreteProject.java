import java.util.List;

public class ConcreteProject implements IProject {
    
    private List<AbstractTask> tasks;
    private List<AbstractTeamMember> team;
    
    public ConcreteProject(List<AbstractTask> tasks, List<AbstractTeamMember> teams) {
        
    }
    
    public void createTask() {
        
    }
    
    @Override
    public void addTask(AbstractTask task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTask() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTeamMember(AbstractTeamMember teamMember) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTeamMember() {
        // TODO Auto-generated method stub

    }

    public List<AbstractTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<AbstractTask> tasks) {
        this.tasks = tasks;
    }

    public List<AbstractTeamMember> getTeam() {
        return team;
    }

    public void setTeam(List<AbstractTeamMember> team) {
        this.team = team;
    }

}
