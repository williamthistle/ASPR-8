package gov.hhs.aspr.ms.gcm.simulation.plugins.regions.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import gov.hhs.aspr.ms.gcm.simulation.plugins.properties.support.PropertyDefinition;
import gov.hhs.aspr.ms.gcm.simulation.plugins.properties.support.PropertyError;
import gov.hhs.aspr.ms.util.errors.ContractException;
import net.jcip.annotations.Immutable;

/**
 * A class for defining a person property with an associated property id and
 * property values for extant people.
 */
@Immutable
public final class RegionPropertyDefinitionInitialization {

	private static class Data {
		RegionPropertyId regionPropertyId;
		PropertyDefinition propertyDefinition;
		List<Pair<RegionId, Object>> propertyValues = new ArrayList<>();
		private boolean locked;

		private Data() {
		}

		private Data(Data data) {
			regionPropertyId = data.regionPropertyId;
			propertyDefinition = data.propertyDefinition;
			propertyValues.addAll(data.propertyValues);
			locked = data.locked;
		}
	}

	private final Data data;

	private RegionPropertyDefinitionInitialization(Data data) {
		this.data = data;
	}

	/**
	 * Returns a new Builder instance
	 */
	public static Builder builder() {
		return new Builder(new Data());
	}

	/**
	 * Builder class for a PropertyDefinitionInitialization
	 */
	public final static class Builder {
		private Data data;

		private Builder(Data data) {
			this.data = data;
		}

		private void validate() {
			if (data.propertyDefinition == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_DEFINITION);
			}

			if (data.regionPropertyId == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_ID);
			}

			Class<?> type = data.propertyDefinition.getType();
			for (Pair<RegionId, Object> pair : data.propertyValues) {
				Object value = pair.getSecond();
				if (!type.isAssignableFrom(value.getClass())) {
					String message = "Definition Type " + type.getName() + " is not compatible with value = " + value;
					throw new ContractException(PropertyError.INCOMPATIBLE_VALUE, message);
				}
			}
		}

		/**
		 * Constructs the PersonPropertyDefinitionInitialization from the collected data
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain PropertyError#NULL_PROPERTY_DEFINITION}
		 *                           if no property definition was assigned to the
		 *                           builder</li>
		 *                           <li>{@linkplain PropertyError#NULL_PROPERTY_ID} if
		 *                           no property id was assigned to the builder</li>
		 *                           <li>{@linkplain PropertyError#INCOMPATIBLE_VALUE}
		 *                           if a collected property value is incompatible with
		 *                           the property definition</li>
		 *                           </ul>
		 */
		public RegionPropertyDefinitionInitialization build() {
			if (!data.locked) {
				validate();
			}
			ensureImmutability();
			return new RegionPropertyDefinitionInitialization(data);
		}

		/**
		 * Sets the property id
		 * 
		 * @throws ContractException {@linkplain PropertyError#NULL_PROPERTY_ID} if the
		 *                           property id is null
		 */
		public Builder setRegionPropertyId(RegionPropertyId regionPropertyId) {
			ensureDataMutability();
			if (regionPropertyId == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_ID);
			}
			data.regionPropertyId = regionPropertyId;
			return this;
		}

		/**
		 * Sets the property definition
		 * 
		 * @throws ContractException {@linkplain PropertyError#NULL_PROPERTY_DEFINITION}
		 *                           if the property definition is null
		 */
		public Builder setPropertyDefinition(PropertyDefinition propertyDefinition) {
			ensureDataMutability();
			if (propertyDefinition == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_DEFINITION);
			}
			data.propertyDefinition = propertyDefinition;
			return this;
		}

		/**
		 * Adds a property value
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain RegionError#NULL_REGION_ID} if the
		 *                           regionId is null</li>
		 *                           <li>{@linkplain PropertyError#NULL_PROPERTY_VALUE}
		 *                           if the property value is null</li>
		 *                           </ul>
		 */
		public Builder addPropertyValue(RegionId regionId, Object value) {
			ensureDataMutability();
			if (regionId == null) {
				throw new ContractException(RegionError.NULL_REGION_ID);
			}
			if (value == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_VALUE);
			}

			data.propertyValues.add(new Pair<>(regionId, value));
			return this;
		}

		private void ensureDataMutability() {
			if (data.locked) {
				data = new Data(data);
				data.locked = false;
			}
		}

		private void ensureImmutability() {
			if (!data.locked) {
				data.locked = true;
			}
		}

	}

	/**
	 * Returns the (non-null) region property id.
	 */
	public RegionPropertyId getRegionPropertyId() {
		return data.regionPropertyId;
	}

	/**
	 * Returns the (non-null) property definition.
	 */
	public PropertyDefinition getPropertyDefinition() {
		return data.propertyDefinition;
	}

	/**
	 * Returns the list of (region,value) pairs collected by the builder in the
	 * order of their addition. All pairs have non-null entries and the values are
	 * compatible with the contained property definition. Duplicate assignments of
	 * values to the same owner may be present.
	 */
	public List<Pair<RegionId, Object>> getPropertyValues() {
		return Collections.unmodifiableList(data.propertyValues);
	}

	/**
	 * Returns a new builder instance that is pre-filled with the current state of
	 * this instance.
	 */
	public Builder toBuilder() {
		return new Builder(data);
	}

}
