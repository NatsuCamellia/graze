package net.natsucamellia.graze;

import net.minecraft.world.entity.animal.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.natsucamellia.graze.world.entity.ai.goal.GrazeGoal;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Graze.MOD_ID)
public class Graze {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "graze";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Graze(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Graze) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("[Graze] Mod loaded");
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Animal animal) {
            switch (animal) {
                case Cow cow:
                    cow.goalSelector.addGoal(3, new GrazeGoal(cow, this::isMatureWheat));
                    break;
                case Sheep sheep:
                    sheep.goalSelector.addGoal(3, new GrazeGoal(sheep, this::isMatureWheat));
                    break;
                case Pig pig:
                    pig.goalSelector.addGoal(3, new GrazeGoal(pig, this::isPigFood));
                    break;
                case Chicken chicken:
                    chicken.goalSelector.addGoal(3, new GrazeGoal(chicken, this::isChickenFood));
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isMatureWheat(BlockState state) {
        return state.is(Blocks.WHEAT) && state.getValue(CropBlock.AGE) == CropBlock.MAX_AGE;
    }

    private boolean isMatureCarrots(BlockState state) {
        return state.is(Blocks.CARROTS) && state.getValue(CarrotBlock.AGE) == CarrotBlock.MAX_AGE;
    }

    private boolean isMaturePotatoes(BlockState state) {
        return state.is(Blocks.POTATOES) && state.getValue(PotatoBlock.AGE) == PotatoBlock.MAX_AGE;
    }

    private boolean isMatureBeetroots(BlockState state) {
        return state.is(Blocks.BEETROOTS) && state.getValue(BeetrootBlock.AGE) == BeetrootBlock.MAX_AGE;
    }

    private boolean isPigFood(BlockState state) {
        return isMatureCarrots(state) || isMaturePotatoes(state) || isMatureBeetroots(state);
    }

    private boolean isChickenFood(BlockState state) {
        return isMatureWheat(state) || isMatureBeetroots(state) || state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN);
    }
}
