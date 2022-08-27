package com.gamerduck.essentials.storage.cold;

import com.gamerduck.essentials.storage.hot.EssPlayer;

import java.util.UUID;

public interface IStorage {
    public EssPlayer retrievePlayerData(UUID uuid);
    public EssPlayer storePlayerData(UUID uuid);
}
