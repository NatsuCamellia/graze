package net.natsucamellia.graze.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class GrazeGoal extends MoveToBlockGoal {
    private final Animal mob;
    private final Predicate<BlockState> foodPredicate;

    @Override
    public double acceptedDistance() {
        return 2.0d;
    }

    @Override
    public boolean canUse() {
        // can breed and can fall in love
        return super.canUse() && this.mob.getAge() == 0 && this.mob.canFallInLove();
    }

    public GrazeGoal(Animal mob, Predicate<BlockState> foodPredicate, int searchRange) {
        super(mob, 1.2d, searchRange, 4);
        this.mob = mob;
        this.foodPredicate = foodPredicate;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        // find mature wheat crop
        BlockState state = level.getBlockState(pos);
        return this.foodPredicate.test(state);
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
                level.destroyBlock(this.blockPos, false, this.mob);
                this.mob.setInLove(null);
                this.stop();
            }
        }
    }
}
