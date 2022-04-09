package kristi71111.ivevchecker.events;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
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
                    doLogic(stackUsed, playerMP, entityPixelmon.getPokemonData(), event.getHand());
                }
                return;
            }
            doLogic(stackUsed, playerMP, entityPixelmon.getPokemonData(), event.getHand());
        }
    }

    private void doLogic(ItemStack stackUsed, EntityPlayerMP playerMP, Pokemon pokemon, EnumHand hand) {
        ITextComponent component = new TextComponentString("");
        //Shiny easter egg formatting
        if (pokemon.isShiny()) {
            component.appendSibling(new TextComponentString(getRainbowChat(pokemon.getDisplayName() + ":") + "\n"));
        } else {
            component.appendSibling(new TextComponentString(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokemon.getDisplayName() + ":" + "\n"));
        }
        IVStore ivStore = pokemon.getIVs();
        component.appendSibling(new TextComponentString(TextFormatting.AQUA + " IV's (" + TextFormatting.GOLD + ivStore.getTotal() + TextFormatting.AQUA + "/" + TextFormatting.GOLD + "186" + TextFormatting.AQUA + ")" + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "HP: " + TextFormatting.AQUA + ivStore.getStat(StatsType.HP) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Atk: " + TextFormatting.AQUA + ivStore.getStat(StatsType.Attack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Def: " + TextFormatting.AQUA + ivStore.getStat(StatsType.Defence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Atk: " + TextFormatting.AQUA + ivStore.getStat(StatsType.SpecialAttack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Def: " + TextFormatting.AQUA + ivStore.getStat(StatsType.SpecialDefence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Spd: " + TextFormatting.AQUA + ivStore.getStat(StatsType.Speed) + "\n"));
        EVStore evStore = pokemon.getEVs();
        component.appendSibling(new TextComponentString(TextFormatting.AQUA + " EV's (" + TextFormatting.GOLD + evStore.getTotal() + TextFormatting.AQUA + "/" + TextFormatting.GOLD + 510 + TextFormatting.AQUA + ")" + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "HP: " + TextFormatting.AQUA + evStore.getStat(StatsType.HP) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Atk: " + TextFormatting.AQUA + evStore.getStat(StatsType.Attack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Def: " + TextFormatting.AQUA + evStore.getStat(StatsType.Defence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Atk: " + TextFormatting.AQUA + evStore.getStat(StatsType.SpecialAttack) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Sp. Def: " + TextFormatting.AQUA + evStore.getStat(StatsType.SpecialDefence) + "\n"));
        component.appendSibling(new TextComponentString(TextFormatting.WHITE + "  - " + TextFormatting.GOLD + "Spd: " + TextFormatting.AQUA + evStore.getStat(StatsType.Speed) + "\n"));
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
