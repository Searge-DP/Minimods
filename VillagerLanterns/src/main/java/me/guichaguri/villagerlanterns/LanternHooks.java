package me.guichaguri.villagerlanterns;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.WireType;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.IESaveData;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalDevices2;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityElectricLantern;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWoodenPost;
import blusunrize.immersiveengineering.common.util.Utils;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public class LanternHooks {
    private static List<Entry<TileEntityElectricLantern, TileEntityElectricLantern>> toProcess = null;
    private static boolean loaded = false;
    public static void addConnectionToProcess(TileEntityElectricLantern l1, TileEntityElectricLantern l2) {
        if(loaded) {
            process(l1, l2);
        } else {
            if(toProcess == null) toProcess = new ArrayList<Entry<TileEntityElectricLantern, TileEntityElectricLantern>>();
            toProcess.add(new SimpleImmutableEntry<TileEntityElectricLantern, TileEntityElectricLantern>(l1, l2));
            System.out.println("ADD LAMP");
        }
    }

    public static void buildLantern(World w, int x, int y, int z) {
        // Build the wooden post
        for(int i = 0; i <= 3; i++) {
            w.setBlock(x, y + i, z, IEContent.blockWoodenDevice, 0, 2);
            if(w.getTileEntity(x, y + i, z) instanceof TileEntityWoodenPost) {
                ((TileEntityWoodenPost)w.getTileEntity(x, y + i, z)).type = (byte)i;
            }
        }

        y += 4;

        // Place the lanterns and search for nearby lanterns
        w.setBlock(x, y, z, IEContent.blockMetalDevice2, BlockMetalDevices2.META_electricLantern, 2);
        if(w.getTileEntity(x, y, z) instanceof TileEntityElectricLantern) {
            TileEntityElectricLantern lantern1 = (TileEntityElectricLantern)w.getTileEntity(x, y, z);
            lantern1.energyStorage = 360000;

            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            for(int cX = -1; cX <= 1; cX++) {
                for(int cZ = -1; cZ <= 1; cZ++) {
                    Iterator it = w.getChunkFromChunkCoords(chunkX + cX, chunkZ + cZ).chunkTileEntityMap.values().iterator();
                    while(it.hasNext()) {
                        TileEntity te = (TileEntity)it.next();
                        if(te instanceof TileEntityElectricLantern) {
                            addConnectionToProcess(lantern1, (TileEntityElectricLantern)te);
                        }
                    }
                }
            }
        }
    }

    private static void process(TileEntityElectricLantern lantern1, TileEntityElectricLantern lantern2) {
        System.out.println("PROCESS");
        World w = lantern1.getWorldObj();
        int x1 = lantern1.xCoord;
        int y1 = lantern1.yCoord;
        int z1 = lantern1.zCoord;
        int x2 = lantern2.xCoord;
        int y2 = lantern2.yCoord;
        int z2 = lantern2.zCoord;
        Vec3 rtOff0 = lantern1.getRaytraceOffset(lantern2).addVector(x1, y1, z1);
        Vec3 rtOff1 = lantern2.getRaytraceOffset(lantern1).addVector(x2, y2, z2);
        boolean canSee = Utils.canBlocksSeeOther(w, new ChunkCoordinates(x1, y1, z1), new ChunkCoordinates(x2, y2, z2), rtOff0, rtOff1);
        int distance = (int) Math.ceil(Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1) ));

        if(canSee && distance <= WireType.COPPER.getMaxLength()) {
            ImmersiveNetHandler.INSTANCE.addConnection(w, Utils.toCC(lantern1), Utils.toCC(lantern2), distance, WireType.COPPER);
            TargetingInfo target = new TargetingInfo(0, 0, 0, 0);
            lantern1.connectCable(WireType.COPPER, target);
            lantern2.connectCable(WireType.COPPER, target);
            IESaveData.setDirty(w.provider.dimensionId);

            lantern1.markDirty();
            w.markBlockForUpdate(x1, y1, z1);
            lantern2.markDirty();
            w.markBlockForUpdate(x2, y2, z2);
        }
    }

    protected static void serverStart() {
        loaded = true;
        if(toProcess != null) {
            for(Entry<TileEntityElectricLantern, TileEntityElectricLantern> e : toProcess) {
                process(e.getKey(), e.getValue());
            }
            toProcess = null;
        }
    }

    protected static void serverStop() {
        toProcess = null;
        loaded = false;
    }
}
