package com.loucaskreger.replanter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Replanter implements ModInitializer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                ItemStack heldStack = player.getStackInHand(hand);

                if (!heldStack.isEmpty()) {
                    this.replaceCrop(heldStack.getItem(), pos, world);
                }
            }
            // Let the game handle the right-click to plant the crop
            return ActionResult.PASS;
        });
    }

    private void replaceCrop(Item heldItem, BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ClientPlayerInteractionManager im = mc.interactionManager;
        if (im == null) return;

        if (heldItem instanceof BlockItem heldBlockItem) {
            Block heldBlock = heldBlockItem.getBlock();

            if (block instanceof CropBlock cropBlock) {
                if (cropBlock.isMature(state) && heldBlock instanceof CropBlock) {
                    im.attackBlock(pos, Direction.DOWN);
                }

            } else if (block instanceof NetherWartBlock wartBlock) {
                // Netherwarts don't tick anymore when they're fully grown
                if (!wartBlock.hasRandomTicks(state) && heldBlock instanceof NetherWartBlock) {
                    im.attackBlock(pos, Direction.DOWN);
                }

            } else if (block instanceof CocoaBlock cocoaBlock) {
                // Cocoa beans don't tick anymore when they're fully grown
                if (!cocoaBlock.hasRandomTicks(state) && heldBlock instanceof CocoaBlock) {
                    // NOTE In survival mode, the cocoa pod only breaks when using an iron axe or better
                    im.attackBlock(pos, state.get(CocoaBlock.FACING));
                }
            }
        }
    }
}