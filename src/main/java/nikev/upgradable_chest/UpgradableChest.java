package nikev.upgradable_chest;

import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import nikev.upgradable_chest.events.BlockUsedEvent;

public class UpgradableChest extends JavaPlugin {

    public UpgradableChest(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(
            UseBlockEvent.class,
            BlockUsedEvent::onBlockUsed
        );
    }
}
