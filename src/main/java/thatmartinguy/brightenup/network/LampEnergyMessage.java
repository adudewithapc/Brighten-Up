package thatmartinguy.brightenup.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thatmartinguy.brightenup.energy.EnergyLevel;
import thatmartinguy.brightenup.tileentity.TileEntityLamp;

public class LampEnergyMessage implements IMessage
{
    private EnergyLevel energyLevel;
    private BlockPos pos;

    public LampEnergyMessage() {};

    public LampEnergyMessage(EnergyLevel energyLevel, BlockPos pos)
    {
        this.energyLevel = energyLevel;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        energyLevel = energyLevel.values()[buf.readInt()];
        final int posX = buf.readInt();
        final int posY = buf.readInt();
        final int posZ = buf.readInt();
        pos = new BlockPos(posX, posY, posZ);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(energyLevel.ordinal());
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class Handler implements IMessageHandler<LampEnergyMessage, IMessage>
    {
        @Override
        public IMessage onMessage(LampEnergyMessage message, MessageContext ctx)
        {
            FMLCommonHandler.instance().getWorldThread(ctx.getClientHandler()).addScheduledTask(() ->
            {
               final World world = Minecraft.getMinecraft().theWorld;
               assert world != null;

               if(world.getTileEntity(message.pos) instanceof TileEntityLamp)
               {
                   TileEntityLamp lamp = (TileEntityLamp) world.getTileEntity(message.pos);
                   world.checkLight(message.pos);
               }
            });
            return null;
        }
    }
}
