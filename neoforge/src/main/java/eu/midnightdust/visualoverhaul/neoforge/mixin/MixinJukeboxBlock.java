package eu.midnightdust.visualoverhaul.neoforge.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.neoforge.InventoryPacketSynchronizer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
public abstract class MixinJukeboxBlock extends BlockWithEntity {

    protected MixinJukeboxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "getTicker", at = @At("RETURN"), cancellable = true)
    private <T extends BlockEntity> void visualoverhaul$wrapServerTicker(World world, BlockState state, BlockEntityType<T> type,
                                                                         CallbackInfoReturnable<BlockEntityTicker<T>> cir) {
        if (world.isClient()) {
            return;
        }

        BlockEntityTicker<T> vanillaTicker = cir.getReturnValue();
        BlockEntityTicker<JukeboxBlockEntity> syncingTicker = (tickWorld, pos, tickState, blockEntity) -> {
            if (vanillaTicker != null) {
                ((BlockEntityTicker<JukeboxBlockEntity>) (Object) vanillaTicker).tick(tickWorld, pos, tickState, blockEntity);
            }
            InventoryPacketSynchronizer.sync(tickWorld, blockEntity, blockEntity, VisualOverhaulCommon.UPDATE_TYPE_RECORD, 1);
        };
        cir.setReturnValue(validateTicker(type, BlockEntityType.JUKEBOX, syncingTicker));
    }
}

