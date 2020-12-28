package org.whyisthisnecessary.eps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.whyisthisnecessary.eps.command.EPSCommand;
import org.whyisthisnecessary.eps.command.EnchantsCommand;
import org.whyisthisnecessary.eps.command.PayTokensCommand;
import org.whyisthisnecessary.eps.command.ScrapCommand;
import org.whyisthisnecessary.eps.command.TokensCommand;
import org.whyisthisnecessary.eps.dependencies.Metrics;
import org.whyisthisnecessary.eps.dependencies.PlaceholderAPIHook;
import org.whyisthisnecessary.eps.dependencies.VaultHook;
import org.whyisthisnecessary.eps.util.DataUtil;
import org.whyisthisnecessary.eps.util.PlugmanUtil;
import org.whyisthisnecessary.eps.visual.EnchantGUI;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

	public static Main plugin;
	public static File DataFolder;
	public static File PackFolder;
	public static File ConfigFile;
	public static File LangFile;
	public static File UUIDDataStore;
	public static FileConfiguration Config;
	public static FileConfiguration LangConfig;
	public static FileConfiguration UUIDDataStoreConfig;
	public static List<Plugin> EnabledPacks = new ArrayList<Plugin>(Arrays.asList());
	
	
	@Override
	public void onEnable()
	{
		plugin = this;
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		Config = this.getConfig();
		ConfigFile = new File(getDataFolder(), "config.yml");
		
		// Create Data Folder
		DataFolder = new File(getDataFolder(), "data");
        if (!DataFolder.exists())
            DataFolder.mkdirs();	
        
        // Create Pack Folder
        PackFolder = new File(getDataFolder(), "packs");
        
		// Create Language File
		LangFile = new File(getDataFolder(), "lang.yml");
		saveDefaultFile("/lang.yml", LangFile);
		LangConfig = YamlConfiguration.loadConfiguration(LangFile);
		
		UUIDDataStore = new File(DataFolder, "usernamestore.yml");
	    if (!UUIDDataStore.exists())
	        createNewFile(UUIDDataStore);
	    UUIDDataStoreConfig = YamlConfiguration.loadConfiguration(UUIDDataStore);
	    
	    // Load metrics for bStats
	    new Metrics(plugin, 9735);
	    
	    // Load dependencies
	    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
	    new PlaceholderAPIHook();
		VaultHook.setupEconomy();
		
		// Load commands
		Bukkit.getPluginCommand("eps").setExecutor(new EPSCommand());
		Bukkit.getPluginCommand("enchants").setExecutor(new EnchantsCommand());
		Bukkit.getPluginCommand("paytokens").setExecutor(new PayTokensCommand());
		Bukkit.getPluginCommand("scrap").setExecutor(new ScrapCommand());
		Bukkit.getPluginCommand("tokens").setExecutor(new TokensCommand());
		EnchantsCommand.setupGUIs();
		
		// Load visuals
		Bukkit.getPluginManager().registerEvents(new EnchantGUI(), this);
		Bukkit.getPluginManager().registerEvents(new EnchantMetaWriter(), this);
		
		// Download sample packs
		downloadPack("AutoUpdater", "https://github.com/dsdd/EnchantmentsPlusMinus/raw/main/Packs/AutoUpdater.jar");
		if (!PackFolder.exists())
		{
            PackFolder.mkdirs();
			downloadPack("TokenRewards", "https://github.com/dsdd/EnchantmentsPlusMinus/raw/main/Packs/TokenRewards.jar");
	    }
		else
			enablePacks();
		
		// Finalize loading
		new BuiltInPackParser(this);
		for (Player p : Bukkit.getOnlinePlayers())
		{
			EnchantGUI.setupGUI(p);
		}
	}
	
	@Override
	public void onDisable()
	{
		for (Plugin pl : EnabledPacks)
		{
			Bukkit.getPluginManager().disablePlugin(pl);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		File DataFile = new File(DataFolder, p.getUniqueId().toString()+".yml");
		UUIDDataStoreConfig.set(p.getName(), p.getUniqueId().toString());
		DataUtil.saveConfig(UUIDDataStoreConfig, UUIDDataStore);
		
		if (!DataFile.exists())
		    createNewFile(DataFile);
		
		FileConfiguration DataConfig = YamlConfiguration.loadConfiguration(DataFile);
		addDefault(DataConfig, "tokens", 0);
		DataUtil.saveConfig(DataConfig, DataFile);
		EnchantGUI.setupGUI(e.getPlayer());
	}
	
	/** Saves a file to the specified destination
	 * if it does not exist
	 * 
	 * @param path The path to copy from
	 * @param dest The file you want to copy to
	 */
	public static void saveDefaultFile(String resource, File dest)
	{
        if (!dest.exists())
        {
            try {
				dest.createNewFile();
		        copyFile(resource, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	private void addDefault(FileConfiguration config, String key, Object value)
	{
		if (config.get(key) == null)
		{
			config.set(key, value);
		} 
	}
	
	private static void copyFile(String str, File dest) {
		try {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = plugin.getClass().getResourceAsStream(str);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[16384];
	        int length;
	        while ((length = is.read(buffer)) != -1) {
	            os.write(buffer, 0, length);
	        }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally {
	        is.close();
	        os.close();
	    }
	}
	catch(IOException e) {}
	}
	
	/** Creates a new file without having to add try/catch statement to reduce lines.
	 * 
	 * @param file
	 * @return Result of file creation
	 */
	public static boolean createNewFile(File file)
	{
		try {
			file.createNewFile();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static void enablePacks()
	{
		File[] files = PackFolder.listFiles();
	    if (files != null)
	    {
	    	for (File file : files)
	    	{
	    		if (file.isFile())
	    		{
	    			if (file.getName().equalsIgnoreCase("PickaxePack.jar") || file.getName().equalsIgnoreCase("PVPPack.jar"))
	    			{
	    				file.delete();
	    			}
	    			else {
	    				Plugin plugin1 = PlugmanUtil.getPluginFromFile(file);
	    				Bukkit.getPluginManager().enablePlugin(plugin1);
	    				EnabledPacks.add(plugin1);
	    			}
	    		}
	    	}

	    }
	}
	
	public static File downloadFile(String localFileName, String fromUrl) { try {
	    File localFile = new File(localFileName);
	    if (!localFile.exists()) {
	    	localFile.createNewFile();
	    }
	    URL url = new URL(fromUrl);
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
	    URLConnection conn = url.openConnection();
	    ((HttpURLConnection) conn).setRequestMethod("GET"); 
	    conn.setRequestProperty("User-Agent", "  Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
	    conn.connect();
	    InputStream in = conn.getInputStream();
	    byte[] buffer = new byte[16384];

	    int numRead;
	    while ((numRead = in.read(buffer)) != -1) {
	        out.write(buffer, 0, numRead);
	    }
	    if (in != null) {
	        in.close();
	    }
	    if (out != null) {
	        out.close();
	    }
	    out.flush();
	    
	    return localFile;   } catch (Exception e){return null;}
	}
	
	public static void downloadPack(String name, String url)
	{
		File file = downloadFile(PackFolder.getPath()+"/"+name+".jar", url); try{        
			Plugin plugin1 = Bukkit.getPluginManager().loadPlugin(file);
			Bukkit.getPluginManager().enablePlugin(plugin1); } catch (Exception e) {}
	}
	
	public static File getJarFile(Plugin pl)
	{ try {
		JavaPlugin plugin1 = (JavaPlugin) plugin.getServer().getPluginManager().getPlugin(pl.getName());
		Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
		getFileMethod.setAccessible(true);
		File file = (File) getFileMethod.invoke(plugin1);
		return file; } catch (Exception e) { e.printStackTrace();  return null; }
	}
}
