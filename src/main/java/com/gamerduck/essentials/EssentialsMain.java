package com.gamerduck.essentials;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.storage.cold.IStorage;
import com.gamerduck.essentials.storage.cold.JSON;
import com.gamerduck.essentials.storage.hot.PlayerRegistry;
import com.gamerduck.essentials.storage.objects.EssServer;
import com.google.common.reflect.ClassPath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EssentialsMain implements ModInitializer {
	public static final String MODID = "dess";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final PlayerRegistry playerRegistry = new PlayerRegistry();
	public static IStorage storage;
	public static MinecraftServer minecraftServer;
	public static EssServer essServer;


	@Override
	public void onInitialize() {
		storage = loadStorage();
		registerCommands("com.gamerduck.essentials.commands");
		ServerLifecycleEvents.SERVER_STARTING.register(this::onLogicalServerStarting);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onLogicalServerStopping);
	}

	private void onLogicalServerStarting(MinecraftServer server) {
		minecraftServer = server;
		essServer = new EssServer(server);
	}
	private void onLogicalServerStopping(MinecraftServer server) {
		essServer.close();
	}

	private void registerCommands(String pack) {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(pack).forEach(ci -> {
				try {classes.add(Class.forName(ci.getName()));
				} catch (ClassNotFoundException e) {e.printStackTrace();}});
		} catch (IOException e) {e.printStackTrace();}
		classes.stream().filter(c -> c.isAnnotationPresent(RegisterCommand.class)).forEach(clazz -> {
			ICommand cmd;
			try {cmd = (ICommand) clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {throw new RuntimeException(e);}
			CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> cmd.register(dispatcher));
		});
	}

	private IStorage loadStorage() {
		return new JSON();
	}
}
