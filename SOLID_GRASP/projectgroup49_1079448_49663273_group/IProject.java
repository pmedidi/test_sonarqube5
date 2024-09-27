public interface IProject {
    public void addTask(AbstractTask task);
    public void removeTask();
    public void addTeamMember(AbstractTeamMember teamMember);
    public void removeTeamMember();
}
