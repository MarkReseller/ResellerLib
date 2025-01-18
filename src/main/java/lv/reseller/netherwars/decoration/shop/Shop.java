package lv.reseller.netherwars.decoration.shop;

import lv.reseller.netherwars.util.Chat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop implements Listener {

    public static final ItemStack BLANK_GLASS = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
    public static final ItemStack PREV_PAGE = new ItemStack(Material.ARROW);
    public static final ItemStack NEXT_PAGE = new ItemStack(Material.ARROW);

    public static final int PREV_PAGE_SLOT = 27;
    public static final int NEXT_PAGE_SLOT = 35;
    public static final int[] categorySlots = {
            1, 2, 3, 4, 5, 6, 7
    };
    public static final int[] productSlots = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    public static final int[] blankSlots = {
            0, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 26,
            36, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    static {
        ItemMeta meta = BLANK_GLASS.getItemMeta();
        meta.setDisplayName(" ");
        BLANK_GLASS.setItemMeta(meta);

        ItemMeta meta1 = PREV_PAGE.getItemMeta();
        meta1.setDisplayName("<<<");
        PREV_PAGE.setItemMeta(meta1);

        meta1.setDisplayName(">>>");
        NEXT_PAGE.setItemMeta(meta1);
    }

    public static void setItemInSlots(Inventory inventory, ItemStack itemStack, int[] slots) {
        for (int slot : slots) {
            inventory.setItem(slot, itemStack);
        }
    }

    public static boolean isProductArea(int slot) {
        for(int productSlot : productSlots) {
            if(productSlot == slot) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCategoryArea(int slot) {
        for(int categorySlot : categorySlots) {
            if(categorySlot == slot) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrevPageArea(int slot) {
        return slot == 27;
    }

    public static boolean isNextPageArea(int slot) {
        return slot == 35;
    }

    private final Plugin plugin;
    private final Map<String, Category> categories;
    private final Map<String, Product> products;
     final List<ItemStack> categoryIcons;

    public Shop(Plugin plugin) {
        this.plugin = plugin;
        this.categories = new HashMap<>();
        this.products = new HashMap<>();
        this.categoryIcons = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public void loadShop(YamlConfiguration config) {
        products.clear();
        categories.clear();

        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");
        for(String name : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(name);
            String displayName = categorySection.getString("displayName");
            List<String> description = categorySection.getStringList("description");
            ItemStack icon = categorySection.getItemStack("icon");
            Category category = new Category(this, name, displayName, description, icon);
            this.categories.put(name, category);
        }
        ConfigurationSection productsSection = config.getConfigurationSection("products");
        for(String name : productsSection.getKeys(false)) {
            ConfigurationSection productSection = productsSection.getConfigurationSection(name);
            String categoryName = productSection.getString("category");
            Category category = categories.get(categoryName);
            if(category == null) {
                plugin.getLogger().warning("Product " + name + " not loaded: Category " + categoryName + " not found");
                continue;
            }
            ConfigurationSection itemsSection = productSection.getConfigurationSection("items");
            ConfigurationSection priceSection = productSection.getConfigurationSection("price");
            int i = 0;
            ItemStack[] items = new ItemStack[itemsSection.getKeys(false).size()];
            for(String itemKey : itemsSection.getKeys(false)) {
                items[i] = itemsSection.getItemStack(itemKey);
                i++;
            }
            i = 0;
            ItemStack[] price = new ItemStack[priceSection.getKeys(false).size()];
            for(String priceKey : priceSection.getKeys(false)) {
                price[i] = priceSection.getItemStack(priceKey);
                i++;
            }
            Product product = new Product(category, name, items, price);
            category.getProducts().put(name, product);
            this.products.put(name, product);
        }
    }

    public void buildViews() {
        categoryIcons.clear();
        for(Category category : this.categories.values()) {
            categoryIcons.add(category.buildItem());
        }
        for(Category category : this.categories.values()) {
            category.buildInventories();
        }
    }

    public void open(Player player) {
        Category category = categories.values().iterator().next();
        category.open(player);
    }

    private Category identificateCategory(Inventory inventory) {
        if(inventory == null) return null;
        for(Category category : this.categories.values()) {
            if(category.getPage(inventory) != -1)
                return category;
        }
        return null;
    }

    private Category identificateCategory(ItemStack itemStack) {
        if(itemStack == null) return null;
        if(itemStack.getType() == Material.AIR) return null;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        String categoryName = nmsItem.getTag().getString("category");
        return categories.get(categoryName);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = ((Player) event.getWhoClicked()).getPlayer();
        Inventory inventory = event.getClickedInventory();
        Category category = identificateCategory(inventory);
        if(category == null) return;
        event.setCancelled(true);
        int page = category.getPage(inventory);
        int slot = event.getSlot();
        ItemStack itemStack = event.getCurrentItem();
        if(isCategoryArea(slot)) {
            Category clickedCategory = identificateCategory(itemStack);
            if(clickedCategory == null) return;
            clickedCategory.open(player);
            return;
        }
        if(isProductArea(slot)) {
            Product product = category.identificateProduct(itemStack);
            if(product == null) return;
            boolean result = product.buy(player);
            if(result) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
            } else {
                player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                player.sendMessage(Chat.colorize("&cNot enough item to buy"));
            }
            return;
        }
        if(isPrevPageArea(slot)) {
            if(!category.isFirstPage(page))
                category.open(player, page - 1);
            return;
        }
        if(isNextPageArea(slot)) {
            if(!category.isLastPage(page))
                category.open(player, page + 1);
        }
    }

}
