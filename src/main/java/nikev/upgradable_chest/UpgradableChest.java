package nikev.upgradable_chest;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import nikev.upgradable_chest.events.BlockUsedEvent;

public class UpgradableChest extends JavaPlugin {

    private BlockUsedEvent blockUsedEvent;

    public UpgradableChest(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.blockUsedEvent = new BlockUsedEvent();
        this.getEntityStoreRegistry().registerSystem(this.blockUsedEvent);
    }

    @Override
    protected void shutdown() {
        this.blockUsedEvent.shutdown();
    }
}
