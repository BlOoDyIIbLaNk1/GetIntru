package Users;

import java.sql.Date;

public class SOCL1  extends User {

    public SOCL1() {
        super(); 
    }

    public SOCL1(String id, String username, String pwd) {
        super(id, username, pwd);
    }
    
    public Alerte modifierAlerte(Alerte a, String Type, String Severite, Date date, String AdrrIpDest , String AdrrIpSource  ) {
        a.setType(Type);
        a.setSeverite(Severite);
        a.setDate(date);
        a.setAdrrIpDest(AdrrIpDest);
        a.setAdrrIpSource(AdrrIpSource);
        return a; 
    }
    
}
