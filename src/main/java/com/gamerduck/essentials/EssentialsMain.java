package com.gamerduck.essentials;

import com.gamerduck.essentials.commands.arguments.GameModeArgumentType;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.google.common.reflect.ClassPath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
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

	@Override
	public void onInitialize() {
		LOGGER.info("STARTING ESSENTIALS");
		ArgumentTypeRegistry.registerArgumentType(new Identifier(MODID, "gamemode"), GameModeArgumentType.class, ConstantArgumentSerializer.of(GameModeArgumentType::gamemodes));

		registerCommands("com.gamerduck.essentials.commands");
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
}
