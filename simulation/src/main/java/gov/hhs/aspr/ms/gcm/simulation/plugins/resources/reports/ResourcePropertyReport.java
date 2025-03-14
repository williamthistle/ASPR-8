package gov.hhs.aspr.ms.gcm.simulation.plugins.resources.reports;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.ReportContext;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportHeader;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportItem;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportLabel;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.datamanagers.ResourcesDataManager;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.events.ResourcePropertyDefinitionEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.events.ResourcePropertyUpdateEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.support.ResourceId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.support.ResourcePropertyId;

/**
 * A Report that displays assigned resource property values over time. Fields
 * Time -- the time in days when the resource property was set Resource -- the
 * resource identifier Property -- the resource property identifier Value -- the
 * value of the resource property
 */
public final class ResourcePropertyReport {
	private final ReportLabel reportLabel;

	public ResourcePropertyReport(ResourcePropertyReportPluginData resourcePropertyReportPluginData) {
		this.reportLabel = resourcePropertyReportPluginData.getReportLabel();
	}

	private ReportHeader reportHeader;

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			reportHeader = ReportHeader.builder()//
					.setReportLabel(reportLabel)//
					.add("time")//
					.add("resource")//
					.add("property")//
					.add("value")//
					.build();//
		}
		return reportHeader;
	}

	private void handleResourcePropertyUpdateEvent(ReportContext reportContext,
			ResourcePropertyUpdateEvent resourcePropertyUpdateEvent) {
		ResourceId resourceId = resourcePropertyUpdateEvent.resourceId();
		ResourcePropertyId resourcePropertyId = resourcePropertyUpdateEvent.resourcePropertyId();
		Object currentPropertyValue = resourcePropertyUpdateEvent.currentPropertyValue();
		writeProperty(reportContext, resourceId, resourcePropertyId, currentPropertyValue);
	}

	public void init(final ReportContext reportContext) {

		reportContext.subscribe(ResourcePropertyUpdateEvent.class, this::handleResourcePropertyUpdateEvent);
		reportContext.subscribe(ResourcePropertyDefinitionEvent.class, this::handleResourcePropertyAdditionEvent);
		if (reportContext.stateRecordingIsScheduled()) {
			reportContext.subscribeToSimulationClose(this::recordSimulationState);
		}

		ResourcesDataManager resourcesDataManager = reportContext.getDataManager(ResourcesDataManager.class);
		for (final ResourceId resourceId : resourcesDataManager.getResourceIds()) {
			for (final ResourcePropertyId resourcePropertyId : resourcesDataManager
					.getResourcePropertyIds(resourceId)) {
				Object resourcePropertyValue = resourcesDataManager.getResourcePropertyValue(resourceId,
						resourcePropertyId);
				writeProperty(reportContext, resourceId, resourcePropertyId, resourcePropertyValue);
			}
		}

		// release report header
		reportContext.releaseOutput(getReportHeader());
	}

	private void recordSimulationState(ReportContext reportContext) {
		ResourcePropertyReportPluginData.Builder builder = ResourcePropertyReportPluginData.builder();
		builder.setReportLabel(reportLabel);
		reportContext.releaseOutput(builder.build());
	}

	private void handleResourcePropertyAdditionEvent(ReportContext reportContext,
			ResourcePropertyDefinitionEvent resourcePropertyDefinitionEvent) {
		ResourceId resourceId = resourcePropertyDefinitionEvent.resourceId();
		ResourcePropertyId resourcePropertyId = resourcePropertyDefinitionEvent.resourcePropertyId();
		Object resourcePropertyValue = resourcePropertyDefinitionEvent.resourcePropertyValue();
		writeProperty(reportContext, resourceId, resourcePropertyId, resourcePropertyValue);
	}

	private void writeProperty(ReportContext reportContext, final ResourceId resourceId,
			final ResourcePropertyId resourcePropertyId, Object resourcePropertyValue) {
		final ReportItem.Builder reportItemBuilder = ReportItem.builder();
		reportItemBuilder.setReportLabel(reportLabel);

		reportItemBuilder.addValue(reportContext.getTime());
		reportItemBuilder.addValue(resourceId.toString());
		reportItemBuilder.addValue(resourcePropertyId.toString());
		reportItemBuilder.addValue(resourcePropertyValue);
		reportContext.releaseOutput(reportItemBuilder.build());
	}

}