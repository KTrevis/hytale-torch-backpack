package nikev.upgradable_chest.events;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerBlockWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer.ItemContainerChangeEvent;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockUsedEvent
    extends EntityEventSystem<EntityStore, UseBlockEvent.Pre>
{

    private static final String UPGRADE_ITEM_ID = "Soil_Gravel";

    private final Map<
        ItemContainerState,
        EventRegistration<?, ?>
    > containerRegistrations = new WeakHashMap<>();

    public BlockUsedEvent() {
        super(UseBlockEvent.Pre.class);
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull UseBlockEvent.Pre event
    ) {
        InteractionContext context = event.getContext();
        Ref<EntityStore> ref = context.getEntity();
        Player player = commandBuffer.getComponent(
            ref,
            Player.getComponentType()
        );
        if (player == null) {
            return;
        }

        Vector3i target = event.getTargetBlock();
        World world = store.getExternalData().getWorld();
        BlockState state = world.getState(target.x, target.y, target.z, true);
        if (!(state instanceof ItemContainerState itemContainerState)) {
            return;
        }

        registerUpgradeListener(itemContainerState, player, ref, store);
    }

    public void shutdown() {
        for (EventRegistration<
            ?,
            ?
        > registration : this.containerRegistrations.values()) {
            registration.unregister();
        }
        this.containerRegistrations.clear();
    }

    private void onContainerChange(
        ItemContainerChangeEvent event,
        ItemContainerState itemContainerState,
        Player player,
        Ref<EntityStore> ref,
        Store<EntityStore> store
    ) {
        if (!event.transaction().succeeded()) {
            return;
        }
        short upgradeItemCount = getUpgradeItemCount(itemContainerState);
        applyUpgrade(itemContainerState, upgradeItemCount, player, ref, store);
    }

    private void registerUpgradeListener(
        ItemContainerState itemContainerState,
        Player player,
        Ref<EntityStore> ref,
        Store<EntityStore> store
    ) {
        EventRegistration<?, ?> existing = this.containerRegistrations.get(
            itemContainerState
        );
        if (existing != null) {
            existing.unregister();
            this.containerRegistrations.remove(itemContainerState);
        }
        EventRegistration<?, ?> registration = itemContainerState
            .getItemContainer()
            .registerChangeEvent(EventPriority.LAST, event ->
                onContainerChange(event, itemContainerState, player, ref, store)
            );
        this.containerRegistrations.put(itemContainerState, registration);
    }

    private static short getUpgradeItemCount(
        ItemContainerState itemContainerState
    ) {
        ItemContainer container = itemContainerState.getItemContainer();
        short upgradeItemCount = 0;

        for (short slot = 0; slot < container.getCapacity(); slot++) {
            ItemStack stack = container.getItemStack(slot);
            if (ItemStack.isEmpty(stack)) {
                continue;
            }
            if (UPGRADE_ITEM_ID.equalsIgnoreCase(stack.getItemId())) {
                upgradeItemCount += stack.getQuantity();
            }
        }
        return upgradeItemCount;
    }

    private void openContainerWindow(
        ItemContainerState itemContainerState,
        Player player,
        Ref<EntityStore> ref,
        Store<EntityStore> store
    ) {
        Vector3i pos = itemContainerState.getBlockPosition();
        World world = player.getWorld();
        WorldChunk chunk = world.getChunk(
            ChunkUtil.indexChunkFromBlock(pos.x, pos.z)
        );
        BlockType blockType = world.getBlockType(pos);
        UUIDComponent uuidComponent = store.getComponent(
            ref,
            UUIDComponent.getComponentType()
        );
        if (uuidComponent == null) {
            return;
        }
        UUID uuid = uuidComponent.getUuid();
        ContainerBlockWindow window = new ContainerBlockWindow(
            pos.x,
            pos.y,
            pos.z,
            chunk.getRotationIndex(pos.x, pos.y, pos.z),
            blockType,
            itemContainerState.getItemContainer()
        );
        Map<UUID, ContainerBlockWindow> windows =
            itemContainerState.getWindows();
        if (windows.putIfAbsent(uuid, window) == null) {
            if (
                !player
                    .getPageManager()
                    .setPageWithWindows(ref, store, Page.Bench, true, window)
            ) {
                windows.remove(uuid, window);
                return;
            }
            window.registerCloseEvent(event -> windows.remove(uuid, window));
        }
    }

    private void refreshContainerWindow(
        ItemContainerState itemContainerState,
        Player player,
        Ref<EntityStore> ref,
        Store<EntityStore> store
    ) {
        WindowManager windowManager = player.getWindowManager();

        for (Window window : itemContainerState.getWindows().values()) {
            windowManager.closeWindow(window.getId());
        }
        itemContainerState.getWindows().clear();
        openContainerWindow(itemContainerState, player, ref, store);
    }

    private void applyUpgrade(
        ItemContainerState itemContainerState,
        short upgradeItemCount,
        Player player,
        Ref<EntityStore> ref,
        Store<EntityStore> store
    ) {
        SimpleItemContainer container =
            (SimpleItemContainer) itemContainerState.getItemContainer();
        short upgradedCapacity = (short) (upgradeItemCount * 9);
        boolean isDoubleChest = container.getCapacity() == 9 * 4;
        if (container.getCapacity() >= upgradedCapacity || !isDoubleChest) {
            return;
        }
        List<ItemStack> remainder = new ObjectArrayList<>();
        SimpleItemContainer resized = ItemContainer.ensureContainerCapacity(
            container,
            upgradedCapacity,
            SimpleItemContainer::new,
            remainder
        );
        itemContainerState.setItemContainer(resized);
        itemContainerState.setCustom(true);
        resized.registerChangeEvent(EventPriority.LAST, event ->
            onContainerChange(event, itemContainerState, player, ref, store)
        );
        refreshContainerWindow(itemContainerState, player, ref, store);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
