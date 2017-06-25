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

import java.lang.reflect.Field;
import java.util.List;

public class TileEntityLamp extends TileEntity implements ITickable, IEnergyReceiver
{
    private EnergyStorage storage;

    private static final Field PLAYER_ENTRY_LIST = ReflectionHelper.findField(PlayerChunkMapEntry.class, "players", "field_187283_c");

    public TileEntityLamp() {}

    public TileEntityLamp(BlockLamp lamp)
    {
        storage = new EnergyStorage(lamp.capacity);
        PLAYER_ENTRY_LIST.setAccessible(true);
    }

    @Override
    public void update()
    {
        if(!worldObj.isRemote)
        {
            if (worldObj.getBlockState(this.pos).getBlock() instanceof BlockLamp)
            {
                BlockLamp lamp = (BlockLamp) worldObj.getBlockState(pos).getBlock();
                EnergyLevel energyLevel = EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored());
                storage.modifyEnergyStored(-lamp.loss);
                if (EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()) != energyLevel)
                {
                    switch (EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()))
                    {
                        case EMPTY:
                            break;
                        case LOW:
                            lamp.lifetime -= 1 * lamp.lowEnergyMultiplier;
                            break;
                        case MEDIUM:
                            lamp.lifetime -= 1 * lamp.mediumEnergyMultiplier;
                            break;
                        case HIGH:
                            lamp.lifetime -= 1 * lamp.highEnergyMultiplier;
                            break;
                    }
                    try
                    {
                        List<EntityPlayerMP> playerList = (List<EntityPlayerMP>) PLAYER_ENTRY_LIST.get(((WorldServer)worldObj).getPlayerChunkMap().getEntry(worldObj.getChunkFromBlockCoords(pos).xPosition, worldObj.getChunkFromBlockCoords(pos).zPosition));
                        LampEnergyMessage message = new LampEnergyMessage(energyLevel, pos);
                        for(EntityPlayerMP player : playerList)
                        {
                            BrightenUp.network.sendTo(message, player);
                        }
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    worldObj.checkLight(pos);
                    worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
                    this.markDirty();
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        storage.writeToNBT(compound);
        if(worldObj.getBlockState(pos).getBlock() instanceof BlockLamp)
            compound.setFloat("lifetime", ((BlockLamp)worldObj.getBlockState(pos).getBlock()).lifetime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        storage.readFromNBT(compound);
        if(worldObj.getBlockState(pos).getBlock() instanceof BlockLamp)
            ((BlockLamp)worldObj.getBlockState(pos).getBlock()).lifetime = compound.getFloat("lifetime");
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
}
