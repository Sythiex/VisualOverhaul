package eu.midnightdust.visualoverhaul.fabric;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import eu.midnightdust.visualoverhaul.util.InventorySyncState;
import eu.midnightdust.visualoverhaul.util.InventorySyncStateHolder;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class InventoryPacketSynchronizer {
    private InventoryPacketSynchronizer() {
    }

    public static void sync(World world, BlockEntity blockEntity, Inventory inventory, Identifier updateType, int slotCount) {
        List<ServerPlayerEntity> trackingPlayers = new ArrayList<>();
        Set<UUID> trackingPlayerIds = new HashSet<>();
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            if (VisualOverhaulCommon.playersWithMod.contains(player.getUuid())) {
                trackingPlayers.add(player);
                trackingPlayerIds.add(player.getUuid());
            }
        }

        InventorySyncState state = ((InventorySyncStateHolder) blockEntity).visualoverhaul$getInventorySyncState();
        InventorySyncState.SyncResult result = state.update(world.getTime(), inventory, slotCount, trackingPlayerIds);
        if (result.recipients().isEmpty()) {
            return;
        }

        UpdateItemsPacket packet = new UpdateItemsPacket(updateType, blockEntity.getPos(), result.inventory());
        for (ServerPlayerEntity player : trackingPlayers) {
            if (result.recipients().contains(player.getUuid())) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }
}
