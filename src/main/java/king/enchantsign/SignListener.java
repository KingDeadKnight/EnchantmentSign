package king.enchantsign;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;

public class SignListener implements Listener {

    public EnchantSign plugin;

    public SignListener(EnchantSign plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event){
        if(event.getLines().length < 4) return;
        String line0 = event.getLine(0);
        String line1 = event.getLine(1);
        String line2 = event.getLine(2);
        String line3 = event.getLine(3);
        if(line3.equals("")) return;
        Player player = event.getPlayer();
        String[] item = line1.split(":");
        if (player.isOp()) {
            if (line0.equalsIgnoreCase("AdminEnchant")) {
                event.setLine(0, "[" + TextFormat.GOLD + "Enchanting" + TextFormat.RESET + "]");
                event.setLine(1, EnchantSign.idToEnchant.get(Integer.parseInt(item[0])) + " " + EnchantSign.intToLevel.get(Integer.parseInt(item[1])));
                event.setLine(2, TextFormat.GOLD + "Level : " + TextFormat.RESET + line2);
                event.setLine(3, TextFormat.GOLD + "Lapis : " + TextFormat.RESET + line3);
                player.sendMessage(TextFormat.GOLD + "Enchant créé !");
            }
        } else {
            if (line0.equalsIgnoreCase("[" + TextFormat.GOLD + "Enchanting" + TextFormat.RESET + "]")) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInterract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockEntity tile = player.getLevel().getBlockEntity(block);
        if (tile instanceof BlockEntitySign) {
            if (!(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR) && (player.getLoginChainData().getDeviceOS() == 7 || player.getLoginChainData().getDeviceOS() == 8)) {
                player.sendMessage(TextFormat.GOLD + "Utilise le clic gauche !");
                return;
            } else {
                String[] text = ((BlockEntitySign) tile).getText();
                if (text[0].equalsIgnoreCase("[" + TextFormat.GOLD + "Enchanting" + TextFormat.RESET + "]")) {
                    this.enchantItem(player, (BlockEntitySign) tile);
                    return;
                }
            }
        }
    }

    private int getRequire(String line){
        return Integer.parseInt(TextFormat.clean(line).split(" : ")[1]);
    }

    private boolean hasThatEnchant(Item item, Enchantment enchant){
        for (Enchantment enchantement : item.getEnchantments()) {
            if(enchant.id == enchantement.id && enchant.getLevel() == enchantement.getLevel()){
                return true;
            }
        }
        return false;
    }

    private boolean hasLowerEnchant(Item item, Enchantment enchant){
        for (Enchantment enchantement : item.getEnchantments()) {
            if(enchant.id == enchantement.id && enchant.getLevel() < enchantement.getLevel()){
                return true;
            }
        }
        return false;
    }

    private int countLapis(Inventory inv){
        int count = 0;
        for (Item item : inv.getContents().values()) {
            if(item.equals(Item.get(ItemID.DYE, DyeColor.BLUE.getDyeData()), true)){
                count += item.count;
            }
        }
        return count;
    }

    private void enchantItem(Player player, BlockEntitySign sign){
        PlayerInventory inv = player.getInventory();
        String[] text = sign.getText();
        Item hand = inv.getItemInHand();
        int xp = player.getExperienceLevel();
        int requireXp = getRequire(text[2]);
        int requireLapis = getRequire(text[3]);
        String[] enchLvl = text[1].split(" ");
        int level = EnchantSign.levelToInt.get(enchLvl[enchLvl.length - 1]);
        int idEnchant = EnchantSign.enchantToId.get(String.join(" ", Arrays.copyOf(enchLvl, enchLvl.length - 1)));
        if(hand == Item.get(Item.AIR)){
            player.sendMessage(TextFormat.GOLD + "Tient l'item que tu veux enchanté dans la main !");
            return;
        }
        if(!(hand instanceof ItemArmor || hand instanceof ItemTool)){
            player.sendMessage(TextFormat.GOLD + "Item non enchantable");
            return;
        }
        if(hand.getCount() != 1){
            player.sendMessage(TextFormat.GOLD + "Destack tes items");
            return;
        }
        if(!inv.contains(Item.get(Item.DYE, DyeColor.BLUE.getDyeData()))){
            player.sendMessage(TextFormat.GOLD + "Tu n'as pas de lapis dans ton inventaire");
            return;
        }
        if(xp < requireXp){
            player.sendMessage(TextFormat.GOLD + "Tu n'as pas assez de level");
            return;
        }
        int lapis = countLapis(inv);
        if(lapis < requireLapis){
            player.sendMessage(TextFormat.GOLD + "Tu n'as pas assez de lapis");
            return;
        }
        if(!Enchantment.get(idEnchant).canEnchant(hand)){
            player.sendMessage(TextFormat.GOLD + "Tu ne peux pas enchanter cette objet avec cet enchantement");
            return;
        }
        if(hasThatEnchant(hand, Enchantment.get(idEnchant).setLevel(level))){
            player.sendMessage(TextFormat.GOLD + "Cet objet possède déjà cette enchantement");
            return;
        }
        if(hasLowerEnchant(hand, Enchantment.get(idEnchant).setLevel(level))){
            player.sendMessage(TextFormat.GOLD + "Cet objet possède déjà cette enchantement, mais de niveau plus bas");
            return;
        }
        hand.addEnchantment(Enchantment.get(idEnchant).setLevel(level));
        inv.setItemInHand(hand);
        inv.remove(Item.get(ItemID.DYE, DyeColor.BLUE.getDyeData()));
        inv.addItem(Item.get(ItemID.DYE, DyeColor.BLUE.getDyeData(), lapis - requireLapis));
        player.setExperience(0, xp - requireXp);
        player.sendMessage(TextFormat.GOLD + "Enchantement effectué !");
    }
}
