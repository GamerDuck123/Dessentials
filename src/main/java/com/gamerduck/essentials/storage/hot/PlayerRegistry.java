package com.gamerduck.essentials.storage.hot;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.objects.EssPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class PlayerRegistry {

    final ArrayList<EssPlayer> players = Lists.newArrayList();

    public void addPlayer(EssPlayer p) {
        players.add(p);
    }

    public void removePlayer(EssPlayer p) {
        players.remove(p);
    }

    public EssPlayer getPlayer(UUID uuid) {
        Optional<EssPlayer> player = players.stream().filter(p -> p.uniqueID.compareTo(uuid) == 0).findFirst();
        return player.isEmpty() ? null : player.get();
    }

    public EssPlayer getPlayer(ServerPlayerEntity ent) {
        Optional<EssPlayer> player = players.stream().filter(p -> p.uniqueID.compareTo(ent.getUuid()) == 0).findFirst();
        return player.isEmpty() ? null : player.get();
    }

}
