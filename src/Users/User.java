package Users;

public class User {
	String Id;
	String Username;
	String Pwd;
	
	 public User() { }

	    public User(String id, String username, String pwd) {
	        this.Id = id;
	        this.Username = username;
	        this.Pwd = pwd;
	    }

	    // Getters
	    public String getId() {
	        return Id;
	    }

	    public String getUsername() {
	        return Username;
	    }

	    public String getPwd() {
	        return Pwd;
	    }

	    // Setters
	    public void setId(String id) {
	        this.Id = id;
	    }

	    public void setUsername(String username) {
	        this.Username = username;
	    }

	    public void setPwd(String pwd) {
	        this.Pwd = pwd;
	    }
}
