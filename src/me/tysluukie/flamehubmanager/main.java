package me.tysluukie.flamehubmanager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import com.connorlinfoot.titleapi.TitleAPI;
import com.nametagedit.plugin.NametagEdit;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class main extends JavaPlugin implements Listener {
	public static main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static int currentLine = 0;
	public static int tid = 0;
	public static int running = 1;
	public static long interval = 120;
	
//PluginStart
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getConfig().addDefault("messages","FlameHubManager/hubmessages.txt");
		getConfig().addDefault("lockdown","false");
		getConfig().addDefault("lockreason","lockdown");
		getConfig().addDefault("spawn.x", "");
		getConfig().addDefault("spawn.y", "");
		getConfig().addDefault("spawn.z", "");
		getConfig().addDefault("spawn.world", "");
		getConfig().options().copyDefaults(true);
		saveConfig();
		Plugin pex = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
		if(pex.equals(null)) {
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "ERROR: ===================================");
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "ERROR: FlameHubManager");
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "ERROR: PEX plugin missing: shutting down");
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "ERROR: ===================================");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				try {
					broadcastMessage("plugins/" + getConfig().getString("messages"));
				}	catch (IOException e) {
					
				}
			}
		}, 0, interval * 20);
}
	
//Broadcaster
	public static void broadcastMessage(String fileName) throws IOException {
		FileInputStream fs;
		fs = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for(int i = 0; i < currentLine; ++i)
			br.readLine();
		String line = br.readLine();
		br.close();
		line = line.replaceAll("&f", ChatColor.WHITE + "");
	    line = line.replaceAll("&0", ChatColor.BLACK + "");
	    line = line.replaceAll("&1", ChatColor.DARK_BLUE + "");
	    line = line.replaceAll("&2", ChatColor.DARK_GREEN + "");
	    line = line.replaceAll("&3", ChatColor.DARK_AQUA + "");
	    line = line.replaceAll("&4", ChatColor.DARK_RED + "");
	    line = line.replaceAll("&5", ChatColor.DARK_PURPLE + "");
	    line = line.replaceAll("&6", ChatColor.GOLD + "");
	    line = line.replaceAll("&7", ChatColor.GRAY + "");
	    line = line.replaceAll("&8", ChatColor.DARK_GRAY + "");
	    line = line.replaceAll("&9", ChatColor.BLUE + "");
	    line = line.replaceAll("&a", ChatColor.GREEN + "");
	    line = line.replaceAll("&b", ChatColor.AQUA + "");
	    line = line.replaceAll("&c", ChatColor.RED + "");
	    line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
	    line = line.replaceAll("&e", ChatColor.YELLOW + "");
	    line = line.replaceAll("&f", ChatColor.WHITE + "");
	    line = line.replaceAll("&k", ChatColor.MAGIC + "");
	    line = line.replaceAll("&l", ChatColor.BOLD + "");
	    line = line.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
	    line = line.replaceAll("&n", ChatColor.UNDERLINE + "");
	    line = line.replaceAll("&o", ChatColor.ITALIC + "");
	    line = line.replaceAll("&r", ChatColor.RESET + "");
	    line = line.replaceAll("$n", "\n" + "");
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.WHITE + line);
		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(fileName)));
		lnr.skip(Long.MAX_VALUE);
		int lastLine = lnr.getLineNumber();
	    lnr.close();
		if(currentLine + 1 == lastLine + 1) {
			currentLine = 0;
		} else {
			currentLine++;
		}
	}
	
