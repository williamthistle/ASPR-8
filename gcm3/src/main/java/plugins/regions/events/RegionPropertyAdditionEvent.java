package plugins.regions.events;

import net.jcip.annotations.Immutable;
import nucleus.Event;
import plugins.regions.support.RegionError;
import plugins.regions.support.RegionPropertyId;
import util.errors.ContractException;

/**
 * Event indicating the addition of a region property
 * 
 * @author Shawn Hatch
 *
 */
@Immutable
public class RegionPropertyAdditionEvent implements Event {

	private final RegionPropertyId regionPropertyId;

	/**
	 * Constructs the event
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain RegionError#NULL_REGION_PROPERTY_ID} if the
	 *             region property id is null</li>
	 * 
	 */
	public RegionPropertyAdditionEvent(RegionPropertyId regionPropertyId) {
		if (regionPropertyId == null) {
			throw new ContractException(RegionError.NULL_REGION_PROPERTY_ID);
		}

		this.regionPropertyId = regionPropertyId;
	}

	/**
	 * Returns the region property id of the recently added region property
	 */
	public RegionPropertyId getRegionPropertyId() {
		return regionPropertyId;
	}

}
