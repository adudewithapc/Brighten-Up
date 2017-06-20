package thatmartinguy.brightenup.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.tileentity.TileEntityLampBase;
import thatmartinguy.brightenup.util.Reference;

public class BlockLampBase extends Block implements ITileEntityProvider
{
    int capacity;
    int loss;
    float lifetime;
    float lowEnergyMultiplier;
    float mediumEnergyMultiplier;
    float highEnergyMultiplier;

    public BlockLampBase(Material material, String name, float lightLevel, float lifetime, int capacity, int loss)
    {
        this(material, name, lightLevel, lifetime, capacity, loss, 1, 2, 3);
    }

    public BlockLampBase(Material material, String name, float lightLevel ,float lifetime, int capacity, int loss, float lowEnergyMultiplier, float mediumEnergyMultiplier, float highEnergyMultiplier)
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
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityLampBase(capacity, loss, lifetime, lowEnergyMultiplier, mediumEnergyMultiplier, highEnergyMultiplier);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if(world.getTileEntity(pos) instanceof TileEntityLampBase)
        {
            TileEntityLampBase tileEntity = (TileEntityLampBase) world.getTileEntity(pos);
            if(tileEntity.getEnergyLevel() == TileEntityLampBase.EnergyLevel.EMPTY)
            {
                return 0;
            }
            else if(tileEntity.getEnergyLevel() == TileEntityLampBase.EnergyLevel.LOW)
            {
                return super.getLightValue(state, world, pos);
            }
            else if(tileEntity.getEnergyLevel() == TileEntityLampBase.EnergyLevel.MEDIUM)
            {
                return super.getLightValue(state, world, pos) * 3;
            }
            return super.getLightValue(state, world, pos) * 5;
        }
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if(worldIn.getTileEntity(pos) instanceof TileEntityLampBase)
        {
            worldIn.removeTileEntity(pos);
        }
    }

    @Override
    public Block setUnlocalizedName(String name)
    {
        return super.setUnlocalizedName(Reference.MOD_ID + ":" + name);
    }
}
