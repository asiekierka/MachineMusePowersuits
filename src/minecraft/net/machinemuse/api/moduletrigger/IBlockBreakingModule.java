package net.machinemuse.api.moduletrigger;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public interface IBlockBreakingModule {
	/**
	 * Return true if using the tool allows the block to drop as an item (e.g. diamond pickaxe on obsidian)
	 * 
	 * @param stack
	 *            ItemStack being used as a tool
	 * @param block
	 *            Block being checked for breakability
	 * @param meta
	 *            Metadata of the block being checked
	 * @param player
	 *            Player doing the breaking
	 * @return True if the player can harvest the block, false if not
	 */
	boolean canHarvestBlock(ItemStack stack, Block block, int meta, EntityPlayer player);

	/**
	 * Get this module's strength versus the targeted block.
	 * 
	 * @param stack
	 * @param block
	 * @param meta
	 * @return
	 */
	float getStrVsBlock(ItemStack stack, Block block, int meta);

	public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLiving entity);

	public void handleHarvestCheck(PlayerEvent.HarvestCheck event);

	public void handleBreakSpeed(PlayerEvent.BreakSpeed event);
}