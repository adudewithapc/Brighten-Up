package thatmartinguy.brightenup.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LampEnergyMessage implements IMessage
{
    private int energyLevel;

    public LampEnergyMessage() {};

    public LampEnergyMessage(int energyLevel)
    {
        this.energyLevel = energyLevel;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.energyLevel = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(energyLevel);
    }

    public static class Handler implements IMessageHandler<LampEnergyMessage, IMessage>
    {
        @Override
        public IMessage onMessage(LampEnergyMessage message, MessageContext ctx)
        {
            return null;
        }
    }
}
