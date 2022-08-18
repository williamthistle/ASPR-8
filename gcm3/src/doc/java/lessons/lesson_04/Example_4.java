package lessons.lesson_04;

import nucleus.Plugin;
import nucleus.PluginId;
import nucleus.SimplePluginId;
import nucleus.Simulation;

public final class Example_4 {

	private Example_4() {
	}

	/**
	 * Introducing the addition of a data manager. Note that we are adding the
	 * actor now via a method reference. The data manager will have two values,
	 * Alpha and Beta. We also introduce the ability of the Actor to plan.
	 */
	
	
	public static void main(String[] args) {

		PluginId pluginId = new SimplePluginId("example plugin");

		Plugin plugin = Plugin	.builder()//
								.setPluginId(pluginId)//
								.setInitializer(pluginContext -> {
									pluginContext.addActor(new ExampleActor()::init);
									pluginContext.addDataManager(new ExampleDataManager());
								})//
								.build();

		Simulation	.builder()//
					.addPlugin(plugin)//
					.build()//
					.execute();
	}
	
	
}
