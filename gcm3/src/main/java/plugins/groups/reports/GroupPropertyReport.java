package plugins.groups.reports;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nucleus.EventLabel;
import nucleus.ReportContext;
import plugins.groups.datacontainers.PersonGroupDataView;
import plugins.groups.events.observation.GroupCreationObservationEvent;
import plugins.groups.events.observation.GroupImminentRemovalObservationEvent;
import plugins.groups.events.observation.GroupPropertyChangeObservationEvent;
import plugins.groups.support.GroupId;
import plugins.groups.support.GroupPropertyId;
import plugins.groups.support.GroupTypeId;
import plugins.reports.support.PeriodicReport;
import plugins.reports.support.ReportHeader;
import plugins.reports.support.ReportItem;
import plugins.reports.support.ReportPeriod;

/**
 * A periodic Report that displays the number of groups having particular values
 * for each group property for a given group type. Only non-zero person counts
 * are reported. The report is further limited to the
 * (GroupType,GroupPropertyId) pairs contained in the
 * GroupPropertyReportSettings instance used to initialize this report.
 * 
 *
 *
 * Fields
 *
 * GroupType -- the group type of group
 *
 * Property -- the group property identifier
 *
 * Value -- the value of the property
 *
 * GroupCount -- the number of groups having the property value for the given
 * group type
 *
 * @author Shawn Hatch
 *
 */
public final class GroupPropertyReport extends PeriodicReport {

	private static class Scaffold {
		private ReportPeriod reportPeriod = ReportPeriod.DAILY;
		private final Map<GroupTypeId, Set<GroupPropertyId>> clientPropertyMap = new LinkedHashMap<>();
		private final Set<GroupTypeId> allProperties = new LinkedHashSet<>();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Scaffold scaffold = new Scaffold();

		private Builder() {
		}

		/**
		 * Returns the GroupPropertyReport instance
		 */
		public GroupPropertyReport build() {
			try {

				return new GroupPropertyReport(scaffold);
			} finally {
				scaffold = new Scaffold();
			}
		}

		/**
		 * Sets the report period for this report
		 * 
		 * @throws RuntimeException
		 *             <li>if the report period is null
		 *             <li>if the report period END_OF_SIMULATION
		 */
		public Builder setReportPeriod(ReportPeriod reportPeriod) {
			if (reportPeriod == null) {
				throw new RuntimeException("null report period");
			}

			if (reportPeriod == ReportPeriod.END_OF_SIMULATION) {
				throw new RuntimeException("cannot be " + ReportPeriod.END_OF_SIMULATION);
			}
			scaffold.reportPeriod = reportPeriod;
			return this;
		}

		/**
		 * Adds all properties for the given group type id
		 * 
		 * @throws RuntimeException
		 *             <li>if the group type id is null
		 */
		public Builder addAllProperties(GroupTypeId groupTypeId) {
			if (groupTypeId == null) {
				throw new RuntimeException("null group type id");
			}
			scaffold.allProperties.add(groupTypeId);
			return this;
		}

		/**
		 * Adds all properties for the given group type id
		 * 
		 * @throws RuntimeException
		 *             <li>if the group type id is null
		 *             <li>if the group property id is null
		 */
		public Builder addProperty(GroupTypeId groupTypeId, GroupPropertyId groupPropertyId) {
			if (groupTypeId == null) {
				throw new RuntimeException("null group type id");
			}
			if (groupPropertyId == null) {
				throw new RuntimeException("null group property id");
			}
			Set<GroupPropertyId> set = scaffold.clientPropertyMap.get(groupTypeId);
			if (set == null) {
				set = new LinkedHashSet<>();
				scaffold.clientPropertyMap.put(groupTypeId, set);
			}
			set.add(groupPropertyId);
			return this;
		}

	}

	private final Scaffold scaffold;

	private GroupPropertyReport(Scaffold scaffold) {
		super(scaffold.reportPeriod);
		this.scaffold = scaffold;
	}

	private static class Counter {
		int count;
	}

	/*
	 * The set of (GroupTypeId,GroupPropertyId) pairs collected from the
	 * GroupPropertyReportSettings supplied during initialization
	 */
	private final Map<GroupTypeId, Set<GroupPropertyId>> clientPropertyMap = new LinkedHashMap<>();

	/*
	 * For each (GroupTypeId,GroupPropertyId,property value) triplet, count the
	 * number of groups having that triplet
	 */
	private final Map<GroupTypeId, Map<GroupPropertyId, Map<Object, Counter>>> groupTypeMap = new LinkedHashMap<>();

