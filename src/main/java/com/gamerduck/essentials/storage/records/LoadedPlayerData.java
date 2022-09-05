package com.gamerduck.essentials.storage.records;

import net.minecraft.text.Text;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public record LoadedPlayerData(List<Home> homes, HashMap<LocalDateTime, Kit> usedKits,
                               boolean godMode, boolean tpAuto, Text nickName) {
}
