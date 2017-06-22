package thatmartinguy.brightenup.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.block.BlockLamp;
import thatmartinguy.brightenup.energy.EnergyLevel;
import thatmartinguy.brightenup.network.LampEnergyMessage;

import java.lang.reflect.Field;
import java.util.List;

import static thatmartinguy.brightenup.energy.EnergyLevel.*;

public class TileEntityLamp extends TileEntity implements ITickable, IEnergyReceiver
{
    EnergyStorage storage;
    int loss;
    float lifetime;
    float lowEnergyMultiplier;
    float mediumEnergyMultiplier;
    float highEnergyMultiplier;
    EnergyLevel energyLevel;

    static final Field PLAYER_ENTRY_LIST = ReflectionHelper.findField(PlayerChunkMapEntry.class, "players", "field_187283_c");

    public TileEntityLamp() {}

    public TileEntityLamp(int capacity, int loss, float lifetime, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
    {
        storage = new EnergyStorage(capacity);
        this.loss = loss;
        this.lifetime = lifetime;
        this.lowEnergyMultiplier = lowEnergyMultiplier;
        this.mediumEnergyMultiplier = mediumEnergyMultiplier;
        this.highEnergyMultiplier = highEnergyMultiplier;
        this.energyLevel = EMPTY;
        PLAYER_ENTRY_LIST.setAccessible(true);
    }

    @Override
    public void update()
    {
        if(!worldObj.isRemote)
        {
            if (worldObj.getBlockState(this.pos).getBlock() instanceof BlockLamp)
            {
                EnergyLevel previousLevel = EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored());
                storage.modifyEnergyStored(-loss);
                if (EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()) != previousLevel)
                {
                    switch (getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()))
                    {
                        case EMPTY:
                            break;
                        case LOW:
                            this.lifetime -= 1 * lowEnergyMultiplier;
                            break;
                        case MEDIUM:
                            this.lifetime -= 1 * mediumEnergyMultiplier;
                            break;
                        case HIGH:
                            this.lifetime -= 1 * highEnergyMultiplier;
                            break;
                    }
                    try
                    {
                        List<EntityPlayerMP> playerList = (List<EntityPlayerMP>) PLAYER_ENTRY_LIST.get(worldObj.getMinecraftServer().worldServerForDimension(worldObj.provider.getDimension()).getPlayerChunkMap().getEntry(worldObj.getChunkFromBlockCoords(pos).xPosition, worldObj.getChunkFromBlockCoords(pos).zPosition));
                        for(int i = 0; i < playerList.size(); i++)
                        {
                            BrightenUp.network.sendTo(new LampEnergyMessage(EnergyLevel.getEnergyLevel(storage.getEnergyStored(), storage.getMaxEnergyStored()).toString(), pos), playerList.get(i));
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
        compound.setFloat("lifetime", lifetime);
        compound.setString("energyLevel", energyLevel.toString());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        storage.readFromNBT(compound);
        lifetime = compound.getFloat("lifetime");
        energyLevel = valueOf(compound.getString("energyLevel").toUpperCase());
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
}
