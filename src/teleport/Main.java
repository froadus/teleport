package teleport;

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

	public static List<ProtectedArea> area;
	public static long saveTime = System.currentTimeMillis();

	public static Map<String, InProgressArea> inProgressAreas = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		instance = this;

		getLogger().info("Froadus plugin startades");
		new PlayerListener(this);

		File dir = getDataFolder();

		if (!dir.exists()) {
			if(!dir.mkdir()) {
				getLogger().info("Kunde inte skapa mappen!");
			}
		}

		Main.area = (List<ProtectedArea>) load(new File(getDataFolder(), "protected_areas.dat"));

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
		save(Main.area, new File(getDataFolder(), "protected_areas.dat"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("newcuboid") && sender instanceof Player) {
			Player p = (Player) sender;

			String owner = sender.getName();

			Set<String> users = Arrays
					.stream(args)
					.filter(s -> !s.equals(owner))
					.collect(Collectors.toSet());

			inProgressAreas.put(owner, new InProgressArea(owner, users));

			p.sendMessage("Välj den första punkten för din area");

			return true;
		}

		if (command.getName().equalsIgnoreCase("listcuboid") && sender instanceof Player) {
			Player p = (Player) sender;

			if (Main.area.isEmpty()) {
				p.sendMessage("Det finns inga areor!");
			} else {
				for (int i = 0; i < Main.area.size(); i++) {
					ProtectedArea area = Main.area.get(i);

					String msg = MessageFormat.format("Area nr {0}: Ägare: {1}, Åtkomst: {2}, Plats(xyz,xyz): {3} / {4} / {5} / {6} / {7} / {8}",
							i + 1,
							area.owner,
							area.users.stream().collect(Collectors.joining(", ")),
							area.x1,
							area.y1,
							area.z1,
							area.x2,
							area.y2,
							area.z2);

					p.sendMessage(msg);
				}
			}

			return true;
		}

		if (command.getName().equalsIgnoreCase("remcuboid") && sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 1) {
				int id = Integer.parseInt(args[0])-1;
				if (id < Main.area.size() && id >= 0) {
					Main.area.remove(id);

					if ((System.currentTimeMillis() - Main.saveTime) > 1000) {
						save(Main.area, new File(getDataFolder(), "protected_areas.dat"));
						Main.saveTime = System.currentTimeMillis();
					}
					p.sendMessage("Den skyddade ytan " + args[0] + " har tagits bort!");
					return true;
				}
			}
		}

		return false;
	}

	public static Main getInstance() {
		return instance;
	}
}
