package com.gamerduck.essentials.commands.handlers;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.objects.EssServer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public interface ICommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher);

    default boolean hasPermission(ServerCommandSource source, String permission, boolean ifFailed) {
        if (!source.isExecutedByPlayer()) return true;
        if (FabricLoader.getInstance().isModLoaded("LuckPerms")) {
            AtomicBoolean toReturn = new AtomicBoolean(false);
            LuckPermsProvider.get().getUserManager().loadUser(source.getPlayer().getUuid())
                    .thenApply(user -> {
                        Tristate tristate = user.getCachedData().getPermissionData(user.getQueryOptions()).checkPermission(permission);
                        return tristate.equals(Tristate.UNDEFINED) ? false : tristate.asBoolean();
                    }).thenAccept(b -> toReturn.set(b));
            return toReturn.get();
        }
        return ifFailed;
    }

    EssServer server = EssentialsMain.essServer;
    default EssServer essServer() {
        return server;
    }
}
