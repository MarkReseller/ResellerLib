package lv.reseller.netherwars.decoration.shop;

import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.util.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class Category {

    final Shop shop;
    final String name;
    String displayName;
    List<String> description;
    ItemStack icon;
    final Map<String, Product> products;

    ItemStack builtItem;
    public Inventory[] inventories;

    Category(Shop shop, String name, String displayName, List<String> description, ItemStack icon) {
        this.shop = shop;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.products = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public ItemStack buildItem() {
        ItemStack itemStack = icon.clone();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("category", name);
        itemStack = Items.setTag(itemStack, tag);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Chat.colorize(displayName));
        itemMeta.setLore(Chat.colorize(description));
        itemStack.setItemMeta(itemMeta);
        this.builtItem = itemStack;
        return itemStack;
    }

    public void buildInventories() {
        int productSize = products.size();
        int pageSize = productSize / Shop.productSlots.length + (productSize % Shop.productSlots.length == 0 ? 0 : 1);
        inventories = new Inventory[pageSize];
        for(int page = 0; page < inventories.length; page++) {
            Inventory inventory = Bukkit.createInventory(null, 54, "Shop. Page " + (page + 1));
            Shop.setItemInSlots(inventory, Shop.BLANK_GLASS, Shop.blankSlots);
            inventory.setItem(Shop.PREV_PAGE_SLOT, isFirstPage(page) ? Shop.BLANK_GLASS : Shop.PREV_PAGE);
            inventory.setItem(Shop.NEXT_PAGE_SLOT, isLastPage(page) ? Shop.BLANK_GLASS : Shop.NEXT_PAGE);
            for(int categoryIndex = 0; categoryIndex < 7; categoryIndex++) {
                if(categoryIndex < shop.categoryIcons.size())  {
                    inventory.setItem(Shop.categorySlots[categoryIndex], shop.categoryIcons.get(categoryIndex));
                } else {
                    break;
                }
            }
            inventories[page] = inventory;
        }
        int i = 0;
        for(Map.Entry<String, Product> entry : products.entrySet()) {
            Product product = entry.getValue();
            ItemStack icon = product.buildItem();
            int page = i / Shop.productSlots.length;
            int slot = i % Shop.productSlots.length;
            inventories[page].setItem(Shop.productSlots[slot], icon);
            i++;
        }
    }

    public boolean isInBounds(int page) {
        return page >= 0 && page < inventories.length;
    }

    public boolean isFirstPage(int page) {
        return page == 0;
    }

    public boolean isLastPage(int page) {
        return page == (inventories.length - 1);
    }

    public int getPage(Inventory inventory) {
        for(int i = 0; i < inventories.length; i++) {
            if(inventories[i].equals(inventory)) {
                return i;
            }
        }
        return -1;
    }

    public Product identificateProduct(ItemStack itemStack) {
        if(itemStack == null) return null;
        if(itemStack.getType() == Material.AIR) return null;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        String productName = nmsItem.getTag().getString("product");
        return products.get(productName);
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        if(isInBounds(page))
            player.openInventory(inventories[page]);
    }

}
