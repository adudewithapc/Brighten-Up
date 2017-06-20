package thatmartinguy.brightenup.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thatmartinguy.brightenup.block.BlockLampBase;

public class TileEntityLampBase extends TileEntity implements ITickable, IEnergyReceiver
{
    EnergyStorage storage;
    int loss;
    float lifetime;
    float lowEnergyMultiplier;
    float mediumEnergyMultiplier;
    float highEnergyMultiplier;

    public TileEntityLampBase(int capacity, int loss, float lifetime, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
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
        if(this.worldObj.getBlockState(this.pos).getBlock() instanceof BlockLampBase)
        {
            EnergyLevel previousLevel = this.getEnergyLevel();
            System.out.println(this.getEnergyPercentage());
            storage.modifyEnergyStored(-loss);
            if(this.getEnergyLevel() != previousLevel)
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
                worldObj.notifyBlockOfStateChange(pos, worldObj.getBlockState(pos).getBlock());
            }
            this.markDirty();
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

    public enum EnergyLevel
    {
        EMPTY,
        LOW,
        MEDIUM,
        HIGH
    }
}
