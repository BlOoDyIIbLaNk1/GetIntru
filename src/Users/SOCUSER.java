package Users;


public class SOCUSER  extends User {
	public SOCROLE role;

    public SOCUSER() {
        super(); 
    }

    public SOCUSER(String id, String username, String pwd,SOCROLE role) {
        super(id, username, pwd);
        this.role=role;
    }
    public SOCROLE getRole() {
    	return role;
    }
    public void setRole(SOCROLE role) {
    	this.role=role;
    }
    public boolean isL1() {
    	return role==SOCROLE.L1;
    }
    public boolean isL2() {
    	return role==SOCROLE.L2;
    }
    public boolean isLeader() {
    	return role==SOCROLE.Leader;
    }
    
  
    
}
