package plugins.util.properties;

import nucleus.util.ContractException;

/**
 * Common interface to all property managers. A property manager manages
 * property values associated with int-based identifiers.
 * 
 * @author Shawn Hatch
 *
 */
public interface IndexedPropertyManager {

	/**
	 * Returns the property value stored for the given id. Does not return null.
	 * Note that this does not imply that the id exists in the simulation.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain PropertyError#NEGATIVE_INDEX} if the id is
	 *             negative</li>
	 */
	public <T> T getPropertyValue(int id);

	/**
	 * Returns the assignment time when the id's property was last set. Note
	 * that this does not imply that the id exists in the simulation. Defaults
	 * to zero for non-valid id values.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain PropertyError#NEGATIVE_INDEX} if the id is
	 *             negative</li>
	 * 
	 *             <li>{@linkplain PropertyError#TIME_TRACKING_OFF} if time
	 *             tracking is off</li>
	 * 
	 *
	 * 
	 */
	public double getPropertyTime(int id);

	/**
	 * Sets the property value stored for the given person. Note that this does
	 * not imply that the person exists in the simulation. The environment must
	 * guard against access to removed people.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain PropertyError#NEGATIVE_INDEX} if the id is
	 *             negative</li>
	 * 
	 */
	public void setPropertyValue(int id, Object propertyValue);

	/**
	 * Removes non-primitive property values for the given id -- use only when
	 * removing the indicated id from the simulation.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain PropertyError#NEGATIVE_INDEX} if the id is
	 *             negative</li>
	 */
	public void removeId(int id);

	/**
	 * Sets the capacity for this manager. Indicates to the manager an
	 * anticipated near term growth so that the manager might more efficiently
	 * expand to hold more data.
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain PropertyError#NEGATIVE_CAPACITY_INCREMENT} if
	 *             the count is negative</li>
	 * 
	 */
	public void incrementCapacity(int count);

}
