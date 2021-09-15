package net.thibmorozier.guicompass.config;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.terraformersmc.modmenu.config.option.StringSetConfigOption;

import net.fabricmc.loader.api.FabricLoader;
import net.thibmorozier.guicompass.GuiCompass;
import net.thibmorozier.guicompass.config.enums.CompassPosEnum;
import net.thibmorozier.guicompass.config.option.CompassBooleanConfigOption;
import net.thibmorozier.guicompass.config.option.CompassPosEnumConfigOption;
import net.thibmorozier.guicompass.config.option.CompassIntegerConfigOption;
import net.thibmorozier.guicompass.config.option.CompassConfigOptionStorage;

public class CompassConfigManager {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    private static File file;

    private static void prepareConfigFile() {
		if (file != null)
			return;

		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), GuiCompass.MOD_ID + ".json");
	}

	public static void initializeConfig() {
		load();
	}

	private static void load() {
		prepareConfigFile();

		try {
			if (!file.exists())
				save();

			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				JsonObject json = new JsonParser().parse(br).getAsJsonObject();

				for (Field field : CompassConfig.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
							JsonArray jsonArray = json.getAsJsonArray(field.getName().toLowerCase(Locale.ROOT));

							if (jsonArray != null) {
								StringSetConfigOption option = (StringSetConfigOption)field.get(null);
								CompassConfigOptionStorage.setStringSet(option.getKey(), Sets.newHashSet(jsonArray).stream().map(JsonElement::getAsString).collect(Collectors.toSet()));
							}
						} else if (CompassIntegerConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isNumber()) {
								CompassIntegerConfigOption option = (CompassIntegerConfigOption)field.get(null);
								CompassConfigOptionStorage.setInteger(option.getKey(), jsonPrimitive.getAsInt());
							}
						} else if (CompassBooleanConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								CompassBooleanConfigOption option = (CompassBooleanConfigOption)field.get(null);
								CompassConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
							}
						} else if (CompassPosEnumConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isString()) {
								CompassPosEnumConfigOption option = (CompassPosEnumConfigOption) field.get(null);
								CompassPosEnum found = null;

								for (CompassPosEnum value : CompassPosEnum.values()) {
									if (value.name().toLowerCase(Locale.ROOT).equals(jsonPrimitive.getAsString())) {
										found = value;
										break;
									}
								}

								if (found != null)
									CompassConfigOptionStorage.setCompassPosEnum(option.getKey(), found);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException | IllegalAccessException e) {
			System.err.println("Couldn't load Mod Menu configuration file; reverting to defaults");
			e.printStackTrace();
		}
	}

	public static void save() {
		prepareConfigFile();
		JsonObject config = new JsonObject();

		try {
			for (Field field : CompassConfig.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (CompassBooleanConfigOption.class.isAssignableFrom(field.getType())) {
						CompassBooleanConfigOption option = (CompassBooleanConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), CompassConfigOptionStorage.getBoolean(option.getKey()));
					} else if (CompassIntegerConfigOption.class.isAssignableFrom(field.getType())) {
						CompassIntegerConfigOption option = (CompassIntegerConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), CompassConfigOptionStorage.getInteger(option.getKey()));
					} else if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
						StringSetConfigOption option = (StringSetConfigOption)field.get(null);
						JsonArray array = new JsonArray();
						CompassConfigOptionStorage.getStringSet(option.getKey()).forEach(array::add);
						config.add(field.getName().toLowerCase(Locale.ROOT), array);
					} else if (CompassPosEnumConfigOption.class.isAssignableFrom(field.getType())) {
						CompassPosEnumConfigOption option = (CompassPosEnumConfigOption) field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), CompassConfigOptionStorage.getCompassPosEnum(option.getKey()).name().toLowerCase(Locale.ROOT));
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		String jsonString = GSON.toJson(config);

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("Couldn't save Mod Menu configuration file");
			e.printStackTrace();
		}
	}
}
