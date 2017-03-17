package net.innectis.innplugin.location;

import net.innectis.innplugin.IdpRuntimeException;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.TileEntity;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * This class takes a snapshot at any given region.
 * The block in that region are copied into memory.
 *
 * It is currently not possible to track Idp objects or IO the snapshot to disk.
 * TODO: This is broken. Fix it up if it is ever to be used again
 */
public class RegionSnapshot extends IdpRegion {

    private static final int TYPE_NONE = 0;
    private static final int TYPE_CHEST = 1;
    private static final int TYPE_DOOR = 2;
    private static final int TYPE_TRAPDOOR = 3;
    private static final int TYPE_WAYPOINT = 4;
    private static final int TYPE_BOOKCASE = 5;
    //
    private String worldname;
    private int[][][] type;
    private int[][][] data;
//    private int[][][] idpType;
//    private int[][][] idpId;
    private TileEntity[][][] tiledata;

    RegionSnapshot(IdpWorldRegion region) {
        super(region);
        this.worldname = region.getWorld().getName();
        snapObjects();
    }

    /**
     * This is not possible with a regionsnapshot.
     * Use :rebase();
     */
    @Override
    public void setPos1(Vector location) {
        throw new IllegalStateException("State location cannot be changed, use RegionState::rebase(location) !");
    }

    /**
     * This is not possible with a regionsnapshot.
     * Use :rebase();
     */
    @Override
    public void setPos2(Vector location) {
        throw new IllegalStateException("State location cannot be changed, use RegionState::rebase(location) !");
    }

    /**
     * This method will take all objects inside the given region.
     */
    private void snapObjects() {
        World world = Bukkit.getWorld(worldname);

        if (world == null) {
            throw new IdpRuntimeException("The world of this snapshot (" + worldname + ") is not loaded!");
        }

        WorldServer server = ((CraftWorld) world).getHandle();

        type = new int[getWidth()][getLength()][getHeight()];
        data = new int[getWidth()][getLength()][getHeight()];

//        idpType = new int[getWidth()][getLength()][getHeight()];
//        idpId = new int[getWidth()][getLength()][getHeight()];

        tiledata = new TileEntity[getWidth()][getLength()][getHeight()];

        int lowX = getLowestX();
        int lowY = getLowestY();
        int lowZ = getLowestZ();

        for (int x = lowX; x <= getHighestX(); x++) {
            for (int z = lowZ; z <= getHighestZ(); z++) {
                for (int y = lowY; y <= getHighestY(); y++) {

                    //type[x - lowX][z - lowZ][y - lowY] = server.getTypeId(x, y, z);
                    //data[x - lowX][z - lowZ][y - lowY] = server.getData(x, y, z);

                    tiledata[x - lowX][z - lowZ][y - lowY] = server.getTileEntity(new BlockPosition(x, y, z));

//                    switch (type[x - lowX][z - lowZ][y - lowY]) {
//                        case 54: //chest
//                        case 146: // Trapped Chest
//                            obj = ChestHandler.getChest(new IdpLocation(idpworld, x, y, z));
//                            if (obj != null) {
//                                idpType[x - lowX][z - lowZ][y - lowY] = getIdpTypeId(obj.getObjectName());
//                                idpId[x - lowX][z - lowZ][y - lowY] = (Integer) obj.getId();
//                            }
//                            break;
//                        case 71: // Iron Door
//                            obj = DoorHandler.getDoor(new IdpLocation(idpworld, x, y, z));
//                            if (obj != null) {
//                                idpType[x - lowX][z - lowZ][y - lowY] = getIdpTypeId(obj.getObjectName());
//                                idpId[x - lowX][z - lowZ][y - lowY] = (Integer) obj.getId();
//                            }
//                            break;
//                        case 96: // Trap Door
//                            obj = TrapdoorHandler.getTrapdoor(new IdpLocation(idpworld, x, y, z));
//                            if (obj != null) {
//                                idpType[x - lowX][z - lowZ][y - lowY] = getIdpTypeId(obj.getObjectName());
//                                idpId[x - lowX][z - lowZ][y - lowY] = (Integer) obj.getId();
//                            }
//                            break;
//                        case 21: // Waypoint
//                            obj = WaypointHandler.getWaypoint(new IdpLocation(idpworld, x, y, z));
//                            if (obj != null) {
//                                idpType[x - lowX][z - lowZ][y - lowY] = getIdpTypeId(obj.getObjectName());
//                                idpId[x - lowX][z - lowZ][y - lowY] = (Integer) obj.getId();
//                            }
//                            break;
//                    }
                }
            }
        }
    }

