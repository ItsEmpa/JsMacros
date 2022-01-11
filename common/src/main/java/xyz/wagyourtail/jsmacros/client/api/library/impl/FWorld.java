package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;
import xyz.wagyourtail.jsmacros.client.api.helpers.*;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * Functions for getting and using world data.
 * 
 * An instance of this class is passed to scripts as the {@code World} variable.
 * 
 * @author Wagyourtail
 */
 @Library("World")
 @SuppressWarnings("unused")
public class FWorld extends BaseLibrary {
    
    private static final Minecraft mc = Minecraft.getInstance();
    /**
     * Don't modify.
     */
    public static double serverInstantTPS = 20;
    /**
     * Don't modify.
     */
    public static double server1MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server5MAverageTPS = 20;
    /**
     * Don't modify.
     */
    public static double server15MAverageTPS = 20;
    
    /**
     * returns whether a world is currently loaded
     * @since 1.3.0
     * @return
     */
    public boolean isWorldLoaded() {
        return mc.world != null;
    }

    /**
     * @return players within render distance.
     */
    public List<PlayerEntityHelper<EntityPlayer>> getLoadedPlayers() {
        assert mc.world != null;
        List<PlayerEntityHelper<EntityPlayer>> players = new ArrayList<>();
        for (EntityPlayer p : ImmutableList.copyOf(mc.world.playerEntities)) {
            players.add(new PlayerEntityHelper<>(p));
        }
        return players;
    }
    
