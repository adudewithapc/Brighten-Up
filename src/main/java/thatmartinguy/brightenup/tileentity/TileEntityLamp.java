package thatmartinguy.brightenup.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.EnumSkyBlock;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.block.BlockLamp;
import thatmartinguy.brightenup.network.LampEnergyMessage;

public class TileEntityLamp extends TileEntity implements ITickable, IEnergyReceiver
{
    EnergyStorage storage;
    int loss;
    float lifetime;
    float lowEnergyMultiplier;
    float mediumEnergyMultiplier;
    float highEnergyMultiplier;

    public TileEntityLamp(int capacity, int loss, float lifetime, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
    {
        storage = new EnergyStorage(capacity);
        this.loss = loss;
        this.lifetime = lifetime;
        this.lowEnergyMultiplier = lowEnergyMultiplier;
        this.mediumEnergyMultiplier = mediumEnergyMultiplier;
        this.highEnergyMultiplier = highEnergyMultiplier;
    }

    @Override
    public void update()
    {
        if(!this.worldObj.isRemote)
        {
            if (this.worldObj.getBlockState(this.pos).getBlock() instanceof BlockLamp)
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
                    BrightenUp.network.sendToAll(new LampEnergyMessage(storage.getEnergyStored(), pos));
                    this.worldObj.checkLight(pos);
                    this.worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 3);
                }
                this.markDirty();
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

    private float getEnergyPercentage()
    {
        return (storage.getEnergyStored() * 100) / storage.getMaxEnergyStored();
    }

    public EnergyLevel getEnergyLevel()
    {
        if(this.getEnergyPercentage() <= 40)
        {
            return EnergyLevel.LOW;
        }
        else if(this.getEnergyPercentage() > 40 && this.getEnergyPercentage() < 60)
        {
            return EnergyLevel.MEDIUM;
        }
        else if(this.getEnergyPercentage() <= 0)
        {
            return EnergyLevel.EMPTY;
        }
        else
        {
            return EnergyLevel.HIGH;
        }
    }

    public static float getEnergyPercentage(int capacity, int energy)
    {
        return (energy * 100) / capacity;
    }

    public enum EnergyLevel
    {
        EMPTY,
        LOW,
        MEDIUM,
        HIGH
    }
}
