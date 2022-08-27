package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class TeleportCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
       final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("teleport")
                .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("location", Vec3ArgumentType.vec3())
                        .executes(ctx -> teleport(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"))))
                    .then(argument("player", EntityArgumentType.players())
                        .executes(ctx -> teleport(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))));
        dispatcher.register(literal("tp").redirect(node));

        final LiteralCommandNode<ServerCommandSource> hereNode = dispatcher.register(literal("teleporthere")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("players", EntityArgumentType.players())
                        .executes(ctx -> teleportHere(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players")))));
        dispatcher.register(literal("tphere").redirect(hereNode));
    }

    public static int teleport(ServerCommandSource source, PosArgument pos) {
        final ServerPlayerEntity self = source.getPlayer();
        Vec3d vec3d = pos.toAbsolutePos(source);
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

    public static int teleport(ServerCommandSource source, ServerPlayerEntity pos) {
        final ServerPlayerEntity self = source.getPlayer();
        self.teleport(pos.getX(), pos.getY(), pos.getZ());
        return Command.SINGLE_SUCCESS;
    }

    public static int teleportHere(ServerCommandSource source, Collection<ServerPlayerEntity> ent) {
        final ServerPlayerEntity self = source.getPlayer();
        ent.forEach(e -> e.teleport(self.getX(), self.getY(), self.getZ()));
        return Command.SINGLE_SUCCESS;
    }

}
