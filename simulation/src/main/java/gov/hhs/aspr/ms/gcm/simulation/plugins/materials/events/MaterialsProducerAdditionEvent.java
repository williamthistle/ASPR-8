package gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events;

import java.util.ArrayList;
import java.util.List;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.Event;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.MaterialsError;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.MaterialsProducerId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.regions.events.RegionAdditionEvent;
import gov.hhs.aspr.ms.util.errors.ContractException;
import net.jcip.annotations.Immutable;

/**
 * An event indicating that a materials producer has been added
 */
@Immutable
public class MaterialsProducerAdditionEvent implements Event {

	private static class Data {
		private MaterialsProducerId materialsProducerId;
		private List<Object> values = new ArrayList<>();
		private boolean locked;

		private Data() {
		}

		private Data(Data data) {
			materialsProducerId = data.materialsProducerId;
			values.addAll(data.values);
			locked = data.locked;
		}
	}

	/**
	 * Returns a new Builder instance
	 */
	public static Builder builder() {
		return new Builder(new Data());
	}

	private final Data data;

	/**
	 * Builder class for {@link RegionAdditionEvent}
	 */
	public static class Builder {

		private Data data;

		private Builder(Data data) {
			this.data = data;
		}

		private void validate() {
			if (data.materialsProducerId == null) {
				throw new ContractException(MaterialsError.NULL_MATERIALS_PRODUCER_ID);
			}
		}

		/**
		 * Builds the Region addition event from the inputs
		 * 
		 * @throws ContractException {@linkplain MaterialsError#NULL_MATERIALS_PRODUCER_ID}
		 *                           if the materials producer id was not set
		 */
		public MaterialsProducerAdditionEvent build() {
			if (!data.locked) {
				validate();
			}
			ensureImmutability();
			return new MaterialsProducerAdditionEvent(data);
		}

		/**
		 * Sets the materials producer id
		 * 
		 * @throws ContractException {@linkplain MaterialsError#NULL_MATERIALS_PRODUCER_ID}
		 *                           if the materials producer id is null
		 */
		public Builder setMaterialsProducerId(MaterialsProducerId materialsProducerId) {
			ensureDataMutability();
			if (materialsProducerId == null) {
				throw new ContractException(MaterialsError.NULL_MATERIALS_PRODUCER_ID);
			}
			data.materialsProducerId = materialsProducerId;
			return this;
		}

		/**
		 * Adds an auxiliary value to be used by observers of materials producer
		 * addition
		 * 
		 * @throws ContractException {@linkplain MaterialsError#NULL_AUXILIARY_DATA} if
		 *                           the value is null
		 */
		public Builder addValue(Object value) {
			ensureDataMutability();
			if (value == null) {
				throw new ContractException(MaterialsError.NULL_AUXILIARY_DATA);
			}
			data.values.add(value);
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

	private MaterialsProducerAdditionEvent(Data data) {
		this.data = data;
	}

	/**
	 * Returns the region id.
	 */
	public MaterialsProducerId getMaterialsProducerId() {
		return data.materialsProducerId;
	}

	/**
	 * Returns the (non-null) auxiliary objects that are instances of the given
	 * class in the order of their addition to the builder.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getValues(Class<T> c) {
		List<T> result = new ArrayList<>();
		for (Object value : data.values) {
			if (c.isAssignableFrom(value.getClass())) {
				result.add((T) value);
			}
		}
		return result;
	}

	/**
	 * Returns a new builder instance that is pre-filled with the current state of
	 * this instance.
	 */
	public Builder toBuilder() {
		return new Builder(data);
	}

}
