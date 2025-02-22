/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lehjr.numina.common.capabilities.render.modelspec;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 2:09 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/8/16.
 */
public enum MorphTarget {
    Hat("hat", EquipmentSlot.HEAD),
    Head("head", EquipmentSlot.HEAD),
    Body("body", EquipmentSlot.CHEST),
    RightArm("right_arm", EquipmentSlot.CHEST),
    LeftArm("left_arm", EquipmentSlot.CHEST),
    RightLeg("right_leg", EquipmentSlot.LEGS),
    LeftLeg("left_leg", EquipmentSlot.LEGS),
    RightFoot("right_foot", EquipmentSlot.FEET),
    LeftFoot("left_foot", EquipmentSlot.FEET),

    /** these would be for something that changes model depending on if equipped in mainhand or offhand,
     * like something that turns into a shield if offhand */
    MainHand("main_hand",  EquipmentSlot.MAINHAND), // do not rely on left/right hand
    OffHand("off_hand",  EquipmentSlot.OFFHAND), // do not rely on left/right hand

    /** these are for hand specific models, like the MPS Power Fist. The equipment slot should be ignored */
    RightHand("right_hand", EquipmentSlot.MAINHAND), // do not rely on slot alone
    Lefthand("left_hand", EquipmentSlot.OFFHAND), // do not rely on slot alone
    AnyHand("any_hand", EquipmentSlot.MAINHAND); // do not rely on slot alone


    String name;
    EquipmentSlot slot;

    MorphTarget(String name, EquipmentSlot slots) {
        this.name = name;
        this.slot = slots;
    }

    public static MorphTarget getMorph(final String name) {
        return Arrays.stream(values()).filter(morph -> name.toLowerCase().equals(morph.name)).findAny().orElseGet(null);
    }

    public HumanoidArm getHandFromEquipmentSlot(LivingEntity entity) {
        assert this.slot.getType() != EquipmentSlot.Type.ARMOR;
        switch (this) {
            case MainHand -> {return entity.getMainArm(); }
            case OffHand -> { return entity.getMainArm().getOpposite(); }
            case Lefthand -> { return HumanoidArm.LEFT; }
            default -> {
                return HumanoidArm.RIGHT;
            }
        }
    }

    public boolean handMatches(LivingEntity entity, EquipmentSlot slot) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR && this.slot.getType() != EquipmentSlot.Type.ARMOR) {
            if (this.equals(MainHand) || this.equals(OffHand)) {
                return slot.equals(this.slot);
            }
            // don't care
            if (this.equals(AnyHand)) {
                return true;
            }

            HumanoidArm arm = slot.equals(EquipmentSlot.MAINHAND) ? entity.getMainArm() : entity.getMainArm().getOpposite();
            return this.getHandFromEquipmentSlot(entity) == arm;
        }
        return false;
    }




    public static List<MorphTarget> getMorphTargetsFromEquipmentSlot(EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> {
                // armor models don't use hat part
                return Arrays.asList(Head);
            }
            case CHEST -> {
                return Arrays.asList(Body, LeftArm, RightArm);
            }
            case LEGS -> {
                return Arrays.asList(LeftLeg, RightLeg);
            }
            case FEET -> {
                return Arrays.asList(LeftFoot, RightFoot);
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    public ModelPart apply(HumanoidModel m) {
        switch (this) {
            case Hat:
            case Head:
                return m.head;

            case Body:
                return m.body;

            case RightHand:
            case RightArm:
                return m.rightArm;

            case Lefthand:
            case LeftArm:
                return m.leftArm;

            case RightFoot:
            case RightLeg:
                return m.rightLeg;

            case LeftFoot:
            case LeftLeg:
                return m.leftLeg;

            default:
                return null;
        }
    }
}