    /**
     * @return players on the tablist.
     */
    public List<PlayerListEntryHelper> getPlayers() {
        NetHandlerPlayClient handler = mc.getNetworkHandler();
        assert handler != null;
        List<PlayerListEntryHelper> players = new ArrayList<>();
        for (NetworkPlayerInfo p : ImmutableList.copyOf(handler.getPlayerList())) {
            players.add(new PlayerListEntryHelper(p));
        }
        return players;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return The block at that position.
     */
    public BlockDataHelper getBlock(int x, int y, int z) {
        assert mc.world != null;
        BlockPos bp = new BlockPos(x, y, z);
        IBlockState b = mc.world.getBlockState(bp);
        TileEntity t = mc.world.getBlockEntity(bp);
        if (b.getBlock().equals(Blocks.AIR)) return null;
        return new BlockDataHelper(b, t, bp);
    }

    public BlockDataHelper getBlock(PositionCommon.Pos3D pos) {
        return getBlock((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public BlockDataHelper getBlock(BlockPosHelper pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * @since 1.6.4
     * @param id
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, String id, int chunkrange) {
        return findBlocksMatchingInternal(centerX, centerZ, s -> Block.REGISTRY.getIdentifier(s).toString().equals(id), null, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param id
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(String id, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = (int) mc.player.x >> 4;
        int playerChunkZ = (int) mc.player.z >> 4;
        return findBlocksMatchingInternal(playerChunkX, playerChunkZ, s -> Block.REGISTRY.getIdentifier(s).toString().equals(id), null, chunkrange);
    }


    /**
     * @since 1.6.4
     * @param ids
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(List<String> ids, int chunkrange) {
        assert mc.player != null;
        int playerChunkX = (int) mc.player.x >> 4;
        int playerChunkZ = (int) mc.player.z >> 4;
        Set<String> ids2 = new HashSet<>(ids);
        return findBlocksMatchingInternal(playerChunkX, playerChunkZ, s -> ids2.contains(Block.REGISTRY.getIdentifier(s).toString()), null, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param centerX
     * @param centerZ
     * @param ids
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int centerX, int centerZ, List<String> ids, int chunkrange) {
        Set<String> ids2 = new HashSet<>(ids);
        return findBlocksMatchingInternal(centerX, centerZ, s -> ids2.contains(Block.REGISTRY.getIdentifier(s).toString()), null, chunkrange);
    }



    /**
     * @since 1.6.4
     * @param idFilter
     * @param nbtFilter
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(MethodWrapper<String, Object, Boolean, ?> idFilter, MethodWrapper<Map<String, String>, Object, Boolean, ?> nbtFilter, int chunkrange) {
        if (idFilter == null) throw new IllegalArgumentException("idFilter cannot be null");
        assert mc.player != null;
        int playerChunkX = (int) mc.player.x >> 4;
        int playerChunkZ = (int) mc.player.z >> 4;
        return findBlocksMatching(playerChunkX, playerChunkZ, idFilter, nbtFilter, chunkrange);
    }

    /**
     * @since 1.6.4
     * @param chunkX
     * @param chunkZ
     * @param idFilter
     * @param nbtFilter
     * @param chunkrange
     *
     * @return
     */
    public List<PositionCommon.Pos3D> findBlocksMatching(int chunkX, int chunkZ, MethodWrapper<String, Object, Boolean, ?> idFilter, MethodWrapper<Map<String, String>, Object, Boolean, ?> nbtFilter, int chunkrange) {
        if (idFilter == null) throw new IllegalArgumentException("idFilter cannot be null");
        return findBlocksMatchingInternal(chunkX, chunkZ, s -> idFilter.apply(Block.REGISTRY.getIdentifier(s).toString()), nbtFilter != null ? e -> nbtFilter.apply(e.getProperties().stream().parallel().collect(Collectors.toMap(IProperty::getName, mp -> e.get(mp).toString()))) : null, chunkrange);
    }

    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(int centerX, int centerZ, Function<Block, Boolean> stateFilter, Function<IBlockState, Boolean> entityFilter, int chunkrange) {
        assert mc.world != null;
        if (chunkrange < 0) throw new IllegalArgumentException("chunkrange must be at least 0");

        List<PositionCommon.Pos2D> chunks = new ArrayList<>();
        for (int x = centerX - chunkrange; x <= centerX + chunkrange; x++) {
            for (int z = centerZ - chunkrange; z <= centerZ + chunkrange; z++) {
                if (mc.world.getChunkProvider().chunkExists(x, z)) {
                    chunks.add(new PositionCommon.Pos2D(x, z));
                }
            }
        }

        return findBlocksMatchingInternal(chunks, stateFilter, entityFilter);

    }

    private List<PositionCommon.Pos3D> findBlocksMatchingInternal(List<PositionCommon.Pos2D> pos, Function<Block, Boolean> stateFilter, Function<IBlockState, Boolean> entityFilter) {
        assert mc.world != null;

        return pos.stream().flatMap(c -> {
            if (!mc.world.getChunkProvider().chunkExists((int) c.x, (int) c.y)) {
                return Stream.empty();
            }
            Chunk chunk = mc.world.getChunk((int) c.x, (int) c.y);
            ExtendedBlockStorage[] sections = chunk.getBlockStorage();
            return IntStream.range(0, sections.length).boxed().flatMap(i -> {
                AtomicBoolean found = new AtomicBoolean(false);
                if (sections[i].func_76663_a()) {
                    return Stream.empty();
                }
                for (char blockState : sections[i].getBlockStates()) {
                    if (stateFilter.apply(Block.BLOCK_STATES.fromId(blockState).getBlock())) {
                        found.set(true);
                    }
                }
                if (!found.get()) {
                    return (Stream<PositionCommon.Pos3D>) (Stream) Stream.empty();
                }
                return IntStream.range(0, 4096).mapToObj(e -> {
                    int y = e >> 8;
                    int x = (e & 255) >> 4;
                    int z = e & 15;
                    IBlockState state = sections[i].getBlockState(x, y, z);
                    if (stateFilter.apply(state.getBlock())) {
                        if (entityFilter != null) {
                            if (entityFilter.apply(state)) {
                                return new PositionCommon.Pos3D(((int)c.x) << 4 | x, y + (i << 4), ((int)c.y) << 4 | z);
                            }
                        } else {
                            return new PositionCommon.Pos3D(((int)c.x) << 4 | x, y + (i << 4), ((int)c.y) << 4 | z);
                        }
                    }
                    return null;
                }).filter(Objects::nonNull);
            });
        }).collect(Collectors.toList());
    }

    /**
     * @since 1.2.9
     * @return a helper for the scoreboards provided to the client.
     */
    public ScoreboardsHelper getScoreboards() {
        assert mc.world != null;
        return new ScoreboardsHelper(mc.world.getScoreboard());
    }
    
    /**
     * @return all entities in the render distance.
     */
    public List<EntityHelper<?>> getEntities() {
        assert mc.world != null;
        List<EntityHelper<?>> entities = new ArrayList<>();
        for (Entity e : ImmutableList.copyOf(mc.world.entities)) {
            entities.add(EntityHelper.create(e));
        }
        return entities;
    }
    
    /**
     * @since 1.1.2
     * @return the current dimension.
     */
    public String getDimension() {
        assert mc.world != null;
        return mc.world.getLevelProperties().getLevelName();
    }
    
    /**
     * @since 1.1.5
     * @return the current biome.
     */
    public String getBiome() {
        assert mc.world != null;
        assert mc.player != null;
        return mc.world.getBiome(mc.player.getBlockPos()).name;
    }
    
    /**
     * @since 1.1.5
     * @return the current world time.
     */
    public long getTime() {
        assert mc.world != null;
        return mc.world.getLastUpdateTime();
    }
    
    /**
     * This is supposed to be time of day, but it appears to be the same as {@link FWorld#getTime()} to me...
     * @since 1.1.5
     * 
     * @return the current world time of day.
     */
    public long getTimeOfDay() {
        assert mc.world != null;
        return mc.world.getTimeOfDay();
    }
    
    /**
     * @since 1.2.6
     * @return respawn position.
     */
    public BlockPosHelper getRespawnPos() {
        assert mc.world != null;
        return new BlockPosHelper( mc.world.getSpawnPos());
    }
    
    /**
     * @since 1.2.6
     * @return world difficulty as an {@link java.lang.Integer Integer}.
     */
    public int getDifficulty() {
        assert mc.world != null;
        return mc.world.getGlobalDifficulty().getId();
    }
    
    /**
     * @since 1.2.6
     * @return moon phase as an {@link java.lang.Integer Integer}.
     */    
    public int getMoonPhase() {
        assert mc.world != null;
        return (int) (mc.world.getMoonPhase() * 4);
    }
    
    /**
     * @since 1.1.2
     * @param x
     * @param y
     * @param z
     * @return sky light as an {@link java.lang.Integer Integer}.
     */
    public int getSkyLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightAtPos(EnumSkyBlock.SKY, new BlockPos(x, y, z));
    }
    
    /**
     * @since 1.1.2
     * @param x
     * @param y
     * @param z
     * @return block light as an {@link java.lang.Integer Integer}.
     */
    public int getBlockLight(int x, int y, int z) {
        assert mc.world != null;
        return mc.world.getLightAtPos(EnumSkyBlock.BLOCK, new BlockPos(x, y, z));
    }
    
    /**
     * plays a sound file using javax's sound stuff.
     * @since 1.1.7
     * 
     * @param file
     * @param volume
     * @return
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public Clip playSoundFile(String file, double volume) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(Core.getInstance().config.macroFolder, file)));
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        double min = gainControl.getMinimum();
        double range = gainControl.getMaximum() - min;
        float gain = (float) ((range * volume) + min);
        gainControl.setValue(gain);
        clip.addLineListener(event -> {
            if(event.getType().equals(LineEvent.Type.STOP)) {
                clip.close();
            }
        });
        clip.start();
        return clip;
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     */
    public void playSound(String id) {
        playSound(id, 1F);
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     * @param volume
     */
    public void playSound(String id, double volume) {
        playSound(id, volume, 0.25F);
    }
    
    /**
     * @since 1.1.7
     * @see FWorld#playSound(String, double, double, double, double, double)
     * @param id
     * @param volume
     * @param pitch
     */
    public void playSound(String id, double volume, double pitch) {
        ResourceLocation sound = new ResourceLocation(id);
        assert sound != null;
        mc.execute(() -> mc.getSoundManager().play(new PositionedSoundRecord(sound, (float) volume, (float) pitch, 0, 0, 0)));
    }
    
    /**
     * plays a minecraft sound using the internal system.
     * @since 1.1.7
     * @param id
     * @param volume
     * @param pitch
     * @param x
     * @param y
     * @param z
     */
    public void playSound(String id, double volume, double pitch, double x, double y, double z) {
        ResourceLocation sound = new ResourceLocation(id);
        assert sound != null;
        mc.execute(() -> mc.getSoundManager().play(new PositionedSoundRecord(sound, (float) volume, (float) pitch, (float) x, (float) y, (float) z)));
    }
    
    /**
     * @since 1.2.1
     * @return a map of boss bars by the boss bar's UUID.
     */
    public BossBarHelper getBossBars() {
        return new BossBarHelper();
    }
    
    /**
     * Check whether a chunk is within the render distance and loaded.
     * @since 1.2.2
     * @param chunkX
     * @param chunkZ
     * @return
     */
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        if (mc.world == null) return false;
        return mc.world.getChunkProvider().chunkExists(chunkX, chunkZ);
    }
    
    /**
     * @since 1.2.2
     * @return the current server address as a string ({@code server.address/server.ip:port}).
     */
    public String getCurrentServerAddress() {
        NetworkManager h = mc.getNetworkHandler().getClientConnection();
        if (h == null) return null;
        SocketAddress c = h.getAddress();
        if (c == null) return null;
        return c.toString();
    }
    
    /**
     * @since 1.2.2 [Citation Needed]
     * @param x
     * @param z
     * @return biome at specified location, only works if the block/chunk is loaded.
     */
    public String getBiomeAt(int x, int z) {
        assert mc.world != null;
        return mc.world.getBiome(new BlockPos(x, 10, z)).name;
    }
    
    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps with various timings.
     */
    public String getServerTPS() {
        return String.format("%.2f, 1M: %.1f, 5M: %.1f, 15M: %.1f", serverInstantTPS, server1MAverageTPS, server5MAverageTPS, server15MAverageTPS);
    }
    
    /**
     * @since 1.3.1
     * @return text helper for the top part of the tab list (above the players)
     */
    public TextHelper getTabListHeader() {
        IChatComponent header = ((IPlayerListHud)mc.inGameHud.getPlayerListWidget()).jsmacros_getHeader();
        if (header != null) return new TextHelper(header);
        return null;
    }
    
    /**
     * @since 1.3.1
     * @return  text helper for the bottom part of the tab list (below the players)
     */
    public TextHelper getTabListFooter() {
        IChatComponent footer = ((IPlayerListHud)mc.inGameHud.getPlayerListWidget()).jsmacros_getFooter();
        if (footer != null) return new TextHelper(footer);
        return null;
    }
    
    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps.
     */
    public double getServerInstantTPS() {
        return serverInstantTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 1 minute average.
     */
    public double getServer1MAverageTPS() {
        return server1MAverageTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 5 minute average.
     */
    public double getServer5MAverageTPS() {
        return server5MAverageTPS;
    }
    

    /**
     * @since 1.2.7
     * @return best attempt to measure and give the server tps over the previous 15 minute average.
     */
    public double getServer15MAverageTPS() {
        return server15MAverageTPS;
    }
}
