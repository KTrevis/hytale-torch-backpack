package nikev.upgradable_chest;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

public class UpgradableChestState extends ItemContainerState {
    public List<ItemStack> resizeContainer(short newCapacity) {
        List<ItemStack> remainder = new ObjectArrayList<>();
        if (newCapacity <= 0) {
            return remainder;
        }

        SimpleItemContainer newContainer = ItemContainer.ensureContainerCapacity(
            this.itemContainer,
            newCapacity,
            SimpleItemContainer::new,
            remainder
        );

        if (newContainer != this.itemContainer) {
            this.itemContainer = newContainer;
            this.itemContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
            this.markNeedsSave();
            WindowManager.closeAndRemoveAll(this.getWindows());
        }

        return remainder;
    }
}