//JoinEvent	
@EventHandler
	public void onJoin(PlayerJoinEvent e) {
			e.getPlayer().performCommand("spawn");
			Player p = e.getPlayer();
		    ItemStack pets = new ItemStack(Material.DRAGON_EGG);
		    ItemMeta petsmeta = pets.getItemMeta();
		    petsmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&k;;&r&cPets&4&k;;&r"));
		    pets.setItemMeta(petsmeta);
		    p.getInventory().setItem(3, pets);
			ScoreboardManager sm = Bukkit.getScoreboardManager();
        	Scoreboard onJoin = sm.getNewScoreboard();
        	Objective o = onJoin.registerNewObjective("FreedomCraft", "dummy");
        	o.setDisplaySlot(DisplaySlot.SIDEBAR);
        	o.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "FlameNetwork");
        	Score spacer = null;
        	Score sp = null;
        	Score nieuws = null;
        	Score nieuws1 = null;
        	Score sc = null;
        	Score ip = null;
        	spacer = o.getScore(ChatColor.AQUA + "");
        	spacer.setScore(6);
        	Score players = o.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Welcome, " + ChatColor.GREEN + "" + ChatColor.BOLD + e.getPlayer().getName());
        	players.setScore(5);
        	sp = o.getScore(ChatColor.RED + "");
        	sp.setScore(4);
        	nieuws = o.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Rank: ");
        	nieuws.setScore(3);
        	getConfig().addDefault("rank." + e.getPlayer().getName(),"Speler");
        	getConfig().options().copyDefaults(true);
        	saveConfig();
        	nieuws1 = o.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + getConfig().getString("rank." + e.getPlayer().getName()));
        	nieuws1.setScore(2);
        	sc = o.getScore(ChatColor.BLUE + "");
        	sc.setScore(1);
        	ip = o.getScore(ChatColor.DARK_RED + "" + ChatColor.BOLD + "FlameNetwork.g-s.nu ");
        	ip.setScore(0);
        	e.getPlayer().setScoreboard(onJoin);
        	TitleAPI.sendTitle(e.getPlayer(), 30, 5 * 20, 10, ChatColor.RED + "Welcome!", "to Flamenetwork!");
        	Player target = e.getPlayer();
        	PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        	if(getConfig().get("rank." + e.getPlayer().getName()).equals("Owner")) {
        		String prefix = "&7[&4Owner&7]&6";
        		user.addGroup("owner");
				NametagEdit.getApi().setPrefix(target, prefix);
    		}
        	if(getConfig().get("rank." + e.getPlayer().getName()).equals("Moderator")) {
        		String prefix = "&7[&2Mod&7]&6";
        		user.addGroup("mod");
				NametagEdit.getApi().setPrefix(target, prefix);
    		}
    		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Helper")) {
    			String prefix = "&7[&3Helper&7]&6";
    			user.addGroup("helper");
				NametagEdit.getApi().setPrefix(target, prefix);
    		}
    		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Admin")) {
    			String prefix = "&7[&1Admin&7]&6";
    			user.addGroup("admin");
				NametagEdit.getApi().setPrefix(target, prefix);
    		}
    		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
    			String prefix = "&7[&3Speler&7]&6";
    			user.addGroup("default");
				NametagEdit.getApi().setPrefix(target, prefix);
    		}
	if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
			if(getConfig().get("lockdown").equals("true")) {
				e.setJoinMessage("");
				String lockreason = getConfig().getString("lockreason");
				e.getPlayer().kickPlayer(lockreason);
			}
			if(getConfig().get("lockdown").equals("false")) {
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage(ChatColor.GRAY + "_____________________________________________");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "                  <FlameNetwork>");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "      Welcome " + e.getPlayer().getName() + "!");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "      Join a server with your" + ChatColor.GREEN + "" + ChatColor.BOLD + " Server Selector");
				e.getPlayer().sendMessage("");
				e.getPlayer().sendMessage(ChatColor.GRAY + "_____________________________________________");
				e.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + e.getPlayer().getName());
			}
		} else {
			for(Player all : Bukkit.getServer().getOnlinePlayers()) {
				if(getConfig().get("rank." + all.getPlayer().getName()).equals("Speler")) {
	    			//do nothing
	    		} else {
	    			all.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "Staff" + ChatColor.GRAY + "] " + ChatColor.GREEN + "" + ChatColor.BOLD + e.getPlayer().getName() + ChatColor.GOLD + "" + ChatColor.BOLD + "joines the server!");
	    		}
			}
			e.getPlayer().setGameMode(GameMode.CREATIVE);
			e.getPlayer().performCommand("staff");
			e.setJoinMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage(ChatColor.GRAY + "_____________________________________________");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "                  <FlameNetwork>");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "      Welcome " + e.getPlayer().getName() + "!");
			e.getPlayer().sendMessage("");
			e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      Staff-mode activated!");
			e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      You are invisible to normal players!!");
			e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      Turn your staff mode on with: " + ChatColor.GOLD + "/staff!");
			e.getPlayer().sendMessage(ChatColor.GRAY + "_____________________________________________");
			return;
		}
	}

