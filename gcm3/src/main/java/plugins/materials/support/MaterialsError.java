package plugins.materials.support;

import util.errors.ContractError;
import util.errors.ContractException;

/**
 * An enumeration supporting {@link ContractException} that acts as a general
 * description of the exception.
 * 
 * @author Shawn Hatch
 *
 */
public enum MaterialsError implements ContractError {

	DUPLICATE_BATCH_PROPERTY_VALUE_ASSIGNMENT("Duplicate batch property value assignment"),
	DUPLICATE_MATERIALS_PRODUCER_PROPERTY_VALUE_ASSIGNMENT("Duplicate materials producer property value assignment"),
	DUPLICATE_MATERIALS_PRODUCER_RESOURCE_ASSIGNMENT("Duplicate materials producer resource assignment"),
	DUPLICATE_MATERIALS_PRODUCER_ID("Duplicate materials producer id"),
	RESOURCE_LOADING_ORDER("Resources must be added before materials producers are added"),
	MATERIALS_PRODUCER_PROPERTY_LOADING_ORDER("Material procuders must be added before materials producer properties are added"),
	BATCH_ALREADY_STAGED("Batch is already staged"),
	DUPLICATE_MATERIAL("Duplicate material addition"),
	BATCH_NOT_STAGED("Batch is not currently staged"),
	BATCH_SHIFT_WITH_MULTIPLE_OWNERS("Cannot shift material from a batch to another batch not owned by the same materials producer"),
	BATCH_STAGED_TO_DIFFERENT_OWNER("Cannot stage a batch onto a stage not owned by the same materials producer"),
	INSUFFICIENT_MATERIAL_AVAILABLE("Material level is insufficient for transaction amount"),
	MATERIAL_ARITHMETIC_EXCEPTION("Material arithmetic error due to non finite sum"),
	MATERIAL_TYPE_MISMATCH("Material identifiers do not match"),
	NEGATIVE_MATERIAL_AMOUNT("Material amount is negative"),	
	NON_FINITE_MATERIAL_AMOUNT("Material amount is not finite"),	
	NULL_BATCH_CONSTRUCTION_INFO("Null batch construction info"),
	NULL_BATCH_ID("Null batch id"),
	NULL_BATCH_PROPERTY_ID("Null batch property id"),	
	NULL_BATCH_PROPERTY_VALUE("Null batch property value"),
	NULL_MATERIAL_ID("Null material id"),
	NULL_MATERIALS_PLUGIN_DATA("Null materials initial data"),
	NULL_MATERIALS_PRODUCER_ID("Null materials producer id"),
	NULL_MATERIALS_PRODUCER_PROPERTY_ID("Null materials producer property id"),
	NULL_MATERIALS_PRODUCER_PROPERTY_VALUE("Null materials producer property value"),
	NULL_STAGE_ID("Null stage id"),
	OFFERED_STAGE_UNALTERABLE("An offered stage and its batches cannot be altered"),
	REFLEXIVE_BATCH_SHIFT("Cannot shift material from a batch to itself"),
	REFLEXIVE_STAGE_TRANSFER("Producer cannot transfer a stage to itself"),
	UNKNOWN_BATCH_ID("Unknown batch id"),
	UNKNOWN_BATCH_PROPERTY_ID("Unknown batch property id"),
	UNKNOWN_MATERIAL_ID("Unknown material id"),
	UNKNOWN_MATERIALS_PRODUCER_ID("Unknown materials producer id"),
	UNKNOWN_MATERIALS_PRODUCER_PROPERTY_ID("Unknown material producer property id"),
	UNKNOWN_STAGE_ID("Unknown stage id"),
	UNOFFERED_STAGE_NOT_TRANSFERABLE("Unoffered stages are not transferable"),
	INSUFFICIENT_MATERIALS_PRODUCER_PROPERTY_VALUE_ASSIGNMENT("A materials producer property definition default value is null and not replaced with sufficient property value assignments"),
	DUPLICATE_BATCH_PROPERTY_DEFINITION("Duplicate batch property definition"),
	DUPLICATE_BATCH_ID("Duplicate batch id"),
	DUPLICATE_MATERIALS_PRODUCER_PROPERTY_DEFINITION("Duplicate materials producer property definition"),
	INCOMPATIBLE_MATERIALS_PRODUCER_PROPERTY_VALUE("Materials producer property value is incompatible with the property definition"),
	INCOMPATIBLE_BATCH_PROPERTY_VALUE("Batch property value is incompatible with the property definition"),
	MATERIALS_PRODUCER_ADDITION_BLOCKED("Materials producer addition requires that all materials producer properties have default values"),
	;

	private final String description;

	private MaterialsError(final String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
