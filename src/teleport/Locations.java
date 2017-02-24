package teleport;

import java.io.Serializable;
import java.util.Set;

@SuppressWarnings("serial")
public class Location implements Serializable {

        public String name;

	 	public Location location;
	    
	    public Location(String name, Location location) {
	        this.name = name;

	        this.location = location;
	    }
}
