package thatmartinguy.brightenup.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thatmartinguy.brightenup.tileentity.TileEntityLamp;

@Mod.EventBusSubscriber
public class ModBlocks
{
    public static BlockLamp blockLightBulb;
    public static final Block[] BLOCKS = {
            blockLightBulb = new BlockLamp(Material.GLASS, "lightBulb", 3, 2000, 1000, 20)
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event)
    {
        for(Block block : BLOCKS)
        {
            event.getRegistry().register(new ItemBlock(block).setUnlocalizedName(block.getUnlocalizedName()).setRegistryName(block.getRegistryName()));
        }
    }

    public static void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityLamp.class, "lampBase");
    }
}