//PETS
@EventHandler
	public void invClick(PlayerInteractEvent e){
    Player p = e.getPlayer();
    Action action = e.getAction();
    if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)){
        if(p.getItemInHand().getType() == Material.DRAGON_EGG){
            e.setCancelled(true);
            p.performCommand("pet menu");
            p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Succesfully opened your pets menu!");
           
        }
    }
}
	public void onDrop(PlayerDropItemEvent e) {
		ItemStack stack = e.getItemDrop().getItemStack();
		ItemStack pets = new ItemStack(Material.DRAGON_EGG);
		if(stack.equals(pets)) {
			e.setCancelled(true);
		} else {
			//do nothing
		}
	}

//Quitevent	
@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage("");
	}

//CHAT
@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + getConfig().getString("rank." + e.getPlayer().getName()) + ChatColor.GRAY + "] " + ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " >> " + ChatColor.GRAY + e.getMessage());
			return;
		}
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("moderator")) {
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + getConfig().getString("rank." + e.getPlayer().getName()) + ChatColor.GRAY + "] " + ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " >> " + ChatColor.GRAY + e.getMessage());
			return;
		}
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Helper")) {
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + getConfig().getString("rank." + e.getPlayer().getName()) + ChatColor.GRAY + "] " + ChatColor.GREEN + e.getPlayer().getName() + ChatColor.RED + " >> " + ChatColor.GRAY + e.getMessage());
			return;
		}
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Admin")) {
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + getConfig().getString("rank." + e.getPlayer().getName()) + ChatColor.GRAY + "] " + ChatColor.AQUA + e.getPlayer().getName() + ChatColor.RED + " >> " + ChatColor.GRAY + e.getMessage());
			return;
		}
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + getConfig().getString("rank." + e.getPlayer().getName()) + ChatColor.GRAY + "] " + ChatColor.DARK_RED + e.getPlayer().getName() + ChatColor.RED + " >> " + ChatColor.GRAY + e.getMessage());
	}

//BlockbreakEvent
@SuppressWarnings("deprecation")
@EventHandler	
	public void onBlockBreak(BlockBreakEvent e){
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
			e.setCancelled(true);
		}
		if(e.getBlock().getType().getId() == 70) {
			e.getPlayer().sendMessage(ChatColor.GREEN + "Launchpad removed!");
		}
	}

//BlockplaceEvent
@SuppressWarnings("deprecation")
@EventHandler	
	public void onBlockPlace(BlockPlaceEvent e){
		if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
			e.setCancelled(true);
		}
		if(e.getBlock().getType().getId() == 70) {
			if(e.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 133) {
				e.getPlayer().sendMessage(ChatColor.GREEN + "Launchpad created!");
			}
		}
	}

//PlayerDamageEvent
@EventHandler	
	public void playerDamage(EntityDamageEvent e){
	Entity entity = e.getEntity();
	if(entity instanceof Player) {
	Player p = (Player)entity;
		e.setCancelled(true);
		p.setHealth(20);
	} else {
		return;
	}
	}

//Foodlossevent
@EventHandler
	public void onFoodLoss(FoodLevelChangeEvent e) {
	e.setCancelled(true);
}

