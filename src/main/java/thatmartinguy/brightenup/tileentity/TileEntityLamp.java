package thatmartinguy.brightenup.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.block.BlockLamp;
import thatmartinguy.brightenup.energy.EnergyLevel;
import thatmartinguy.brightenup.network.LampEnergyMessage;
import thatmartinguy.brightenup.util.LogHelper;

import java.lang.reflect.Field;
import java.util.List;

public class TileEntityLamp extends TileEntity implements ITickable, IEnergyReceiver
{
    private EnergyStorage storage;
    private float lifetime;
    private EnergyLevel lastLevel;

    private static final Field PLAYER_ENTRY_LIST = ReflectionHelper.findField(PlayerChunkMapEntry.class, "players", "field_187283_c");

    public TileEntityLamp()
    {
        storage = new EnergyStorage(Integer.MIN_VALUE);
        PLAYER_ENTRY_LIST.setAccessible(true);
        lastLevel = EnergyLevel.EMPTY;
    }
    
    @Override
    public void update()
    {
        if (worldObj.getBlockState(this.pos).getBlock() instanceof BlockLamp)
        {
            BlockLamp lamp = (BlockLamp) worldObj.getBlockState(pos).getBlock();
            if(lifetime == 0)
                lifetime = lamp.maxLifetime;
            if(storage.getMaxEnergyStored() == Integer.MIN_VALUE)
                storage = new EnergyStorage(lamp.capacity);
            if (!worldObj.isRemote)
            {
                storage.modifyEnergyStored(-lamp.loss);
                if (EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()) != lastLevel)
                {
                    switch (EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()))
                    {
                        case EMPTY:
                            break;
                        case LOW:
                            lifetime -= 1 * lamp.lowEnergyMultiplier;
                            break;
                        case MEDIUM:
                            lifetime -= 1 * lamp.mediumEnergyMultiplier;
                            break;
                        case HIGH:
                            lifetime -= 1 * lamp.highEnergyMultiplier;
                            break;
                    }
                    try
                    {
                        List<EntityPlayerMP> playerList = (List<EntityPlayerMP>) PLAYER_ENTRY_LIST.get(((WorldServer) worldObj).getPlayerChunkMap().getEntry(worldObj.getChunkFromBlockCoords(pos).xPosition, worldObj.getChunkFromBlockCoords(pos).zPosition));
                        LampEnergyMessage message = new LampEnergyMessage(storage.getEnergyStored(), pos);
                        LogHelper.info(EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()));
                        for (EntityPlayerMP player : playerList)
                        {
                            BrightenUp.network.sendTo(message, player);
                        }
                    } catch (IllegalAccessException e)
                    {

                    }
                    worldObj.checkLight(pos);
                    worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
                    this.markDirty();
                    lastLevel = EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored());
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        storage.writeToNBT(compound);
        compound.setFloat("lifetime", lifetime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        storage.readFromNBT(compound);
        lifetime = compound.getFloat("lifetime");
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from)
    {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from)
    {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from)
    {
        return from == EnumFacing.DOWN || from == EnumFacing.UP;
    }

    public EnergyLevel getEnergyLevel()
    {
        return EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public void setEnergyStored(int energy)
    {
        storage.setEnergyStored(energy);
    }
}
