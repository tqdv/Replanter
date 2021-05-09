package com.loucaskreger.replanter.client;

import com.loucaskreger.replanter.Replanter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Replanter.MOD_ID, value = Dist.CLIENT)
public class EventSubscriber {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void onPlayerRightClick(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getSide() == LogicalSide.CLIENT) {
			BlockRayTraceResult result = event.getHitVec();
			World world = event.getWorld();
			BlockPos pos = result.getBlockPos();
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			ItemStack stack = event.getItemStack();
			Item item = stack.getItem();
			PlayerController pc = mc.gameMode;

			if (item instanceof BlockItem) {
				Block itemBlock = ((BlockItem) item).getBlock();
				if (block instanceof CropsBlock) {

					if (isPlantable(itemBlock, block, world, pos)) {
						CropsBlock cropsBlock = ((CropsBlock) block);

						if (cropsBlock.isMaxAge(state)) {
							pc.startDestroyBlock(pos, Direction.DOWN);
						}
					}
				} else if (block instanceof NetherWartBlock) {

					if (isPlantable(itemBlock, block, world, pos)) {
						NetherWartBlock wartBlock = (NetherWartBlock) block;
						// If nether wart block outline shape y value is max size then the block is
						// fully grown.
						if ((wartBlock.getShape(state, world, pos, ISelectionContext.empty()).bounds().getYsize()
								* 16) == 14) {
							pc.startDestroyBlock(pos, Direction.DOWN);
						}
					}
				}
			}
		}
	}

	private static boolean isPlantable(Block itemBlock, Block block, World world, BlockPos pos) {
		if (itemBlock instanceof IPlantable) {
			PlantType itemType = ((IPlantable) itemBlock).getPlantType(world, pos);
			PlantType blockType = ((IPlantable) block).getPlantType(world, pos);
			if (itemType == blockType) {
				return true;
			}
		}
		return false;
	}

}
