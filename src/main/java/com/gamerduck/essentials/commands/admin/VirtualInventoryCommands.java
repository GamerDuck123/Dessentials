package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;

import java.io.File;

import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class VirtualInventoryCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> enderChestNode = dispatcher.register(literal("enderchest")
                .requires(source -> hasPermission(source, "essentials.enderchest", source.hasPermissionLevel(2)))
                .executes(ctx -> enderChest(ctx))
                .then(argument("target", player())
                        .requires(source -> hasPermission(source, "essentials.enderchest.other", source.hasPermissionLevel(2)))
                        .executes(ctx -> otherEnderChest(ctx, getPlayer(ctx, "target")))));
        dispatcher.register(literal("ec").redirect(enderChestNode));
        final LiteralCommandNode<ServerCommandSource> loomNode = dispatcher.register(literal("loom")
                .requires(source -> hasPermission(source, "essentials.loom", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.LOOM)));
        final LiteralCommandNode<ServerCommandSource> anvilNode = dispatcher.register(literal("anvil")
                .requires(source -> hasPermission(source, "essentials.anvil", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.ANVIL)));
        final LiteralCommandNode<ServerCommandSource> stoneCutterNode = dispatcher.register(literal("stonecutter")
                .requires(source -> hasPermission(source, "essentials.stonecutter", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.STONECUTTER)));
        final LiteralCommandNode<ServerCommandSource> grindstoneNode = dispatcher.register(literal("grindstone")
                .requires(source -> hasPermission(source, "essentials.grindstone", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.GRINDSTONE)));
        final LiteralCommandNode<ServerCommandSource> craftingNode = dispatcher.register(literal("craft")
                .requires(source -> hasPermission(source, "essentials.craft", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.CRAFTING)));
        dispatcher.register(literal("workbench").redirect(craftingNode));
        final LiteralCommandNode<ServerCommandSource> enchantmentNode = dispatcher.register(literal("enchant")
                .requires(source -> hasPermission(source, "essentials.enchant", source.hasPermissionLevel(2)))
                .executes(ctx -> openInventory(ctx, ScreenHandlerType.ENCHANTMENT)));
        final LiteralCommandNode<ServerCommandSource> invSeeNode = dispatcher.register(literal("invsee")
                .requires(source -> hasPermission(source, "essentials.invsee", source.hasPermissionLevel(4)))
                .then(argument("target", player())
                        .executes(ctx -> invsee(ctx, getPlayer(ctx, "target")))));
    }

    public static int invsee(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity otherPlayer) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        PlayerInventory requestedInventroy = otherPlayer.getInventory();
        SimpleGui gui = new PlayerEnderChest(ScreenHandlerType.GENERIC_9X4, player, otherPlayer);
        gui.setTitle(otherPlayer.getName());
        for (int i = 0; i < requestedInventroy.size(); i++)
            gui.setSlotRedirect(i, new Slot(requestedInventroy, i, 0, 0));
        gui.open();
        return Command.SINGLE_SUCCESS;
    }
    public static int enderChest(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        EnderChestInventory requestedEchest = player.getEnderChestInventory();
        SimpleGui gui = new PlayerEnderChest(ScreenHandlerType.GENERIC_9X3, player);
        gui.setTitle(Text.literal("Enderchest"));
        for (int i = 0; i < requestedEchest.size(); i++)
            gui.setSlotRedirect(i, new Slot(requestedEchest, i, 0, 0));
        gui.open();

        return Command.SINGLE_SUCCESS;
    }

    public static int otherEnderChest(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity otherPlayer) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        EnderChestInventory requestedEchest = otherPlayer.getEnderChestInventory();
        SimpleGui gui = new PlayerEnderChest(ScreenHandlerType.GENERIC_9X3, player, otherPlayer);
        gui.setTitle(otherPlayer.getName());
        for (int i = 0; i < requestedEchest.size(); i++)
            gui.setSlotRedirect(i, new Slot(requestedEchest, i, 0, 0));
        gui.open();

        return Command.SINGLE_SUCCESS;
    }

    public static int openInventory(CommandContext<ServerCommandSource> ctx, ScreenHandlerType type) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        SimpleGui gui = new SimpleGui(type, player, true);
        gui.open();
        return Command.SINGLE_SUCCESS;
    }


    private static class PlayerEnderChest extends SimpleGui {
        private final ServerPlayerEntity player;
        public PlayerEnderChest(ScreenHandlerType<?> type, ServerPlayerEntity player) {
            super(type, player, false);
            this.player = player;
        }
        public PlayerEnderChest(ScreenHandlerType<?> type, ServerPlayerEntity player, ServerPlayerEntity otherPlayer) {
            super(type, player, false);
            this.player = otherPlayer;
        }

        @Override
        public void onClose() {
            File playerDataDir = EssentialsMain.minecraftServer.getSavePath(WorldSavePath.PLAYERDATA).toFile();
            try {
                NbtCompound compoundTag = player.writeNbt(new NbtCompound());
                File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", playerDataDir);
                NbtIo.writeCompressed(compoundTag, file);
                File file2 = new File(playerDataDir, player.getUuidAsString() + ".dat");
                File file3 = new File(playerDataDir, player.getUuidAsString() + ".dat_old");
                Util.backupAndReplace(file2, file, file3);
            } catch (Exception var6) {
                LogManager.getLogger().warn("Failed to save player data for {}", player.getName().getString());
            }
        }
    }
}