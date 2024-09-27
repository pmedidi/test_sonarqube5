
public class TeamLead extends AbstractTeamMember implements Manager, ProjectOwner {
    private int seniority;
    private int numEmployees;
    private IProject p;
    
    public int getSeniority() {
        return seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }

    public int getNumEmployees() {
        return numEmployees;
    }

    public void setNumEmployees(int numEmployees) {
        this.numEmployees = numEmployees;
    }

    public IProject getP() {
        return p;
    }

    public void setP(IProject p) {
        this.p = p;
    }

    @Override
    public void makeProject() {

    }

    @Override
    public void hire() {

    }

    @Override
    public void fire() {

    }

    @Override
    public void assignRole() {

    }

}
