package eu.midnightdust.visualoverhaul.neoforge.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.neoforge.InventoryPacketSynchronizer;
import eu.midnightdust.visualoverhaul.util.InventorySyncState;
import eu.midnightdust.visualoverhaul.util.InventorySyncStateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements InventorySyncStateHolder {

    @Unique
    private final InventorySyncState visualoverhaul$inventorySyncState = new InventorySyncState();

    protected MixinAbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!world.isClient && world.getBlockState(pos).hasBlockEntity()) {
            InventoryPacketSynchronizer.sync(world, blockEntity, blockEntity, VisualOverhaulCommon.UPDATE_TYPE_FURNACE_ITEMS, 3);
        }
    }

    @Override
    public InventorySyncState visualoverhaul$getInventorySyncState() {
        return visualoverhaul$inventorySyncState;
    }
}
