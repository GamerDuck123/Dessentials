package com.gamerduck.essentials.storage.records;

import net.minecraft.item.ItemStack;

public record Kit(String name, Long cooldown, ItemStack[] items) {
}
