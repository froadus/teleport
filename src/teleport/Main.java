package teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

	private static Main instance;

	public static List<Locations> area;
	public static long saveTime = System.currentTimeMillis();

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		instance = this;

		getLogger().info("Locations startades");

		File dir = getDataFolder();

		if (!dir.exists()) {
			if(!dir.mkdir()) {
				getLogger().info("Kunde inte skapa mappen!");
			}
		}

		Main.area = (List<Locations>) load(new File(getDataFolder(), "platser.dat"));

		if (Main.area == null) {
			Main.area = new ArrayList<>();
		}
	}

	public void save(Object o, File f) {
		try {
			if(!f.exists()) {
				f.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(o);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object load(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public void onDisable() {
		save(Main.area, new File(getDataFolder(), "platser.dat"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("w") && sender instanceof Player) {
			Player p = (Player) sender;

			if (args.length > 0) {
				for (int i = 0; i < Main.area.size(); i++) {
					Locations a = Main.area.get(i);
					if (a.name.equalsIgnoreCase(args[0])) {
					    Location loc = new Location(p.getWorld(),a.x,a.y,a.z,a.yaw,a.pitch);
						p.teleport(loc);
                        p.sendMessage(ChatColor.GREEN + "Teleporterade till: " + ChatColor.GOLD + a.name);
						return true;
					}
				}
			}
			if (args.length == 0) {
                p.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.WHITE + "Platser för teleportering " + ChatColor.YELLOW + "---------------------");
                p.sendMessage(ChatColor.GRAY + "För teleportering, använd: /w [platsnamn]");
                for (int i = 0; i < Main.area.size(); i++) {
                    p.sendMessage(ChatColor.GOLD + area.get(i).name);
                }
                return true;
            }
            p.sendMessage(ChatColor.RED + "Platsen finns inte!");
			return true;
		}

		if (command.getName().equalsIgnoreCase("wadd") && sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0) {
			    for (int i = 0; i < Main.area.size(); i++) {
			        if (Main.area.get(i).name.equalsIgnoreCase(args[0])) {
                        p.sendMessage(ChatColor.RED + "Namnet änvänds redan!");
			            return true;
                    }
                }
				Main.area.add(new Locations(args[0], p.getLocation()));
                p.sendMessage(ChatColor.GREEN + "Ny plats tillagd: " + ChatColor.GOLD + args[0]);
				if ((System.currentTimeMillis() - Main.saveTime) > 1000) {
					save(Main.area, new File(getDataFolder(), "platser.dat"));
					Main.saveTime = System.currentTimeMillis();
				}
				return true;
			} else {
				return false;
			}
		}

		if (command.getName().equalsIgnoreCase("wlist") && sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.WHITE + "Platser för teleportering " + ChatColor.YELLOW + "---------------------");
			p.sendMessage(ChatColor.GRAY + "För teleportering, använd: /w [platsnamn]");
			for (int i = 0; i < Main.area.size(); i++) {
				p.sendMessage(ChatColor.GOLD + area.get(i).name);
			}
			return true;
		}

		if (command.getName().equalsIgnoreCase("wrem") && sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                for (int i = 0; i < Main.area.size(); i++) {
                    String name = Main.area.get(i).name;
                    if (name.equalsIgnoreCase(args[0])) {
                        Main.area.remove(i);
                        p.sendMessage(ChatColor.GREEN + "Plats borttagen: " + ChatColor.GOLD + name);
                        return true;
                    }
                }
            }
            p.sendMessage(ChatColor.RED + "Platsen finns inte!");
			return true;
		}

		return false;
	}
}
