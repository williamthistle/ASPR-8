package plugins.regions.support;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import nucleus.NucleusError;
import plugins.partitions.support.FilterSensitivity;
import plugins.partitions.support.PartitionsContext;
import plugins.partitions.support.filters.Filter;
import plugins.people.support.PersonId;
import plugins.regions.datamanagers.RegionsDataManager;
import plugins.regions.events.PersonRegionUpdateEvent;
import util.errors.ContractException;

public final class RegionFilter extends Filter {

	private final Set<RegionId> regionIds = new LinkedHashSet<>();

	private RegionsDataManager regionsDataManager;

	private void validateRegionId( final RegionId regionId) {

		if (regionId == null) {
			throw new ContractException(RegionError.NULL_REGION_ID);
		}

		if (!regionsDataManager.regionIdExists(regionId)) {
			throw new ContractException(RegionError.UNKNOWN_REGION_ID, regionId);
		}
	}

	public RegionFilter(final RegionId... regionIds) {
		for (RegionId regionId : regionIds) {
			this.regionIds.add(regionId);
		}
	}

	@Override
	public void validate(PartitionsContext partitionsContext) {
		if (partitionsContext == null) {
			throw new ContractException(NucleusError.NULL_SIMULATION_CONTEXT);
		}

		if (regionsDataManager == null) {
			regionsDataManager = partitionsContext.getDataManager(RegionsDataManager.class);
		}
		
		for (RegionId regionId : regionIds) {
			validateRegionId(regionId);
		}

	}

	public RegionFilter(final Set<RegionId> regionIds) {
		this.regionIds.addAll(regionIds);
	}

	@Override
	public boolean evaluate(PartitionsContext partitionsContext, PersonId personId) {
		if (partitionsContext == null) {
			throw new ContractException(NucleusError.NULL_SIMULATION_CONTEXT);
		}

		if (regionsDataManager == null) {
			regionsDataManager = partitionsContext.getDataManager(RegionsDataManager.class);
		}
		return regionIds.contains(regionsDataManager.getPersonRegion(personId));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegionFilter [regionIds=");
		builder.append(regionIds);
		builder.append("]");
		return builder.toString();
	}

	private Optional<PersonId> requiresRefresh(PartitionsContext partitionsContext, PersonRegionUpdateEvent event) {
		boolean previousRegionIdContained = regionIds.contains(event.previousRegionId());
		boolean currentRegionIdContained = regionIds.contains(event.currentRegionId());
		if (previousRegionIdContained != currentRegionIdContained) {
			return Optional.of(event.personId());
		}
		return Optional.empty();
	}

	@Override
	public Set<FilterSensitivity<?>> getFilterSensitivities() {
		Set<FilterSensitivity<?>> result = new LinkedHashSet<>();
		result.add(new FilterSensitivity<PersonRegionUpdateEvent>(PersonRegionUpdateEvent.class, this::requiresRefresh));
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regionIds == null) ? 0 : regionIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RegionFilter)) {
			return false;
		}
		RegionFilter other = (RegionFilter) obj;
		if (regionIds == null) {
			if (other.regionIds != null) {
				return false;
			}
		} else if (!regionIds.equals(other.regionIds)) {
			return false;
		}
		return true;
	}
	
	

}
