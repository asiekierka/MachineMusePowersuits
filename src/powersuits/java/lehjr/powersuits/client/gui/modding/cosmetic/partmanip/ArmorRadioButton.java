package lehjr.powersuits.client.gui.modding.cosmetic.partmanip;

import lehjr.numina.client.gui.clickable.RadioButton;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import lehjr.numina.client.render.IconUtils;
import lehjr.numina.common.math.Colour;

public class ArmorRadioButton extends RadioButton {
    public ArmorRadioButton(double leftPos, double topPos, Colour colour) {
        super(IconUtils.getIcon().normalArmor, 8, 8, leftPos, topPos);
    }

    public ArmorRadioButton(MusePoint2D pos, Colour colour) {
        super(IconUtils.getIcon().normalArmor, new MusePoint2D(8,8), pos);
    }
}