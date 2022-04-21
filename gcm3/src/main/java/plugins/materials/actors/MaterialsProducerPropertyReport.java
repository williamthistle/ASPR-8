package plugins.materials.actors;

import nucleus.ActorContext;
import plugins.materials.datamangers.MaterialsDataManager;
import plugins.materials.events.MaterialsProducerPropertyUpdateEvent;
import plugins.materials.support.MaterialsProducerId;
import plugins.materials.support.MaterialsProducerPropertyId;
import plugins.reports.support.ReportHeader;
import plugins.reports.support.ReportId;
import plugins.reports.support.ReportItem;

/**
 * A Report that displays assigned materials producer property values over time.
 *
 *
 * Fields
 *
 * Time -- the time in days when the materials producer property was set
 *
 * MaterialsProducer -- the materials producer identifier
 *
 * Property -- the region property identifier
 *
 * Value -- the value of the region property
 *
 * @author Shawn Hatch
 *
 */
public final class MaterialsProducerPropertyReport {

	private final ReportId reportId;

	public MaterialsProducerPropertyReport(ReportId reportId) {
		this.reportId = reportId;
	}

	private ReportHeader reportHeader;

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			reportHeader = ReportHeader	.builder()//
										.add("time")//
										.add("materials_producer")//
										.add("property")//
										.add("value")//
										.build();//
		}
		return reportHeader;
	}

	private void handleMaterialsProducerPropertyUpdateEvent(ActorContext actorContext, MaterialsProducerPropertyUpdateEvent materialsProducerPropertyUpdateEvent) {
		MaterialsProducerId materialsProducerId = materialsProducerPropertyUpdateEvent.getMaterialsProducerId();
		MaterialsProducerPropertyId materialsProducerPropertyId = materialsProducerPropertyUpdateEvent.getMaterialsProducerPropertyId();
		Object currentPropertyValue = materialsProducerPropertyUpdateEvent.getCurrentPropertyValue();
		writeProperty(actorContext, materialsProducerId, materialsProducerPropertyId, currentPropertyValue);
	}

	public void init(final ActorContext actorContext) {

		actorContext.subscribe(MaterialsProducerPropertyUpdateEvent.class, this::handleMaterialsProducerPropertyUpdateEvent);

		MaterialsDataManager materialsDataManager = actorContext.getDataManager(MaterialsDataManager.class);

		for (final MaterialsProducerId materialsProducerId : materialsDataManager.getMaterialsProducerIds()) {
			for (final MaterialsProducerPropertyId materialsProducerPropertyId : materialsDataManager.getMaterialsProducerPropertyIds()) {
				final Object materialsProducerPropertyValue = materialsDataManager.getMaterialsProducerPropertyValue(materialsProducerId, materialsProducerPropertyId);
				writeProperty(actorContext, materialsProducerId, materialsProducerPropertyId, materialsProducerPropertyValue);
			}
		}
	}

	private void writeProperty(ActorContext actorContext, final MaterialsProducerId materialsProducerId, final MaterialsProducerPropertyId materialsProducerPropertyId,
			Object materialsProducerPropertyValue) {
		final ReportItem.Builder reportItemBuilder = ReportItem.builder();
		reportItemBuilder.setReportHeader(getReportHeader());
		reportItemBuilder.setReportId(reportId);
		reportItemBuilder.addValue(actorContext.getTime());
		reportItemBuilder.addValue(materialsProducerId.toString());
		reportItemBuilder.addValue(materialsProducerPropertyId.toString());
		reportItemBuilder.addValue(materialsProducerPropertyValue);
		actorContext.releaseOutput(reportItemBuilder.build());
	}

}