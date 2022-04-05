package kristi71111.ivevchecker;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import java.util.regex.Pattern;


public class ConfigRegistry {
    private static Configuration config;
    public static int maxUsage;
    public static ItemStack selectedItem;
    public static Item selectedActualItem;
    public static boolean OnlyWorkOnOwnedPokemon;
    public static String SelectedItemName;
    private static final Pattern finalPattern = Pattern.compile("&", Pattern.LITERAL);

    public static void init(Configuration c) {
        config = c;
        createOrSyncConfig();
    }

    public static void createOrSyncConfig() {
        String itemString = config.getString("item", "default", "minecraft:stick", "Define the item you want to use");
        selectedItem = getSelectedItem(itemString);
        selectedActualItem = selectedItem.getItem();
        String ItemName = finalPattern.matcher(config.getString("itemName", "default", "&4IV/EV Checker", "Define the name of the item")).replaceAll("§");
        SelectedItemName = ItemName;
        selectedItem.setStackDisplayName(ItemName);
        maxUsage = config.getInt("maxUsage", "default", 10, 1, Integer.MAX_VALUE, "Set's the maximum usage of defined item");
        OnlyWorkOnOwnedPokemon = config.getBoolean("onlyWorkOnOwnedPokemon", "default", false, "Should the item only work on pokemon that are owned?");
        config.save();
    }

    private static ItemStack getSelectedItem(String selectedItem) {
        ResourceLocation resourcelocation = new ResourceLocation(selectedItem);
        Item item = Item.REGISTRY.getObject(resourcelocation);
        if (item == null) {
            //default to stick
            Ivevchecker.logger.error("Failed to parse item from config. Defaulting to stick.");
            return new ItemStack(Items.STICK);
        } else {
            return new ItemStack(item);
        }
    }
}
