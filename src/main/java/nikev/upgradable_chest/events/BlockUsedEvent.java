package nikev.upgradable_chest.events;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockUsedEvent
    extends EntityEventSystem<EntityStore, UseBlockEvent.Pre>
{

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
        short newCapacity = 1;
        List<ItemStack> remainder = new ObjectArrayList<>();
        SimpleItemContainer resized = ItemContainer.ensureContainerCapacity(
            (SimpleItemContainer) itemContainerState.getItemContainer(),
            newCapacity,
            SimpleItemContainer::new,
            remainder
        );
        itemContainerState.setItemContainer(resized);
        player.sendMessage(
            Message.raw(String.format("chest resized to %d", newCapacity))
        );
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
