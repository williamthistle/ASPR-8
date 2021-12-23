package plugins.compartments.events.mutation;

import net.jcip.annotations.Immutable;
import nucleus.Event;
import plugins.compartments.events.observation.PersonCompartmentChangeObservationEvent;
import plugins.compartments.support.CompartmentId;
import plugins.people.support.PersonId;

/**
 * An event for setting a person's compartment. A corresponding
 * {@linkplain PersonCompartmentChangeObservationEvent} event is generated by event
 * resolution and distributed to the relevant subscribers.
 */
@Immutable
public final class PersonCompartmentAssignmentEvent implements Event {

	private final PersonId personId;

	private final CompartmentId compartmentId;

	/**
	 * Constructs this event. 
	 *
	 */
	public PersonCompartmentAssignmentEvent(PersonId personId, CompartmentId compartmentId) {
		super();
		this.personId = personId;
		this.compartmentId = compartmentId;
	}

	/**
	 * Returns the {@link PersonId} for this event
	 */
	public PersonId getPersonId() {
		return personId;
	}

	/**
	 * Returns the {@link CompartmentId} for this event
	 */
	public CompartmentId getCompartmentId() {
		return compartmentId;
	}

}
