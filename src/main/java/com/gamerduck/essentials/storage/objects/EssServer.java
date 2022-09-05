package com.gamerduck.essentials.storage.objects;

import com.gamerduck.essentials.storage.records.Jail;
import com.gamerduck.essentials.storage.records.Kit;
import com.gamerduck.essentials.storage.records.Warp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EssServer {

    private final MinecraftServer server;
    public final ArrayList<Warp> warps = Lists.newArrayList();
    public final ArrayList<Kit> kits = Lists.newArrayList();
    public final ArrayList<Jail> jails = Lists.newArrayList();
    public final ArrayList<ServerPlayerEntity> inJail = Lists.newArrayList();
    private Vec3d spawn;
    private Text MOTD;
    public final HashMap<Integer, Text> rules = Maps.newHashMap();

    public EssServer(MinecraftServer server) {
        this.server = server;
        rules.put(1, Text.of("Page 1"));
        rules.put(2, Text.of("Page 2"));
    }
    public void close() {

    }

    public void setSpawn(Vec3d location) {
        spawn = location;
    }

    public Vec3d getSpawn() {
        return spawn;
    }

    public void setMOTD(Text text) {
        MOTD = text;
    }

    public Text getMOTD() {
        return MOTD;
    }

    public Boolean isWarp(String name) {
        Optional<Warp> warp = warps.stream().filter(w -> w.name().equals(name)).findFirst();
        return !warp.isEmpty();
    }
    public Warp getWarp(String name) {
        Optional<Warp> warp = warps.stream().filter(w -> w.name().equals(name)).findFirst();
        return warp.isEmpty() ? null : warp.get();
    }
    public CompletableFuture<Warp> setWarp(String name, Vec3d location) {
        Warp warp = new Warp(name, location);
        warps.add(warp);
        return CompletableFuture.completedFuture(warp);
    }
    public CompletableFuture<Boolean> delWarp(String name) {
        Warp warp = getWarp(name);
        warps.remove(warp);
        return CompletableFuture.completedFuture(warps.contains(warp));
    }

    public Boolean isKit(String name) {
        Optional<Kit> kit = kits.stream().filter(k -> k.name().equals(name)).findFirst();
        return !kit.isEmpty();
    }
    public Kit getKit(String name) {
        Optional<Kit> kit = kits.stream().filter(k -> k.name().equals(name)).findFirst();
        return kit.isEmpty() ? null : kit.get();
    }
    public CompletableFuture<Kit> addKit(String name, Long cooldown, ItemStack... items) {
        Kit kit = new Kit(name, cooldown, items);
        kits.add(kit);
        return CompletableFuture.completedFuture(kit);
    }
    public CompletableFuture<Boolean> delKit(String name) {
        Kit kit = getKit(name);
        kits.remove(kit);
        return CompletableFuture.completedFuture(kits.contains(kit));
    }

    public Boolean isJail(String name) {
        Optional<Jail> jail = jails.stream().filter(j -> j.name().equals(name)).findFirst();
        return !jail.isEmpty();
    }
    public Jail getJail(String name) {
        Optional<Jail> jail = jails.stream().filter(j -> j.name().equals(name)).findFirst();
        return jail.isEmpty() ? null : jail.get();
    }
    public CompletableFuture<Jail> addJail(String name, Vec3d location) {
        Jail jail = new Jail(name, location);
        jails.add(jail);
        return CompletableFuture.completedFuture(jail);
    }
    public CompletableFuture<Boolean> delJail(String name) {
        Jail jail = getJail(name);
        jails.remove(jail);
        return CompletableFuture.completedFuture(jails.contains(jail));
    }
}
