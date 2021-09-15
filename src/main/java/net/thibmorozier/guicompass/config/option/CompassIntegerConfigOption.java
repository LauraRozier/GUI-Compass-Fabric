package net.thibmorozier.guicompass.config.option;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.thibmorozier.guicompass.util.CompassTranslationUtil;

public class CompassIntegerConfigOption implements OptionConvertable {
	private final String key, translationKey;
	private final Text toolTip;
	private final Integer defaultValue;
	private final Integer minValue;
	private final Integer maxValue;

	public CompassIntegerConfigOption(String key, Integer defaultValue, Integer minValue, Integer maxValue) {
		CompassConfigOptionStorage.setInteger(key, defaultValue);
		this.key = key;
		this.translationKey = CompassTranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public CompassIntegerConfigOption(String key, Integer defaultValue) {
		this(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public String getKey() {
		return key;
	}

	public Integer getValue() {
		return CompassConfigOptionStorage.getInteger(key);
	}

	public void setValue(Integer value) {
		CompassConfigOptionStorage.setInteger(key, value);
	}

	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public DoubleOption asOption() {
		return new DoubleOption(translationKey, minValue, maxValue, 1.0F,
			ignored -> (double)CompassConfigOptionStorage.getInteger(key),
			(option, value) -> CompassConfigOptionStorage.setInteger(key, value.intValue()),
			(ignored, option) -> new TranslatableText(translationKey, CompassConfigOptionStorage.getInteger(key)),
			client -> client.textRenderer.wrapLines(toolTip, 200)
		);
	}
}
