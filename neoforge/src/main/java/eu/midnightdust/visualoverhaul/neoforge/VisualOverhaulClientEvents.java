package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.FakeBlocks;
import eu.midnightdust.visualoverhaul.IconicButtons;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.LOGGER;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.MOD_ID;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.id;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualOverhaulClientEvents {
    private static final List<FileSystem> OPEN_RESOURCE_PACK_FILE_SYSTEMS = new ArrayList<>();

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER, FurnaceWoodenPlanksModel::getTexturedModelData);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (VOConfig.brewingstand) {
            event.registerBlockEntityRenderer(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        }
        if (VOConfig.jukebox) {
            event.registerBlockEntityRenderer(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        }
        if (VOConfig.furnace) {
            event.registerBlockEntityRenderer(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityType.SMOKER, FurnaceBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityType.BLAST_FURNACE, FurnaceBlockEntityRenderer::new);
        }
    }
    @SubscribeEvent
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new IconReloadListener());
    }
    public static class IconReloadListener implements SynchronousResourceReloader {
        @Override
        public void reload(ResourceManager manager) {
            IconicButtons.reload(manager);
            FakeBlocks.reload(manager);
        }
    }
    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            registerResourcePack(event, "nobrewingbottles", "[VO:L] No Brewing Bottles");
            registerResourcePack(event, "fancyfurnace", "[VO:L] Fancy Furnaces");
            registerResourcePack(event, "coloredwaterbucket", "[VO:L] Colored Water Buckets");
        }
    }

    private static void registerResourcePack(AddPackFindersEvent event, String path, String displayName) {
        event.addRepositorySource(profileAdder -> {
            Path packPath = resolveResourcePackPath(path);
            if (packPath == null) return;

            Identifier packId = id(path);
            ResourcePackProfile.PackFactory packFactory = new DirectoryResourcePack.DirectoryBackedFactory(packPath);
            ResourcePackInfo info = new ResourcePackInfo(
                    packId.toString(),
                    Text.literal(displayName),
                    ResourcePackSource.BUILTIN,
                    Optional.empty()
            );
            ResourcePackProfile profile = ResourcePackProfile.create(
                    info,
                    packFactory,
                    ResourceType.CLIENT_RESOURCES,
                    new ResourcePackPosition(false, ResourcePackProfile.InsertionPosition.TOP, false)
            );

            if (profile == null) {
                LOGGER.error("Could not read built-in resource pack metadata: {}", packPath);
                return;
            }
            profileAdder.accept(profile);
        });
    }

    private static Path resolveResourcePackPath(String path) {
        IModFile modFile = ModList.get().getModFileById(MOD_ID).getFile();
        Path productionPath = modFile.findResource("resourcepacks", path);
        if (Files.isRegularFile(productionPath.resolve("pack.mcmeta"))) return productionPath;

        String metadataResource = "resourcepacks/" + path + "/pack.mcmeta";
        URL metadataUrl = VisualOverhaulClientEvents.class.getClassLoader().getResource(metadataResource);
        if (metadataUrl == null) {
            LOGGER.error("Could not locate built-in resource pack: {}", metadataResource);
            return null;
        }

        try {
            Path metadataPath = pathFromUri(metadataUrl.toURI());
            return metadataPath.getParent();
        } catch (IOException | URISyntaxException | RuntimeException exception) {
            LOGGER.error("Could not resolve built-in resource pack: {}", metadataResource, exception);
            return null;
        }
    }

    private static Path pathFromUri(URI uri) throws IOException {
        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException exception) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of());
            OPEN_RESOURCE_PACK_FILE_SYSTEMS.add(fileSystem);
            return Paths.get(uri);
        }
    }
}
