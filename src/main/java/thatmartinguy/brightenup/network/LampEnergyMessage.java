package thatmartinguy.brightenup.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thatmartinguy.brightenup.block.BlockLamp;

public class LampEnergyMessage implements IMessage
{
    private int energyLevel;
    private BlockPos pos;

    public LampEnergyMessage() {};

    public LampEnergyMessage(int energyLevel, BlockPos pos)
    {
        this.energyLevel = energyLevel;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.energyLevel = buf.readInt();
        final int posX = buf.readInt();
        final int posY = buf.readInt();
        final int posZ = buf.readInt();
        pos = new BlockPos(posX, posY, posZ);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(energyLevel);
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
                   BlockLamp lamp = (BlockLamp) world.getBlockState(message.pos).getBlock();
                   world.checkLight(message.pos);
                   System.out.println("Message sent");
               }
            });
            return null;
        }
    }
}
