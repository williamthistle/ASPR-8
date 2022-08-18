package lessons.lesson_10.plugins.disease;

import nucleus.Plugin;

public final class DiseasePlugin {

	private DiseasePlugin() {

	}

	public static Plugin getDiseasePlugin(DiseasePluginData diseasePluginData) {

		return Plugin	.builder()//
						.addPluginData(diseasePluginData)//
						.setPluginId(DiseasePluginId.PLUGIN_ID)//						
						.setInitializer((c) -> {
							DiseasePluginData pluginData = c.getPluginData(DiseasePluginData.class);
							c.addDataManager(new DiseaseDataManager(pluginData));
						})//
						.build();
	}

}
