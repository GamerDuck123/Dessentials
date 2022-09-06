package com.gamerduck.essentials.commands.TODO;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.storage.objects.EssServer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.literal;


@RegisterCommand
public final class TeleportRandomCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> randomNode = dispatcher.register(literal("rtp")
                .requires(source -> hasPermission(source, "essentials.rtp", source.hasPermissionLevel(0)))
                .executes(ctx -> randomTP(ctx)));
        dispatcher.register(literal("randomtp").redirect(randomNode));
        dispatcher.register(literal("tprandom").redirect(randomNode));
    }

    public static int randomTP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        EssentialsMain.essServer.setRandomWorld(player.getWorld());
        player.sendMessage(Text.of("Finding safe location..."));
        AtomicInteger amountOfTimes = new AtomicInteger();
        findLocation(amountOfTimes::set).thenAccept((pos) -> {
            player.sendMessage(Text.of("Found new location after " + amountOfTimes + " tries"));
            EssentialsMain.playerRegistry.getPlayer(player).teleport(pos, 5);
            player.sendMessage(Text.of("Teleporting..."));
        });
        return Command.SINGLE_SUCCESS;
    }


        /**
         * This class will contain all of the commands relating to teleport requesting
         * such as tpr, rtp, tpr set, tpr max, tpr min, etc.
         */
    private static CompletableFuture<BlockPos> findLocation(Consumer<Integer> amountOfTimes) {
        return CompletableFuture.supplyAsync(() -> {
            Random rand = new Random();
            int x,z;
            BlockPos blockPos;
            BlockState highestBlock;
            int times = 0;
            do {
                x = rand.nextInt(EssentialsMain.essServer.getRandomMin(), EssentialsMain.essServer.getRandomMax());
                x = rand.nextBoolean() ? -x : x;
                z = rand.nextInt(EssentialsMain.essServer.getRandomMin(), EssentialsMain.essServer.getRandomMax());
                z = rand.nextBoolean() ? -z : z;
                EssentialsMain.essServer.getRandomWorld().getChunkManager().addTicket(ChunkTicketType.FORCED, new ChunkPos(new BlockPos(x, 0, z)), 1, null);
                blockPos = EssentialsMain.essServer.getRandomWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(x,0,z));
                highestBlock = EssentialsMain.essServer.getRandomWorld().getBlockState(blockPos);
                times++;
            } while (highestBlock.getMaterial() == Material.LAVA || highestBlock.getMaterial() == Material.WATER);
            amountOfTimes.accept(times);
            return blockPos;
        });
    }
}
