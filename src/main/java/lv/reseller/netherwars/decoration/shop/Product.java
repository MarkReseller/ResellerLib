package lv.reseller.netherwars.decoration.shop;

import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.util.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private final Category category;
    private final String name;
    ItemStack[] items;
    ItemStack[] price;

    Product(Category category, String name, ItemStack[] items, ItemStack[] price) {
        this.category = category;
        this.name = name;
        this.items = items;
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public ItemStack[] getPrice() {
        return price;
    }

    private boolean canBuy(Player player) {
        for(ItemStack priceItem : price) {
            if(!player.getInventory().containsAtLeast(priceItem, priceItem.getAmount()))
                return false;
        }
        return true;
    }

    public boolean buy(Player player) {
        if(!canBuy(player)) return false;
        player.getInventory().removeItem(price);
        player.getInventory().addItem(items);
        return true;
    }

    public ItemStack buildItem() {
        ItemStack item = items[0].clone();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("product", name);
        item = Items.setTag(item, tag);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");
        if(getPrice().length > 1) {
            lore.add(Chat.colorize("&7&lPrice:"));
            for(ItemStack price : price) {
                lore.add(Chat.colorize(" " + priceToText(price)));
            }
        } else {
            lore.add(Chat.colorize("&7&lPrice: &r" + priceToText(price[0])));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String priceToText(ItemStack price) {
        return "&f" + price.getType() + " &e" + price.getAmount() + "x";
    }

}
