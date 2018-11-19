package com.bartz24.externaltweaker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@Mod(modid = ExternalTweaker.MODID, name = ExternalTweaker.MODNAME, version = ExternalTweaker.VERSION, dependencies = "required-after:crafttweaker", useMetadata = true)
public class ExternalTweaker {
	public static final String MODID = "externaltweaker";
	public static final String MODNAME = "External Tweaker";
	public static final String VERSION = "0.3";

	public static List<String> methodList;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		loadMethodList(event);
	}

	@Mod.EventHandler
	public void serverLoading(FMLServerStartingEvent event) {
		event.registerServerCommand(new OpenTweakerCmd());
	}

	@SuppressWarnings("finally")
	public void loadMethodList(FMLPreInitializationEvent event) {
		List<String> methods = new ArrayList();
		Set<ASMDataTable.ASMData> asmDatas = event.getAsmData().getAll(ZenClass.class.getCanonicalName());
		for (ASMDataTable.ASMData asmData : asmDatas) {
			Class zenClass;
			try {
				zenClass = Class.forName(asmData.getClassName());
				for (Method method : zenClass.getMethods()) {
					if (method.isAnnotationPresent(ZenMethod.class)) {
						String param = "";
						for (int i = 0; i < method.getParameterTypes().length; i++) {
							if (i > 0)
								param += ", ";
							Class paramType = method.getParameterTypes()[i];
							param += (hasOptAnnot(method, i) ? "optional." : "") + paramType.getSimpleName();
						}
						String methodForThing = getClassName(
								((ZenClass) zenClass.getAnnotation(ZenClass.class)).value()) + "." + method.getName()
								+ "(" + param + ");";
						if (!isBlacklisted(methodForThing))
							methods.add(methodForThing);
					}
				}
			} finally {
				continue;
			}
		}
		methodList = methods;
	}

	private boolean hasOptAnnot(Method method, int paramIndex) {
		for (int a = 0; a < method.getParameterAnnotations()[paramIndex].length; a++) {
			if (method.getParameterAnnotations()[paramIndex][a] instanceof Optional)
				return true;
		}
		return false;
	}

	private String getClassName(String name) {
		String check = "crafttweaker.";
		if (name.startsWith(check + "recipes.IRecipeManager"))
			return "recipes";
		else if (name.startsWith(check + "crafttweaker.recipes.IFurnaceManager"))
			return "furnace";
		return name;
	}

	private boolean isBlacklisted(String name) {
		String check = "crafttweaker.";
		if (name.startsWith(check)) {
			name = name.substring(check.length());
			if (name.startsWith("event") || name.startsWith("data") || name.startsWith("game")
					|| name.startsWith("container") || name.startsWith("container") || name.startsWith("entity")
					|| name.startsWith("liquid") || name.startsWith("item") || name.startsWith("block")
					|| name.startsWith("vanilla") || name.startsWith("server") || name.startsWith("world")
					|| name.startsWith("recipes") || name.startsWith("player") || name.startsWith("IMineTweaker")
					|| name.startsWith("oredict") || name.startsWith("enchantments") || name.startsWith("damage")
					|| name.startsWith("mods") || name.startsWith("potions") || name.startsWith("util")
					|| name.startsWith("creativetabs") || name.startsWith("command")) {
				return true;
			}
		}
		if(name.startsWith("mods.contenttweaker"))
			return true;
		return false;
	}
}
