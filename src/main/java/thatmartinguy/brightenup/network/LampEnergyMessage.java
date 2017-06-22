package thatmartinguy.brightenup.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thatmartinguy.brightenup.block.BlockLamp;
import thatmartinguy.brightenup.energy.EnergyLevel;
import thatmartinguy.brightenup.tileentity.TileEntityLamp;

public class LampEnergyMessage implements IMessage
{
    private String energyLevel;
    private BlockPos pos;

    public LampEnergyMessage() {};

    public LampEnergyMessage(String energyLevel, BlockPos pos)
    {
        this.energyLevel = energyLevel;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        energyLevel = ByteBufUtils.readUTF8String(buf);
        final int posX = buf.readInt();
        final int posY = buf.readInt();
        final int posZ = buf.readInt();
        pos = new BlockPos(posX, posY, posZ);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, energyLevel);
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

               if(world.getBlockState(message.pos).getBlock() instanceof BlockLamp)
               {
                   world.setBlockState(message.pos, world.getBlockState(message.pos).withProperty(BlockLamp.ENERGY_LEVEL, Enum.valueOf(EnergyLevel.class, message.energyLevel.toUpperCase())));
                   world.checkLight(message.pos);
                   System.out.println("Message sent as " + message.energyLevel);
               }
            });
            return null;
        }
    }
}
