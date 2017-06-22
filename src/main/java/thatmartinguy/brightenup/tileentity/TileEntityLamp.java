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
import thatmartinguy.brightenup.network.LampEnergyMessage;

import java.lang.reflect.Field;
import java.util.List;

import static thatmartinguy.brightenup.tileentity.TileEntityLamp.EnergyLevel.EMPTY;

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

    public TileEntityLamp(int capacity, int loss, float lifetime, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
    {
        storage = new EnergyStorage(capacity);
        this.loss = loss;
        this.lifetime = lifetime;
        this.lowEnergyMultiplier = lowEnergyMultiplier;
        this.mediumEnergyMultiplier = mediumEnergyMultiplier;
        this.highEnergyMultiplier = highEnergyMultiplier;
        this.energyLevel = EMPTY;
    }

    @Override
    public void update()
    {
        if(!worldObj.isRemote)
        {
            if (worldObj.getBlockState(this.pos).getBlock() instanceof BlockLamp)
            {
                EnergyLevel previousLevel = this.getEnergyLevel();
                storage.modifyEnergyStored(-loss);
                if (this.getEnergyLevel() != previousLevel)
                {
                    switch (this.getEnergyLevel())
                    {
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
                    List<EntityPlayerMP> playerList = (List<EntityPlayerMP>) PLAYER_ENTRY_LIST.getGenericType();
                    for(int i = 0; i < playerList.size(); i++)
                    {
                        BrightenUp.network.sendTo(new LampEnergyMessage(storage.getEnergyStored(), pos), playerList.get(i));
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
        energyLevel = EnergyLevel.valueOf(compound.getString("energyLevel"));
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

    private float getEnergyPercentage()
    {
        return (storage.getEnergyStored() * 100) / storage.getMaxEnergyStored();
    }

    public EnergyLevel getEnergyLevel()
    {
        if(this.getEnergyPercentage() <= 0)
        {
            this.energyLevel = EMPTY;
        }
        else if(this.getEnergyPercentage() <= 40)
        {
            this.energyLevel = EnergyLevel.LOW;
        }
        else if(this.getEnergyPercentage() > 40 && this.getEnergyPercentage() < 60)
        {
            this.energyLevel = EnergyLevel.MEDIUM;
        }
        else
        {
            this.energyLevel = EnergyLevel.HIGH;
        }
        return energyLevel;
    }

    public enum EnergyLevel
    {
        EMPTY,
        LOW,
        MEDIUM,
        HIGH
    }
}
