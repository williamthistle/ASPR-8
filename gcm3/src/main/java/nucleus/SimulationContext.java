package nucleus;

import java.util.Optional;
import java.util.function.Consumer;

import util.ContractException;

/**
 * A context provides basic access to the nucleus engine and published data.
 * 
 * @author Shawn Hatch
 *
 */
public interface SimulationContext {

	/**
	 * Sends output to whatever consumer of output is registered with nucleus,
	 * if any
	 */
	public void releaseOutput(Object output);

	
	public <T extends DataManager> Optional<T> getDataManager(Class<T> dataManagerClass);

	/**
	 * Returns the current time in the simulation
	 */
	public double getTime();
	/**
	 * Terminates the simulation after the current plan is fully executed.
	 */
	public void halt();
	
	/**
	 * Adds an event labeler to nucleus.
	 * 
	 * @throws ContractException
	 *             <li>{@link NucleusError#NULL_EVENT_LABELER} if the event
	 *             labeler is null
	 *             <li>{@link NucleusError#NULL_EVENT_CLASS_IN_EVENT_LABELER} if
	 *             the event class is null
	 *             <li>{@link NucleusError#NULL_LABELER_ID_IN_EVENT_LABELER} if
	 *             the event labeler contains a null labeler id
	 *             <li>{@link NucleusError#DUPLICATE_LABELER_ID_IN_EVENT_LABELER}
	 *             if the event labeler contains a labeler id that is the id of
	 *             a previously added event labeler
	 */
	public <T extends Event> void addEventLabeler(EventLabeler<T> eventLabeler);
	
	/**
	 * Returns true if an only if an agent is associated with the given id.
	 * Tolerates null values.
	 */
	public boolean agentExists(AgentId agentId);
	
	/**
	 * Adds an agent to the simulation. The agent is added immediately, but the
	 * consumer of AgentContext is invoked after event resolution is finished
	 * and before time progresses.
	 * 
	 * @throws ContractException
	 *             
	 *             <li>{@link NucleusError#NULL_AGENT_CONTEXT_CONSUMER} if the
	 *             agent context consumer is null
	 * 
	 */
	public AgentId addAgent(Consumer<AgentContext> consumer);
	
	
	/**
	 * Removes the given agent from the simulation.
	 * 
	 * @throws ContractException
	 *             <li>{@link NucleusError#NULL_AGENT_ID} if the agentId is null
	 *             <li>{@link NucleusError#NEGATIVE_AGENT_ID} if the agent id is
	 *             negative
	 *             <li>{@link NucleusError#UNKNOWN_AGENT_ID} if the agent id
	 *             does not correspond to a known agent
	 */
	public void removeAgent(AgentId agentId);
}
