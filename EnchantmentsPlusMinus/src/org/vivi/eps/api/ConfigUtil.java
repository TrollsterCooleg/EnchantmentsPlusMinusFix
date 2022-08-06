package org.vivi.eps.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.Main;
import org.vivi.eps.legacy.NameUtil;
import org.vivi.eps.util.DataUtil;

@Deprecated
public class ConfigUtil {
    
	// This map is used to improve efficiency of getting values from each enchant config
	protected static Map<Enchantment, FileConfiguration> fgMap = new HashMap<Enchantment, FileConfiguration>();
	
	/** Gets the main config file.
	 * 
	 * @return The config.yml file's FileConfiguration
	 */
    public static FileConfiguration getConfig()
	{
		return Main.Config;
	}
    
    /** Reloads all enchant configurations
     */
    public static void reloadConfigs()
    {
    	Enchantment[] enchants = Enchantment.values();
    	for (Enchantment enchant : enchants)
    		fgMap.put(enchant, YamlConfiguration.loadConfiguration(getEnchantFile(enchant)));
    }
	
	/** Gets the config of the specified enchant.s
	 * 
	 * @param enchant The enchant in question
	 * @return The config
	 */
	public static FileConfiguration getEnchantConfig(Enchantment enchant)
	{
		return fgMap.get(enchant);
	}
	
