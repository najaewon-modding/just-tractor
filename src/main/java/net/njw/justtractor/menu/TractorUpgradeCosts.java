package net.njw.justtractor.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TractorUpgradeCosts {
    private static final int PLAYER_INVENTORY_SIZE = 36;

    private static final Cost[] FORTUNE_COSTS = {
            new Cost(Items.LAPIS_LAZULI, 32),
            new Cost(Items.GOLD_INGOT, 32),
            new Cost(Items.DIAMOND, 32)
    };

    private static final Cost[] SPEED_COSTS = {
            new Cost(Items.IRON_INGOT, 64),
            new Cost(Items.COPPER_INGOT, 64),
            new Cost(Items.REDSTONE, 64),
            new Cost(Items.GOLD_INGOT, 64),
            new Cost(Items.DIAMOND, 64)
    };

    private TractorUpgradeCosts() {}

    public static Cost getFortuneCost(int targetLevel) {
        if (targetLevel < 1 || targetLevel > FORTUNE_COSTS.length) throw new IllegalArgumentException("Invalid fortune level: " + targetLevel);
        return FORTUNE_COSTS[targetLevel - 1];
    }

    public static Cost getSpeedCost(int targetLevel) {
        if (targetLevel < 1 || targetLevel > SPEED_COSTS.length) throw new IllegalArgumentException("Invalid speed level: " + targetLevel);
        return SPEED_COSTS[targetLevel - 1];
    }

    public static int count(Player player, Cost cost) {
        Inventory inventory = player.getInventory();
        int total = 0;
        int size = Math.min(PLAYER_INVENTORY_SIZE, inventory.getContainerSize());

        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty() && stack.getItem() == cost.item()) {
                total += stack.getCount();
            }
        }

        return total;
    }

    public static boolean canAfford(Player player, Cost cost) {
        return player.getAbilities().instabuild || count(player, cost) >= cost.count();
    }

    public static boolean consume(Player player, Cost cost) {
        if (player.getAbilities().instabuild) return true;
        if (!canAfford(player, cost)) return false;

        Inventory inventory = player.getInventory();
        int remaining = cost.count();
        int size = Math.min(PLAYER_INVENTORY_SIZE, inventory.getContainerSize());

        for (int i = 0; i < size && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);

            if (stack.isEmpty() || stack.getItem() != cost.item()) continue;

            int amount = Math.min(remaining, stack.getCount());
            stack.shrink(amount);
            remaining -= amount;
        }

        inventory.setChanged();
        return remaining == 0;
    }

    public record Cost(Item item, int count) {}
}