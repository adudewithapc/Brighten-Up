package thatmartinguy.brightenup.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.energy.EnergyLevel;
import thatmartinguy.brightenup.tileentity.TileEntityLamp;
import thatmartinguy.brightenup.util.Reference;

public class BlockLamp extends Block
{
    public static final PropertyEnum<EnergyLevel> ENERGY_LEVEL = PropertyEnum.create("energylevel", EnergyLevel.class);

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
        this.setDefaultState(getBlockState().getBaseState().withProperty(ENERGY_LEVEL, EnergyLevel.EMPTY));

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
        switch(state.getValue(ENERGY_LEVEL))
        {
            case LOW:
                return getBaseLightValue();
            case MEDIUM:
                return getBaseLightValue() * 2;
            case HIGH:
                return getBaseLightValue() * 5;
        }
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder(this).add(ENERGY_LEVEL).build();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        return this.getDefaultState().withProperty(ENERGY_LEVEL, EnergyLevel.EMPTY);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        switch(state.getValue(ENERGY_LEVEL))
        {
            case LOW:
                return 1;
            case MEDIUM:
                return 2;
            case HIGH:
                return 3;
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
