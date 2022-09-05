package com.gamerduck.essentials.storage.cold;

import com.gamerduck.essentials.storage.objects.EssPlayer;
import com.gamerduck.essentials.storage.records.LoadedPlayerData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface IStorage {
    public CompletableFuture<LoadedPlayerData> retrievePlayerData(UUID player);
    public CompletableFuture<Void> storePlayerData(EssPlayer player);
}
