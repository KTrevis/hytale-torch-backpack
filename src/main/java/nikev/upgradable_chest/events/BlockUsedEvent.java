package nikev.upgradable_chest.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BlockUsedEvent {

    public static void onBlockUsed(UseBlockEvent event) {
        System.err.println("hello");
        InteractionContext context = event.getContext();
        Ref<EntityStore> ref = context.getEntity();
        BlockType blockType = event.getBlockType();

        if (ref == null || !ref.isValid()) {
            return;
        }
        Player player = context
            .getCommandBuffer()
            .getComponent(ref, Player.getComponentType());
        player.sendMessage(Message.raw(blockType.getStateForBlock(blockType)));
    }
}
