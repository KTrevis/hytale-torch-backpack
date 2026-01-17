package nikev.backpack_torch;

import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;
import nikev.backpack_torch.commands.ExampleCommand;
import nikev.backpack_torch.events.InventoryChangeEvent;

public class BackpackTorch extends JavaPlugin {

    public BackpackTorch(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(
            new ExampleCommand("backpack-torch", "")
        );
        this.getEventRegistry().registerGlobal(
            LivingEntityInventoryChangeEvent.class,
            InventoryChangeEvent::onInventoryChange
        );
    }
}
