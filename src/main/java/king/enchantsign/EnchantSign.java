package king.enchantsign;

import cn.nukkit.plugin.PluginBase;

import java.util.HashMap;
import java.util.Map;

public class EnchantSign extends PluginBase {

    public static HashMap<Integer, String> idToEnchant = new HashMap<Integer, String>();
    public static HashMap<String, Integer> enchantToId = new HashMap<String, Integer>();
    public static HashMap<String, Integer> levelToInt = new HashMap<String, Integer>();
    public static HashMap<Integer, String> intToLevel = new HashMap<Integer, String>();

    @Override
    public void onEnable() {
        this.getServer().getLogger().info("EnchantSign Enabled !");
        this.getServer().getPluginManager().registerEvents(new SignListener(this), this);
        this.registerEnchantement();
    }

    private void registerEnchantement(){
        idToEnchant.put(0, "Protection");
        idToEnchant.put(1, "Fire Protection");
        idToEnchant.put(2, "Feather Falling");
        idToEnchant.put(3, "Blast Protection");
        idToEnchant.put(4, "Projectile Protection");
        idToEnchant.put(5, "Thorns");
        idToEnchant.put(6, "Water Breathing");
        idToEnchant.put(7, "Water Walker");
        idToEnchant.put(8, "Water Worker");
        idToEnchant.put(9, "Sharpness");
        idToEnchant.put(10, "Smite");
        idToEnchant.put(11, "Bane of Arthropods");
        idToEnchant.put(12, "Knockback");
        idToEnchant.put(13, "Fire Aspect");
        idToEnchant.put(14, "Looting");
        idToEnchant.put(15, "Efficiency");
        idToEnchant.put(16, "Silk Touch");
        idToEnchant.put(17, "Unbreaking");
        idToEnchant.put(18, "Fortune");
        idToEnchant.put(19, "Power");
        idToEnchant.put(20, "Punch");
        idToEnchant.put(21, "Flame");
        idToEnchant.put(22, "Infinity");
        idToEnchant.put(23, "Luck of the Sea");
        idToEnchant.put(24, "Lure");
        idToEnchant.put(26, "Mending");
        for(Map.Entry<Integer, String> entry : idToEnchant.entrySet()){
            enchantToId.put(entry.getValue(), entry.getKey());
        }
        levelToInt.put("I", 1);
        levelToInt.put("II", 2);
        levelToInt.put("III", 3);
        levelToInt.put("IV", 4);
        levelToInt.put("V", 5);
        for(Map.Entry<String, Integer> entry : levelToInt.entrySet()){
            intToLevel.put(entry.getValue(), entry.getKey());
        }
    }
}
