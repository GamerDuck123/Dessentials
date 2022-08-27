package com.gamerduck.essentials.storage.cold;

import com.gamerduck.essentials.storage.hot.EssPlayer;

import java.util.UUID;

public class Database implements IStorage {
    @Override
    public EssPlayer retrievePlayerData(UUID uuid) {
        return null;
    }

    @Override
    public EssPlayer storePlayerData(UUID uuid) {
        return null;
    }
}
