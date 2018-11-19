package com.bartz24.externaltweaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.bartz24.externaltweaker.app.AppFrame;
import com.google.common.base.Strings;

import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class OpenTweakerCmd extends CommandBase implements ICommand {
	private List<String> aliases;

	public OpenTweakerCmd() {
		aliases = new ArrayList<String>();
		aliases.add("extTweaker");
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		return Collections.<String> emptyList();
	}

	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return Collections.<String> emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		new AppFrame(getItemList(), getFluidList(), getOreDictList(), ExternalTweaker.methodList);
	}

	public Object[][] getItemList() {

		NonNullList<ItemStack> stacks = NonNullList.create();

		for (ResourceLocation name : Item.REGISTRY.getKeys()) {
			Item item = (Item) Item.REGISTRY.getObject(name);
			if (item != null) {
				item.getSubItems(CreativeTabs.SEARCH, stacks);
			}
		}
		HashMap stackMappings = new HashMap();
		for (ItemStack s : stacks) {
			if (!s.isEmpty())
				stackMappings.put(new MCItemStack(s).toString(), new MCItemStack(s).getDisplayName());
		}

		Object[][] array = new Object[stackMappings.size()][2];
		Iterator entriesIterator = stackMappings.entrySet().iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {

			Map.Entry mapping = (Map.Entry) entriesIterator.next();
			array[i][0] = mapping.getKey();
			array[i][1] = mapping.getValue();
			i++;
		}
		return array;
	}

	public Object[][] getFluidList() {

		HashMap fluidMappings = new HashMap();
		for (Fluid f : FluidRegistry.getRegisteredFluids().values()) {
			if (f != null) {
				fluidMappings.put("<liquid:" + f.getName() + ">", f.getLocalizedName(new FluidStack(f, 0)));
			}
		}

		Object[][] array = new Object[fluidMappings.size()][2];
		Iterator entriesIterator = fluidMappings.entrySet().iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {

			Map.Entry mapping = (Map.Entry) entriesIterator.next();
			array[i][0] = mapping.getKey();
			array[i][1] = mapping.getValue();
			i++;
		}
		return array;
	}

	public Object[][] getOreDictList() {

		HashMap oreDictMappings = new HashMap();
		for (String o : OreDictionary.getOreNames()) {
			if (!Strings.isNullOrEmpty(o)) {
				oreDictMappings.put("<ore:" + o + ">", o);
			}
		}

		Object[][] array = new Object[oreDictMappings.size()][2];
		Iterator entriesIterator = oreDictMappings.entrySet().iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {

			Map.Entry mapping = (Map.Entry) entriesIterator.next();
			array[i][0] = mapping.getKey();
			array[i][1] = mapping.getValue();
			i++;
		}
		return array;
	}

	@Override
	public String getName() {
		return aliases.get(0);
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return null;
	}
}