package com.lothrazar.samsbeetroot;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry; 

public class ItemBlockRegistry 
{ 
	public static ArrayList<Item> items = new ArrayList<Item>();

	public static Item beetroot_seed ;
	public static Item beetrootItem;
	public static Item beetrootSoup;	
 
	public static BlockCropBeetroot beetroot_crop;
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	
	public static void registerBlock(Block s, String name)
	{    
		s.setUnlocalizedName(name); 
		 
		GameRegistry.registerBlock(s, name);
		  
		blocks.add(s);
	}
	public static void registerItems()
	{     
		beetroot_crop =  new BlockCropBeetroot();

		registerBlock(beetroot_crop, "beetroot_crop"); 
		
		beetroot_seed = new ItemSeeds(beetroot_crop, Blocks.farmland).setCreativeTab(CreativeTabs.tabFood);
		ItemBlockRegistry.registerItem(beetroot_seed, "beetroot_seed");
		
		beetrootItem = new ItemFood(3, false).setCreativeTab(CreativeTabs.tabFood);
		ItemBlockRegistry.registerItem(beetrootItem, "beetroot_item");
	
		beetrootSoup = new ItemSoup(8).setCreativeTab(CreativeTabs.tabFood);
		ItemBlockRegistry.registerItem(beetrootSoup, "beetroot_soup");
		
		GameRegistry.addRecipe(new ItemStack(beetrootSoup), 
				"bbb", 
				"bbb",
				" u ", 
				'b', beetrootItem, 
				'u', Items.bowl
				); 
	}
	
	public static void registerItem(Item item, String name)
	{ 
		 item.setUnlocalizedName(name);
		 
		 GameRegistry.registerItem(item, name);
		 
		 items.add(item);
	}
}
