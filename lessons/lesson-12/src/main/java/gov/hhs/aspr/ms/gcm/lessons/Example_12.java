package gov.hhs.aspr.ms.gcm.lessons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import gov.hhs.aspr.ms.gcm.lessons.plugins.family.FamilyPlugin;
import gov.hhs.aspr.ms.gcm.lessons.plugins.family.FamilyPluginData;
import gov.hhs.aspr.ms.gcm.lessons.plugins.model.ModelLabel;
import gov.hhs.aspr.ms.gcm.lessons.plugins.model.ModelPlugin;
import gov.hhs.aspr.ms.gcm.lessons.plugins.person.PersonPlugin;
import gov.hhs.aspr.ms.gcm.lessons.plugins.vaccine.VaccinePlugin;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Dimension;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Experiment;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.FunctionalDimension;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.FunctionalDimensionData;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Plugin;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.NIOReportItemHandler;
import gov.hhs.aspr.ms.gcm.simulation.plugins.stochastics.StochasticsPlugin;
import gov.hhs.aspr.ms.gcm.simulation.plugins.stochastics.datamanagers.StochasticsPluginData;
import gov.hhs.aspr.ms.gcm.simulation.plugins.stochastics.support.WellState;

public final class Example_12 {

	private Example_12() {
	}

	/* start code_ref=reports_plugin_family_dimension|code_cap=The family dimension set the maximum family size to four values.*/
	private static Dimension getFamilySizeDimension() {
		FunctionalDimensionData.Builder builder = FunctionalDimensionData.builder();//

		List<Integer> maxFamilySizes = new ArrayList<>();

		maxFamilySizes.add(3);
		maxFamilySizes.add(5);
		maxFamilySizes.add(7);
		maxFamilySizes.add(10);

		for (int i = 0; i < maxFamilySizes.size(); i++) {
			Integer maxFamilySize = maxFamilySizes.get(i);
			builder.addValue("Level_" + i, (context) -> {
				FamilyPluginData.Builder pluginDataBuilder = context
						.getPluginDataBuilder(FamilyPluginData.Builder.class);
				pluginDataBuilder.setMaxFamilySize(maxFamilySize);

				ArrayList<String> result = new ArrayList<>();
				result.add(Double.toString(maxFamilySize));

				return result;
			});//
		}

		builder.addMetaDatum("max_family_size");//

		FunctionalDimensionData functionalDimensionData = builder.build();
		return new FunctionalDimension(functionalDimensionData);

	}
	/* end */

	/* start code_ref=reports_plugin_example_12_plugins|code_cap= Initialization of the various plugins. */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			throw new RuntimeException("One output directory argument is required");
		}
		Path outputDirectory = Paths.get(args[0]);
		if (!Files.exists(outputDirectory)) {
			Files.createDirectory(outputDirectory);
		} else {
			if (!Files.isDirectory(outputDirectory)) {
				throw new IOException("Provided path is not a directory");
			}
		}

		Plugin personPlugin = PersonPlugin.getPersonPlugin();

		Plugin vaccinePlugin = VaccinePlugin.getVaccinePlugin();

		Plugin modelPlugin = ModelPlugin.getModelPlugin();

		WellState wellState = WellState.builder().setSeed(452363456L).build();
		StochasticsPluginData stochasticsPluginData = StochasticsPluginData.builder().setMainRNGState(wellState)
				.build();
		Plugin stochasticsPlugin = StochasticsPlugin.getStochasticsPlugin(stochasticsPluginData);

		FamilyPluginData familyPluginData = FamilyPluginData.builder()//
				.setFamilyCount(30)//
				.setMaxFamilySize(5)//
				.build();
		Plugin familyPlugin = FamilyPlugin.getFamilyPlugin(familyPluginData);

		/* end */

		/* start code_ref=reports_plugin_nio|code_cap=The three reports in this experiment each produce report items and release them as output.  The NIOReportItemHandler is initialized here by indicating the file associated with each report.  */
		NIOReportItemHandler nioReportItemHandler = NIOReportItemHandler.builder()//
				.addReport(ModelLabel.FAMILY_VACCINE_REPORT, outputDirectory.resolve("family_vaccine_report.xls"))//
				.addReport(ModelLabel.HOURLY_VACCINE_REPORT, outputDirectory.resolve("hourly_vaccine_report.xls"))//
				.addReport(ModelLabel.STATELESS_VACCINE_REPORT, outputDirectory.resolve("stateless_vaccine_report.xls"))//
				.build();
		/* end */

		/* start code_ref=reports_plugin_example_12_execution|code_cap=The experiment is executed using the NIOReportItemHandler as an experiment output consumer.*/
		Dimension familySizeDimension = getFamilySizeDimension();

		Experiment.builder()//
				.addPlugin(vaccinePlugin)//
				.addPlugin(familyPlugin)//
				.addPlugin(personPlugin)//
				.addPlugin(modelPlugin)//
				.addPlugin(stochasticsPlugin)//
				.addDimension(familySizeDimension)//
				.addExperimentContextConsumer(nioReportItemHandler)//
				.build()//
				.execute();
		/* end */
	}
}
