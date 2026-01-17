package nikev.backpack_torch.events;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.DynamicLight;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class InventoryChangeEvent {

    private static Item findItemInContainer(
        ItemContainer container,
        String itemName
    ) {
        for (short slot = 0; slot < container.getCapacity(); slot++) {
            ItemStack stack = container.getItemStack(slot);
            if (ItemStack.isEmpty(stack)) {
                continue;
            }
            Item item = stack.getItem();

            if (item.getId().toLowerCase().contains(itemName)) {
                return item;
            }
        }
        return null;
    }

    public static void applyPlayerLight(Player player, Item item) {
        ComponentType<EntityStore, DynamicLight> componentType =
            DynamicLight.getComponentType();

        if (player.getReference() == null || !player.getReference().isValid()) {
            return;
        }

        Store<EntityStore> store = player.getReference().getStore();
        DynamicLight existing = store.getComponent(
            player.getReference(),
            componentType
        );

        if (item == null) {
            if (existing != null) {
                store.removeComponent(player.getReference(), componentType);
            }
            return;
        }
        ColorLight light = new ColorLight(
            (byte) 1,
            (byte) 11,
            (byte) 10,
            (byte) 10
        );
        if (existing == null) {
            store.addComponent(
                player.getReference(),
                componentType,
                new DynamicLight(light)
            );
        } else {
            existing.setColorLight(light);
        }
    }

    public static void onInventoryChange(
        LivingEntityInventoryChangeEvent event
    ) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemContainer playerBackpack = player.getInventory().getBackpack();
        Item item = findItemInContainer(playerBackpack, "torch");
        World world = player.getWorld();
        world.execute(() -> applyPlayerLight(player, item));
    }
}
