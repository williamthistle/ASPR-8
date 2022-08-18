package lessons.lesson_08.plugins.disease;

import nucleus.Plugin;

 public final class DiseasePlugin {

	private DiseasePlugin() {

	}

	public static Plugin getDiseasePlugin(DiseasePluginData diseasePluginData) {

		return Plugin	.builder()//
						.addPluginData(diseasePluginData)//
						.setPluginId(DiseasePluginId.PLUGIN_ID)//						
						.setInitializer((pluginContext) -> {
							DiseasePluginData pluginData = pluginContext.getPluginData(DiseasePluginData.class);
							pluginContext.addDataManager(new DiseaseDataManager(pluginData));
						})//
						.build();
	}

 }
 
 
 
