package com.gamerduck.essentials.storage.objects;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.records.Home;
import com.gamerduck.essentials.storage.records.Kit;
import com.gamerduck.essentials.storage.records.Warp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EssPlayer {
    public final ServerPlayerEntity playerEntity;
    public final UUID uniqueID;
    public final ArrayList<Home> homes = Lists.newArrayList();
    public final HashMap<LocalDateTime, Kit> usedKits = Maps.newHashMap();
    final PlayerAbilities abilities;
    Vec3d backLocation = null;
    Text nickName;
    public EssPlayer(ServerPlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
        this.uniqueID = playerEntity.getUuid();
        this.abilities = playerEntity.getAbilities();

    }

    public void onConnect(Consumer<Void> finished) {
//        EssentialsMain.storage.retrievePlayerData(uniqueID).thenAccept(data -> {
//            this.homes.addAll(data.homes());
//            this.usedKits.putAll(data.usedKits());
//            this.tpAuto = data.tpAuto();
//            this.nickName = data.nickName();
//            finished.accept(null);
//        });
        playerEntity.sendMessage(EssentialsMain.essServer.getMOTD());
        finished.accept(null);
    }

    public void onDisconnect(Consumer<Void> finished) {
//        EssentialsMain.storage.storePlayerData(this).thenAccept(v -> finished.accept(v));
        finished.accept(null);
    }

    private CompletableFuture<Boolean> hasPermission(String permission, boolean ifFailed) {
        if (FabricLoader.getInstance().isModLoaded("LuckPerms")) {
            return LuckPermsProvider.get().getUserManager().loadUser(playerEntity.getUuid())
                    .thenApply(user -> {
                        Tristate tristate = user.getCachedData()
                                .getPermissionData(user.getQueryOptions()).checkPermission(permission);
                        return !tristate.equals(Tristate.UNDEFINED) && tristate.asBoolean();
                    });
        }
        return CompletableFuture.completedFuture(ifFailed);
    }

    public void teleport(double x, double y, double z, double warmup) {
        hasPermission("essentials.teleport.instant", playerEntity.hasPermissionLevel(4)).thenAccept(hasPerm -> {
            ServerWorld world = playerEntity.getWorld();
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos(new BlockPos(x, y, z)), 1, playerEntity.getId());
            playerEntity.stopRiding();
            if (playerEntity.isSleeping()) playerEntity.wakeUp(true, true);
            if (!hasPerm) {
                //APPLY WARMUP
            } else {
                if (world == playerEntity.world) playerEntity.networkHandler.requestTeleport(x, y, z, playerEntity.getYaw(), playerEntity.getPitch());
                else playerEntity.teleport(world, x, y, z, playerEntity.getYaw(), playerEntity.getPitch());
            }
        });
    }
    public void teleport(Vec3d vec3d, double warmup) {
        teleport(vec3d.x, vec3d.y, vec3d.z, warmup);
    }
    public void teleport(BlockPos pos, double warmup) {
        teleport(pos.getX(), pos.getY(), pos.getZ(), warmup);
    }
    public void teleport(GlobalPos pos, double warmup) {
        teleport(pos.getPos(), warmup);
    }

    public void changeGodMode() {
        playerEntity.setInvulnerable(!playerEntity.isInvulnerable());
    }

    public void changeFly() {
        abilities.allowFlying = !abilities.allowFlying;
        if (abilities.flying && !abilities.allowFlying) abilities.flying = false;
        playerEntity.sendAbilitiesUpdate();
    }

    public void changeSpeed(float amount, boolean flySpeed) {
        if (flySpeed) abilities.setFlySpeed(amount / 10);
        else abilities.setWalkSpeed(amount / 10);
        playerEntity.sendAbilitiesUpdate();
    }

    public void changeSpeed(float amount) {
        changeSpeed(amount, abilities.flying);
    }

    public Vec3d getBackLocation() {
        return backLocation;
    }

    public void setBackLocation(Vec3d location) {
        backLocation = location;
    }
    public Boolean gotoBackLocation() {
        if (!playerEntity.getLastDeathPos().isEmpty()) teleport(playerEntity.getLastDeathPos().get(), 5);
        return !playerEntity.getLastDeathPos().isEmpty();
    }

    public Boolean isHome(String name) {
        Optional<Home> home = homes.stream().filter(h -> h.name().equals(name)).findFirst();
        return !home.isEmpty();
    }
    public Home getHome(String name) {
        Optional<Home> home = homes.stream().filter(h -> h.name().equals(name)).findFirst();
        return home.isEmpty() ? null : home.get();
    }
    public CompletableFuture<Home> setHome(String name, Vec3d location) {
        Home home = new Home(name, location);
        homes.add(home);
        return CompletableFuture.completedFuture(home);
    }
    public CompletableFuture<Boolean> delHome(String name) {
        Home home = getHome(name);
        homes.remove(home);
        return CompletableFuture.completedFuture(homes.contains(home));
    }

    public Text getNickName() {
        return nickName == null ? playerEntity.getName() : nickName;
    }

    public void setNickName(Text name) {
        nickName = name;
    }

}
