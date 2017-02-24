package teleport;

import org.bukkit.Location;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Locations implements Serializable {
    public String name;

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public Locations(String name, Location location) {
        this.name = name;

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
}
