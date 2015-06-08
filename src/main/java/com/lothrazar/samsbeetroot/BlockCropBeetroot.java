package com.lothrazar.samsbeetroot;

import java.util.List;
import java.util.Random;
 
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCropBeetroot extends BlockBush implements IGrowable
{
	private static int LIGHT = 9;
	private static int GROWTHMAX = 3;//wheat uses 7 but carrot/potato has 7
	private static int MAXDROPPED = 3;
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, GROWTHMAX);

	public BlockCropBeetroot()
	{
		setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
		setTickRandomly(true);
		setStepSound(Block.soundTypeGrass);
		setCreativeTab((CreativeTabs)null);
		setHardness(0.0F);
		
		float size = 0.5F;
		setBlockBounds(0.5F - size, 0.0F, 0.5F - size, 0.5F + size, 0.25F, 0.5F + size);
		
		disableStats();
	}

	@Override
	protected boolean canPlaceBlockOn(Block ground)
	{
		return ground == Blocks.farmland; 
	}
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(worldIn, pos, state, rand);
		if (worldIn.getLightFromNeighbors(pos.up()) >= LIGHT)
		{
			int i = ((Integer)state.getValue(AGE)).intValue();
			if (i < GROWTHMAX)
			{
				float f = getGrowthChance(this, worldIn, pos);
				if (rand.nextInt((int)(25.0F / f) + 1) == 0) 
				{
				  worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
				}
			}
		}
	}
	 
	public static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos)
	{
		//i didnt write this raw, just imported it from source and tweaked it
	    float f = 1.0F;
	    BlockPos blockpos1 = pos.down();
	    for (int i = -1; i <= 1; i++) 
	    {
			for (int j = -1; j <= 1; j++)
			{
				float f1 = 0.0F;
				IBlockState iblockstate = worldIn.getBlockState(blockpos1.add(i, 0, j));
				if (iblockstate.getBlock().canSustainPlant(worldIn, blockpos1.add(i, 0, j), EnumFacing.UP, (IPlantable)blockIn))
				{
					f1 = 1.0F;
					if (iblockstate.getBlock().isFertile(worldIn, blockpos1.add(i, 0, j))) 
					{
					  f1 = 3.0F;
					}
				}
				if ((i != 0) || (j != 0)) 
				{
				  f1 /= 4.0F;
				}
				f += f1;
			}
	    }
	    BlockPos north = pos.north();
	    BlockPos south = pos.south();
	    BlockPos west = pos.west();
	    BlockPos east = pos.east();
	    boolean flag = (blockIn == worldIn.getBlockState(west).getBlock()) || (blockIn == worldIn.getBlockState(east).getBlock());
	    boolean flag1 = (blockIn == worldIn.getBlockState(north).getBlock()) || (blockIn == worldIn.getBlockState(south).getBlock());
	    if ((flag) && (flag1))
	    {
	    	f /= 2.0F;
	    }
	    else
	    {
	      boolean flag2 = (blockIn == worldIn.getBlockState(west.north()).getBlock()) || (blockIn == worldIn.getBlockState(east.north()).getBlock()) || (blockIn == worldIn.getBlockState(east.south()).getBlock()) || (blockIn == worldIn.getBlockState(west.south()).getBlock());
	      if (flag2) 
	      {
	    	  f /= 2.0F;
	      }
	    }
	    return f;
	}
	
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		return worldIn.getLight(pos) >= LIGHT - 1 || //should this be AND?  meaning it needs light AND water?
			 worldIn.getBlockState(pos.down()).getBlock().canSustainPlant(worldIn, pos.down(), EnumFacing.UP, this);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World worldIn, BlockPos pos)
	{
		return ItemBlockRegistry.beetroot_seed;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return ((Integer)state.getValue(AGE)).intValue() == GROWTHMAX ? ItemBlockRegistry.beetrootItem: ItemBlockRegistry.beetroot_seed;
	}
	
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state,	boolean isClient) 
	{
		return ((Integer)state.getValue(AGE)).intValue() < GROWTHMAX;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
		int age = ((Integer)state.getValue(AGE)).intValue();
		Random rand = (world instanceof World) ? ((World)world).rand : new Random();
		if (age >= GROWTHMAX)
		{
			int k = MAXDROPPED + fortune;
			for (int i = 0; i < MAXDROPPED + fortune; i++) 
			{
				if (rand.nextInt(15) <= age) 
				{
					ret.add(new ItemStack(ItemBlockRegistry.beetrootItem)); 
					int seeds = rand.nextInt(3);//0,1,2 only
					if(seeds > 0)
						ret.add(new ItemStack(ItemBlockRegistry.beetroot_seed,seeds)); 
				}
			}
		}

		return ret;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos,	IBlockState state) 
	{ 
		return ((Integer)state.getValue(AGE)).intValue() < GROWTHMAX;
	}
	
	public IBlockState getStateFromMeta(int meta)
	{
	  return getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}
	
	public int getMetaFromState(IBlockState state)
	{
		return ((Integer)state.getValue(AGE)).intValue();
	}
	
	public BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { AGE });
	}
	
	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
	    int age = ((Integer)state.getValue(AGE)).intValue() + 1;
	    
		if (age > GROWTHMAX) 
		{
			age = GROWTHMAX;
		}
		 
		//the 2 is a notification flag
		worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(age)), 2);
	}
}
