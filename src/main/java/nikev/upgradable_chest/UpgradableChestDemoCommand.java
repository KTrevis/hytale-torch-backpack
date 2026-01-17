package nikev.upgradable_chest;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpgradableChestDemoCommand extends AbstractCommand {
    public UpgradableChestDemoCommand(String name, String description) {
        super(name, description);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("Player only."));
            return CompletableFuture.completedFuture(null);
        }

        Ref<EntityStore> ref = context.senderAsPlayerRef();
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());

        if (transformComponent == null) {
            context.sendMessage(Message.raw("Missing transform."));
            return CompletableFuture.completedFuture(null);
        }

        Vector3d position = transformComponent.getPosition();
        int blockX = MathUtil.floor(position.getX());
        int blockY = MathUtil.floor(position.getY());
        int blockZ = MathUtil.floor(position.getZ());
        BlockState blockState = world.getState(blockX, blockY, blockZ, true);
        if (blockState instanceof UpgradableChestState upgradableChest) {
            short capacity = 54;
            upgradableChest.resizeContainer(capacity);
            context.sendMessage(Message.raw("Chest resized to " + capacity));
        } else if (blockState instanceof ItemContainerState) {
            context.sendMessage(Message.raw("Target is not UpgradableChestState"));
        } else {
            context.sendMessage(Message.raw("Target block has no container"));
        }

        return CompletableFuture.completedFuture(null);
    }
}
