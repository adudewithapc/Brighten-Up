package thatmartinguy.brightenup;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import thatmartinguy.brightenup.block.ModBlocks;
import thatmartinguy.brightenup.network.LampEnergyMessage;
import thatmartinguy.brightenup.proxy.IProxy;
import thatmartinguy.brightenup.util.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class BrightenUp
{
    public static SimpleNetworkWrapper network;

    public static CreativeTabs tabBrightenUp = new CreativeTabs("brightenUp")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.REDSTONE_LAMP);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModBlocks.registerTileEntities();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

        int networkID = -1;
        network.registerMessage(LampEnergyMessage.Handler.class, LampEnergyMessage.class, networkID++, Side.CLIENT);

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_LOCATION, serverSide = Reference.SERVER_PROXY_LOCATION, modId = Reference.MOD_ID)
    public static IProxy proxy;

    @Instance
    public static BrightenUp instance;
}
