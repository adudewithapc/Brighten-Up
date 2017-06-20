package thatmartinguy.brightenup.item;

import net.minecraft.item.Item;
import thatmartinguy.brightenup.BrightenUp;
import thatmartinguy.brightenup.util.Reference;

public class ItemBase extends Item
{
    public ItemBase(String name)
    {
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setCreativeTab(BrightenUp.tabBrightenUp);
    }

    @Override
    public Item setUnlocalizedName(String unlocalizedName)
    {
        return super.setUnlocalizedName(Reference.MOD_ID + ":" + unlocalizedName);
    }
}
