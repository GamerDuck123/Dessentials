package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.command.argument.Vec3ArgumentType.getPosArgument;
import static net.minecraft.command.argument.Vec3ArgumentType.vec3;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class TeleportCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
       final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("teleport")
                .requires(source -> hasPermission(source, "essentials.teleport", source.hasPermissionLevel(2)))
                    .then(argument("location", vec3())
                        .executes(ctx -> teleport(ctx, getPosArgument(ctx, "location"))))
                    .then(argument("player", player())
                        .executes(ctx -> teleport(ctx, getPlayer(ctx, "player")))));
        dispatcher.register(literal("tp").redirect(node));

        final LiteralCommandNode<ServerCommandSource> hereNode = dispatcher.register(literal("teleporthere")
                .requires(source -> hasPermission(source, "essentials.teleport.here", source.hasPermissionLevel(2)))
                .then(argument("players", players())
                        .executes(ctx -> teleportHere(ctx, getPlayers(ctx, "players")))));
        dispatcher.register(literal("tphere").redirect(hereNode));

        final LiteralCommandNode<ServerCommandSource> allNode = dispatcher.register(literal("teleportall")
                .requires(source -> hasPermission(source, "essentials.teleport.all", source.hasPermissionLevel(2)))
                        .executes(ctx -> teleportAll(ctx)));
        dispatcher.register(literal("tpall").redirect(allNode));
    }

    public static int teleport(CommandContext<ServerCommandSource> ctx, PosArgument pos) {
        final ServerPlayerEntity self = ctx.getSource().getPlayer();
        Vec3d vec3d = pos.toAbsolutePos(ctx.getSource());
        double x = vec3d.x;
        double y = vec3d.y;
        double z = vec3d.z;
        ServerWorld world = self.getWorld();
        world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos(new BlockPos(x, y, z)), 1, self.getId());
        self.stopRiding();
        if (self.isSleeping()) self.wakeUp(true, true);
        if (world == self.world) self.networkHandler.requestTeleport(x, y, z, self.getYaw(), self.getPitch());
        else self.teleport(world, x, y, z, self.getYaw(), self.getPitch());
        return Command.SINGLE_SUCCESS;
    }

    public static int teleport(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity pos) {
        final ServerPlayerEntity self = ctx.getSource().getPlayer();
        self.teleport(pos.getX(), pos.getY(), pos.getZ());
        return Command.SINGLE_SUCCESS;
    }

    public static int teleportHere(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> ent) {
        final ServerPlayerEntity self = ctx.getSource().getPlayer();
        ent.forEach(e -> e.teleport(self.getX(), self.getY(), self.getZ()));
        return Command.SINGLE_SUCCESS;
    }

    public static int teleportAll(CommandContext<ServerCommandSource> ctx) {
        final ServerPlayerEntity self = ctx.getSource().getPlayer();
        self.getServer().getPlayerManager().getPlayerList().forEach(p -> p.teleport(self.getX(),self.getY(),self.getZ()));
        return Command.SINGLE_SUCCESS;
    }

}
