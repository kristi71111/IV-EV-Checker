package kristi71111.ivevchecker.events;

import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import com.pixelmongenerations.common.entity.pixelmon.stats.EVsStore;
import com.pixelmongenerations.common.entity.pixelmon.stats.IVStore;
import com.pixelmongenerations.common.entity.pixelmon.stats.StatsType;
import com.pixelmongenerations.core.storage.PlayerStorage;
import kristi71111.ivevchecker.ConfigRegistry;
import kristi71111.ivevchecker.commands.MainCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static kristi71111.ivevchecker.ConfigRegistry.OnlyWorkOnOwnedPokemon;
import static kristi71111.ivevchecker.ConfigRegistry.SelectedItemName;

public class EntityInteract {
    @SubscribeEvent
    public void entityInteractEvent(PlayerInteractEvent.EntityInteract event) {
        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getTarget();
        if (entity instanceof EntityPixelmon) {
            EntityPixelmon entityPixelmon = (EntityPixelmon) entity;
            ItemStack stackUsed = event.getItemStack();
            if (stackUsed.getItem() != ConfigRegistry.selectedActualItem) {
                return;
            }
            NBTTagCompound compound = stackUsed.getTagCompound();
            if (compound == null || !(compound.hasKey("ivevusage"))) {
                return;
            }
            EntityPlayerMP playerMP = (EntityPlayerMP) event.getEntityPlayer();
            if(stackUsed.getCount() > 1){
                playerMP.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "You can only use this item if it's not stacked!"));
                return;
            }
            if (OnlyWorkOnOwnedPokemon) {
                if (entityPixelmon.getOwnerId() != null) {
                    doLogic(stackUsed, playerMP, entityPixelmon, event.getHand());
                }
                return;
            }
            doLogic(stackUsed, playerMP, entityPixelmon, event.getHand());
        }
    }

    private void doLogic(ItemStack stackUsed, EntityPlayerMP playerMP, EntityPixelmon pokemon, EnumHand hand) {
        ITextComponent component = new TextComponentString("");
        //Shiny easter egg formatting
        if (pokemon.isShiny()) {
            component.appendSibling(new TextComponentString(getRainbowChat(pokemon.getDisplayName().getUnformattedText() + ":") + "\n"));
        } else {
            component.appendSibling(new TextComponentString(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokemon.getDisplayName().getUnformattedText() + ":" + "\n"));
        }
        IVStore ivStore = pokemon.stats.IVs;
        component.appendSibling(new TextComponentString(TextFormatting.AQUA + " IV's (" + TextFormatting.GOLD + ivStore.getTotal() + TextFormatting.AQUA + "/" + TextFormatting.GOLD + "186" + TextFormatting.AQUA + ")" + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "HP: " + TextFormatting.AQUA + ivStore.get(StatsType.HP) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Atk: " + TextFormatting.AQUA + ivStore.get(StatsType.Attack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Def: " + TextFormatting.AQUA + ivStore.get(StatsType.Defence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Atk: " + TextFormatting.AQUA + ivStore.get(StatsType.SpecialAttack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Def: " + TextFormatting.AQUA + ivStore.get(StatsType.SpecialDefence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Spd: " + TextFormatting.AQUA + ivStore.get(StatsType.Speed) + "\n"));
        EVsStore evStore = pokemon.stats.EVs;
        //The way generations does this is... more code
        int EVHP = evStore.get(StatsType.HP);
        int EVAttack = evStore.get(StatsType.HP);
        int EVDefence = evStore.get(StatsType.HP);
        int EVSpecialAttack = evStore.get(StatsType.HP);
        int EVSpecialDefence = evStore.get(StatsType.HP);
        int EVSpeed = evStore.get(StatsType.HP);
        int totalEv = EVHP + EVAttack + EVDefence  + EVSpecialAttack + EVSpecialDefence + EVSpeed;
        component.appendSibling(new TextComponentString(TextFormatting.AQUA + " EV's (" + TextFormatting.GOLD + totalEv + TextFormatting.AQUA + "/" + TextFormatting.GOLD + 510 + TextFormatting.AQUA + ")" + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "HP: " + TextFormatting.AQUA + EVHP + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Atk: " + TextFormatting.AQUA + EVAttack + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Def: " + TextFormatting.AQUA + EVDefence + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Atk: " + TextFormatting.AQUA + EVSpecialAttack+ "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Def: " + TextFormatting.AQUA + EVSpecialDefence + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Spd: " + TextFormatting.AQUA + EVSpeed + "\n"));
        //Reduce usages or vanish
        NBTTagCompound compound = stackUsed.getTagCompound();
        int count = compound.getInteger("ivevusage");
        //Unlimited handling
        if (count == -1) {
            playerMP.sendMessage(component);
            return;
        }
        //Remove Item
        if (--count == 0) {
            playerMP.setHeldItem(hand, ItemStack.EMPTY);
            component.appendSibling(new TextComponentString(SelectedItemName + "'s" + TextFormatting.DARK_RED + " usage has been exceeded!"));
            playerMP.inventoryContainer.detectAndSendChanges();
            playerMP.sendMessage(component);
            return;
        } else {
            String usage = count == 1 ? " usage" : " usages";
            component.appendSibling(new TextComponentString(SelectedItemName + TextFormatting.GOLD + " has " + TextFormatting.AQUA + count + TextFormatting.GOLD + usage + " left!"));
        }
        compound.setInteger("ivevusage", count);
        MainCommand.addLore(count, stackUsed);
        playerMP.inventoryContainer.detectAndSendChanges();
        playerMP.sendMessage(component);
    }


    private final TextFormatting[] textColorsArray = new TextFormatting[]{TextFormatting.DARK_RED, TextFormatting.RED, TextFormatting.GOLD, TextFormatting.YELLOW, TextFormatting.DARK_GREEN, TextFormatting.GREEN, TextFormatting.AQUA, TextFormatting.DARK_AQUA, TextFormatting.DARK_BLUE, TextFormatting.BLUE, TextFormatting.LIGHT_PURPLE, TextFormatting.DARK_PURPLE};

    private String getRainbowChat(String pokeName) {
        int counter = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pokeName.length(); i++) {
            char character = pokeName.charAt(i);
            if (counter > textColorsArray.length - 1) {
                counter = 0;
            }
            builder.append(textColorsArray[counter]).append(TextFormatting.BOLD).append(character);
            counter++;
        }
        return builder.toString();
    }
}