	/**
	 * Returns the file which correlates to the enchant's configuration.
	 * 
	 * @param enchant The correlating enchant
	 * @return Returns the file which correlates to the enchant's configuration.
	 */
	public static File getEnchantFile(Enchantment enchant)
	{
		File file = new File(Main.EnchantsFolder, NameUtil.getName(enchant)+".yml");
		return file;
	}
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Automatically fills %lvl% so you don't have to mess with script engines.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the double value of the specified path of the specified enchant's file.
	 */
	public static Double getAutofilledDouble(Enchantment enchant, Integer enchlvl, String path)
    {
    	String value = getEnchantConfig(enchant).getString(path);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
	    return eval(value);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Automatically fills %lvl% so you don't have to mess with script engines.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the Integer value of the specified path of the specified enchant's file.
	 */
	public static Integer getAutofilledInt(Enchantment enchant, Integer enchlvl, String path)
    {
    	String value = getEnchantConfig(enchant).getString(path);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
	    return (int)eval(value);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the String value of the specified path of the specified enchant's file.
	 */
	public static String getString(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getString(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the int value of the specified path of the specified enchant's file.
	 */
	public static int getInt(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getInt(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the double value of the specified path of the specified enchant's file.
	 */
	public static double getDouble(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getDouble(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the Boolean value of the specified path of the specified enchant's file.
	 */
	public static Boolean getBoolean(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getBoolean(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Can be casted, but give an unchecked warning.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the Object value of the specified path of the specified enchant's file.
	 */
	public static Object get(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).get(path);
    }
	
	/**
	 * Returns the value of the specified path of the main config's misc section.
	 * e.g. If you want to find "misc.yourpathhere.something" you would write "yourpathhere.something"
	 * 
	 * @param path The path you want to search for.
	 * @return Returns the value of the specified path of the main config's misc section.
	 */
	@Deprecated
	public static Object getMiscKey(String path)
    {
        return Main.Config.get("misc."+path);
    }
    
    /** Sets the path of the specified enchant to the specified value.
     * Automatically saves.
     * 
     * @param enchant The enchant to find its config for
     * @param path The path to be set to
     * @param value The value to set to the path
     */
    public static void setDefault(Enchantment enchant, String path, Object value)
    {
    	setConfigValue(enchant, path, value);
    }
    
    /** Sets the specified path in the main config file's misc section to the specified value.
     * 
     * e.g. If you want to set "misc.yourfirstthing.anotherthing" to 2, you would write
     *  setDefaultMisc("yourfirstthing.anotherthing", 2);
     * 
     * @param path The path to be set to
     * @param value The value to set to the path
     */
    @Deprecated
    public static void setDefaultMisc(String path, Object value)
    {
    	setMiscValue("misc."+path, value);
    }
    
    /** Automatically fills the max level, scrap value, upgrade material, upgrade description,
     *  cost type, cost start value and value of the specified enchant for easier value setting.
     *  
     *  Max level is set to 10, scrap value is set to half cost, upgrade icon is set to BOOK,
     *  upgrade description is set to the specified description (so people can understand what the
     *  enchant does), the cost type is set to linear and the start value and value is set to the 
     *  specified cost.
     * 
     * @param enchant The enchant to fill
     * @param desc The description to fill
     * @param cost The cost you want to set
     */
    public static void autoFillEnchantConfig(Enchantment enchant, String desc, Integer cost)
    {
    	setDefault(enchant, "maxlevel", 10);
    	setDefault(enchant, "scrapvalue", cost/2);
    	setDefault(enchant, "upgradeicon", Material.BOOK);
    	setDefault(enchant, "upgradedesc", desc);
    	setDefault(enchant, "cost.type", "linear");
    	setDefault(enchant, "cost.startvalue", cost);
    	setDefault(enchant, "cost.value", cost);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param path The path to replace
     * @param replace The replacement
     */
    @Deprecated
    private static void setMiscValue(String path, Object replace)
    {
		setDefault(path, replace);
    }
    
    /** Sets the path to the specified replacement if it doesn't exist in the main config file
     * 
     * @param path The path to replace
     * @param replace The replacement
     */
    public static void setDefault(String path, Object replace)
    {
    	if (!Main.Config.isSet(path))
		{
			Main.Config.set(path, replace);
			if (Main.ConfigFile.exists())
			{
				DataUtil.saveConfig(Main.Config, Main.ConfigFile);
			}
		}
    }
    
    /**
     * Creates a new file in the enchants folder with the specified enchant name.
     * 
     * @param enchant The enchant (Will be converted to its name)
     * @return The new file
     */
    public static File newEnchantFile(Enchantment enchant)
    {
    	File file = getEnchantFile(enchant);
    	if (!file.exists())
    		Main.createNewFile(file);
    	return file;
    }
    
    /**
     * Creates a new file in the enchants folder with the specified name.
     * 
     * @param enchant The enchant
     * @param name The file name
     * @return The new file
     */
    public static File newEnchantFile(Enchantment enchant, String name)
    {
    	File file = new File(Main.EnchantsFolder, name);
    	if (!file.exists())
    		Main.createNewFile(file);
    	return file;
    }
    
    private static void setConfigValue(Enchantment enchant, String path, Object replace)
    {
    	FileConfiguration config = getEnchantConfig(enchant);
    	File file = getEnchantFile(enchant);
		if (!config.isSet(path))
		{
			config.set(path, replace);
			if (file.exists())
				DataUtil.saveConfig(config, file);
		}
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param type
     */
    @Deprecated
    public static void setDefaultCostType(Enchantment enchant, String type)
    {
    	setConfigValue(enchant, "cost.type", type);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param value
     */
    @Deprecated
    public static void setDefaultCostStartValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "cost.startvalue", value);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param value
     */
    @Deprecated
    public static void setDefaultCostValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "cost.value", value);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param enchlvl
     * @param price
     */
    @Deprecated
    public static void setDefaultCostPrice(Enchantment enchant, Integer enchlvl, Integer price)
    {
    	setConfigValue(enchant, "cost."+enchlvl.toString(), price);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param multi
     */
    @Deprecated
    public static void setDefaultCostMulti(Enchantment enchant, Double multi)
    {
    	setConfigValue(enchant, "cost.multi", multi);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param level
     */
    @Deprecated
    public static void setDefaultMaxLevel(Enchantment enchant, Integer level)
    {
    	setConfigValue(enchant, "maxlevel", level);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param value
     */
    @Deprecated
    public static void setDefaultScrapValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "scrapvalue", value);
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param icon
     */
    @Deprecated
    public static void setDefaultUpgradeIcon(Enchantment enchant, Material icon)
    {
    	setConfigValue(enchant, "upgradeicon", NameUtil.getMaterialName(icon));
    }
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param material
     */
    @Deprecated
    public static void setDefaultUpgradeIcon(Enchantment enchant, String material)
    {
    	setConfigValue(enchant, "upgradeicon", material);
    }
    
    
    /** Deprecated. Use setDefault() instead.
     * 
     * @param enchant
     * @param desc
     */
    @Deprecated
    public static void setDefaultUpgradeDesc(Enchantment enchant, String desc)
    {
    	setConfigValue(enchant, "upgradedesc", desc);
    }
    
    public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}
