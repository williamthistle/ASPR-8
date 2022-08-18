package lessons.lesson_10.plugins.disease;

import nucleus.PluginId;
import nucleus.SimplePluginId;
/**
 * Static plugin id implementation for the GlobalsPlugin
 * 
 * @author Shawn Hatch
 *
 */

public final class DiseasePluginId implements PluginId {
	private DiseasePluginId() {};
	public final static PluginId PLUGIN_ID = new SimplePluginId("disease plugin id");
	
}
