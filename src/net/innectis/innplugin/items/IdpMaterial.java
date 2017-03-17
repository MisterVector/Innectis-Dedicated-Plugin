package net.innectis.innplugin.items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.annotation.Nullable;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionType;

/**
 * Material types.
 * @todo: This system needs improving
 */
@Nullable
public enum IdpMaterial {

    // #FORMAT_START
    // Blocks
// #FORMAT_START
    // Blocks
    AIR                     (0   , 0 , "Air"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STONE                   (1   , 0 , "Stone"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST , "rock"),
    GRANITE                 (1   , 1 , "Granite"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POLISHED_GRANITE        (1   , 2 , "PolishedGranite"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIORITE                 (1   , 3 , "Diorite"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POLISHED_DIORITE        (1   , 4 , "PolishedDiorite"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ANDESITE                (1   , 5 , "Andesite"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POLISHED_ANDESITE       (1   , 6 , "PolishedAndesite"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GRASS                   (2   , 0 , "Grass"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIRT                    (3   , 0 , "Dirt"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COARSE_DIRT             (3   , 1 , "CoarseDirt"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PODZOL                  (3   , 2 , "podzol"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COBBLESTONE             (4   , 0 , "Cobblestone"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cobble"),
    OAK_PLANK               (5   , 0 , "Plank"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodplank", "plank", "woodplanks", "planks", "oakplank", "wood"),
    SPRUCE_PLANK            (5   , 1 , "SprucePlank"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "sprucewood", "spruceplanks"),
    BIRCH_PLANK             (5   , 2 , "BirchPlank"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "birchwood", "birchplanks"),
    JUNGLE_PLANK            (5   , 3 , "JunglePlank"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "junglewood", "jungleplanks"),
    ACACIA_PLANK            (5   , 4 , "AcaciaPlank"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "acaciawood", "acaciaplanks"),
    DARK_OAK_PLANK          (5   , 5 , "DarkOakPlank"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "darkoakwood", "darkoakplanks"),
    OAK_SAPLING             (6   , 0 , "OakSapling"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "seedling"),
    SPRUCE_SAPLING          (6   , 1 , "SpuceSapling"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ssapling", "sseedling"),
    BIRCH_SAPLING           (6   , 2 , "BirchSapling"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bsapling", "bseedling"),
    JUNGLE_SAPLING          (6   , 3 , "JungleSapling"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "jsapling", "jseedling"),
    ACACIA_SAPLING          (6   , 4 , "AcaciaSapling"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "asapling", "aseedling"),
    DARK_OAK_SAPLING        (6   , 5 , "DarkOakSapling"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "dosapling", "doseedling"),
    BEDROCK                 (7   , 0 , "Bedrock"               , PlayerGroup.ADMIN    , PlayerGroup.MODERATOR, "adminium"),
    WATER                   (8   , 0 , "Water(flowing)"        , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "watermoving", "movingwater", "flowingwater", "waterflowing"),
    STATIONARY_WATER        (9   , 0 , "Water"                 , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "waterstationary", "stationarywater", "stillwater"),
    LAVA                    (10  , 0 , "Lava(flowing)"         , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "lavamoving", "movinglava", "flowinglava", "lavaflowing"),
    STATIONARY_LAVA         (11  , 0 , "Lava"                  , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "lavastationary", "stationarylava", "stilllava"),
    SAND                    (12  , 0 , "Sand"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_SAND                (12  , 1 , "RedSand"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GRAVEL                  (13  , 0 , "Gravel"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLD_ORE                (14  , 0 , "GoldOre"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_ORE                (15  , 0 , "IronOre"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COAL_ORE                (16  , 0 , "CoalOre"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    OAK_LOG                 (17  , 0 , "Log"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST, "tree", "log", "oak", "oaklog"),
    SPRUCE_LOG              (17  , 1 , "SpruceLog"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "slog", "spruce", "redwood"),
    BIRCH_LOG               (17  , 2 , "BirchLog"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blog", "birch"),
    JUNGLE_LOG              (17  , 3 , "JungleLog"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "jlog", "jungle"),
    SIDE_OAK_LOG_EAST       (17  , 4 , "EastOakLog"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "etree", "eoak"),
    SIDE_SPRUCE_LOG_EAST    (17  , 5 , "EastSpruceLog"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "slog", "espruce", "eredwood"),
    SIDE_BIRCH_LOG_EAST     (17  , 6 , "EastBirchLog"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "eblog", "ebirch"),
    SIDE_JUNGLE_LOG_EAST    (17  , 7 , "EastJungleLog"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ejlog", "ejungle"),
    SIDE_OAK_LOG_NORTH      (17  , 8 , "NorthOakLog"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ntree", "nlog", "noak"),
    SIDE_SPRUCE_LOG_NORTH   (17  , 9 , "NorthSpruceLog"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "nlog", "nspruce", "nredwood"),
    SIDE_BIRCH_LOG_NORTH    (17  , 10, "NorthBirchLog"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "nblog", "nbirch"),
    SIDE_JUNGLE_LOG_NORTH   (17  , 11, "NorthJungleLog"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "njlog", "njungle"),
    BARK_OAK_LOG            (17  , 12, "BarkOakLog"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "btree", "bplog", "boak"),
    BARK_SPRUCE_LOG         (17  , 13, "BarkSpruceLog"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blog", "bspruce", "bredwood"),
    BARK_BIRCH_LOG          (17  , 14, "BarkBirchLog"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bblog", "bbirch"),
    BARK_JUNGLE_LOG         (17  , 15, "BarkJungleLog"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bjlog", "bjungle"),
    OAK_LEAVES              (18  , 0 , "Leaves"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leaf"),
    SPRUCE_LEAVES           (18  , 1 , "SpruceLeaves"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "sleaves", "sleaf"),
    BIRCH_LEAVES            (18  , 2 , "BirchLeaves"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bleaves", "bleaf"),
    JUNGLE_LEAVES           (18  , 3 , "JungleLeaves"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "jleaves", "jleaf"),
    SPONGE                  (19  , 0 , "Sponge"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WET_SPONGE              (19  , 1 , "WetSponge"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GLASS                   (20  , 0 , "Glass"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LAPIS_LAZULI_OREBLOCK   (21  , 0 , "LapisLazuliOre"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blueore", "lapisore"),
    LAPIS_LAZULI_BLOCK      (22  , 0 , "LapisBlock"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "lapis", "lazuli", "lapislazuliblock", "lapisblock", "bluerock"),
    DISPENSER               (23  , 0 , "Dispenser"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SANDSTONE               (24  , 0 , "Sandstone"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PRETTY_SANDSTONE        (24  , 1 , "PrettySandstone"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "prettysandstone", "decorativesandstone"),
    SMOOTH_SANDSTONE        (24  , 2 , "SmoothSandstone"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "smoothsandstone", "smoothsand"),
    NOTE_BLOCK              (25  , 0 , "NoteBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "musicblock", "note", "music", "instrument"),
    BED_BLOCK               (26  , 0 , "Bed", false            , PlayerGroup.MODERATOR, PlayerGroup.GUEST),
    POWERED_RAIL            (27  , 0 , "PoweredRail"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "boosterrail", "poweredtrack", "boostertrack", "booster"),
    DETECTOR_RAIL           (28  , 0 , "DetectorRail"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "detector"),
    STICKY_PISTON           (29  , 0 , "StickyPiston"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WEB                     (30  , 0 , "Web"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST, "spiderweb"),
    TALL_GRASS              (31  , 0 , "LongGrass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "tallgrass"),
    SHRUBS                  (32  , 0 , "Shrub"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "deadbush", "deadshrub", "tumbleweed"),
    PISTON                  (33  , 0 , "Piston"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PISTON_EXTENSION        (34  , 0 , "PistonExtension", false, PlayerGroup.ADMIN    , PlayerGroup.GUEST, "pistonhead"),
    WOOL_WHITE              (35  , 0 , "Wool"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cloth"),
    WOOL_ORANGE             (35  , 1 , "OrangeWool"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_MAGENTA            (35  , 2 , "MagentaWool"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_LIGHTBLUE          (35  , 3 , "LightBlueWool"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_YELLOW             (35  , 4 , "YellowWool"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_LIGHTGREEN         (35  , 5 , "LightGreenWool"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "greenwool"),
    WOOL_PINK               (35  , 6 , "PinkWool"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_GRAY               (35  , 7 , "GrayWool"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_LIGHTGRAY          (35  , 8 , "LightGrayWool"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_CYAN               (35  , 9 , "CyanWool"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_PURPLE             (35  , 10, "PurpleWool"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_BLUE               (35  , 11, "BlueWool"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "darkbluewool"),
    WOOL_BROWN              (35  , 12, "BrownWool"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_DARKGREEN          (35  , 13, "DarkGreenWool"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOL_RED                (35  , 14, "RedWool"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redwool"),
    WOOL_BLACK              (35  , 15, "BlackWool"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PISTON_MOVING_PIECE     (36  , 0 , "PistonMovingPiece",false,PlayerGroup.ADMIN    , PlayerGroup.GUEST, "movingpiston"),
    DANDELION               (37  , 0 , "Dandelion"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "flower"),
    POPPY                   (38  , 0 , "Poppy"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redflower", "rose"),
    BLUE_ORCHID             (38  , 1 , "BlueOrchid"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ALLIUM                  (38  , 2 , "Allium"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    AZURE_BLUET             (38  , 3 , "AzureBluet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_TULIP               (38  , 4 , "RedTulip"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ORANGE_TULIP            (38  , 5 , "OrangeTulip"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WHITE_TULIP             (38  , 6 , "WhiteTulip"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PINK_TULIP              (38  , 7 , "PinkTulip"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    OXEYE_DAISY             (38  , 8 , "OxeyeDaisy"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BROWN_MUSHROOM          (39  , 0 , "BrownMushroom"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "mushroom"),
    RED_MUSHROOM            (40  , 0 , "RedMushroom"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLD_BLOCK              (41  , 0 , "GoldBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "gold"),
    IRON_BLOCK              (42  , 0 , "IronBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "iron"),
    DOUBLE_STONE_SLAB       (43  , 0 , "DblStoneSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "doubleslab", "doublestoneslab"),
    DOUBLE_SANDSTONE_SLAB   (43  , 1 , "SandDoubleSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_WOOD_SLAB        (43  , 2 , "WoodDoubleSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_COBBLE_SLAB      (43  , 3 , "CobbleDblSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_BRICK_SLAB       (43  , 4 , "BrickDblSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_STONE_BRICK_SLAB (43  , 5 , "SBDoubleSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_NETHER_BRICK_SLAB(43  , 6 , "NBDoubleSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_QUARTZ_SLAB      (43  , 7 , "QuartzDblSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_SMOOTH_STONE_SLAB(43  , 8 , "SmthStoneDblSlabTwo"   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_SMOOTH_SAND_SLAB (43  , 9 , "SmthSandDblSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STONE_SLAB              (44  , 0 , "Slab"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stoneslab", "step", "halfstep"),
    SANDSTONE_SLAB          (44  , 1 , "SandstoneSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOD_SLAB               (44  , 2 , "WoodSlab"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COBBLE_SLAB             (44  , 3 , "CobbleSlab"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BRICK_SLAB              (44  , 4 , "BrickSlab"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STONE_BRICK_SLAB        (44  , 5 , "StoneBrickSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_BRICK_SLAB       (44  , 6 , "NetherBrickSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    QUARTZ_SLAB             (44  , 7 , "QuartzSlab"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_STONE_SLAB           (44  , 8 , "UpStoneSlab"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_SANDSTONE_SLAB       (44  , 9 , "UpSandstoneSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_WOOD_SLAB            (44  , 10, "UpWoodSlab"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_COBBLE_SLAB          (44  , 11, "UpCobbleSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_BRICK_SLAB           (44  , 12, "UpBrickSlab"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_STONE_BRICK_SLAB     (44  , 13, "UpSBrickSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_NETHER_BRICK_SLAB    (44  , 14, "UpNBSlab"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_QUARTZ_SLAB          (44  , 15, "UpQuartzSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BRICK                   (45  , 0 , "Brick"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "brickblock"),
    TNT                     (46  , 0 , "TNT"                   , PlayerGroup.USER     , PlayerGroup.GUEST, "c4", "explosive"),
    BOOKCASE                (47  , 0 , "Bookcase"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bookshelf", "bookshelves", "bookcases"),
    MOSSY_COBBLESTONE       (48  , 0 , "MossyCobble"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "mossstone", "mossystone", "mosscobblestone", "mossycobble", "moss", "mossy", "sossymobblecone"),
    OBSIDIAN                (49  , 0 , "Obsidian"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "obby", "oby"),
    TORCH                   (50  , 0 , "Torch"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "torches", "light", "candle"),
    FIRE                    (51  , 0 , "Fire"                  , PlayerGroup.ADMIN    , PlayerGroup.GUEST, "flame", "flames"),
    MOB_SPAWNER             (52  , 0 , "MobSpawner"            , PlayerGroup.ADMIN    , PlayerGroup.GUEST, "spawner"),
    WOODEN_STAIRS           (53  , 0 , "WoodenStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodstair", "woodstairs", "woodenstair"),
    CHEST                   (54  , 0 , "Chest"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "storage", "storagechest"),
    REDSTONE_WIRE           (55  , 0 , "RedstoneWire", false   , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "redstone", "redstoneblock"),
    DIAMOND_ORE             (56  , 0 , "DiamondOre"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND_BLOCK           (57  , 0 , "DiamondBlock"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamond"),
    WORKBENCH               (58  , 0 , "Workbench"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "table", "craftingtable", "crafting"),
    WHEAT_BLOCK             (59  , 0 , "Crops",false           , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "crop", "plant", "plants"),
    FARMLAND                (60  , 0 , "Farmland"              , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "soil"),
    FURNACE                 (61  , 0 , "Furnace"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BURNING_FURNACE         (62  , 0 , "Furnace(burning)",false, PlayerGroup.MODERATOR, PlayerGroup.GUEST, "burningfurnace", "litfurnace"),
    SIGN_POST               (63  , 0 , "SignPost",false        , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "sign"),
    OAK_DOOR_BLOCK          (64  , 0 , "WoodenDoor",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "wooddoor", "door"),
    LADDER                  (65  , 0 , "Ladder"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAILS                   (66  , 0 , "MinecartTracks"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "track", "tracks", "minecrattrack", "rails", "rail"),
    COBBLESTONE_STAIRS      (67  , 0 , "CobblestoneStairs"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cobblestonestair", "cobblestair", "cobblestairs"),
    WALL_SIGN               (68  , 0 , "WallSign",false        , PlayerGroup.MODERATOR, PlayerGroup.GUEST),
    LEVER                   (69  , 0 , "Lever"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "switch", "stonelever", "stoneswitch"),
    STONE_PRESSURE_PLATE    (70  , 0 , "StonePressurePlate"    , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stoneplate"),
    IRON_DOOR_BLOCK         (71  , 0 , "IronDoor",false        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOODEN_PRESSURE_PLATE   (72  , 0 , "WoodenPressurePlate"   , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodpressureplate", "woodplate", "woodenplate", "plate", "pressureplate"),
    REDSTONE_ORE            (73  , 0 , "RedstoneOre"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GLOWING_REDSTONE_ORE    (74  , 0 , "GlowingRedstoneOre",false,PlayerGroup.GUEST   , PlayerGroup.GUEST),
    REDSTONE_TORCH_OFF      (75  , 0 , "RedstoneTorchOff",false, PlayerGroup.GUEST    , PlayerGroup.GUEST, "redstonetorchoff", "rstorchoff"),
    REDSTONE_TORCH_ON       (76  , 0 , "RedstoneTorch"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redstonetorch", "redstonetorchon", "rstorchon", "redtorch"),
    STONE_BUTTON            (77  , 0 , "StoneButton"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "button"),
    SNOW_LAYER              (78  , 0 , "Snow"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ICE                     (79  , 0 , "Ice"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SNOW_BLOCK              (80  , 0 , "SnowBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CACTUS                  (81  , 0 , "Cactus"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cacti"),
    CLAY                    (82  , 0 , "Clay"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SUGAR_CANE              (83  , 0 , "Reed",false            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cane", "sugarcane", "sugarcanes"),
    JUKEBOX                 (84  , 0 , "Jukebox"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "recordplayer"),
    OAK_FENCE               (85  , 0 , "OakFence"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PUMPKIN                 (86  , 0 , "Pumpkin"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHERRACK              (87  , 0 , "Netherrack"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redmossycobblestone", "redcobblestone", "redmosstone", "redcobble", "netherstone", "nether", "hellstone"),
    SOUL_SAND               (88  , 0 , "SoulSand"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "slowmud", "mud", "hellmud"),
    GLOWSTONE               (89  , 0 , "Glowstone"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "brittlegold", "lightstone", "brimstone", "australium"),
    PORTAL                  (90  , 0 , "Portal", false         , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    JACK_O_LANTERN          (91  , 0 , "Jack-O-Lantern"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "jackolantern", "pumpkinlighted", "pumpkinon", "litpumpkin"),
    CAKE                    (92  , 0 , "Cake",false            , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "cakeblock"),
    REDSTONE_REPEATER_OFF   (93  , 0 , "RedstoneRepeater(off)",false,PlayerGroup.MODERATOR,PlayerGroup.GUEST, "diodeoff", "redstonerepeater", "repeater", "delayer"),
    REDSTONE_REPEATER_ON    (94  , 0 , "RedstoneRepeater(on)",false,PlayerGroup.MODERATOR,PlayerGroup.GUEST, "diode", "diodeon", "redstonerepeateron", "repeateron", "delayeron"),
    GLASS_STAINED_WHITE     (95  , 0 , "WhiteGlass"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedwhiteglass"),
    GLASS_STAINED_ORANGE    (95  , 1 , "OrgangeGlass"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedorangeglass"),
    GLASS_STAINED_MAGENTA   (95  , 2 , "MagentaGlass"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedmagentaglass"),
    GLASS_STAINED_LIGHT_BLUE(95  , 3 , "LightBlueGlass"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedlightblueglass"),
    GLASS_STAINED_YELLOW    (95  , 4 , "YellowGlass"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedyellowglass"),
    GLASS_STAINED_LIME      (95  , 5 , "LimeGlass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedlimeglass"),
    GLASS_STAINED_PINK      (95  , 6 , "PinkGlass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedpinkglass"),
    GLASS_STAINED_GRAY      (95  , 7 , "GrayGlass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedgrayglass"),
    GLASS_STAINED_LIGHT_GRAY(95  , 8 , "LightGrayGlass"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedlightgrayglass"),
    GLASS_STAINED_CYAN      (95  , 9 , "CyanGlass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedcyanglass"),
    GLASS_STAINED_PURPLE    (95  , 10, "PurpleGlass"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedpurpleglass"),
    GLASS_STAINED_BLUE      (95  , 11, "BlueGlass"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedblueglass"),
    GLASS_STAINED_BROWN     (95  , 12, "BrownGlass"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedbrownglass"),
    GLASS_STAINED_GREEN     (95  , 13, "GreenGlass"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedgreenglass"),
    GLASS_STAINED_RED       (95  , 14, "RedGlass"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedredglass"),
    GLASS_STAINED_BLACK     (95  , 15, "BlackGlass"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedblackglass"),
    TRAP_DOOR               (96  , 0 , "TrapDoor"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "hatch", "floordoor"),
    MONSTER_BLOCK_STONE     (97  , 0 , "SilverfishBlockStone"  , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    MONSTER_BLOCK_COBBLE    (97  , 1 , "SilverfishBlockCobble" , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    MONSTER_BLOCK_BRICK     (97  , 2 , "SilverfishBlockBrick"  , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    MONSTER_BLOCK_MOSSYBRICK(97  , 3 , "SilverfishBlockMossyBrick", PlayerGroup.ADMIN , PlayerGroup.GUEST),
    MONSTER_BLOCK_CRACKSTONE(97  , 4 , "SilverfishBlockCrackStone", PlayerGroup.ADMIN , PlayerGroup.GUEST),
    MONSTER_BLOCK_CHISELBRICK(97 , 5 , "SilverfishBlockChiselBrick",PlayerGroup.ADMIN , PlayerGroup.GUEST),
    STONE_BRICKS            (98  , 0 , "StoneBricks"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "dungeonblock", "stonebrick"),
    MOSSY_STONE_BRICKS      (98  , 1 , "MossyStoneBricks"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "mossybrick", "mossybricks", "mossystonebrick"),
    CRACKED_STONE_BRICKS    (98  , 2 , "CrackedStoneBricks"    , PlayerGroup.GUEST    , PlayerGroup.GUEST, "crackedbrick", "crackedbricks", "crackedstonebrick", "cobblebrick", "cobblebricks"),
    CIRCLE_STONE_BRICKS     (98  , 3 , "CircleStoneBricks"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "circlestonebrick", "circlebricks", "circlestone", "stonecirclebrick"),
    BROWN_MUSHROOM_BLOCK    (99  , 0 , "BrownMushroomBlock"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_MUSHROOM_BLOCK      (100 , 0 , "RedMushroomBlock"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_BARS               (101 , 0 , "IronBars"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "jail", "jailbar", "jailbars", "prison", "prisonbar", "prisonbars"),
    GLASS_PANE              (102 , 0 , "GlassPane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "paneglass"),
    MELON_BLOCK             (103 , 0 , "MelonBlock"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PUMPKIN_STEM            (104 , 0 , "PumpkinStem",false     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MELON_STEM              (105 , 0 , "MelonStem", false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    VINES                   (106 , 0 , "Vines"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "grassvine", "vine"),
    OAK_FENCE_GATE          (107 , 0 , "OakFenceGate"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "gate", "gatefence"),
    BRICK_STAIRS            (108 , 0 , "BrickStairs"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "brickstair"),
    STONE_BRICK_STAIRS      (109 , 0 , "StonebrickStairs"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stonebrickstair", "dungeonstairs"),
    MYCELIUM                (110 , 0 , "Mycelium"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LILY_PAD                (111 , 0 , "Lilypad"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "lily", "lilly"),
    NETHER_BRICK            (112 , 0 , "Netherbrick"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_BRICK_FENCE      (113 , 0 , "NetherBrickFence"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "netherfence"),
    NETHER_BRICK_STAIRS     (114 , 0 , "NetherBrickStairs"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "netherbrickstair", "metherstairs"),
    NETHER_WART             (115 , 0 , "NetherWart", false     , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "netherwarts"),
    ENCHANTMENT_TABLE       (116 , 0 , "EnchantmentTable"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "etable", "ennchant", "enchantment"),
    BREWING_STAND           (117 , 0 , "BrewingStand", false   , PlayerGroup.MODERATOR, PlayerGroup.GUEST, "brew", "brewing"),
    CAULDRON_BLOCK          (118 , 0 , "Cauldron", false       , PlayerGroup.MODERATOR, PlayerGroup.GUEST),
    END_PORTAL              (119 , 0 , "EndPortal", false      , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    END_PORTAL_FRAME        (120 , 0 , "EndPortalFrame"        , PlayerGroup.ADMIN    , PlayerGroup.GUEST, "endframe"),
    END_STONE               (121 , 0 , "EndStone"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "whitestone"),
    DRAGON_EGG              (122 , 0 , "DragonEgg"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_LAMP_OFF       (123 , 0 , "RedstoneLamp(off)"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redstonelamp", "lamp"),
    REDSTONE_LAMP_ON        (124 , 0 , "RedstoneLamp(on)",false, PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_OAK_WOOD_SLAB    (125 , 0 , "DoubleOakSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_SPRUCE_WOOD_SLAB (125 , 1 , "DubSpruceSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_BIRCH_WOOD_SLAB  (125 , 2 , "DoubleBirchSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_JUNGLE_WOOD_SLAB (125 , 3 , "DubJungleSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_ACACIA_WOOD_SLAB (125 , 4 , "DubAcaciaSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_DARKOAK_WOOD_SLAB(125 , 5 , "DubDarkOakSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    OAK_WOOD_SLAB           (126 , 0 , "OakWoodSlab"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_WOOD_SLAB        (126 , 1 , "SpruceWoodSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_WOOD_SLAB         (126 , 2 , "BirchWoodSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_WOOD_SLAB        (126 , 3 , "JungleWoodSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_WOOD_SLAB        (126 , 4 , "AcaciaWoodSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_WOOD_SLAB      (126 , 5 , "DarkOakWoodSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_OAK_WOOD_SLAB        (126 , 8 , "UpOakSlab"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_SPRUCE_WOOD_SLAB     (126 , 9 , "UpSpruceSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_BIRCH_WOOD_SLAB      (126 , 10, "UpBirchSlab"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_JUNGLE_WOOD_SLAB     (126 , 11, "UpJungleSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_ACACIA_WOOD_SLAB     (126 , 12, "UpAcaciaSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_DARK_OAK_WOOD_SLAB   (126 , 13, "UpDarkOakSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COCOA_PLANT             (127 , 0 , "CocoaPlant",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SANDSTONE_STAIRS        (128 , 0 , "SandstoneStairs"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "sandstonestair", "sandstairs", "sandstair"),
    EMERALD_ORE             (129 , 0 , "EmeraldOre"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ENDER_CHEST             (130 , 0 , "EnderChest"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    TRIPWIRE_HOOK           (131 , 0 , "TripwireHook"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    TRIPWIRE                (132 , 0 , "Tripwire",false        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    EMERALD_BLOCK           (133 , 0 , "EmeraldBlock"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_STAIRS           (134 , 0 , "SpruceStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "sprucestair"),
    BIRCH_STAIRS            (135 , 0 , "BirchStairs"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "birchstair"),
    JUNGLE_STAIRS           (136 , 0 , "JungleStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "junglestair"),
    COMMAND_BLOCK           (137 , 0 , "CommandBlock"          , PlayerGroup.ADMIN    , PlayerGroup.ADMIN, "commandblock"),
    BEACON                  (138 , 0 , "Beacon"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COBBLESTONE_WALL        (139 , 0 , "CobblestoneWall"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cobblewall", "cobblestonewall"),
    COBBLESTONE_MOSSY_WALL  (139 , 1 , "MossyWall"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "mossycobblestonewall", "mossycobblewall"),
    FLOWERPOT_BLOCK         (140 , 0 , "FlowerPotBlock",false  , PlayerGroup.GUEST    , PlayerGroup.GUEST, "flowrpot", "flowerpotblock"),
    CARROT_BLOCK            (141 , 0 , "CarrotPlantBlock",false, PlayerGroup.GUEST    , PlayerGroup.GUEST, "carrotblock", "carrotplant"),
    POTATO_BLOCK            (142 , 0 , "PotatoPlantBlock",false, PlayerGroup.GUEST    , PlayerGroup.GUEST, "potatoblock", "potatoplant"),
    WOOD_BUTTON             (143 , 0 , "WoodenButton"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodbutton", "woodenbutton"),
    SKULL_BLOCK             (144 , 0 , "SkullBlock",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ANVIL                   (145 , 0 , "Anvil"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SLIGHTLY_DAMAGED_ANVIL  (145 , 1 , "SlightlyDamagedAnvil"  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    VERY_DAMAGED_ANVIL      (145 , 2 , "VeryDamagedAnvil"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    TRAPPED_CHEST           (146 , 0 , "TrappedChest"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LIGHT_PRESSURE_PLATE    (147 , 0 , "LightPressurePlate"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    HEAVY_PRESSURE_PLATE    (148 , 0 , "HeavyPressurePlate"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_COMPARATOR_OFF (149 , 0 , "RedstoneCompatorBlock",false, PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_COMPARATOR_ON  (150 , 0 , "RedstoneCompatorBlock",false , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DAYLIGHT_DETECTOR       (151 , 0 , "DaylightDetector"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_BLOCK          (152 , 0 , "RedstoneBlock"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_QUARTZ_ORE       (153 , 0 , "NetherQuartzOre"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    HOPPER                  (154 , 0 , "Hopper"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    QUARTZ_BLOCK            (155 , 0 , "QuartzBlock"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    QUARTZ_BLOCK_CHISELLED  (155 , 1 , "ChiselledQuartzBlock"  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    QUARTZ_BLOCK_PILLAR     (155 , 2 , "PillaredQuartzBlock"   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    QUARTZ_STAIRS           (156 , 0 , "QuartzStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "quartzstair"),
    ACTIVATOR_RAIL          (157 , 0 , "ActivatorRail"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DROPPER                 (158 , 0 , "Dropper"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_WHITE              (159  , 0 , "WhiteClay"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stainedClay", "colorClay", "colouredClay"),
    CLAY_ORANGE             (159  , 1 , "OrangeClay"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_MAGENTA            (159  , 2 , "MagentaClay"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_LIGHTBLUE          (159  , 3 , "LightBlueClay"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_YELLOW             (159  , 4 , "YellowClay"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_LIGHTGREEN         (159  , 5 , "LigthGreenClay"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "greenclay"),
    CLAY_PINK               (159  , 6 , "PinkClay"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_GRAY               (159  , 7 , "GrayClay"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_LIGHTGRAY          (159  , 8 , "LightGrayClay"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_CYAN               (159  , 9 , "CyanClay"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_PURPLE             (159  , 10, "PurpleClay"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_BLUE               (159  , 11, "BlueClay"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "darkblueclay"),
    CLAY_BROWN              (159  , 12, "BrownClay"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_DARKGREEN          (159  , 13, "DarkGreenClay"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_RED                (159  , 14, "RedClay"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_BLACK              (159  , 15, "BlackClay"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GLASS_PANE_WHITE        (160  , 0 , "WhitePane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "whiteglasspane"),
    GLASS_PANE_ORANGE       (160  , 1 , "OrgangePane"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "orangeglasspane"),
    GLASS_PANE_MAGENTA      (160  , 2 , "MagentaPane"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "magentaglasspane"),
    GLASS_PANE_LIGHT_BLUE   (160  , 3 , "LightBluePane"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "lightblueglasspane"),
    GLASS_PANE_YELLOW       (160  , 4 , "YellowPane"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "yellowglasspane"),
    GLASS_PANE_LIME         (160  , 5 , "LimePane"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "limeglasspane"),
    GLASS_PANE_PINK         (160  , 6 , "PinkPane"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "pinkglasspane"),
    GLASS_PANE_GRAY         (160  , 7 , "GrayPane"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "grayglasspane"),
    GLASS_PANE_LIGHT_GRAY   (160  , 8 , "LightGrayPane"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "lightgrayglasspane"),
    GLASS_PANE_CYAN         (160  , 9 , "CyanPane"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cyanglasspane"),
    GLASS_PANE_PURPLE       (160  , 10, "PurplePane"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "purpleglasspane"),
    GLASS_PANE_BLUE         (160  , 11, "BluePane"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blueglasspane"),
    GLASS_PANE_BROWN        (160  , 12, "BrownPane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "brownglasspane"),
    GLASS_PANE_GREEN        (160  , 13, "GreenPane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "greenglasspane"),
    GLASS_PANE_RED          (160  , 14, "RedPane"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "redglasspane"),
    GLASS_PANE_BLACK        (160  , 15, "BlackPane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blackglasspane"),
    ACACIA_LEAVES           (161  , 0 , "AcaciaLeaves"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "aleaves", "aleaf"),
    DARK_OAK_LEAVES         (161  , 1 , "DarkOakLeaves"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "doleaves", "doleaf"),
    ACACIA_LOG              (162  , 0 , "AcaciaLog"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_LOG            (162  , 1 , "DarkOakLog"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SIDE_ACACIA_LOG_EAST    (162  , 4 , "AcaciaLogEast"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SIDE_DARK_OAK_LOG_EAST  (162  , 5 , "DarkOakLogEast"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SIDE_ACACIA_LOG_NORTH   (162  , 8 , "AcaciaLogNorth"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SIDE_DARK_OAK_LOG_NORTH (162  , 9 , "DarkOakLogNorth"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BARK_ACACIA_LOG         (162  , 12, "AcaciaLogBark"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BARK_DARK_OAK_LOG       (162  , 13, "DarkOakLogBark"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_STAIRS           (163  , 0 , "AcaciaStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "acaciastair"),
    DARK_OAK_STAIRS         (164  , 0 , "DarkOakStairs"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "darkoakstair"),
    SLIME_BLOCK             (165  , 0 , "SlimeBlock"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BARRIER                 (166  , 0 , "Barrier"               , PlayerGroup.ADMIN    , PlayerGroup.ADMIN),
    IRON_TRAP_DOOR          (167  , 0 , "IronTrapdoor"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PRISMARINE              (168  , 0 , "Prismarine"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PRISMARINE_BRICKS       (168  , 1 , "PrismarineBricks"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_PRISMARINE         (168  , 2 , "DarkPrismarine"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SEA_LANTERN             (169  , 0 , "SeaLantern"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    HAY                     (170  , 0 , "Hay"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST, "hayblock", "blockofhay"),
    CARPET_WHITE            (171  , 0 , "Carpet"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "whitecarpet"),
    CARPET_ORANGE           (171  , 1 , "OrangeCarpet"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_MAGENTA          (171  , 2 , "MagentaCarpet"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_LIGHTBLUE        (171  , 3 , "LightBlueCarpet"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_YELLOW           (171  , 4 , "YellowCarpet"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_LIGHTGREEN       (171  , 5 , "LigthGreenCarpet"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "greencarpet"),
    CARPET_PINK             (171  , 6 , "PinkCarpet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_GRAY             (171  , 7 , "GrayCarpet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_LIGHTGRAY        (171  , 8 , "LightGrayCarpet"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_CYAN             (171  , 9 , "CyanCarpet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_PURPLE           (171  , 10, "PurpleCarpet"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_BLUE             (171  , 11, "BlueCarpet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "darkbluecarpet"),
    CARPET_BROWN            (171  , 12, "BrownCarpet"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_DARKGREEN        (171  , 13, "DarkGreenCarpet"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_RED              (171  , 14, "RedCarpet"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARPET_BLACK            (171  , 15, "BlackCarpet"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLAY_HARD               (172  , 0 , "HardenedClay"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "hardclay", "clayhard"),
    COAL_BLOCK              (173  , 0 , "CoalBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blockcoal", "blockofcoal"),
    PACKED_ICE              (174  , 0 , "PackedIce"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SUNFLOWER               (175  , 0 , "Sunflower"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LILAC                   (175  , 1 , "Lilac"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_TALLGRASS        (175  , 2 , "DoubleTallGrass"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LARGE_FERN              (175  , 3 , "LargeFern"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ROSE_BUSH               (175  , 4 , "RoseBush"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PEONY                   (175  , 5 , "Peony"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FREE_STAND_BANNER       (176  , 0 , "FreeStandBanner",false , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WALL_MOUNT_BANNER       (177  , 0 , "WallMountBanner",false , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    INVERTED_DAY_DETECTOR   (178  , 0 , "InvertDayDetector"     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_SANDSTONE           (179  , 0 , "RedSandstone"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHISELED_RED_SANDSTONE  (179  , 1 , "ChlRedSandstone"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SMOOTH_RED_SANDSTONE    (179  , 2 , "SthRedSandstone"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_SANDSTONE_STAIRS    (180  , 0 , "RedSandStairs"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DBL_RED_SANDSTONE_SLAB  (181  , 0 , "DblRedSandSlab"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SMTH_DBL_RED_SAND_SLAB  (181  , 8 , "SthDRedSandSlab"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_SANDSTONE_SLAB      (182  , 0 , "RedSandSlab"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_RED_SANDSTONE_SLAB   (182  , 8 , "UpRedSandSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_FENCE_GATE       (183  , 0 , "SpruceGate"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_FENCE_GATE        (184  , 0 , "BirchGate"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_FENCE_GATE       (185  , 0 , "JungleGate"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_FENCE_GATE     (186  , 0 , "DarkOakGate"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_FENCE_GATE       (187  , 0 , "AcaciaGate"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_FENCE            (188  , 0 , "SpruceFence"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_FENCE             (189  , 0 , "BirchFence"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_FENCE            (190  , 0 , "JungleFence"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_FENCE          (191  , 0 , "DarkOakFence"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_FENCE            (192  , 0 , "AcaciaFence"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_DOOR_BLOCK       (193  , 0 , "SpruceDoor",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_DOOR_BLOCK        (194  , 0 , "BirchDoor",false       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_DOOR_BLOCK       (195  , 0 , "JungleDoor",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_DOOR_BLOCK       (196  , 0 , "AcaciaDoor",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_DOOR_BLOCK     (197  , 0 , "DarkOakDoor",false     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    END_ROD                 (198  , 0 , "EndRod"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHORUS_PLANT            (199  , 0 , "ChorusPlant"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHORUS_FLOWER           (200  , 0 , "ChorusFlower"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PURPUR_BLOCK            (201  , 0 , "PurPurBlock"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PURPUR_PILLAR           (202  , 0 , "PurPurPillar"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PURPUR_STAIRS           (203  , 0 , "PurPurStairs"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DOUBLE_PURPUR_SLAB      (204  , 0 , "DblPurPurSlab"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PURPUR_SLAB             (205  , 0 , "PurPurSlab"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    UP_PURPUR_SLAB          (205  , 8 , "UpPurPurSlab"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ENDSTONE_BRICK          (206  , 0 , "EndstoneBrick"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BEETROOT_BLOCK          (207  , 0 , "BeetrootBlock",false   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GRASS_PATH              (208  , 0 , "GrassPath"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    END_GATEWAY             (209  , 0 , "EndGateway",false      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REPEAT_COMMAND_BLOCK    (210  , 0 , "RepeatCmdBlock"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHAIN_COMMAND_BLOCK     (211  , 0 , "ChainCmdBlock"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FROSTED_ICE             (212  , 0 , "FrostedIce"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MAGMA_BLOCK             (213  , 0 , "MagmaBlock"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_WART_BLOCK       (214  , 0 , "NetherWartBlock"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_NETHER_BRICK        (215  , 0 , "RedNetherBrick"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BONE_BLOCK              (216  , 0 , "BoneBlock"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STRUCTURE_VOID          (217  , 0 , "StructureVoid"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    OBSERVER                (218  , 0 , "Observer"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WHITE_SHULKER_BOX       (219  , 0 , "WhiteShulkerBox"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ORANGE_SHULKER_BOX      (220  , 0 , "OrangeShulkerBox"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MAGENTA_SHULKER_BOX     (221  , 0 , "MagentaShulkerBox"     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LIGHT_BLUE_SHULKER_BOX  (222  , 0 , "LightBlueShulkerBox"   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    YELLOW_SHULKER_BOX      (223  , 0 , "YellowShulkerBox"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LIME_SHULKER_BOX        (224  , 0 , "LimeShulkerBox"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PINK_SHULKER_BOX        (225  , 0 , "PinkShulkerBox"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GRAY_SHULKER_BOX        (226  , 0 , "GrayShulkerBox"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LIGHT_GRAY_SHULKER_BOX  (227  , 0 , "LightGrayShulkerBox"   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CYAN_SHULKER_BOX        (228  , 0 , "CyanShulkerBox"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PURPLE_SHULKER_BOX      (229  , 0 , "PurpleShulkerBox"      , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BLUE_SHULKER_BOX        (230  , 0 , "BlueShulkerBox"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BROWN_SHULKER_BOX       (231  , 0 , "BrownShulkerBox"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GREEN_SHULKER_BOX       (232  , 0 , "GreenShulkerBox"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RED_SHULKER_BOX         (233  , 0 , "RedShulkerBox"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BLACK_SHULKER_BOX       (234  , 0 , "BlackShulkerBox"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STRUCTURE_BLOCK_SAVE    (255  , 0 , "StructureBlockSave"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STRUCTURE_BLOCK_LOAD    (255  , 1 , "StructureVoidLoad"     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STRUCTURE_BLOCK_CORNER  (255  , 2 , "StructureVoidCorner"   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STRUCTURE_BLOCK_DATA    (255  , 3 , "StructureBlockData"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    //
    //
    //
    //
    //
    // Items
    IRON_SHOVEL             (256 , 0 , "IronShovel"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_PICKAXE            (257 , 0 , "IronPickaxe"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ironpick"),
    IRON_AXE                (258 , 0 , "IronAxe"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FLINT_AND_TINDER        (259 , 0 , "FlintAndTinder"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "flint", "lighter", "flintandsteel", "flintsteel", "flintandiron", "flintnsteel", "flintniron", "flintntinder"),
    RED_APPLE               (260 , 0 , "RedApple"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "apple"),
    BOW                     (261 , 0 , "Bow"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ARROW                   (262 , 0 , "Arrow"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COAL                    (263 , 0 , "Coal"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHARCOAL                (263 , 1 , "Charcoal"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND                 (264 , 0 , "Diamond"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_INGOT              (265 , 0 , "IronIngot"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "iron"),
    GOLD_INGOT              (266 , 0 , "GoldIngot"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "gold"),
    IRON_SWORD              (267 , 0 , "IronSword"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WOOD_SWORD              (268 , 0 , "WoodenSword"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodsword"),
    WOOD_SHOVEL             (269 , 0 , "WoodenShovel"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodshovel"),
    WOOD_PICKAXE            (270 , 0 , "WoodenPickaxe"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodpick"),
    WOOD_AXE                (271 , 0 , "WoodenAxe"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodaxe"),
    STONE_SWORD             (272 , 0 , "StoneSword"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STONE_SHOVEL            (273 , 0 , "StoneShovel"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STONE_PICKAXE           (274 , 0 , "StonePickaxe"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "stonepick"),
    STONE_AXE               (275 , 0 , "StoneAxe"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND_SWORD           (276 , 0 , "DiamondSword"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND_SHOVEL          (277 , 0 , "DiamondShovel"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND_PICKAXE         (278 , 0 , "DiamondPickaxe"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamondpick"),
    DIAMOND_AXE             (279 , 0 , "DiamondAxe"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STICK                   (280 , 0 , "Stick"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BOWL                    (281 , 0 , "Bowl"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MUSHROOM_SOUP           (282 , 0 , "MushroomSoup"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "soup", "brbsoup"),
    GOLD_SWORD              (283 , 0 , "GoldenSword"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldsword"),
    GOLD_SHOVEL             (284 , 0 , "GoldenShovel"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldshovel"),
    GOLD_PICKAXE            (285 , 0 , "GoldenPickaxe"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldpick"),
    GOLD_AXE                (286 , 0 , "GoldenAxe"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldaxe"),
    STRING                  (287 , 0 , "String"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FEATHER                 (288 , 0 , "Feather"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GUNPOWDER               (289 , 0 , "Gunpowder"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "gunpowder", "sulphur"),
    WOOD_HOE                (290 , 0 , "WoodenHoe"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodhoe"),
    STONE_HOE               (291 , 0 , "StoneHoe"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_HOE                (292 , 0 , "IronHoe"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DIAMOND_HOE             (293 , 0 , "DiamondHoe"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLD_HOE                (294 , 0 , "GoldenHoe"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldhoe"),
    SEEDS                   (295 , 0 , "Seeds"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "seed"),
    WHEAT                   (296 , 0 , "Wheat"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BREAD                   (297 , 0 , "Bread"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LEATHER_HELMET          (298 , 0 , "LeatherHelmet"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leatherhat"),
    LEATHER_CHEST           (299 , 0 , "LeatherChestplate"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leatherchest", "leathervest", "leatherbreastplate", "leatherplate", "leathercplate", "leatherbody"),
    LEATHER_LEGGINGS        (300 , 0 , "LeatherPants"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leatherleggings", "leathergreaves", "leatherlegs", "leatherleggings", "leatherstockings", "leatherbreeches"),
    LEATHER_BOOTS           (301 , 0 , "LeatherBoots"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leathershoes", "leatherfoot", "leatherfeet"),
    CHAINMAIL_HELMET        (302 , 0 , "ChainmailHelmet"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "chainmailhat"),
    CHAINMAIL_CHEST         (303 , 0 , "ChainmailChestplate"   , PlayerGroup.GUEST    , PlayerGroup.GUEST, "chainmailchest", "chainmailvest", "chainmailbreastplate", "chainmailplate", "chainmailcplate", "chainmailbody"),
    CHAINMAIL_LEGGINGS      (304 , 0 , "ChainmailLeggings"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "chainmailpants", "chainmailgreaves", "chainmaillegs", "chainmailstockings", "chainmailbreeches"),
    CHAINMAIL_BOOTS         (305 , 0 , "ChainmailBoots"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "chainmailshoes", "chainmailfoot", "chainmailfeet"),
    IRON_HELMET             (306 , 0 , "IronHelmet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ironhat"),
    IRON_CHEST              (307 , 0 , "IronChestplate"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ironchest", "ironvest", "ironbreastplate", "ironplate", "ironcplate", "ironbody"),
    IRON_LEGGINGS           (308 , 0 , "IronLeggings"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ironpants", "irongreaves", "ironlegs", "ironstockings", "ironbreeches"),
    IRON_BOOTS              (309 , 0 , "IronBoots"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ironshoes", "ironfoot", "ironfeet"),
    DIAMOND_HELMET          (310 , 0 , "DiamondHelmet"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamondhat"),
    DIAMOND_CHEST           (311 , 0 , "DiamondChestplate"     , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamondchest", "diamondvest", "diamondbreastplate", "diamondplate", "diamondcplate", "diamondbody"),
    DIAMOND_LEGGINS         (312 , 0 , "DiamondLeggings"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamondpants", "diamondgreaves", "diamondlegs", "diamondstockings", "diamondbreeches"),
    DIAMOND_BOOTS           (313 , 0 , "DiamondBoots"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diamondshoes", "diamondfoot", "diamondfeet"),
    GOLD_HELMET             (314 , 0 , "GoldHelmet"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldhat"),
    GOLD_CHEST              (315 , 0 , "GoldChestplate"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldchest", "goldvest", "goldbreastplate", "goldplate", "goldcplate", "goldbody"),
    GOLD_LEGGINGS           (316 , 0 , "GoldLeggings"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldpants", "goldgreaves", "goldlegs", "goldstockings", "goldbreeches"),
    GOLD_BOOTS              (317 , 0 , "GoldBoots"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldshoes", "goldfoot", "goldfeet"),
    FLINT                   (318 , 0 , "Flint"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAW_PORKCHOP            (319 , 0 , "RawPorkchop"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "rawpork", "rawbacon", "baconstrips", "rawmeat"),
    COOKED_PORKCHOP         (320 , 0 , "CookedPorkchop"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "pork", "cookedpork", "cookedbacon", "bacon", "meat"),
    PAINTING                (321 , 0 , "Painting"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLD_APPLE              (322 , 0 , "GoldenApple"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldapple"),
    ENCHANTED_APPLE         (322 , 1 , "EnchantedApple"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "enchantapple", "enchantedapple"),
    SIGN                    (323 , 0 , "WoodenSign"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "sign"),
    OAK_DOOR_ITEM           (324 , 0 , "WoodenDoor"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "door"),
    BUCKET                  (325 , 0 , "Bucket"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WATER_BUCKET            (326 , 0 , "WaterBucket"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LAVA_BUCKET             (327 , 0 , "LavaBucket"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MINECART                (328 , 0 , "Minecart"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cart"),
    SADDLE                  (329 , 0 , "Saddle"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_DOOR_ITEM          (330 , 0 , "IronDoor"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_DUST           (331 , 0 , "RedstoneDust"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "reddust", "redstone", "dust", "wire"),
    SNOWBALL                (332 , 0 , "Snowball"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    OAK_BOAT                (333 , 0 , "OakBoat"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "woodboat", "boat"),
    LEATHER                 (334 , 0 , "Leather"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cowhide"),
    MILK_BUCKET             (335 , 0 , "MilkBucket"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "milk"),
    BRICK_BAR               (336 , 0 , "Brick"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "brickbar"),
    CLAY_BALL               (337 , 0 , "ClayBall"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SUGAR_CANE_ITEM         (338 , 0 , "SugarCane"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "reed", "reeds"),
    PAPER                   (339 , 0 , "Paper"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BOOK                    (340 , 0 , "Book"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SLIME_BALL              (341 , 0 , "SlimeBall"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "slime"),
    STORAGE_MINECART        (342 , 0 , "StorageMinecart"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "storagecart"),
    POWERED_MINECART        (343 , 0 , "PoweredMinecart"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "poweredcart"),
    EGG                     (344 , 0 , "Egg"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COMPASS                 (345 , 0 , "Compass"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FISHING_ROD             (346 , 0 , "FishingRod"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "fishingpole"),
    WATCH                   (347 , 0 , "Watch"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "clock", "timer"),
    GLOWSTONE_DUST          (348 , 0 , "GlowstoneDust"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "lightstonedust", "brightstonedust", "brittlegolddust", "brimstonedust"),
    RAW_FISH                (349 , 0 , "RawFish"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAW_SALMON              (349 , 2 , "RawSalmon"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CLOWNFISH               (349 , 3 , "Clownfish"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PUFFERFISH              (349 , 4 , "Pufferfish"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COOKED_FISH             (350 , 0 , "CookedFish"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "fish"),
    COOKED_SALMON           (350 , 1 , "CookedSalmon"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    INK_SAC                 (351 , 0 , "InkSac"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "ink", "blackdye", "inksack"),
    DYE_RED                 (351 , 1 , "RedDye"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "rosered"),
    DYE_GREEN               (351 , 2 , "GreenDye"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "cactusgreen"),
    COCOA_BEANS             (351 , 3 , "CocoaBeans"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "browndye", "cacoabeans", "cocoa"),
    LAPIS_LAZULI            (351 , 4 , "Lapis"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bluedye"),
    DYE_PURPLE              (351 , 5 , "PurpleDye"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_CYAN                (351 , 6 , "CyanDye"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_LIGHTGRAY           (351 , 7 , "LightGrayDye"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_GRAY                (351 , 8 , "GrayDye"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_PINK                (351 , 9 , "PinkDye"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_LIME                (351 , 10, "LimeDye"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_YELLOW              (351 , 11, "YellowDye"             , PlayerGroup.GUEST    , PlayerGroup.GUEST, "dandilionyellow"),
    DYE_LIGHTBLUE           (351 , 12, "LightBlueDye"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_MAGENTA             (351 , 13, "MagentaDye"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DYE_ORANGE              (351 , 14, "OrangeDye"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BONEMEAL                (351 , 15, "Bonemeal"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "whitedye"),
    BONE                    (352 , 0 , "Bone"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SUGAR                   (353 , 0 , "Sugar"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CAKE_ITEM               (354 , 0 , "Cake"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BED_ITEM                (355 , 0 , "Bed"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    REDSTONE_REPEATER       (356 , 0 , "RedstoneRepeater"      , PlayerGroup.GUEST    , PlayerGroup.GUEST, "diode", "delayer", "repeater"),
    COOKIE                  (357 , 0 , "Cookie"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MAP                     (358 , 0 , "Map"                   , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SHEARS                  (359 , 0 , "Shears"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "scissors"),
    MELON_ITEM              (360 , 0 , "MelonSlice"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "melon"),
    PUMPKIN_SEEDS           (361 , 0 , "PumpkinSeeds"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MELON_SEEDS             (362 , 0 , "MelonSeeds"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAW_BEEF                (363 , 0 , "RawBeef"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    STEAK                   (364 , 0 , "Steak"                 , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAW_CHICKEN             (365 , 0 , "RawChicken"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COOKED_CHICKEN          (366 , 0 , "CookedChicken"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ROTTEN_FLESH            (367 , 0 , "RottenFlesh"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ENDER_PEARL             (368 , 0 , "EnderPearl"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BLAZE_ROD               (369 , 0 , "BlazeRod"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GHAST_TEAR              (370 , 0 , "GhastTear"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLD_NUGGET             (371 , 0 , "GoldNugget"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_WART_ITEM        (372 , 0 , "NetherWart"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POTIONS                 (373 , 0 , "Potions"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GLASS_BOTTLE            (374 , 0 , "GlassBottle"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPIDER_EYE              (375 , 0 , "SpiderEye"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FERMENTED_SPIDER_EYE    (376 , 0 , "FermentedSpiderEye"    , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BLAZE_POWDER            (377 , 0 , "BlazePowder"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    MAGMA_CREAM             (378 , 0 , "MagmaCream"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BREWING_STAND_ITEM      (379 , 0 , "BrewingStand"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CAULDRON_ITEM           (380 , 0 , "Cauldron"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    EYE_OF_ENDER            (381 , 0 , "EyeofEnder"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GLISTERING_MELON        (382 , 0 , "GlisteringMelon"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPAWN_EGG               (383 , 0 , "SpawnEgg"              , PlayerGroup.ADMIN    , PlayerGroup.GUEST),
    BOTTLE_O_ENCHANTING     (384 , 0 , "Bottleo'Enchanting"    , PlayerGroup.GUEST    , PlayerGroup.GUEST, "bottleoenchanting", "bottleofenchanting", "enchantingbottle"),
    FIRE_CHARGE             (385 , 0 , "FireCharge"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BOOK_AND_QUILL          (386 , 0 , "BookandQuill"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WRITTEN_BOOK            (387 , 0 , "WrittenBook"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    EMERALD                 (388 , 0 , "Emerald"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ITEM_FRAME              (389 , 0 , "ItemFrame"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    FLOWER_POT              (390 , 0 , "FlowerPot"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARROT                  (391 , 0 , "Carrot"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POTATO                  (392 , 0 , "Potato"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BAKED_POTATO            (393 , 0 , "BakedPotato"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POISON_POTATO           (394 , 0 , "PoisonousPotato"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BLANK_MAP               (395 , 0 , "BlankMap"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    GOLDEN_CARROT           (396 , 0 , "GoldenCarrot"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SKELETON_SKULL          (397 , 0 , "SkeletonSkull"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    WITHER_SKELETON_SKULL   (397 , 1 , "WitherSkull"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ZOMBIE_SKULL            (397 , 2 , "ZombieSkull"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PLAYER_SKULL            (397 , 3 , "PlayerSkull"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CREEPER_SKULL           (397 , 4 , "CreeperSkull"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DRAGON_SKULL            (397 , 5 , "DragonHead"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CARROT_ON_STICK         (398 , 0 , "CarrotonaStick"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "carrotstick", "carrotonstick", "carrotonastick"),
    NETHER_STAR             (399 , 0 , "NetherStar"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "netherstar"),
    PUMPKIN_PIE             (400 , 0 , "PumpkinPie"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "pumpkinpie"),
    FIREWORK_ROCKET         (401 , 0 , "Firework"              , PlayerGroup.GUEST    , PlayerGroup.GUEST, "rocket", "fireworkrocket", "fireworks"),
    FIREWORK_STAR           (402 , 0 , "FireworkStar"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "rocketstar", "fireworkstar", "fireworkball"),
    ENCHANTED_BOOK          (403 , 0 , "EnchantedBook"         , PlayerGroup.GUEST    , PlayerGroup.GUEST, "enchantbook", "enchantingbook", "enchant"),
    REDSTONE_COMPARATOR     (404 , 0 , "RedstoneComparatorItem", PlayerGroup.GUEST    , PlayerGroup.GUEST),
    NETHER_BRICK_BAR        (405 , 0 , "NetherBrickBar"        , PlayerGroup.GUEST    , PlayerGroup.GUEST, "netherbar"),
    NETHER_QUARTZ           (406 , 0 , "NetherQuartz"          , PlayerGroup.GUEST    , PlayerGroup.GUEST, "Quartz", "Quarts"),
    TNT_MINECART            (407 , 0 , "MinecartWithTNT"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "TNTCart", "TNTMinecart", "MinecartTNT"),
    HOPPER_MINECART         (408 , 0 , "MinecartWithHopper"    , PlayerGroup.GUEST    , PlayerGroup.GUEST, "HopperCart", "HopperMinecart", "MinecarHopper"),
    PRISMARINE_SHARD        (409 , 0 , "PrismarineShard"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    PRISMARINE_CRYSTAL      (410 , 0 , "PrismarineCrystal"     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RAW_RABBIT              (411 , 0 , "RawRabbit"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COOKED_RABBIT           (412 , 0 , "CookedRabbit"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RABBIT_STEW             (413 , 0 , "RabbitStew"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    RABBITS_FOOT            (414 , 0 , "RabbitsFoot"           , PlayerGroup.GUEST    , PlayerGroup.GUEST, "rabbitfoot"),
    RABBIT_HIDE             (415 , 0 , "RabbitHide"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ARMOR_STAND             (416 , 0 , "ArmorStand"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    HORSE_ARMOUR_IRON       (417 , 0 , "IronHorseArmour"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "HorseIronArmour", "IronArmour", "IronHorseArmor", "HorseIronArmor", "IronArmor"),
    HORSE_ARMOUR_GOLD       (418 , 0 , "GoldHorseArmour"       , PlayerGroup.GUEST    , PlayerGroup.GUEST, "HorseGoldArmour", "GoldArmour", "GoldHorseArmor", "HorseGoldArmor", "GoldArmor"),
    HORSE_ARMOUR_DIAMOND    (419 , 0 , "DiamondHorseArmour"    , PlayerGroup.GUEST    , PlayerGroup.GUEST, "HorseDiamondArmour", "DiamondArmour", "DiamondHorseArmor", "HorseDiamondArmor", "DiamondArmor"),
    LEAD                    (420 , 0 , "Lead"                  , PlayerGroup.GUEST    , PlayerGroup.GUEST, "leash", "leed", "leesh", "rope", "uad", "universalAttachmentDevice"),
    NAME_TAG                (421 , 0 , "NameTag"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "tag", "name"),
    RAW_MUTTON              (423 , 0 , "RawMutton"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    COOKED_MUTTON           (424 , 0 , "CookedMutton"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_BLACK            (425 , 0, "BlackBanner"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_RED              (425 , 1, "RedBanner"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_GREEN            (425 , 2, "GreenBanner"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_BROWN            (425 , 3, "BrownBanner"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_BLUE             (425 , 4, "BlueBanner"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_PURPLE           (425 , 5, "PurpleBanner"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_CYAN             (425 , 6 ,"CyanBanner"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_LIGHT_GRAY       (425 , 7 ,"LightGrayBanner"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_GRAY             (425 , 8 ,"GrayBanner"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_PINK             (425 , 9 ,"PinkBanner"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_LIME             (425 , 10,"LimeBanner"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_YELLOW           (425 , 11,"YellowBanner"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_LIGHT_BLUE       (425 , 12,"LightBlueBanner"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_MAGENTA          (425 , 13,"MagentaBanner"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_ORANGE           (425 , 14,"OrangeBanner"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BANNER_WHITE            (425 , 15,"WhiteBanner"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ENDER_CRYSTAL           (426 , 0 ,"EnderCrystal"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_DOOR_ITEM        (427 , 0 , "SpruceDoorItem"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_DOOR_ITEM         (428 , 0 , "BirchDoorItem"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_DOOR_ITEM        (429 , 0 , "JungleDoorItem"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_DOOR_ITEM        (430 , 0 , "AcaciaDoor"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_DOOR_ITEM      (431 , 0 , "DarkOakDoorItem"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    CHORUS_FRUIT            (432 , 0 , "ChorusFruit"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    POPPED_CHORUS_FRUIT     (433 , 0 , "PoppedChorusFruit"     , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BEETROOT                (434 , 0 , "BeetRoot"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BEETROOT_SEEDS          (435 , 0 , "BeetRootSeeds"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BEETROOT_SOUP           (436 , 0 , "BeetRootSoup"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DRAGONS_BREATH          (437 , 0 , "DragonsBreath"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPLASH_POTION           (438 , 0 , "SplashPotion"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPECTRAL_ARROW          (439 , 0 , "SpectralArrow"         , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    TIPPED_ARROW            (440 , 0 , "TippedArrow"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    LINGERING_POTION        (441 , 0 , "LingeringPotion"       , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SHIELD                  (442 , 0 , "Shield"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ELYTRA                  (443 , 0 , "Elytra"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SPRUCE_BOAT             (444 , 0 , "SpruceBoat"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    BIRCH_BOAT              (445 , 0 , "BirchBoat"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    JUNGLE_BOAT             (446 , 0 , "JungleBoat"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    ACACIA_BOAT             (447 , 0 , "AcaciaBoat"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DARK_OAK_BOAT           (448 , 0 , "DarkOakBoat"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    TOTEM_OF_UNDYING        (449 , 0 , "TotemOfUndying"        , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    IRON_NUGGET             (452 , 0 , "IronNugget"            , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    SHULKER_SHELL           (460 , 0 , "ShulkerShell"          , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    //
    DISC_13                 (2256, 0 , "13Disc"                , PlayerGroup.GUEST    , PlayerGroup.GUEST, "goldrecord", "golddisc"),
    DISC_CAT                (2257, 0 , "CatDisc"               , PlayerGroup.GUEST    , PlayerGroup.GUEST, "greenrecord", "greenddisc"),
    DISC_BLOCKS             (2258, 0 , "BlocksDisc"            , PlayerGroup.GUEST    , PlayerGroup.GUEST, "blockdisc"),
    DISC_CHIRP              (2259, 0 , "ChirpDisc"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_FAR                (2260, 0 , "FarDisc"               , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_MAL                (2261, 0 , "MallDisc"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_MELLOHI            (2262, 0 , "MellohiDisc"           , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_STAL               (2263, 0 , "StalDisc"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_STRAD              (2264, 0 , "StradDisc"             , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_WARD               (2265, 0 , "WardDisc"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_11                 (2266, 0 , "11Disc"                , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    DISC_WAIT               (2267, 0 , "WaitDisc"              , PlayerGroup.GUEST    , PlayerGroup.GUEST),
    //
    //
    //
    //
    // Innectis Material
    WOOL_ALL                (35  , -1, "wool"                  , PlayerGroup.NONE     , PlayerGroup.NONE),
    CLAY_ALL                (159 , -1, "clay"                  , PlayerGroup.NONE     , PlayerGroup.NONE),
    CARPET_ALL              (171 , -1, "carpet"                , PlayerGroup.NONE     , PlayerGroup.NONE),
    LOCKED                  (0   , -1, "LockedBlock"           , PlayerGroup.ADMIN    , PlayerGroup.ADMIN, "lock", "locked", "lockblock", "lockedblock"),
    UN_LOCKED               (0   , -1, "Un-LockedBlock"        , PlayerGroup.ADMIN    , PlayerGroup.ADMIN, "unlock", "unlocked", "unlockblock", "unlockedblock"),
    NON_VIRTUAL             (0   , -1, "Virtual"               , PlayerGroup.ADMIN    , PlayerGroup.ADMIN),
    VIRTUAL                 (0   , -1, "NonVirtual"            , PlayerGroup.ADMIN    , PlayerGroup.ADMIN);
    //
    // #FORMAT_END
    //
    /**
     * Stores a map of the IDs for fast access.
     */
    private static final Map<String, IdpMaterial> ids = new LinkedHashMap<String, IdpMaterial>();
    /**
     * Stores a map of the names for fast access.
     */
    private static final Map<String, IdpMaterial> lookup = new LinkedHashMap<String, IdpMaterial>();
    private final int id;
    private final int data;
    private final String name;
    private final String[] lookupKeys;
    private final boolean isInventoryItem;
    private final PlayerGroup requiredGroupToPlace;
    private final PlayerGroup requiredGroupToBreak;

    static {
        for (IdpMaterial type : values()) {
            ids.put(type.id + ":" + type.getData(), type);

            if (type.name != null) {
                lookup.put(type.name.toLowerCase(), type);
            }

            for (String key : type.lookupKeys) {
                lookup.put(key.toLowerCase(), type);
            }
        }
    }

    /**
     * Construct the type.
     *
     * @param id
     * @param name
     */
    IdpMaterial(int id, int data, String name, PlayerGroup requiredGroupToPlace, PlayerGroup requiredGroupToBreak, String... lookupKeys) {
        this(id, data, name, true, requiredGroupToPlace, requiredGroupToBreak, lookupKeys);
    }

    /**
     * Construct the type.
     *
     * @param id
     * @param name
     */
    IdpMaterial(int id, int data, String name, boolean isInventoryItem, PlayerGroup requiredGroupToPlace, PlayerGroup requiredGroupToBreak, String... lookupKeys) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.isInventoryItem = isInventoryItem;
        this.requiredGroupToPlace = requiredGroupToPlace;
        this.requiredGroupToBreak = requiredGroupToBreak;
        this.lookupKeys = lookupKeys;
    }

    /**
     * Returns the bukkit material object
     *
     * @return
     */
    public org.bukkit.Material getBukkitMaterial() {
        return org.bukkit.Material.getMaterial(id);
    }

    /**
     * Get item numeric ID.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get item data value
     *
     * @return
     */
    public int getData() {
        return data;
    }

    /**
     * Get user-friendly item name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a string representation of the ID and data
     * @return
     */
    public String getIdData() {
        return id + ":" + data;
    }

    /**
     * Gets if this material can be in the inventory
     * @return
     */
    public boolean isInventoryItem() {
        return isInventoryItem;
    }

    /**
     * Get a list of aliases.
     *
     * @return
     */
    public String[] getAliases() {
        return lookupKeys;
    }

    /**
     * Get the required PlayerGroup to place this material
     *
     * @return
     */
    public PlayerGroup getRequiredGroupToPlace() {
        return requiredGroupToPlace;
    }

    /**
     * Get the required PlayerGroup to break this material
     *
     * @return
     */
    public PlayerGroup getRequiredGroupToBreak() {
        return requiredGroupToBreak;
    }

    public boolean canPlayerPlaceMaterial(IdpPlayer player) {
        return player.getGroup().equalsOrInherits(getRequiredGroupToPlace());
    }

    public boolean canPlayerBreakMaterial(IdpPlayer player) {
        return player.getGroup().equalsOrInherits(getRequiredGroupToBreak());
    }

    /**
     * Checks if a block is weak, meaning it will be removed
     * if a block supporting it is removed
     *
     * @return
     */
    public boolean isWeakBlock() {
        switch (this) {
            case TORCH:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case REDSTONE_WIRE:
            case REDSTONE_REPEATER_OFF:
            case REDSTONE_REPEATER_ON:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case TRIPWIRE:
            case RAILS:
            case LEVER:
            case LADDER:
            case LILY_PAD:
            case NETHER_WART:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case ACTIVATOR_RAIL:
            case TRIPWIRE_HOOK:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case SIGN_POST:
            case WALL_SIGN:
            case TALL_GRASS:
            case SHRUBS:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case DANDELION:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case JUNGLE_SAPLING:
            case ACACIA_SAPLING:
            case DARK_OAK_SAPLING:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case FIRE:
            case OAK_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case IRON_DOOR_BLOCK:
            case STONE_PRESSURE_PLATE:
            case WOODEN_PRESSURE_PLATE:
            case LIGHT_PRESSURE_PLATE:
            case HEAVY_PRESSURE_PLATE:
            case SNOW_LAYER:
            case SUGAR_CANE:
            case CAKE:
            case TRAP_DOOR:
            case IRON_TRAP_DOOR:
            case COCOA_PLANT:
            case CACTUS:
            case PUMPKIN_STEM:
            case MELON_STEM:
            case WHEAT_BLOCK:
            case FLOWERPOT_BLOCK:
            case CARROT_BLOCK:
            case POTATO_BLOCK:
            case SKULL_BLOCK:
            case FREE_STAND_BANNER:
            case WALL_MOUNT_BANNER:
            case CARPET_WHITE:
            case CARPET_ORANGE:
            case CARPET_MAGENTA:
            case CARPET_LIGHTBLUE:
            case CARPET_YELLOW:
            case CARPET_LIGHTGREEN:
            case CARPET_PINK:
            case CARPET_GRAY:
            case CARPET_LIGHTGRAY:
            case CARPET_CYAN:
            case CARPET_PURPLE:
            case CARPET_BLUE:
            case CARPET_BROWN:
            case CARPET_DARKGREEN:
            case CARPET_RED:
            case CARPET_BLACK:
            case CHORUS_PLANT:
            case CHORUS_FLOWER:
                return true;
        }

        return false;
    }

    /**
     * Checks if this block is hazardous
     * @return
     */
    public boolean isHazard() {
        return (isLava() || this == IdpMaterial.FIRE || this == IdpMaterial.MAGMA_BLOCK);
    }

    /**
     * Returns if this this material represents a tall block
     * (a tall block is one in which a player cannot jump over)
     * @return
     */
    public boolean isTall() {
        switch (this) {
            case OAK_FENCE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE:
            case DARK_OAK_FENCE_GATE:
            case NETHER_BRICK_FENCE:
            case COBBLESTONE_WALL:
            case COBBLESTONE_MOSSY_WALL:
                return true;
        }

        return false;
    }

    /**
     * Checks if the material is a solid block
     *
     * @return
     */
    public boolean isSolid() {
        boolean solid = getBukkitMaterial().isSolid();

        if (solid) {
            // These really aren't solids as players can pass through them
            switch (getBukkitMaterial()) {
                case SIGN_POST:
                case WALL_SIGN:
                    return false;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the block is a non solid block. <br />
     *
     * @return
     */
    public boolean isNonSolid(Boolean includeFluids) {
        // This isn't solid
        if (isSolid()) {
            return false;
        }

        switch (this) {
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
            case WATER:
            case LAVA:
                return includeFluids;
        }

        return true;
    }

    /**
     * Returns true if its a block.
     * <i>Air is counted as a block</i>
     *
     * @param id
     * @return last id = 122
     */
    public boolean isBlock() {
        return !(id < 0 || id == 36 || id > 255);
    }

    /**
     * Checks if the material is durable.
     * @return true when its durable
     */
    public boolean isDurable() {
        return getMaxDurability() > 0;
    }

    /**
     * This will check if this material can be stacked.
     * <p/>
     * This method will yield the same result as !isDurable()
     * @return
     */
    public boolean canStack() {
        switch (this) {
            // Potions should never stack
            case POTIONS: {
                return false;
            }
        }
        return !isDurable(); // When durable it cant stack
    }

    /**
     * The maximum amount of items that is allowed in a stack of this material.
     * @return
     */
    public int getMaxStackSize() {
        return this.getBukkitMaterial().getMaxStackSize();
    }

    /**
     * Returns the max damage of this material
     *
     * @return
     */
    public int getMaxDurability() {
        return this.getBukkitMaterial().getMaxDurability();
    }

    /**
     * Returns if this item may be able to drop in the world from
     * being hit with the mining stick
     * @return
     */
    public boolean canDrop() {
        switch (this) {
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case MOB_SPAWNER:
            case DRAGON_EGG:
            case PORTAL:
            case END_PORTAL:
            case END_GATEWAY:
            case CAKE:
            case ANVIL:
                return false;
        }

        return true;
    }

    /**
     * Returns true if the Material is an ore
     *
     * @return
     */
    public boolean isOre() {
        switch (this) {
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case REDSTONE_ORE:
            case NETHER_QUARTZ_ORE:
            case GLOWING_REDSTONE_ORE:
            case LAPIS_LAZULI_OREBLOCK:
                return true;
        }
        return false;
    }

    /**
     * Checks if this material is a seed material
     * @return
     */
    public boolean isSeed() {
        switch (this) {
            case WHEAT_BLOCK:
            case MELON_STEM:
            case PUMPKIN_STEM:
            case NETHER_WART:
            case CARROT_BLOCK:
            case POTATO_BLOCK:
                return true;
        }

        return false;
    }

    /**
     * Returns true if the block can be interacted with by bonemeal
     *
     * @param mat
     * @return
     */
    public boolean isGrowableFromBonemeal() {
        switch (this) {
            case GRASS:
            case DIRT:
            case OAK_SAPLING:
            case WHEAT:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case PUMPKIN_SEEDS:
            case PUMPKIN_STEM:
            case MELON_SEEDS:
            case MELON_STEM:
            case CARROT_BLOCK:
            case POTATO_BLOCK:
                return true;
        }

        return false;
    }

    /**
     * Checks if the block is water
     *
     * @return
     */
    public boolean isWater() {
        return this == WATER || this == STATIONARY_WATER;
    }

    /**
     * Checks if the block is lava
     *
     * @return
     */
    public boolean isLava() {
        return this == LAVA || this == STATIONARY_LAVA;
    }

    /**
     * Checks if the block is water or lava
     *
     * @return
     */
    public boolean isLiquid() {
        return isWater() || isLava();
    }

    /**
     * Checks if this material is a type of the target material
     * @param targetMaterial
     * @return
     */
    public boolean isTypeOf(IdpMaterial targetMaterial) {
        return (getId() == targetMaterial.getId());
    }

    /**
     * Checks if this material can be painted white with bonemeal
     * @return
     */
    public boolean isPaintableWithBonemeal() {
        return (isTypeOf(IdpMaterial.WOOL_WHITE) || isTypeOf(IdpMaterial.CLAY_WHITE)
                || isTypeOf(IdpMaterial.CARPET_WHITE));
    }

    /**
     * Gets the base material of this material
     * @return
     */
    public IdpMaterial getBaseMaterial() {
        switch (this) {
            case WOOL_WHITE:
            case WOOL_ORANGE:
            case WOOL_MAGENTA:
            case WOOL_LIGHTBLUE:
            case WOOL_YELLOW:
            case WOOL_LIGHTGREEN:
            case WOOL_PINK:
            case WOOL_GRAY:
            case WOOL_LIGHTGRAY:
            case WOOL_CYAN:
            case WOOL_PURPLE:
            case WOOL_BLUE:
            case WOOL_BROWN:
            case WOOL_DARKGREEN:
            case WOOL_RED:
            case WOOL_BLACK:
                return IdpMaterial.WOOL_WHITE;
            case CLAY_WHITE:
            case CLAY_ORANGE:
            case CLAY_MAGENTA:
            case CLAY_LIGHTBLUE:
            case CLAY_YELLOW:
            case CLAY_LIGHTGREEN:
            case CLAY_PINK:
            case CLAY_GRAY:
            case CLAY_LIGHTGRAY:
            case CLAY_CYAN:
            case CLAY_PURPLE:
            case CLAY_BLUE:
            case CLAY_BROWN:
            case CLAY_DARKGREEN:
            case CLAY_RED:
            case CLAY_BLACK:
                return IdpMaterial.CLAY_HARD;
            case CARPET_WHITE:
            case CARPET_ORANGE:
            case CARPET_MAGENTA:
            case CARPET_LIGHTBLUE:
            case CARPET_YELLOW:
            case CARPET_LIGHTGREEN:
            case CARPET_PINK:
            case CARPET_GRAY:
            case CARPET_LIGHTGRAY:
            case CARPET_CYAN:
            case CARPET_PURPLE:
            case CARPET_BLUE:
            case CARPET_BROWN:
            case CARPET_DARKGREEN:
            case CARPET_RED:
            case CARPET_BLACK:
                return IdpMaterial.CARPET_WHITE;
            default:
                return this;
        }
    }

    public boolean isBanner() {
        return isTypeOf(IdpMaterial.BANNER_BLACK);
    }

    /**
     * Checks if this material is clay
     * @return
     */
    public boolean isClay() {
        return isTypeOf(IdpMaterial.CLAY_WHITE);
    }

    /**
     * Checks if this material is carpet
     * @return
     */
    public boolean isCarpet() {
        return isTypeOf(IdpMaterial.CARPET_WHITE);
    }

    /**
     * Checks if this material is a single slab
     * @return
     */
    public boolean isSingleSlab() {
        return (isTypeOf(IdpMaterial.STONE_SLAB) || isTypeOf(IdpMaterial.OAK_WOOD_SLAB)
                || isTypeOf(IdpMaterial.DBL_RED_SANDSTONE_SLAB) || isTypeOf(IdpMaterial.PURPUR_SLAB));
    }

    /**
     * Checks if this material is wool
     * @return
     */
    public boolean isWool() {
        return isTypeOf(IdpMaterial.WOOL_WHITE);
    }

   /**
     * Checks if the material is interactable. This is only used to check the
     * 'real' interactablity of blocks
     *
     * @param block
     * @return
     */
    public boolean isInteractable() {
        switch (this) {
            case DISPENSER:
            case DROPPER:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case CHEST:
            case TRAPPED_CHEST:
            case WORKBENCH:
            case FURNACE:
            case BURNING_FURNACE:
            case SIGN_POST:
            case OAK_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case WALL_SIGN:
            case LEVER:
            case IRON_DOOR_BLOCK:
            case STONE_BUTTON:
            //case JUKEBOX: //You can actually place a block on this
            case CAKE:
            case REDSTONE_COMPARATOR:
            case REDSTONE_COMPARATOR_ON:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_REPEATER:
            case REDSTONE_REPEATER_ON:
            case REDSTONE_REPEATER_OFF:
            case TRAP_DOOR:
            case IRON_TRAP_DOOR:
            case OAK_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case DAYLIGHT_DETECTOR:
            case INVERTED_DAY_DETECTOR:
            case ENCHANTMENT_TABLE:
            case BREWING_STAND:
            case CAULDRON_BLOCK:
            case WOOD_BUTTON:
            case FLOWER_POT:
            case COMMAND_BLOCK:
            case REPEAT_COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case DRAGON_EGG:
            case BEACON:
            case ANVIL:
                return true;
        }

        // All shulker boxes are interactable
        if (isTypeOf(IdpMaterial.WHITE_SHULKER_BOX)) {
            return true;
        }

        return false;
    }

    public IdpMaterial toDiamond() {
        switch (this) {
            case LEATHER_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
                return DIAMOND_HELMET;
            case LEATHER_CHEST:
            case IRON_CHEST:
            case CHAINMAIL_CHEST:
            case GOLD_CHEST:
                return DIAMOND_CHEST;
            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case GOLD_LEGGINGS:
                return DIAMOND_LEGGINS;
            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case GOLD_BOOTS:
                return DIAMOND_BOOTS;
            case WOOD_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
                return DIAMOND_SWORD;
            case WOOD_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLD_PICKAXE:
                return DIAMOND_PICKAXE;
            case WOOD_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLD_SHOVEL:
                return DIAMOND_SHOVEL;
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLD_AXE:
                return DIAMOND_AXE;
            case WOOD_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLD_HOE:
                return DIAMOND_HOE;
            default:
                return this;
        }
    }

    /**
     * Returns a list of all other variations of this tool.
     * e.g. Gold Sword -> Diamond Sword, Iron Sword, Stone Sword, Wood Sword.
     * @return
     */
    public List<IdpMaterial> getOtherToolTypes() {
        List<IdpMaterial> materialList = new ArrayList<IdpMaterial>();

        switch (this) {
            case WOOD_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
            case DIAMOND_SWORD:
                materialList.add(WOOD_SWORD);
                materialList.add(STONE_SWORD);
                materialList.add(IRON_SWORD);
                materialList.add(GOLD_SWORD);
                materialList.add(DIAMOND_SWORD);
                break;
            case WOOD_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLD_PICKAXE:
            case DIAMOND_PICKAXE:
                materialList.add(WOOD_PICKAXE);
                materialList.add(STONE_PICKAXE);
                materialList.add(IRON_PICKAXE);
                materialList.add(GOLD_PICKAXE);
                materialList.add(DIAMOND_PICKAXE);
                break;
            case WOOD_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLD_SHOVEL:
            case DIAMOND_SHOVEL:
                materialList.add(WOOD_SHOVEL);
                materialList.add(STONE_SHOVEL);
                materialList.add(IRON_SHOVEL);
                materialList.add(GOLD_SHOVEL);
                materialList.add(DIAMOND_SHOVEL);
                break;
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLD_AXE:
            case DIAMOND_AXE:
                materialList.add(WOOD_AXE);
                materialList.add(STONE_AXE);
                materialList.add(IRON_AXE);
                materialList.add(GOLD_AXE);
                materialList.add(DIAMOND_AXE);
                break;
            case WOOD_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLD_HOE:
            case DIAMOND_HOE:
                materialList.add(WOOD_HOE);
                materialList.add(STONE_HOE);
                materialList.add(IRON_HOE);
                materialList.add(GOLD_HOE);
                materialList.add(DIAMOND_HOE);
                break;
        }

        // Removes itself from this list.
        if (materialList.contains(this)) {
            materialList.remove(this);
        }

        return materialList;
    }

    public boolean isChest() {
        switch (this) {
            case CHEST:
            case TRAPPED_CHEST:
                return true;
        }

        return false;
    }

    public double getDropChance() {
        switch (this) {
            case RED_MUSHROOM_BLOCK:
            case BROWN_MUSHROOM_BLOCK:
                return 0.5;
            case JUNGLE_LEAVES:
                return 0.025;
            case OAK_LEAVES:
            case BIRCH_LEAVES:
            case SPRUCE_LEAVES:
            case ACACIA_LEAVES:
            case DARK_OAK_LEAVES:
                return 0.05;
            case VINES:
                return 0;
            default:
                return 1;
        }
    }

    public IdpItemStack getDrops() {
        switch (this) {
            case JUNGLE_LEAVES:
                return new IdpItemStack(IdpMaterial.JUNGLE_SAPLING, 1);
            case OAK_LEAVES:
                return new IdpItemStack(IdpMaterial.OAK_SAPLING, 1);
            case BIRCH_LEAVES:
                return new IdpItemStack(IdpMaterial.BIRCH_SAPLING, 1);
            case SPRUCE_LEAVES:
                return new IdpItemStack(IdpMaterial.SPRUCE_SAPLING, 1);
            case ACACIA_LEAVES:
                return new IdpItemStack(IdpMaterial.ACACIA_SAPLING, 1);
            case DARK_OAK_LEAVES:
                return new IdpItemStack(IdpMaterial.DARK_OAK_SAPLING, 1);
            case RED_MUSHROOM_BLOCK:
                return new IdpItemStack(IdpMaterial.RED_MUSHROOM, new Random().nextInt(2) + 1);
            case BROWN_MUSHROOM_BLOCK:
                return new IdpItemStack(IdpMaterial.BROWN_MUSHROOM, new Random().nextInt(2) + 1);
            default:
                return new IdpItemStack(this, 1);
        }

    }

    //*************************************************************************
    // STATIC FUNCTIONS
    //*************************************************************************
    //<editor-fold defaultstate="collapsed" desc="Search functions">
    /**
     * Return the material from the string. The string must be in the format
     * 'id:data' or 'id'. If the material is not found, it will look for the
     * name. If its still not found it will return null.
     *
     * @param value
     * @return material
     */
    public static IdpMaterial fromString(String value) {
        return fromString(value, true);
    }

    /**
     * Return the material from the string. The string must be in the format
     * 'id:data' or 'id'. If the material is not found, it will look for the
     * name. If its still not found it will return null.
     *
     * @param value
     * @param includeFuzzy (also do a fuzzy search when looking up the name)
     * @return material
     */
    public static IdpMaterial fromString(String value, Boolean includeFuzzy) {
        // Check for null or empty string
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            if (value.contains(":")) {
                // 2 values, ID & Data so just parse then
                // If it fails its not in the right format..
                String[] split = value.split(":");
                return fromID(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            } else {
                try {
                    // Just the ID, try to parse it.
                    return fromID(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    // If not parsable, check if the value is a name
                    return IdpMaterial.lookup(value, includeFuzzy);
                }
            }
        } catch (NumberFormatException nfe) {
            // Do nothing
        }
        return null;
    }

    /**
     * Gets an IDP Material from its bukkit equivalent
     * @param mat
     * @return
     */
    public static IdpMaterial fromBukkitMaterial(Material mat) {
        return fromID(mat.getId());
    }

    /**
     * Gets an IdpMaterial from the specified material data
     * @param md
     * @return
     */
    public static IdpMaterial fromMaterialData(MaterialData md) {
        return fromID(md.getItemTypeId(), md.getData());
    }

    /**
     * Return type from ID. May return null.
     *
     * @param id
     * @param data
     * @return
     */
    public static IdpMaterial fromID(int id, int data) {
        if (id == 29 || id == 33) {
            data = 7;
        }

        IdpMaterial mat = ids.get(id + ":" + data);
        if (mat == null) {
            mat = ids.get(id + ":0");
        }
        if (mat == null) {
            mat = AIR;
        }
        return mat;
    }

    /**
     * Return type from ID. May return null.
     *
     * @param id
     * @return
     */
    public static IdpMaterial fromID(int id) {
        return fromID(id, 0);
    }

    /**
     * Gets a material from a bukkit block while filtering
     * the data based on specific material
     * @param block
     * @return
     */
    public static IdpMaterial fromFilteredBlock(Block block) {
        int id = BlockHandler.getBlockTypeId(block);
        int dat = BlockHandler.getBlockData(block);
        Material mat = block.getType();

        if (mat == Material.LEAVES || mat == Material.LEAVES_2) {
            dat &= 0x3;
        } else if (mat == Material.ANVIL) {
            dat >>= 2;
        }

        return fromID(id, dat);
    }

    /**
     * Return type from a bukkit Block. May return null.
     *
     * @param block
     * @return
     */
    public static IdpMaterial fromBlock(Block block) {
        int id = BlockHandler.getBlockTypeId(block);
        byte dat = BlockHandler.getBlockData(block);

        return fromID(id, dat);
    }

    /**
     * Return type from an bukkit ItemStack. May return null.
     *
     * @param block
     * @return
     */
    public static IdpMaterial fromItemStack(ItemStack item) {
        return fromID(item.getTypeId(), (item.getData() == null ? 0 : item.getData().getData()));
    }

    /**
     * Get a name for the item.
     *
     * @param id
     * @return
     */
    public static String toName(int id, int data) {
        IdpMaterial type = ids.get(id + ":" + data);
        if (type != null) {
            return type.getName();
        } else {
            return "#" + id + ":" + data;
        }
    }

    /**
     * Return type from name. May return null.
     *
     * @param name
     * @return
     */
    public static IdpMaterial lookup(String name) {
        return lookup(name, true);
    }

    /**
     * Return type from name. May return null.
     *
     * @param name
     * @param fuzzy
     * @return
     */
    public static IdpMaterial lookup(String name, boolean fuzzy) {
        String testName = name.replace(" ", "").toLowerCase();

        IdpMaterial type = lookup.get(testName);

        if (type != null) {
            return type;
        }

        if (!fuzzy) {
            return null;
        }

        int minDist = -1;

        for (Entry<String, IdpMaterial> entry : lookup.entrySet()) {
            if (entry.getKey() != null && entry.getKey().charAt(0) != testName.charAt(0)) {
                continue;
            }

            int dist = StringUtil.getLevenshteinDistance(entry.getKey(), testName);

            if ((dist < minDist || minDist == -1) && dist < 2) {
                minDist = dist;
                type = entry.getValue();
            }
        }

        return type;
    }
    //</editor-fold>

    /**
     * Returns true if the potion got a positive effect
`     *
     * @param type
     * @return
     */
    public static boolean isPositivePotion(PotionType type) {
        switch (type) {
            case WATER:
            case REGEN:
            case SPEED:
            case FIRE_RESISTANCE:
            case INSTANT_HEAL:
            case STRENGTH:
                return true;
        }

        return false;
    }

    public boolean isAxe() {
        switch (this) {
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLD_AXE:
            case DIAMOND_AXE:
                return true;
            default:
                return false;
        }
    }

}
