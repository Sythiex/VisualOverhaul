package eu.midnightdust.visualoverhaul.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks the inventory state and players synchronized for one block entity.
 */
public final class InventorySyncState {
    private DefaultedList<ItemStack> lastInventory;
    private final Set<UUID> trackingPlayers = new HashSet<>();
    private long lastObservedTick = Long.MIN_VALUE;

    public SyncResult update(long currentTick, Inventory inventory, int slotCount, Collection<UUID> currentTrackingPlayers) {
        // A skipped tick means watcher departures could not be observed, so prior recipients are no longer reliable.
        if (lastObservedTick != Long.MIN_VALUE && currentTick != lastObservedTick + 1) {
            trackingPlayers.clear();
        }
        lastObservedTick = currentTick;

        boolean inventoryChanged = inventoryChanged(inventory, slotCount);
        if (inventoryChanged) {
            lastInventory = copyInventory(inventory, slotCount);
        }

        Set<UUID> recipients = new HashSet<>(currentTrackingPlayers);
        if (!inventoryChanged) {
            recipients.removeAll(trackingPlayers);
        }

        trackingPlayers.clear();
        trackingPlayers.addAll(currentTrackingPlayers);
        return new SyncResult(lastInventory, Set.copyOf(recipients));
    }

    private boolean inventoryChanged(Inventory inventory, int slotCount) {
        if (lastInventory == null || lastInventory.size() != slotCount) {
            return true;
        }

        for (int slot = 0; slot < slotCount; slot++) {
            if (!ItemStack.areEqual(lastInventory.get(slot), inventory.getStack(slot))) {
                return true;
            }
        }
        return false;
    }

    private static DefaultedList<ItemStack> copyInventory(Inventory inventory, int slotCount) {
        DefaultedList<ItemStack> copy = DefaultedList.ofSize(slotCount, ItemStack.EMPTY);
        for (int slot = 0; slot < slotCount; slot++) {
            copy.set(slot, inventory.getStack(slot).copy());
        }
        return copy;
    }

    public record SyncResult(DefaultedList<ItemStack> inventory, Set<UUID> recipients) {
    }
}
