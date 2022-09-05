package com.gamerduck.essentials.storage.cold;

import com.gamerduck.essentials.storage.objects.EssPlayer;
import com.gamerduck.essentials.storage.records.LoadedPlayerData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database implements IStorage {
    @Override
    public CompletableFuture<LoadedPlayerData> retrievePlayerData(UUID player) {

        return CompletableFuture.completedFuture(new LoadedPlayerData(null, null, false, false, null));
    }

    @Override
    public CompletableFuture<Void> storePlayerData(EssPlayer player) {

        return CompletableFuture.completedFuture(null);
    }
}
