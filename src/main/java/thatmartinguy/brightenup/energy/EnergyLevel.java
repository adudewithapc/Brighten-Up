package thatmartinguy.brightenup.energy;

import net.minecraft.util.IStringSerializable;

public enum EnergyLevel implements IStringSerializable {
    EMPTY("empty"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String name;

    EnergyLevel(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public static EnergyLevel getEnergyLevel(int energy, int capacity)
    {
        float energyPercentage = (energy * 100) / capacity;
        if(energyPercentage <= 0)
        {
            return EMPTY;
        }
        else if(energyPercentage <= 40)
        {
            return LOW;
        }
        else if(energyPercentage > 40 && energyPercentage < 60)
        {
            return MEDIUM;
        }
        else
        {
            return HIGH;
        }
    }
}