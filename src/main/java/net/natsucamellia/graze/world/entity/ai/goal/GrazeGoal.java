package net.natsucamellia.graze.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GrazeGoal extends MoveToBlockGoal {
    private final Animal mob;

    @Override
    public double acceptedDistance() {
        return 2.0d;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.mob.canFallInLove() && !this.mob.isBaby();
    }

    public GrazeGoal(Animal mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange, 4);
        this.mob = mob;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        // find mature wheat crop
        BlockState state = level.getBlockState(pos);
        return state.is(Blocks.WHEAT) && state.getValue(CropBlock.AGE) >= 7;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.mob.level();
        if (!level.isClientSide) {
            // Look at the crop block
            this.mob.getLookControl().setLookAt(
                    (double) this.blockPos.getX() + 0.5D,
                    this.blockPos.getY(),
                    (double) this.blockPos.getZ() + 0.5D,
                    10.0F,
                    (float) this.mob.getMaxHeadXRot()
            );
            if (this.isReachedTarget()) {
                onReachedTarget();
                this.stop();
            }
        }
    }

    private void onReachedTarget() {
        Level level = this.mob.level();
        BlockPos cropPos = this.blockPos;
        BlockState state = level.getBlockState(cropPos);
        if (state.getBlock() instanceof CropBlock) {
            // reset crop age to simulate re-plant
            BlockState newState = state.setValue(CropBlock.AGE, 0);
            level.setBlockAndUpdate(cropPos, newState);

            // fall in love
            this.mob.setInLove(null);

            this.mob.playSound(SoundEvents.CROP_BREAK, 1.0F, 1.0F);
        }
    }
}
