package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.util.InventorySyncState;
import eu.midnightdust.visualoverhaul.util.InventorySyncStateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(JukeboxBlockEntity.class)
public abstract class MixinJukeboxBlockEntity extends BlockEntity implements InventorySyncStateHolder {
    @Unique
    private final InventorySyncState visualoverhaul$inventorySyncState = new InventorySyncState();

    public MixinJukeboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public InventorySyncState visualoverhaul$getInventorySyncState() {
        return visualoverhaul$inventorySyncState;
    }
}
