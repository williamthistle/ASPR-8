package plugins.materials;

import nucleus.Plugin;
import plugins.materials.datamangers.MaterialsDataManager;
import plugins.regions.RegionPluginId;
import plugins.reports.ReportsPluginId;
import plugins.resources.ResourcesPluginId;

/**
 * A plugin providing a materials data manager to the simulation.
 * 
 * @author Shawn Hatch
 *
 */
public final class MaterialsPlugin {
	private MaterialsPlugin() {
	}

	public static Plugin getMaterialsPlugin(MaterialsPluginData materialsPluginData) {

		return Plugin	.builder()//
						.setPluginId(MaterialsPluginId.PLUGIN_ID)//
						.addPluginData(materialsPluginData)//
						.addPluginDependency(ReportsPluginId.PLUGIN_ID)//
						.addPluginDependency(RegionPluginId.PLUGIN_ID)//
						.addPluginDependency(ResourcesPluginId.PLUGIN_ID)//
						.setInitializer((c) -> {
							MaterialsPluginData pluginData = c.getPluginData(MaterialsPluginData.class).get();
							c.addDataManager(new MaterialsDataManager(pluginData));
						}).build();

	}

}
