package plugins.partitions.testsupport.attributes;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nucleus.DataManager;
import nucleus.DataManagerContext;
import nucleus.util.ContractException;
import plugins.partitions.testsupport.attributes.events.AttributeUpdateEvent;
import plugins.partitions.testsupport.attributes.support.AttributeDefinition;
import plugins.partitions.testsupport.attributes.support.AttributeError;
import plugins.partitions.testsupport.attributes.support.AttributeId;
import plugins.people.PersonDataManager;
import plugins.people.events.PersonImminentRemovalEvent;
import plugins.people.support.PersonError;
import plugins.people.support.PersonId;

/**
 * Published data view that provides attribute information.
 * 
 * @author Shawn Hatch
 *
 */

public final class AttributesDataManager extends DataManager {

	private DataManagerContext dataManagerContext;

	private final Map<AttributeId, AttributeDefinition> attributeDefinitions = new LinkedHashMap<>();

	private final Map<AttributeId, Map<PersonId, Object>> attributeValues = new LinkedHashMap<>();

	/**
	 * Returns the attribute definition associated with the given attribute id
	 * without validation.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain AttributeError#NULL_ATTRIBUTE_ID} if the
	 *             attribute id is null</li>
	 *             <li>{@linkplain AttributeError#UNKNOWN_ATTRIBUTE_ID} if the
	 *             attribute id unknown</li>
	 */
	public AttributeDefinition getAttributeDefinition(final AttributeId attributeId) {
		validateAttributeId(attributeId);
		return attributeDefinitions.get(attributeId);
	}

	private void validateAttributeId(AttributeId attributeId) {
		if (attributeId == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_ID);
		}

