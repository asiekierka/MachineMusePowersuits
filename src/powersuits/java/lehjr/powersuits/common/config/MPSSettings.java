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

package lehjr.powersuits.common.config;

import lehjr.numina.client.config.IMeterConfig;
import lehjr.numina.common.capabilities.module.powermodule.IConfig;
import lehjr.numina.common.config.ModuleConfig;
import lehjr.numina.common.math.Color;
import lehjr.numina.common.math.MathUtils;
import lehjr.powersuits.client.config.ClientConfig;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.constants.MPSRegistryNames;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class MPSSettings {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ServerConfig SERVER_CONFIG;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        {
            final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT_SPEC = clientSpecPair.getRight();
            CLIENT_CONFIG = clientSpecPair.getLeft();
        }
        {
            final Pair<ServerConfig, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
            SERVER_SPEC = serverSpecPair.getRight();
            SERVER_CONFIG = serverSpecPair.getLeft();
        }
    }

    /** Client ------------------------------------------------------------------------------------ */
    // HUD ---------------------------------------------------------------------------------------
    public static boolean useGraphicalMeters() {
        return CLIENT_SPEC.isLoaded() ? CLIENT_CONFIG.HUD_USE_GRAPHICAL_METERS.get() : false;
    }

    public static boolean displayHud() {
        return SERVER_SPEC.isLoaded() ? CLIENT_CONFIG.HUD_DISPLAY_HUD.get() : false;
    }

    public static boolean use24HourClock() {
        return SERVER_SPEC.isLoaded() ? CLIENT_CONFIG.HUD_USE_24_HOUR_CLOCK.get() : false;
    }

    public static float getHudKeybindX() {
        return SERVER_SPEC.isLoaded() ? toFloat(CLIENT_CONFIG.HUD_KEYBIND_X.get()) : 8.0F;
    }

    public static float getHudKeybindY() {
        return SERVER_SPEC.isLoaded() ? toFloat(CLIENT_CONFIG.HUD_KEYBIND_Y.get()) : 32.0F;
    }

    public static IMeterConfig getHeatMeterConfig() {
        return HeatMeterConfig.INSTANCE;
    }

    public enum HeatMeterConfig implements IMeterConfig {
        INSTANCE;

        @Override
        public float getDebugValue() {
            return (float) (0.01 * MathUtils.clampDouble(CLIENT_CONFIG.HEAT_METER_DEBUG_VAL.get(), 0, 100));
        }

        @Override
        public Color getGlassColor() {
            float red = CLIENT_CONFIG.HUD_HEAT_METER_GLASS_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_HEAT_METER_GLASS_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_HEAT_METER_GLASS_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_HEAT_METER_GLASS_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }

        @Override
        public Color getBarColor() {
            float red = CLIENT_CONFIG.HUD_HEAT_METER_BAR_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_HEAT_METER_BAR_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_HEAT_METER_BAR_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_HEAT_METER_BAR_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }
    }

    public static List<ResourceLocation> getExternalModItemsAsToolModuleList() {
        List<?> externalTools = SERVER_SPEC.isLoaded() ?
                SERVER_CONFIG.GENERAL_MOD_ITEMS_AS_TOOL_MODULES.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        externalTools.stream().filter(o->o instanceof String).map(Object::toString).forEach(tool-> {
            retList.add(new ResourceLocation(tool));;
        });
        return retList;
    }

    public static Map<ResourceLocation, ResourceLocation> getExternalModItemsAsToolModules() {
        Map<ResourceLocation, ResourceLocation> retMap = new HashMap<>();
        getExternalModItemsAsToolModuleList().forEach(location -> retMap.put(location, MPSRegistryNames.getRegName(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location)).getDescriptionId())));
        return retMap;
    }

    public static List<ResourceLocation> getExternalModItemsAsWeaponModuleList() {
        List<?> externalWeapons = SERVER_SPEC.isLoaded() ?
                SERVER_CONFIG.GENERAL_MOD_ITEMS_AS_WEAPON_MODULES.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        externalWeapons.stream().filter(o->o instanceof String).map(Object::toString).forEach(weapon-> {
            retList.add(new ResourceLocation(weapon));;
        });
        return retList;
    }

    public static Map<ResourceLocation, ResourceLocation> getExternalModItemsAsWeaponModules() {
        Map<ResourceLocation, ResourceLocation> retMap = new HashMap<>();
        getExternalModItemsAsWeaponModuleList().forEach(location -> retMap.put(location, MPSRegistryNames.getRegName(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location)).getDescriptionId())));
        return retMap;
    }

    public static IMeterConfig getEnergyMeterConfig() {
        return EnergyMeterConfig.INSTANCE;
    }

    public enum EnergyMeterConfig implements IMeterConfig {
        INSTANCE;

        @Override
        public float getDebugValue() {
            return (float) (0.01 * MathUtils.clampDouble(CLIENT_CONFIG.ENERGY_METER_DEBUG_VAL.get(), 0, 100));
        }

        @Override
        public Color getGlassColor() {
            float red = CLIENT_CONFIG.HUD_ENERGY_METER_GLASS_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_ENERGY_METER_GLASS_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_ENERGY_METER_GLASS_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_ENERGY_METER_GLASS_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }

        @Override
        public Color getBarColor() {
            float red = CLIENT_CONFIG.HUD_ENERGY_METER_BAR_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_ENERGY_METER_BAR_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_ENERGY_METER_BAR_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_ENERGY_METER_BAR_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }
    }

    public static IMeterConfig getPlasmaMeterConfig() {
        return PlasmaMeterConfig.INSTANCE;
    }

    public enum PlasmaMeterConfig implements IMeterConfig {
        INSTANCE;

        @Override
        public float getDebugValue() {
            return (float) (0.01 * MathUtils.clampDouble(CLIENT_CONFIG.PLASMA_METER_DEBUG_VAL.get(), 0, 100));
        }

        @Override
        public Color getGlassColor() {
            float red = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_GLASS_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_GLASS_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_GLASS_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_GLASS_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }

        @Override
        public Color getBarColor() {
            float red = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_BAR_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_BAR_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_BAR_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_WEAPON_CHARGE_METER_BAR_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }
    }

    public static IMeterConfig getWaterMeterConfig() {
        return WaterMeterConfig.INSTANCE;
    }

    public enum WaterMeterConfig implements IMeterConfig {
        INSTANCE;

        @Override
        public float getDebugValue() {
            return (float) (0.01 * MathUtils.clampDouble(CLIENT_CONFIG.WATER_METER_DEBUG_VAL.get(), 0, 100));
        }

        @Override
        public Color getGlassColor() {
            float red = CLIENT_CONFIG.HUD_WATER_METER_GLASS_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_WATER_METER_GLASS_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_WATER_METER_GLASS_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_WATER_METER_GLASS_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }

        @Override
        public Color getBarColor() {
            float red = CLIENT_CONFIG.HUD_WATER_METER_BAR_RED.get() * 0.01F;
            float green = CLIENT_CONFIG.HUD_WATER_METER_BAR_GREEN.get() * 0.01F;
            float blue = CLIENT_CONFIG.HUD_WATER_METER_BAR_BLUE.get() * 0.01F;
            float alpha = CLIENT_CONFIG.HUD_WATER_METER_BAR_ALPHA.get() * 0.01F;

            return new Color(red, green, blue, alpha);
        }
    }

    public static boolean showMetersWhenPaused() {
        return SERVER_SPEC.isLoaded() ? CLIENT_CONFIG.SHOW_METERS_WHEN_PAUSED.get() : false;
    }

    /**
     * Server -------------------------------------------------------------------------------------
     */
    // General ------------------------------------------------------------------------------------
    public static double getMaxFlyingSpeed() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_MAX_FLYING_SPEED.get() : 25.0;
    }

    public static double getMaxHeatPowerFist() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_BASE_MAX_HEAT_POWERFIST.get() : 5.0D;
    }

    public static double getMaxHeatHelmet() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_BASE_MAX_HEAT_HELMET.get() : 5.0D;
    }

    public static double getMaxHeatChestplate() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_BASE_MAX_HEAT_CHEST.get() : 20.0D;
    }

    public static double getMaxHeatLegs() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_BASE_MAX_HEAT_LEGS.get() : 15.0D;
    }

    public static double getMaxHeatBoots() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.GENERAL_BASE_MAX_HEAT_FEET.get() : 15.0D;
    }

    // Cosmetic -----------------------------------------------------------------------------------
    public static boolean useLegacyCosmeticSystem() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get() : false;
    }

    public static boolean allowHighPollyArmor() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS.get() : true;
    }

    public static boolean allowPowerFistCustomization() {
        return SERVER_SPEC.isLoaded() ? SERVER_CONFIG.COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN.get() : true;
    }

    public static List<ResourceLocation> getOreList() {
        List<?> ores = SERVER_SPEC.isLoaded() ?
                SERVER_CONFIG.GENERAL_VEIN_MINER_ORE_LIST.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        ores.stream().filter(o->o instanceof String).map(Object::toString).forEach(ore-> {
            retList.add(new ResourceLocation(ore));;
        });
        return retList;
    }

    public static List<ResourceLocation> getBlockList() {
        List<?> blocks = SERVER_SPEC.isLoaded() ?
                SERVER_CONFIG.GENERAL_VEIN_MINER_BLOCK_LIST.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        blocks.stream().filter(o->o instanceof String).map(Object::toString).forEach(block-> {
            retList.add(new ResourceLocation(block));;
        });
        return retList;
    }

    /** Modules ----------------------------------------------------------------------------------- */
    static NonNullLazy<IConfig> moduleConfig = NonNullLazy.of(() ->new ModuleConfig(MPSConstants.MOD_ID));
    public static IConfig getModuleConfig() {
        return moduleConfig.get();
    }

    static float toFloat(double val) {
        return (float)val;
    }
}