//CommandBlocker
@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
	if(e.getMessage().contains("pex")) {
		if(e.getPlayer().hasPermission("*")) {
			return;
		} else {
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
			e.setCancelled(true);
		}
	}
	if(getConfig().get("rank." + e.getPlayer().getName()).equals("Speler")) {
	if(e.getMessage().contains("plugins")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	if(e.getMessage().contains("pl")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	if(e.getMessage().contains("?")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	if(e.getMessage().contains("bukkit:help")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	if(e.getMessage().contains("version")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	if(e.getMessage().contains("about")) {
		e.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are not allowed to use this command!");
		e.setCancelled(true);
	}
	} else {
		return;
	}
}

//launchpad
@SuppressWarnings("deprecation")
@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
  Player player = event.getPlayer();
  Location playerLoc = player.getLocation();
  int ID = playerLoc.getWorld().getBlockAt(playerLoc).getRelative(0, -1, 0).getTypeId();
  int plate = playerLoc.getWorld().getBlockAt(playerLoc).getTypeId();
  if (((player instanceof Player)))
  {
    if (ID == 133)
    {
      if (plate == 70)
      {
        player.setVelocity(player.getLocation().getDirection().multiply(5));
        player.setVelocity(new Vector(player.getVelocity().getX(), 1.0D, player.getVelocity().getZ()));
        player.playSound(player.getLocation(), Sound.valueOf("BAT_TAKEOFF".toUpperCase()), 1.0F, 1.0F);
        for (Player all : Bukkit.getOnlinePlayers())
          all.playEffect(player.getLocation(), Effect.valueOf("ENDER_SIGNAL".toUpperCase()), 4);
      }
    }
  }
}

//Commands
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//help
		if(cmd.getName().equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.GRAY + "_____________________________________________");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GOLD + "/skyblock: " + ChatColor.GREEN + "Join skyblock!");
			sender.sendMessage(ChatColor.GOLD + "/kitpvp: " + ChatColor.GREEN + "Join kitpvp!");
			sender.sendMessage(ChatColor.GOLD + "/spawn: " + ChatColor.GREEN + "Go back to the center of the hub!");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GRAY + "_____________________________________________");
			return true;
		}
		//Setrank
		if(cmd.getName().equalsIgnoreCase("setrank")) {
			if(sender.hasPermission("FlameHubManager.admin")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "/setrank (player) (rank)");
				return true;
			}
			if(args.length == 1) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "/setrank (player) (rank)");
				return true;
			}
			if(args.length == 2) {
				Player target = Bukkit.getServer().getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Could not find player " + args[0] + ChatColor.RED + " !");
					return true;
				}
				getConfig().set("rank." + target.getName(), args[1]);
				if(target.equals(sender)) {
					//do nothing
				} else {
				target.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Your rank is now: " + getConfig().getString("rank." + target.getName()) + ChatColor.RED + " !");
				}
				saveConfig();
				Player p = (Player) sender;
				PermissionUser user = PermissionsEx.getUser(p);
				if(args[1].equalsIgnoreCase("owner")) {
					user.addGroup("owner");
				}
				if(args[1].equalsIgnoreCase("admin")) {
					user.addGroup("admin");
				}
				if(args[1].equalsIgnoreCase("moderator")) {
					user.addGroup("mod");
				}
				if(args[1].equalsIgnoreCase("helper")) {
					user.addGroup("helper");
				}
				if(args[1].equalsIgnoreCase("speler")) {
					user.addGroup("default");
					NametagEdit.getApi().clearNametag(target);
				}
				target.kickPlayer(ChatColor.GRAY + "---------------------------------"
						+ ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork]"
						+ ChatColor.GOLD + "Processing rank..."
						+ ChatColor.GREEN + "You can rejoin!"
						+ ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork]"
						+ ChatColor.GRAY + "---------------------------------");
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Player " + args[0] + " has rank: " + args[1] + " !");
				}
		} else { 
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
		}
		}
		//kitpvp BUNGEE
		if(cmd.getName().equalsIgnoreCase("kitpvp")) {
			sender.sendMessage(ChatColor.GOLD + "Sending you to kitpvp...");
			Player p = (Player)sender;
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Connect");
				out.writeUTF("kitpvp");
				} catch (IOException e) {
					e.printStackTrace();
				}
				p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
		}
		//kingdom BUNGEE
		if(cmd.getName().equalsIgnoreCase("skyblock")) {
			sender.sendMessage(ChatColor.GOLD + "Sending you to skyblock...");
			Player p = (Player)sender;
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Connect");
				out.writeUTF("skyblock");
				} catch (IOException e) {
					e.printStackTrace();
				}
				p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
		}
		if(cmd.getName().equalsIgnoreCase("gm")) {
			if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
			} else {
				if(args.length == 0) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "/gm (0/1/2/3) [(player)]");
				}
				if(args.length == 1) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Your gamemode has been updated!");
					if(args[0].equalsIgnoreCase("0")) {
						Player player = (Player) sender;
						player.setGameMode(GameMode.SURVIVAL);
					}
					if(args[0].equalsIgnoreCase("1")) {
						Player player = (Player) sender;
						player.setGameMode(GameMode.CREATIVE);
					}
					if(args[0].equalsIgnoreCase("2")) {
						Player player = (Player) sender;
						player.setGameMode(GameMode.ADVENTURE);
					}
					if(args[0].equalsIgnoreCase("3")) {
						Player player = (Player) sender;
						player.setGameMode(GameMode.SPECTATOR);
					}
				}
				if(args.length == 2) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Gamemode has been updated!");
					Player target = Bukkit.getServer().getPlayer(args[0]);
					if (target == null) {
						sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Could not find player " + args[0] + ChatColor.RED + " !");
						return true;
					}
					if(args[0].equalsIgnoreCase("0")) {
						Player player = (Player) target;
						player.setGameMode(GameMode.SURVIVAL);
					}
					if(args[0].equalsIgnoreCase("1")) {
						Player player = (Player) target;
						player.setGameMode(GameMode.CREATIVE);
					}
					if(args[0].equalsIgnoreCase("2")) {
						Player player = (Player) target;
						player.setGameMode(GameMode.ADVENTURE);
					}
					if(args[0].equalsIgnoreCase("3")) {
						Player player = (Player) target;
						player.setGameMode(GameMode.SPECTATOR);
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("spawn")) {
			if(getConfig().get("spawn.x").equals("")) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "ERROR: missing spawn location, contact staff!");
			} else {
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Teleporting...");
			Player p = (Player) sender;
			double x = getConfig().getDouble("spawn.x");
			double y = getConfig().getDouble("spawn.y");
			double z = getConfig().getDouble("spawn.z");
			World w = Bukkit.getServer().getWorld(getConfig().getString("spawn.world"));
			p.teleport(new Location(w,x,y,z));
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("setspawn")) {
			if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
			} else {
			Player p = (Player) sender;
			double x = p.getLocation().getX();
			double y = p.getLocation().getY();
			double z = p.getLocation().getZ();
			String world = p.getLocation().getWorld().getName();
			getConfig().set("spawn.x", x);
			getConfig().set("spawn.y", y);
			getConfig().set("spawn.z", z);
			getConfig().set("spawn.world", world);
			saveConfig();
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "Location of spawn set!");
			}
		}
		if(cmd.getName().equalsIgnoreCase("flamehub")) {
			if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
			} else {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "/flamehub [reload/stop]");
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "reloading...");
					Bukkit.getServer().getPluginManager().disablePlugin(this);
					Bukkit.getServer().getPluginManager().enablePlugin(this);
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "Reload finished!");
				}
				if(args[0].equalsIgnoreCase("stop")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "stopping FlameHubManager...");
					Bukkit.getServer().getPluginManager().disablePlugin(this);
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.GREEN + "/flamehub [reload/start/stop]");
			}
		}
		}
		if(cmd.getName().equalsIgnoreCase("stopbroadcast")) {
			if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
			} else {
			if(running == 1) {
				Bukkit.getServer().getScheduler().cancelTask(tid);
				Player player = (Player) sender;
				player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + " Broadcast is gestopt!");
				running = 0;
			} else {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.RED + " Broadcast is niet actief!");
			}
			}
		}
			if (commandLabel.equalsIgnoreCase("startbroadcast")) {
				if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
				} else {
				if(running == 1) {
					Player player = (Player) sender;
					player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.RED + " Broadcast is al actief!");
				} else {
					Player player = (Player) sender;
					player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + " Broadcast is gestart!");
					tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
						public void run() {
							try {
								broadcastMessage("plugins/FlameHubManager/hubmessages.txt");
							}	catch (IOException e) {
								
							}
						}
					}, 0, interval * 20);
					running = 1;
				}
			}
			}
			if(cmd.getName().equalsIgnoreCase("shutdown")) {
				if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
				} else {
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				TitleAPI.sendTitle(player, 30, 5 * 20, 10, ChatColor.RED + "Attention!", "Server is shutting down in 5 seconds!");
			    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	                public void run() {
	                    Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Shutting down...");
	                	Bukkit.shutdown();
	                    return;
	                }
	            }, 100);
			}
				}
			}
			//announce command
			if(cmd.getName().equalsIgnoreCase("announce")) {
				if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
				} else {
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(args.length == 0) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "Invalid syntax!");
					return true;
				}
				if(args.length == 1) {
					String message = args[0];
					TitleAPI.sendTitle(player, 30, 5 * 20, 10, ChatColor.RED + "Attention!", message);
					return true;
				}
				if(args.length == 2) {
					String message = args[0] + " " + args[1];
					TitleAPI.sendTitle(player, 30, 5 * 20, 10, ChatColor.RED + "Attention!", message);
					return true;
				}
				if(args.length == 3) {
					String message = args[0] + " " + args[1] + " " + args[2];
					TitleAPI.sendTitle(player, 30, 5 * 20, 10, ChatColor.RED + "Attention!", message);
					return true;
				}
				if(args.length == 4) {
					String message = args[0] + " " + args[1] + " " + args[2] + " " + args[3];
					TitleAPI.sendTitle(player, 30, 5 * 20, 10, ChatColor.RED + "Attention!", message);
					return true;
				}
				if(args.length >= 4) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You can only use 4 words max.");
					return true;
				}
				}
			}
			}
			//lockdown command
			if (commandLabel.equalsIgnoreCase("lock")) {
				if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
					sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[FlameNetwork] " + ChatColor.RED + "You are not allowed to use this command!");
				} else {
				if(args.length == 0) {
					if(getConfig().getString("lockdown").equals("false")){
						getConfig().set("lockreason", ChatColor.GOLD + "Lockdown activated! See you soon!");
						getConfig().set("lockdown", "true");
						saveConfig();
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown is turned on with reason: " + getConfig().getString("lockreason"));
						for(Player player : Bukkit.getServer().getOnlinePlayers()) {
							if(getConfig().get("rank." + sender.getName()).equals("Speler")) {
								player.kickPlayer(getConfig().getString("lockreason"));
							} else {
								player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned on!");
							}
						}
					} else if(getConfig().getString("lockdown").equals("true")){
						getConfig().set("lockreason", "");
						getConfig().set("lockdown", "false");
						saveConfig();
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned off!");
					}
					}
				if(args.length == 1) {
					if(getConfig().getString("lockdown").equals("false")){
						String reason = args[1];
						getConfig().set("lockreason", ChatColor.RED +  reason);
						getConfig().set("lockdown", "true");
						saveConfig();
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown is turned on with reason: " + getConfig().getString("lockreason"));
						for(Player player : Bukkit.getServer().getOnlinePlayers()) {
							if (player.hasPermission("lockdown.bypass")) {
								player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned on!");
							} else {
								player.kickPlayer(getConfig().getString("lockreason"));
							}
						}
						} else if(getConfig().getString("lockdown").equals("true")){
						getConfig().set("lockreason", "");
						getConfig().set("lockdown", "false");
						saveConfig();
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned off!");
					}
				}
				if(args.length == 2) {
					if(getConfig().getString("lockdown").equals("false")){
						String reason1 = args[1];
						String reason2 = args[2];
						getConfig().set("lockreason", ChatColor.RED +  reason1 + " " + reason2);
						getConfig().set("lockdown", "true");
						saveConfig();
						sender.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + " De lockdown is aangezet met rede: " + getConfig().getString("lockreason"));
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "De lockdown is aangezet met rede: " + getConfig().getString("lockreason"));
						for(Player player : Bukkit.getServer().getOnlinePlayers()) {
							if (player.hasPermission("lockdown.bypass")) {
								player.sendMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned on!");
							} else {
								player.kickPlayer(getConfig().getString("lockreason"));
							}
						}
						} else if(getConfig().getString("lockdown").equals("true")){
						getConfig().set("lockreason", "");
						getConfig().set("lockdown", "false");
						saveConfig();
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "[FlameNetwork]" + ChatColor.GREEN + "Lockdown turned off!");
					}
				}
				if(args.length >= 2) {
					sender.sendMessage(ChatColor.RED + "Use 2 words max!g");
				}
				} 
			}
		
		return true;
}
}