    /**
     * Converts an IdpTypename to an integer
     * @param name
     * @return
     */
    private int getIdpTypeId(String name) {
        if (name.equals("Chest")) {
            return TYPE_CHEST;
        }
        if (name.equals("Door")) {
            return TYPE_DOOR;
        }
        if (name.equals("Trapdoor")) {
            return TYPE_TRAPDOOR;
        }
        if (name.equals("Waypoint")) {
            return TYPE_WAYPOINT;
        }
        if (name.equals("Bookcase")) {
            return TYPE_BOOKCASE;
        }

        return TYPE_NONE;
    }

    /**
     * This method will restore all blocks to the state when the snapshot was taken.
     */
    public void restore() {
        /*
        World world = Bukkit.getWorld(worldname);

        if (world == null) {
            throw new IdpRuntimeException("The world of this snapshot (" + worldname + ") is not loaded!");
        }

        WorldServer server = ((CraftWorld) world).getHandle();

        int lowX = getLowestX();
        int lowY = getLowestY();
        int lowZ = getLowestZ();

        for (int x = lowX; x <= getHighestX(); x++) {
            for (int z = lowZ; z <= getHighestZ(); z++) {
                for (int y = lowY; y <= getHighestY(); y++) {

                    if (server.getTypeId(x, y, z) != type[x - lowX][z - lowZ][y - lowY]) {
                        //server.setTypeAndData(x, y, z, CraftMagicNumbers.getBlock(type[x - lowX][z - lowZ][y - lowY]), data[x - lowX][z - lowZ][y - lowY]);

                        if (tiledata[x - lowX][z - lowZ][y - lowY] != null) {
                            server.setTileEntity(x, y, z, tiledata[x - lowX][z - lowZ][y - lowY]);

                            //                    switch (type[x - lowX][z - lowZ][y - lowY]) {
                            //                        case TYPE_CHEST:
                            //                        case TYPE_DOOR:
                            //                        case TYPE_TRAPDOOR:
                            //                        case TYPE_WAYPOINT:
                            //                        case TYPE_BOOKCASE:
                            //                    }
                        }                    }
                }
            }
        }
        */
    }

    /**
     * This method will rebase the region for the given location.
     * The old first location will be used as starting point
     * @param location
     */
    public void rebase(Vector location) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * This function will save the contents of the snapshot to disk.
     */
//    public String toData() {
//        try {
//            File file = new File("C:\\file.txt");
//
//            BufferedWriter output = new BufferedWriter(new FileWriter(file, false));
//
//            output.write("loc1\t" + getPos1().getBlockX() + "\t" + getPos1().getBlockY() + "\t" + getPos1().getBlockZ());
//            output.newLine();
//            output.write("loc2\t" + getPos2().getBlockX() + "\t" + getPos2().getBlockY() + "\t" + getPos2().getBlockZ());
//            output.newLine();
//            output.write("world\t" + worldname);
//            output.newLine();
//
//            System.out.println("Blocks....");
//            output.write("blocks");
//            output.newLine();
//            output.flush();
//
//            int lastprocess = 0;
//            double width = getWidth();
//
//            for (int x = 0; x < getWidth(); x++) {
//                int proc = (int) ((double) ((double) x / width) * 100d);
//                if (lastprocess != proc) {
//                    System.out.println("Done: " + proc);
//                    lastprocess = proc;
//                }
//
//                for (int z = 0; z < getLength(); z++) {
//                    for (int y = 0; y < getHeight(); y++) {
//                        if (type[x][z][y] != 0) {
//                            output.write("\t");
//
//                            output.write(String.valueOf(x));
//                            output.write("\t");
//                            output.write(String.valueOf(y));
//                            output.write("\t");
//                            output.write(String.valueOf(z));
//                            output.write("\t");
//                            output.write(String.valueOf(type[x][z][y]));
//                            output.write("\t");
//                            output.write(String.valueOf(data[x][z][y]));
//                            output.write("\t");
//
//                            if (idpType[x][z][y] != 0) {
//                                output.write(String.valueOf(idpId[x][z][y]));
//                                output.write("\t");
//                                output.write(String.valueOf(idpType[x][z][y]));
//                                output.write("\t");
//                            } else {
//                                output.write("X\t");
//                            }
//
//                            //                        element = doc.createElement("tiledata");
//                            //                        element.appendChild(doc.createTextNode(String.valueOf(type[x][z][y])));
//                            //                        region.appendChild(block);
//
//                            output.newLine();
//                            output.flush();
//                        }
//                    }
//                }
//            }
//
//            output.close();
//            System.out.println("That took a while...");
//
//            System.out.println("File saved!");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return "";
//    }

}
