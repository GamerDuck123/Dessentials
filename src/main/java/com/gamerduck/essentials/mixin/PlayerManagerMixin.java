package com.gamerduck.essentials.mixin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.objects.EssPlayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"), cancellable = true)
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (!player.isDisconnected()) {
            EssPlayer p = new EssPlayer(player);
            p.onConnect((v) -> EssentialsMain.playerRegistry.addPlayer(p));
            EssentialsMain.LOGGER.info("Joined " + p.playerEntity.getName().toString());
        }
    }

    @Inject(method = "remove", at = @At("TAIL"), cancellable = true)
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        EssPlayer p = EssentialsMain.playerRegistry.getPlayer(player);
        p.onDisconnect((v) -> EssentialsMain.playerRegistry.removePlayer(p));
        EssentialsMain.LOGGER.info("Quit " + p.playerEntity.getName().toString());
    }

}
