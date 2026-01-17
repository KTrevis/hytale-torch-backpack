package nikev.backpack_torch.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExampleCommand extends AbstractCommand {

    public ExampleCommand(String name, String description) {
        super(name, description);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        context.sendMessage(
            Message.raw("Backpack torch succesfully installed !")
        );
        return CompletableFuture.completedFuture(null);
    }
}