	private ReportHeader reportHeader;

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			ReportHeader.Builder reportHeaderBuilder = ReportHeader.builder();
			reportHeader = addTimeFieldHeaders(reportHeaderBuilder)//
																	.add("GroupType")//
																	.add("Property")//
																	.add("Value")//
																	.add("GroupCount")//
																	.build();//
		}
		return reportHeader;
	}

	/*
	 * Decrement the number of groups for the given
	 * (GroupTypeId,GroupPropertyId,property value) triplet
	 */
	private void decrement(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		getCounter(groupTypeId, groupPropertyId, groupPropertyValue).count--;
	}

	@Override
	protected void flush(ReportContext reportContext) {

		final ReportItem.Builder reportItemBuilder = ReportItem.builder();

		for (final GroupTypeId groupTypeId : groupTypeMap.keySet()) {
			final Map<GroupPropertyId, Map<Object, Counter>> propertyIdMap = groupTypeMap.get(groupTypeId);
			for (final GroupPropertyId groupPropertyId : propertyIdMap.keySet()) {
				final Map<Object, Counter> groupPropertyValueMap = propertyIdMap.get(groupPropertyId);
				for (final Object groupPropertyValue : groupPropertyValueMap.keySet()) {
					final Counter counter = groupPropertyValueMap.get(groupPropertyValue);
					if (counter.count > 0) {
						final int personCount = counter.count;
						reportItemBuilder.setReportHeader(getReportHeader());
						reportItemBuilder.setReportId(reportContext.getCurrentReportId());

						fillTimeFields(reportItemBuilder);
						reportItemBuilder.addValue(groupTypeId.toString());
						reportItemBuilder.addValue(groupPropertyId.toString());
						reportItemBuilder.addValue(groupPropertyValue);
						reportItemBuilder.addValue(personCount);

						reportContext.releaseOutput(reportItemBuilder.build());
					}
				}
			}
		}
	}

	private Counter getCounter(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		final Map<Object, Counter> propertyValueMap = groupTypeMap.get(groupTypeId).get(groupPropertyId);
		Counter counter = propertyValueMap.get(groupPropertyValue);
		if (counter == null) {
			counter = new Counter();
			propertyValueMap.put(groupPropertyValue, counter);
		}
		return counter;
	}

	/*
	 * Increment the number of groups for the given
	 * (GroupTypeId,GroupPropertyId,property value) triplet
	 */
	private void increment(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		getCounter(groupTypeId, groupPropertyId, groupPropertyValue).count++;
	}

	private PersonGroupDataView personGroupDataView;

	@Override
	public void init(final ReportContext reportContext) {
		super.init(reportContext);

		personGroupDataView = reportContext.getDataView(PersonGroupDataView.class).get();

		// transfer all VALID property selections from the scaffold
		Set<GroupTypeId> groupTypeIds = personGroupDataView.getGroupTypeIds();
		for (GroupTypeId groupTypeId : groupTypeIds) {
			Set<GroupPropertyId> groupPropertyIds = new LinkedHashSet<>();
			if (scaffold.allProperties.contains(groupTypeId)) {
				groupPropertyIds.addAll(personGroupDataView.getGroupPropertyIds(groupTypeId));
			} else {
				Set<GroupPropertyId> selectedPropertyIds = scaffold.clientPropertyMap.get(groupTypeId);
				if (selectedPropertyIds != null) {
					Set<GroupPropertyId> allPropertyIds = personGroupDataView.getGroupPropertyIds(groupTypeId);
					for (GroupPropertyId groupPropertyId : allPropertyIds) {
						if (selectedPropertyIds.contains(groupPropertyId)) {
							groupPropertyIds.add(groupPropertyId);
						}
					}
				}
			}
			clientPropertyMap.put(groupTypeId, groupPropertyIds);
		}

		// determine the subscriptions for group creation
		if (clientPropertyMap.keySet().equals(personGroupDataView.getGroupTypeIds())) {
			reportContext.subscribe(GroupCreationObservationEvent.class, this::handleGroupCreationObservationEvent);
		} else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				EventLabel<GroupCreationObservationEvent> eventLabelByGroupType = GroupCreationObservationEvent.getEventLabelByGroupType(reportContext, groupTypeId);
				reportContext.subscribe(eventLabelByGroupType, this::handleGroupCreationObservationEvent);
			}
		}

		//determine the subscriptions for group removal observations
		if (clientPropertyMap.keySet().equals(personGroupDataView.getGroupTypeIds())) {
			reportContext.subscribe(GroupImminentRemovalObservationEvent.class, this::handleGroupDestructionObservationEvent);
		} else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				EventLabel<GroupImminentRemovalObservationEvent> eventLabelByGroupType = GroupImminentRemovalObservationEvent.getEventLabelByGroupType(reportContext, groupTypeId);
				reportContext.subscribe(eventLabelByGroupType, this::handleGroupDestructionObservationEvent);
			}
		}
		
		//determine the subscriptions for group property changes		
		boolean allPropertiesRequired = false;
		if (clientPropertyMap.keySet().equals(personGroupDataView.getGroupTypeIds())) {
			allPropertiesRequired = true;
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				if(!clientPropertyMap.get(groupTypeId).equals(personGroupDataView.getGroupPropertyIds(groupTypeId))) {
					allPropertiesRequired = false;
					break;
				}
			}
		}
		
		if(allPropertiesRequired) {
			reportContext.subscribe(GroupPropertyChangeObservationEvent.class, this::handleGroupPropertyChangeObservationEvent);	
		}else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				if(clientPropertyMap.get(groupTypeId).equals(personGroupDataView.getGroupPropertyIds(groupTypeId))) {
					EventLabel<GroupPropertyChangeObservationEvent> eventLabelByGroupType = GroupPropertyChangeObservationEvent.getEventLabelByGroupType(reportContext, groupTypeId);
					reportContext.subscribe(eventLabelByGroupType, this::handleGroupPropertyChangeObservationEvent);
				}else {
					for(GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
						EventLabel<GroupPropertyChangeObservationEvent> eventLabelByGroupTypeAndProperty = GroupPropertyChangeObservationEvent.getEventLabelByGroupTypeAndProperty(reportContext, groupTypeId, groupPropertyId);
						reportContext.subscribe(eventLabelByGroupTypeAndProperty, this::handleGroupPropertyChangeObservationEvent);
					}
				}
			}
		}

		/*
		 * Fill the top layers of the groupTypeMap. We do not yet know the set
		 * of property values, so we leave that layer empty.
		 *
		 */

		for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
			final Map<GroupPropertyId, Map<Object, Counter>> propertyIdMap = new LinkedHashMap<>();
			groupTypeMap.put(groupTypeId, propertyIdMap);
			Set<GroupPropertyId> groupPropertyIds = clientPropertyMap.get(groupTypeId);
			for (final GroupPropertyId groupPropertyId : groupPropertyIds) {
				final Map<Object, Counter> propertyValueMap = new LinkedHashMap<>();
				propertyIdMap.put(groupPropertyId, propertyValueMap);
			}
		}

		// group addition
		for (GroupId groupId : personGroupDataView.getGroupIds()) {
			final GroupTypeId groupTypeId = personGroupDataView.getGroupType(groupId);
			if (clientPropertyMap.containsKey(groupTypeId)) {
				for (final GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
					final Object groupPropertyValue = personGroupDataView.getGroupPropertyValue(groupId, groupPropertyId);
					increment(groupTypeId, groupPropertyId, groupPropertyValue);
				}
			}
		}
	}

	private void handleGroupPropertyChangeObservationEvent(ReportContext reportContext, GroupPropertyChangeObservationEvent groupPropertyChangeObservationEvent) {
		GroupId groupId = groupPropertyChangeObservationEvent.getGroupId();

		final GroupTypeId groupTypeId = personGroupDataView.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {

			GroupPropertyId groupPropertyId = groupPropertyChangeObservationEvent.getGroupPropertyId();
			Object previousPropertyValue = groupPropertyChangeObservationEvent.getPreviousPropertyValue();
			Object currentPropertyValue = groupPropertyChangeObservationEvent.getCurrentPropertyValue();

			if (clientPropertyMap.get(groupTypeId).contains(groupPropertyId)) {

				increment(groupTypeId, groupPropertyId, currentPropertyValue);
				decrement(groupTypeId, groupPropertyId, previousPropertyValue);
			}
		}

	}

	private void handleGroupCreationObservationEvent(ReportContext reportContext, GroupCreationObservationEvent groupCreationObservationEvent) {
		GroupId groupId = groupCreationObservationEvent.getGroupId();
		final GroupTypeId groupTypeId = personGroupDataView.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {
			for (final GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
				final Object groupPropertyValue = personGroupDataView.getGroupPropertyValue(groupId, groupPropertyId);
				increment(groupTypeId, groupPropertyId, groupPropertyValue);
			}
		}
	}

	private void handleGroupDestructionObservationEvent(ReportContext reportContext, GroupImminentRemovalObservationEvent groupImminentRemovalObservationEvent) {
		GroupId groupId = groupImminentRemovalObservationEvent.getGroupId();
		final GroupTypeId groupTypeId = personGroupDataView.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {
			Set<GroupPropertyId> groupPropertyIds = personGroupDataView.getGroupPropertyIds(groupTypeId);
			for (final GroupPropertyId groupPropertyId : groupPropertyIds) {
				if (clientPropertyMap.get(groupTypeId).contains(groupPropertyId)) {
					final Object groupPropertyValue = personGroupDataView.getGroupPropertyValue(groupId, groupPropertyId);
					decrement(groupTypeId, groupPropertyId, groupPropertyValue);
				}
			}
		}
	}
}