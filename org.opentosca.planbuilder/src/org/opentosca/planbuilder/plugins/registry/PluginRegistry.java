package org.opentosca.planbuilder.plugins.registry;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;

/**
 * <p>
 * This class is the registry for all plugins of the PlanBuilder
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PluginRegistry {
	
	private static List<IPlanBuilderPostPhasePlugin> postPhasePlugins = new ArrayList<IPlanBuilderPostPhasePlugin>();
	private static List<IPlanBuilderPrePhaseDAPlugin> prePhaseDaPlugins = new ArrayList<IPlanBuilderPrePhaseDAPlugin>();
	private static List<IPlanBuilderPrePhaseIAPlugin> prePhaseIAPlugins = new ArrayList<IPlanBuilderPrePhaseIAPlugin>();
	private static List<IPlanBuilderProvPhaseOperationPlugin> provPhaseOperationPlugins = new ArrayList<IPlanBuilderProvPhaseOperationPlugin>();
	private static List<IPlanBuilderTypePlugin> genericPlugins = new ArrayList<IPlanBuilderTypePlugin>();
	private static List<IScalingPlanBuilderSelectionPlugin> selectionPlugins = new ArrayList<IScalingPlanBuilderSelectionPlugin>();
	
	
	
	/**
	 * Returns all registered GenericPlugins
	 * 
	 * @return a List of IPlanBuilderTypePlugin
	 */
	public static List<IPlanBuilderTypePlugin> getGenericPlugins() {
		return PluginRegistry.genericPlugins;
	}
	
	/**
	 * Returns all registered ProvPhasePlugins
	 * 
	 * @return a List of IPlanBuilderProvPhaseOperationPlugin
	 */
	public static List<IPlanBuilderProvPhaseOperationPlugin> getProvPlugins() {
		return PluginRegistry.provPhaseOperationPlugins;
	}
	
	/**
	 * Returns all registered PrePhaseIAPlugins
	 * 
	 * @return a List of IPlanBuilderPrePhaseIAPlugin
	 */
	public static List<IPlanBuilderPrePhaseIAPlugin> getIaPlugins() {
		return PluginRegistry.prePhaseIAPlugins;
	}
	
	/**
	 * Returns all registered PrePhaseDAPlugins
	 * 
	 * @return a List of IPlanBuilderPrePhaseDAPlugin
	 */
	public static List<IPlanBuilderPrePhaseDAPlugin> getDaPlugins() {
		return PluginRegistry.prePhaseDaPlugins;
	}
	
	/**
	 * Returns all registered PostPhasePlugins
	 * 
	 * @return a List of IPlanBuilderPostPhasePlugin
	 */
	public static List<IPlanBuilderPostPhasePlugin> getPostPlugins() {
		return PluginRegistry.postPhasePlugins;
	}
	
	/**
	 * Returns all registered SelectionPlugins
	 * 
	 * @return a List of IScalingPlanBuilderSelectionPlugin
	 */
	public static List<IScalingPlanBuilderSelectionPlugin> getSelectionPlugins() {
		return PluginRegistry.selectionPlugins;
	}
	
	/**
	 * Registers a PostPhasePlugin in this registry
	 * 
	 * @param postPhasePlugin a IPlanBuilderPostPhasePlugin to register
	 */
	protected static void bindPostPhasePlugin(IPlanBuilderPostPhasePlugin postPhasePlugin) {
		PluginRegistry.postPhasePlugins.add(postPhasePlugin);
	}
	
	/**
	 * De-registers a PostPhasePlugin in this registry
	 * 
	 * @param postPhasePlugin a IPlanBuilderPostPhasePlugin to de-register
	 */
	protected static void unbindPostPhasePlugin(IPlanBuilderPostPhasePlugin postPhasePlugin) {
		IPlanBuilderPostPhasePlugin toRemove = null;
		for (IPlanBuilderPostPhasePlugin plugin : PluginRegistry.postPhasePlugins) {
			if (plugin.getID().equals(postPhasePlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.postPhasePlugins.remove(toRemove);
		}
	}
	
	/**
	 * Registers a PrePhaseDAPlugin in this registry
	 * 
	 * @param prePhaseDaPlugin a IPlanBuilderPrePhaseDAPlugin to register
	 */
	protected void bindPrePhaseDaPlugin(IPlanBuilderPrePhaseDAPlugin prePhaseDaPlugin) {
		PluginRegistry.prePhaseDaPlugins.add(prePhaseDaPlugin);
	}
	
	/**
	 * De-registers a PrePhaseDAPlugin in this registry
	 * 
	 * @param prePhaseDaPlugin a IPlanBuilderPrePhaseDAPlugin to de-register
	 */
	protected static void unbindPrePhaseDaPlugin(IPlanBuilderPrePhaseDAPlugin prePhaseDaPlugin) {
		IPlanBuilderPrePhaseDAPlugin toRemove = null;
		for (IPlanBuilderPrePhaseDAPlugin plugin : PluginRegistry.prePhaseDaPlugins) {
			if (plugin.getID().equals(prePhaseDaPlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.prePhaseDaPlugins.remove(toRemove);
		}
	}
	
	/**
	 * Registers a PrePhaseIAPlugin in this registry
	 * 
	 * @param prePhaseIaPlugin a IPlanBuilderPrePhaseIAPlugin to register
	 */
	protected static void bindPrePhaseIaPlugin(IPlanBuilderPrePhaseIAPlugin prePhaseIaPlugin) {
		PluginRegistry.prePhaseIAPlugins.add(prePhaseIaPlugin);
	}
	
	/**
	 * De-registers a PrePhaseIAPlugin in this registry
	 * 
	 * @param prePhaseIaPlugin a IPlanBuilderPrePhaseIAPlugin to de-register
	 */
	protected static void unbindPrePhaseIaPlugin(IPlanBuilderPrePhaseIAPlugin prePhaseIaPlugin) {
		IPlanBuilderPrePhaseIAPlugin toRemove = null;
		for (IPlanBuilderPrePhaseIAPlugin plugin : PluginRegistry.prePhaseIAPlugins) {
			if (plugin.getID().equals(prePhaseIaPlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.prePhaseIAPlugins.remove(toRemove);
		}
	}
	
	/**
	 * Registers a ProvPhasePlugin in this registry
	 * 
	 * @param provPhasePlugin a IPlanBuilderProvPhaseOperationPlugin to register
	 */
	protected static void bindProvPhaseOperationPlugin(IPlanBuilderProvPhaseOperationPlugin provPhasePlugin) {
		PluginRegistry.provPhaseOperationPlugins.add(provPhasePlugin);
	}
	
	/**
	 * De-registers a ProvPhasePlugin in this registry
	 * 
	 * @param provPhasePlugin a IPlanBuilderProvPhaseOperationPlugin to
	 *            de-register
	 */
	protected static void unbindProvPhaseOperationPlugin(IPlanBuilderProvPhaseOperationPlugin provPhasePlugin) {
		IPlanBuilderProvPhaseOperationPlugin toRemove = null;
		for (IPlanBuilderProvPhaseOperationPlugin plugin : PluginRegistry.provPhaseOperationPlugins) {
			if (plugin.getID().equals(provPhasePlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.provPhaseOperationPlugins.remove(toRemove);
		}
	}
	
	/**
	 * Registers a GenericPlugin in this registry
	 * 
	 * @param genericPlugin a IPlanBuilderTypePlugin to register
	 */
	protected static void bindGenericPlugin(IPlanBuilderTypePlugin genericPlugin) {
		PluginRegistry.genericPlugins.add(genericPlugin);
	}
	
	/**
	 * De-registers a GenericPlugin in this registry
	 * 
	 * @param genericPlugin a IPlanBuilderTypePlugin to de-register
	 */
	protected static void unbindGenericPlugin(IPlanBuilderTypePlugin genericPlugin) {
		IPlanBuilderTypePlugin toRemove = null;
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.genericPlugins) {
			if (plugin.getID().equals(genericPlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.genericPlugins.remove(toRemove);
		}
	}
	
	/**
	 * Registers a SelectionPlugin in this registry
	 * 
	 * @param selectionPlugin a IScalingPlanBuilderSelectionPlugin to register
	 */
	protected static void bindSelectionPlugin(IScalingPlanBuilderSelectionPlugin selectionPlugin) {
		PluginRegistry.selectionPlugins.add(selectionPlugin);
	}
	
	/**
	 * De-registers a SelectionPlugin in this registry
	 * 
	 * @param selectionPlugin a IScalingPlanBuilderSelectionPlugin to de-register
	 */
	protected static void unbindSelectionPlugin(IScalingPlanBuilderSelectionPlugin selectionPlugin) {
		IScalingPlanBuilderSelectionPlugin toRemove = null;
		for (IScalingPlanBuilderSelectionPlugin plugin : PluginRegistry.selectionPlugins) {
			if (plugin.getID().equals(selectionPlugin.getID())) {
				toRemove = plugin;
			}
		}
		if (toRemove != null) {
			PluginRegistry.selectionPlugins.remove(toRemove);
		}
	}
	
}
