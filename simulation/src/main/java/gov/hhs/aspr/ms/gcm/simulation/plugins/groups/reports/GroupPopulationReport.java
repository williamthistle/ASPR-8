package gov.hhs.aspr.ms.gcm.simulation.plugins.groups.reports;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.ReportContext;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.datamanagers.GroupsDataManager;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.support.GroupId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.support.GroupTypeId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.PeriodicReport;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportHeader;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportItem;

/**
 * A periodic Report that displays the number of groups having a particular
 * number of people for a given group type. Fields GroupType -- the group type
 * of group PersonCount -- the number of people in each group GroupCount -- the
 * number of groups having the person count
 */
public final class GroupPopulationReport extends PeriodicReport {

	public GroupPopulationReport(GroupPopulationReportPluginData groupPopulationReportPluginData) {
		super(groupPopulationReportPluginData.getReportLabel(), groupPopulationReportPluginData.getReportPeriod());
	}

	/*
	 * Count of the number of groups having a particular person count for a
	 * particular group type
	 */
	private static class Counter {
		int count;
	}

	private ReportHeader reportHeader;

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			ReportHeader.Builder reportHeaderBuilder = ReportHeader.builder();
			reportHeader = addTimeFieldHeaders(reportHeaderBuilder)//
					.setReportLabel(getReportLabel())//
					.add("group_type")//
					.add("person_count")//
					.add("group_count")//
					.build();//
		}
		return reportHeader;
	}

	@Override
	protected void flush(ReportContext reportContext) {

		/*
		 * Count the number of groups of each size that exist for each group type
		 */
		Map<GroupTypeId, Map<Integer, Counter>> groupTypePopulationMap = new LinkedHashMap<>();
		for (GroupTypeId groupTypeId : groupsDataManager.getGroupTypeIds()) {
			Map<Integer, Counter> groupSizeMap = new LinkedHashMap<>();
			groupTypePopulationMap.put(groupTypeId, groupSizeMap);
			for (GroupId groupId : groupsDataManager.getGroupsForGroupType(groupTypeId)) {
				Integer personCountForGroup = groupsDataManager.getPersonCountForGroup(groupId);
				Counter counter = groupSizeMap.get(personCountForGroup);
				if (counter == null) {
					counter = new Counter();
					groupSizeMap.put(personCountForGroup, counter);
				}
				counter.count++;
			}
		}

		/*
		 * Report the collected group counters
		 */
		for (final GroupTypeId groupTypeId : groupTypePopulationMap.keySet()) {
			Map<Integer, Counter> groupSizeMap = groupTypePopulationMap.get(groupTypeId);
			for (final Integer personCount : groupSizeMap.keySet()) {
				ReportItem.Builder reportItemBuilder = ReportItem.builder();
				Counter counter = groupSizeMap.get(personCount);

				final int groupCount = counter.count;
				fillTimeFields(reportItemBuilder);
				reportItemBuilder//
						.setReportLabel(getReportLabel())//
						.addValue(groupTypeId.toString())//
						.addValue(personCount)//
						.addValue(groupCount);
				ReportItem reportItem = reportItemBuilder.build();
				reportContext.releaseOutput(reportItem);
			}
		}

	}

	private GroupsDataManager groupsDataManager;

	@Override
	protected void prepare(ReportContext reportContext) {
		groupsDataManager = reportContext.getDataManager(GroupsDataManager.class);
		if (reportContext.stateRecordingIsScheduled()) {
			reportContext.subscribeToSimulationClose(this::recordSimulationState);
		}

		// release header
		reportContext.releaseOutput(getReportHeader());
	}

	private void recordSimulationState(ReportContext reportContext) {
		GroupPopulationReportPluginData.Builder builder = GroupPopulationReportPluginData.builder();
		builder.setReportLabel(getReportLabel());
		builder.setReportPeriod(getReportPeriod());
		reportContext.releaseOutput(builder.build());
	}

}