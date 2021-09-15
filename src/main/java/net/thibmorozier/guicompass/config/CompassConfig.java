package net.thibmorozier.guicompass.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.Option;
import net.thibmorozier.guicompass.config.enums.CompassPosEnum;
import net.thibmorozier.guicompass.config.option.CompassBooleanConfigOption;
import net.thibmorozier.guicompass.config.option.CompassPosEnumConfigOption;
import net.thibmorozier.guicompass.config.option.CompassIntegerConfigOption;

public class CompassConfig {
    public static final CompassBooleanConfigOption MUST_HAVE_COMPASS_IN_INVENTORY = new CompassBooleanConfigOption("must_have_compass_in_inventory", true);
    public static final CompassIntegerConfigOption RGB_R = new CompassIntegerConfigOption("rgb_r", 224, 0, 255);
    public static final CompassIntegerConfigOption RGB_G = new CompassIntegerConfigOption("rgb_g", 224, 0, 255);
    public static final CompassIntegerConfigOption RGB_B = new CompassIntegerConfigOption("rgb_b", 224, 0, 255);
    public static final CompassPosEnumConfigOption POSITION = new CompassPosEnumConfigOption("position", CompassPosEnum.BOTTOM_LEFT);

    public static Option[] asOptions() {
		ArrayList<Option> options = new ArrayList<>();

		for (Field field : CompassConfig.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && OptionConvertable.class.isAssignableFrom(field.getType()) && !field.getName().equals("HIDE_CONFIG_BUTTONS") && !field.getName().equals("MODIFY_TITLE_SCREEN") && !field.getName().equals("MODIFY_GAME_MENU")) {
				try {
					options.add(((OptionConvertable) field.get(null)).asOption());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return options.stream().toArray(Option[]::new);
	}
}
