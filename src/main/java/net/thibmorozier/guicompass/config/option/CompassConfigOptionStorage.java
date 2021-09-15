package net.thibmorozier.guicompass.config.option;

import java.util.HashMap;
import java.util.Map;

import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;

import net.thibmorozier.guicompass.config.enums.CompassPosEnum;

public class CompassConfigOptionStorage extends ConfigOptionStorage {
    private static final Map<String, Integer> INTEGER_OPTIONS = new HashMap<>();
	private static final Map<String, CompassPosEnum> COMPASS_POS_ENUM_OPTIONS = new HashMap<>();

	public static void setInteger(String key, Integer value) {
		INTEGER_OPTIONS.put(key, value);
	}

	public static Integer getInteger(String key) {
		return INTEGER_OPTIONS.get(key);
	}

	public static void setCompassPosEnum(String key, CompassPosEnum value) {
		COMPASS_POS_ENUM_OPTIONS.put(key, value);
	}

	public static CompassPosEnum getCompassPosEnum(String key) {
		return COMPASS_POS_ENUM_OPTIONS.get(key);
	}

	public static CompassPosEnum cycleCompassPosEnum(String key) {
		return cycleCompassPosEnum(key, 1);
	}

	public static CompassPosEnum cycleCompassPosEnum(String key, int amount) {
		CompassPosEnum[] values = CompassPosEnum.values();
		CompassPosEnum currentValue = getCompassPosEnum(key);
		CompassPosEnum newValue = values[(currentValue.ordinal() + amount) % values.length];
		setCompassPosEnum(key, newValue);
		return newValue;
	}
}
