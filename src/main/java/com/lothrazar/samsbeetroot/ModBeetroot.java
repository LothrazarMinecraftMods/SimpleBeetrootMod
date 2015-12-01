package com.lothrazar.samsbeetroot;
 
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ModBeetroot.MODID, useMetadata=true , updateJSON = "https://raw.githubusercontent.com/LothrazarMinecraftMods/SimpleBeetrootMod/master/update.json")
public class ModBeetroot
{
    public static final String MODID = "samsbeetroot";
	public static final String TEXTURE_LOCATION = MODID + ":"; 
	
	@Instance(value = MODID)
	public static ModBeetroot instance;	
	@SidedProxy(clientSide="com.lothrazar.samsbeetroot.ClientProxy", serverSide="com.lothrazar.samsbeetroot.CommonProxy")
	public static CommonProxy proxy;  
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{  
 		ItemBlockRegistry.registerItems(); 
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {  
		MinecraftForge.EVENT_BUS.register(instance); 
		proxy.registerRenderers();
    }
    
	@SubscribeEvent
	public void onHoeUse(UseHoeEvent event)
	{  
		//this fires BEFORE the block turns into farmland (is cancellable) so check for grass and dirt, not farmland
		
		Block clicked = event.world.getBlockState(event.pos).getBlock();
		
		if( (clicked == Blocks.grass || clicked == Blocks.dirt ) 
			&& event.world.isAirBlock(event.pos.up()) 
			&& ItemBlockRegistry.beetroot_seed != null
			&& event.world.rand.nextInt(16) == 0) //it is a 1/15 chance
		{			
			if(event.world.isRemote == false)
			{ 
				event.world.spawnEntityInWorld(new EntityItem(event.world, event.pos.getX(),event.pos.getY(),event.pos.getZ()
						, new ItemStack(ItemBlockRegistry.beetroot_seed)));
			}
		}
	}	
}