		if (!attributeExists(attributeId)) {
			throw new ContractException(AttributeError.UNKNOWN_ATTRIBUTE_ID);
		}

	}

	/**
	 * Returns the attribute ids
	 */
	@SuppressWarnings("unchecked")
	public <T extends AttributeId> Set<T> getAttributeIds() {
		final Set<T> result = new LinkedHashSet<>(attributeDefinitions.keySet().size());
		for (final AttributeId attributeId : attributeDefinitions.keySet()) {
			result.add((T) attributeId);
		}
		return result;
	}

	/**
	 * Returns the attribute value associated with the given attribute id
	 * 
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain AttributeError#NULL_ATTRIBUTE_ID} if the
	 *             attribute id is null</li>
	 *             <li>{@linkplain AttributeError#UNKNOWN_ATTRIBUTE_ID} if the
	 *             attribute id unknown</li>
	 *             <li>{@linkplain PersonError#NULL_PERSON_ID} if the person id
	 *             is null</li>
	 *             <li>{@linkplain PersonError#UNKNOWN_PERSON_ID} if the person
	 *             id unknown</li>
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttributeValue(final PersonId personId, final AttributeId attributeId) {
		validateAttributeId(attributeId);
		validatePersonId(personId);
		Object value = attributeValues.get(attributeId).get(personId);
		if (value == null) {
			value = attributeDefinitions.get(attributeId).getDefaultValue();
		}
		return (T) value;
	}

	private void validatePersonId(PersonId personId) {
		if (personId == null) {
			throw new ContractException(PersonError.NULL_PERSON_ID);
		}
		if (!personDataManager.personExists(personId)) {
			throw new ContractException(PersonError.UNKNOWN_PERSON_ID);
		}
	}

	private PersonDataManager personDataManager;

	/**
	 * Returns true if and only if the attribute is contained
	 */
	public boolean attributeExists(final AttributeId attributeId) {
		return attributeDefinitions.containsKey(attributeId);
	}

	private final AttributesPluginData attributesPluginData;

	/**
	 * Constructs this data manager from the given context
	 *
	 */
	public AttributesDataManager(AttributesPluginData attributesPluginData) {
		if (attributesPluginData == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_INITIAL_DATA);
		}
		this.attributesPluginData = attributesPluginData;
	}

	@Override
	public void init(DataManagerContext dataManagerContext) {
		super.init(dataManagerContext);
		this.dataManagerContext = dataManagerContext;
		personDataManager = dataManagerContext.getDataManager(PersonDataManager.class);

		dataManagerContext.addEventLabeler(AttributeUpdateEvent.getEventLabeler());

		for (AttributeId attributeId : attributesPluginData.getAttributeIds()) {
			AttributeDefinition attributeDefinition = attributesPluginData.getAttributeDefinition(attributeId);
			addAttribute(attributeId, attributeDefinition);
		}

		dataManagerContext.subscribe(PersonImminentRemovalEvent.class, this::handlePersonImminentRemovalEvent);

	}

	private void handlePersonImminentRemovalEvent(final DataManagerContext dataManagerContext, final PersonImminentRemovalEvent personImminentRemovalEvent) {
		validatePersonExists(dataManagerContext, personImminentRemovalEvent.getPersonId());
		dataManagerContext.addPlan((c) -> {
			for (AttributeId attributeId : attributeValues.keySet()) {
				attributeValues.get(attributeId).remove(personImminentRemovalEvent.getPersonId());
			}
		}, dataManagerContext.getTime());
	}

	/*
	 * Adds an attribute definition
	 * 
	 * @throws ContractException
	 * 
	 * <li>{@linkplain AttributeError#NULL_ATTRIBUTE_ID} if the attribute id is
	 * null</li> <li>{@linkplain AttributeError#NULL_ATTRIBUTE_DEFINITION} if
	 * the attribute definition is null</li> <li>{@linkplain
	 * AttributeError#DUPLICATE_ATTRIBUTE_DEFINITION} if the attribute
	 * definition was previously added</li>
	 */
	private void addAttribute(AttributeId attributeId, AttributeDefinition attributeDefinition) {
		if (attributeId == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_ID);
		}

		if (attributeDefinition == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_DEFINITION);
		}

		if (attributeDefinitions.containsKey(attributeId)) {
			throw new ContractException(AttributeError.DUPLICATE_ATTRIBUTE_DEFINITION);
		}

		attributeDefinitions.put(attributeId, attributeDefinition);
		attributeValues.put(attributeId, new LinkedHashMap<>());
	}

	/**
	 * Updates the person's current attribute value. Generates a corresponding
	 * {@linkplain AttributeUpdateEvent}
	 * 
	 * 
	 * Throws {@link ContractException}
	 *
	 * <ul>
	 * <li>{@link PersonError#NULL_PERSON_ID} if the person id is null</li>
	 * <li>{@link PersonError#UNKNOWN_PERSON_ID} if the person id is
	 * unknown</li>
	 * <li>{@link AttributeError#NULL_ATTRIBUTE_ID} if the attribute id is
	 * null</li>
	 * <li>{@link AttributeError#UNKNOWN_ATTRIBUTE_ID} if the attribute id is
	 * unknown</li>
	 * <li>{@link AttributeError#NULL_ATTRIBUTE_VALUE} if the attribute value is
	 * null</li>
	 * <li>{@link AttributeError#INCOMPATIBLE_VALUE} if the attribute value is
	 * incompatible with the associated attribute definition</li>
	 * </ul>
	 */
	public void setAttributeValue(final PersonId personId, final AttributeId attributeId, final Object value) {
		validatePersonExists(dataManagerContext, personId);
		validateAttributeId(dataManagerContext, attributeId);
		validateValueNotNull(dataManagerContext, value);
		final AttributeDefinition attributeDefinition = getAttributeDefinition(attributeId);
		validateValueCompatibility(dataManagerContext, attributeId, attributeDefinition, value);
		Object previousValue = getAttributeValue(personId, attributeId);
		attributeValues.get(attributeId).put(personId, value);
		dataManagerContext.releaseEvent(new AttributeUpdateEvent(personId, attributeId, previousValue, value));
	}

	private void validatePersonExists(final DataManagerContext dataManagerContext, final PersonId personId) {
		if (personId == null) {
			throw new ContractException(PersonError.NULL_PERSON_ID);
		}
		if (!personDataManager.personExists(personId)) {
			throw new ContractException(PersonError.UNKNOWN_PERSON_ID);
		}
	}

	private static void validateValueNotNull(final DataManagerContext dataManagerContext, final Object value) {
		if (value == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_VALUE);
		}
	}

	private static void validateValueCompatibility(final DataManagerContext dataManagerContext, final AttributeId attributeId, final AttributeDefinition attributeDefinition, final Object value) {
		if (!attributeDefinition.getType().isAssignableFrom(value.getClass())) {
			throw new ContractException(AttributeError.INCOMPATIBLE_VALUE,
					"Attribute value " + value + " is not of type " + attributeDefinition.getType().getName() + " and does not match definition of " + attributeId);
		}
	}

	private void validateAttributeId(DataManagerContext dataManagerContext, final AttributeId attributeId) {
		if (attributeId == null) {
			throw new ContractException(AttributeError.NULL_ATTRIBUTE_ID);
		}
		if (!attributeExists(attributeId)) {
			throw new ContractException(AttributeError.UNKNOWN_ATTRIBUTE_ID, attributeId);
		}
	}

}
