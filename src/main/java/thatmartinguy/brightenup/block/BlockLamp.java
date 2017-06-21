package thatmartinguy.brightenup.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.tileentity.TileEntityLamp;
import thatmartinguy.brightenup.util.Reference;

public class BlockLamp extends Block
{
    int capacity;
    int loss;
    float lifetime;
    float lowEnergyMultiplier;
    float mediumEnergyMultiplier;
    float highEnergyMultiplier;

    public BlockLamp(Material material, String name, float lightLevel, float lifetime, int capacity, int loss)
    {
        this(material, name, lightLevel, lifetime, capacity, loss, 1, 2, 3);
    }

    public BlockLamp(Material material, String name, float lightLevel , float lifetime, int capacity, int loss, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
    {
        super(material);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setCreativeTab(BrightenUp.tabBrightenUp);
        this.setLightLevel(lightLevel);

        this.loss = loss;
        this.capacity = capacity;
        this.lifetime = lifetime;
        this.lowEnergyMultiplier = lowEnergyMultiplier;
        this.mediumEnergyMultiplier = mediumEnergyMultiplier;
        this.highEnergyMultiplier = highEnergyMultiplier;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityLamp(capacity, loss, lifetime, lowEnergyMultiplier, mediumEnergyMultiplier, highEnergyMultiplier);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if(worldIn.getTileEntity(pos) instanceof TileEntityLamp)
        {
            worldIn.removeTileEntity(pos);
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof TileEntityLamp)
        {
            TileEntityLamp tileEntity = (TileEntityLamp) world.getTileEntity(pos);
            TileEntityLamp.EnergyLevel energyLevel = tileEntity.getEnergyLevel();

            switch (energyLevel)
            {
                case LOW:
                    return getBaseLightValue();
                case MEDIUM:
                    return getBaseLightValue() * 3;
                case HIGH:
                    return getBaseLightValue() * 5;
                default:
                    return 0;
            }
        }
        return 0;
    }

    @Override
    public Block setUnlocalizedName(String name)
    {
        return super.setUnlocalizedName(Reference.MOD_ID + ":" + name);
    }

    public int getBaseLightValue()
    {
        return this.lightValue;
    }
}