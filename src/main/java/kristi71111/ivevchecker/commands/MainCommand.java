package kristi71111.ivevchecker.commands;

import kristi71111.ivevchecker.ConfigRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainCommand extends CommandBase {
    private static final String commandUsage = "Usage /ivevchecker <command>\nAvailable commands:\n - /ivchecker give <username> <unlimitedUsage>\nExample:\n - /ivchecker give kristi71111 true";
    private static final List<String> tabComplete = new ArrayList<>(Collections.singletonList("give"));
    private static final List<String> tabCompleteBool = new ArrayList<>(Arrays.asList("true", "false"));

    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "ivevchecker";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return commandUsage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        if ("give".equals(args[0])) {
            //Getting the player from the second arg
            EntityPlayerMP entityplayer = getPlayer(server, sender, args[1]);
            //Optional unlimited usage arg
            boolean isUnlimited = false;
            if (args.length > 2) {
                isUnlimited = parseBoolean(args[2]);
            }
            int usageAmount = ConfigRegistry.maxUsage;
            if (isUnlimited) {
                usageAmount = -1;
            }
            ItemStack givenStack = ConfigRegistry.selectedItem.copy();
            NBTTagCompound compound = givenStack.getTagCompound();
            if (compound == null) {
                compound = new NBTTagCompound();
                compound.setInteger("ivevusage", usageAmount);
                givenStack.setTagCompound(compound);
            } else {
                compound.setInteger("ivevusage", usageAmount);
            }
            addLore(usageAmount, givenStack);
            boolean flag = entityplayer.inventory.addItemStackToInventory(givenStack);
            if (flag) {
                entityplayer.inventoryContainer.detectAndSendChanges();
            } else {
                sender.sendMessage(new TextComponentString("Players inventory was full so we couldn't add the item."));
            }
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return tabComplete;
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else {
            return tabCompleteBool;
        }
    }

    public static void addLore(int usages, ItemStack stack) {
        NBTTagList lore = new NBTTagList();
        String usagesLeft = String.valueOf(usages);
        if (usages == -1) {
            usagesLeft = "unlimited";
        }
        lore.appendTag(new NBTTagString(TextFormatting.DARK_PURPLE + "Usages left: " + TextFormatting.WHITE + usagesLeft));
        //Can't be null
        NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
        display.setTag("Lore", lore);
    }
}
