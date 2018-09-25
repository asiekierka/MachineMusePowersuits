package net.machinemuse.powersuits.recipe;

import com.google.gson.JsonObject;
import net.machinemuse.powersuits.common.ModCompatibility;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class MPSRecipeConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        boolean value = JsonUtils.getBoolean(json , "getValue", true);

        if(JsonUtils.hasField(json, "type")) {
            String key = JsonUtils.getString(json, "type");

            // Thermal Expansion
            if (key.equals("powersuits:thermal_expansion_recipes_enabled"))
                return () -> ModCompatibility.isThermalExpansionLoaded() == value;

            // EnderIO
            if (key.equals("powersuits:enderio_recipes_enabled"))
                return () -> ModCompatibility.isEnderIOLoaded() == value;

            // Original recipe loading code set priority for TechReborn recipes instead of Gregtech or Industrialcraft
            // Tech Reborn
            if (key.equals("powersuits:tech_reborn_recipes_enabled"))
                return () -> ModCompatibility.isTechRebornLoaded() == value;

            // GregTech
            if (key.equals("powersuits:gregtech_recipes_enabled"))
                return () -> (ModCompatibility.isGregTechLoaded() && !ModCompatibility.isTechRebornLoaded()) == value;

            // IC2
            if (key.equals("powersuits:ic2_recipes_enabled"))
                return () -> (ModCompatibility.isIndustrialCraftExpLoaded() &&
                        (!ModCompatibility.isGregTechLoaded() && !ModCompatibility.isTechRebornLoaded())) == value;

            // IC2 Classic
            if (key.equals("powersuits:ic2_classic_recipes_enabled"))
                return () -> (ModCompatibility.isIndustrialCraftClassicLoaded() &&
                        (!ModCompatibility.isGregTechLoaded() && !ModCompatibility.isTechRebornLoaded())) == value;


            // Vanilla reciples only as fallback
            if (key.equals("powersuits:vanilla_recipes_enabled")) {
                return () -> (ModCompatibility.isEnderIOLoaded() ||
                        ModCompatibility.isGregTechLoaded() ||
                        ModCompatibility.isIndustrialCraftLoaded() ||
                        ModCompatibility.isTechRebornLoaded() ||
                        ModCompatibility.isThermalExpansionLoaded()
                ) != value;
            }
        }
        return () -> false;
    }
}