package net.thibmorozier.guicompass.config.option;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.thibmorozier.guicompass.config.enums.CompassPosEnum;
import net.thibmorozier.guicompass.util.CompassTranslationUtil;

public class CompassPosEnumConfigOption implements OptionConvertable {
	private final String key, translationKey;
	private final Text toolTip;
	private final CompassPosEnum defaultValue;

	public CompassPosEnumConfigOption(String key, CompassPosEnum defaultValue) {
		CompassConfigOptionStorage.setCompassPosEnum(key, defaultValue);
		this.key = key;
		this.translationKey = CompassTranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public CompassPosEnum getValue() {
		return CompassConfigOptionStorage.getCompassPosEnum(key);
	}

	public void setValue(CompassPosEnum value) {
		CompassConfigOptionStorage.setCompassPosEnum(key, value);
	}

	public void cycleValue() {
		CompassConfigOptionStorage.cycleCompassPosEnum(key);
	}

	public void cycleValue(int amount) {
		CompassConfigOptionStorage.cycleCompassPosEnum(key, amount);
	}

	public CompassPosEnum getDefaultValue() {
		return defaultValue;
	}

	private static Text getValueText(CompassPosEnumConfigOption option, CompassPosEnum value) {
		return new TranslatableText(option.translationKey + "." + value.name().toLowerCase(Locale.ROOT));
	}

	public Text getButtonText() {
		return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValueText(this, getValue()));
	}

	@Override
	public Option asOption() {
		return CyclingOption.create(translationKey, CompassPosEnum.values(),
            value -> getValueText(this, value),
            ignored -> CompassConfigOptionStorage.getCompassPosEnum(key),
            (ignored, option, value) -> CompassConfigOptionStorage.setCompassPosEnum(key, value)
        ).tooltip((client) -> {
            List<OrderedText> list = client.textRenderer.wrapLines(toolTip, 200);
            return (value) -> { return list; };
        });
	}
}

