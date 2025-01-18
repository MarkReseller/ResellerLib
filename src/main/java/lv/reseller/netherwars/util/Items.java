package lv.reseller.netherwars.util;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Items {

    public static ItemStack setTag(ItemStack item, NBTTagCompound tag) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        nms.setTag(tag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static NBTTagCompound getTag(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms.getTag();
    }

}
