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

package com.github.lehjr.numina.util.capabilities.module.powermodule;

import net.minecraft.util.text.TranslationTextComponent;

/**
 * The module categories
 */
public enum EnumModuleCategory {
    NONE("module.category.none", "None"),
    DEBUG("module.category.debug", "Debug_Modules"),
    ARMOR("module.category.armor", "Armor_Modules"),
    ENERGY_STORAGE("module.category.energystorage", "Energy_Storage_Modules"),
    ENERGY_GENERATION("module.category.energygeneration", "Energy_Generation_Modules"),
    TOOL("module.category.tool", "Tool_Modules"),
    WEAPON("module.category.weapon", "Weapon_Modules"),
    MOVEMENT("module.category.movement", "Movement_Modules"),
    COSMETIC("module.category.cosmetic", "Cosmetic_Modules"),
    VISION("module.category.vision", "Vision_Modules"),
    ENVIRONMENTAL("module.category.environment", "Environment_Modules"),
    SPECIAL("module.category.special", "Special_Modules"),
    MINING_ENHANCEMENT("module.category.miningenhancement", "Mining_Enhancement_Modules");

    private final String configTitle;
    private final TranslationTextComponent translation;


    //TODO: add translation stuff
    EnumModuleCategory(String translationString, String configTitle) {
        this.translation = new TranslationTextComponent(translationString);
        this.configTitle = configTitle;
    }

    public TranslationTextComponent getTranslation() {
        return translation;
    }

    public String getConfigTitle() {
        return configTitle;
    }
}
