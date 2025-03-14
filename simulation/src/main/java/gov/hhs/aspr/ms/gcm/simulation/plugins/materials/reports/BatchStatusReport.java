package gov.hhs.aspr.ms.gcm.simulation.plugins.materials.reports;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.ReportContext;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.datamangers.MaterialsDataManager;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.BatchAdditionEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.BatchAmountUpdateEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.BatchImminentRemovalEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.BatchPropertyDefinitionEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.BatchPropertyUpdateEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.MaterialIdAdditionEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.StageMembershipAdditionEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.events.StageMembershipRemovalEvent;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.BatchId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.BatchPropertyId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.MaterialId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.MaterialsProducerId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.materials.support.StageId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportHeader;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportItem;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportLabel;

/**
 * A Report that displays the state of batches over time. The batch properties
 * included in this report are limited to those present during initialization.
 * Fields Time -- the time in days when batch state was updated Batch -- the
 * batch identifier Stage -- the stage associated with the batch
 * MaterialsProducer -- the materials producer of the owner of the batch Offered
 * -- the offered state of the batch Material -- the material of the batch
 * Amount -- the amount of material in the batch Material.PropertyId -- multiple
 * columns for the batch properties selected for the report .add("time")//
 * .add("batch")// .add("materials_producer")// .add("stage")//
 * .add("material")// .add("amount");// }
 */
public final class BatchStatusReport {

	private static class BatchRecord {
		private double time;
		private BatchId batchId;
		private MaterialsProducerId materialsProducerId;
		private StageId stageId;
		private MaterialId materialId;
		private double amount;
		private Map<BatchPropertyId, Object> propertyValues = new LinkedHashMap<>();

	}

	private final ReportLabel reportLabel;

	public BatchStatusReport(BatchStatusReportPluginData batchStatusReportPluginData) {
		this.reportLabel = batchStatusReportPluginData.getReportLabel();
	}

	private Map<BatchId, BatchRecord> batchRecords = new LinkedHashMap<>();

	private Map<MaterialId, Set<BatchPropertyId>> batchPropertyMap = new LinkedHashMap<>();

	/*
	 * Releases a report item for each updated batch that still exists
	 */
	private void reportBatch(ReportContext reportContext, BatchRecord batchRecord) {

		// report the batch - make sure batch exists

		final ReportItem.Builder reportItemBuilder = ReportItem.builder()//
				.setReportLabel(reportLabel)//
				.addValue(batchRecord.time)//
				.addValue(batchRecord.batchId)//
				.addValue(batchRecord.materialsProducerId);

		if (batchRecord.stageId != null) {
			reportItemBuilder.addValue(batchRecord.stageId);
		} else {
			reportItemBuilder.addValue("");
		}

		reportItemBuilder.addValue(batchRecord.materialId);
		reportItemBuilder.addValue(batchRecord.amount);

		for (MaterialId materialId : batchPropertyMap.keySet()) {
			boolean matchingMaterial = batchRecord.materialId.equals(materialId);
			Set<BatchPropertyId> batchPropertyIds = batchPropertyMap.get(materialId);
			for (BatchPropertyId batchPropertyId : batchPropertyIds) {
				if (matchingMaterial) {
					reportItemBuilder.addValue(batchRecord.propertyValues.get(batchPropertyId));
				} else {
					reportItemBuilder.addValue("");
				}
			}
		}
		reportContext.releaseOutput(reportItemBuilder.build());

	}

	private ReportHeader reportHeader;

	/*
	 * Returns the ReportHeader based on the batch properties selected by the
	 * client.
	 */
	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			ReportHeader.Builder builder = ReportHeader.builder()//
					.setReportLabel(reportLabel)//
					.add("time")//
					.add("batch")//
					.add("materials_producer")//
					.add("stage")//
					.add("material")//
					.add("amount");//
			Set<MaterialId> materialIds = materialsDataManager.getMaterialIds();
			for (MaterialId materialId : materialIds) {
				Set<BatchPropertyId> batchPropertyIds = materialsDataManager.getBatchPropertyIds(materialId);
				for (BatchPropertyId batchPropertyId : batchPropertyIds) {
					builder.add(materialId + "." + batchPropertyId);
				}
			}
			reportHeader = builder.build();
		}
		return reportHeader;

	}

	private BatchRecord createBatchRecord(ReportContext reportContext, BatchId batchId) {

		BatchRecord batchRecord = new BatchRecord();

		batchRecord.time = reportContext.getTime();
		batchRecord.batchId = batchId;
		batchRecord.materialsProducerId = materialsDataManager.getBatchProducer(batchId);
		Optional<StageId> optionalStageId = materialsDataManager.getBatchStageId(batchId);
		if (optionalStageId.isPresent()) {
			batchRecord.stageId = optionalStageId.get();
		} else {
			batchRecord.stageId = null;
		}
		batchRecord.materialId = materialsDataManager.getBatchMaterial(batchId);
		batchRecord.amount = materialsDataManager.getBatchAmount(batchId);

		Set<BatchPropertyId> batchPropertyIds = materialsDataManager.getBatchPropertyIds(batchRecord.materialId);
		for (BatchPropertyId batchPropertyId : batchPropertyIds) {
			Object batchPropertyValue = materialsDataManager.getBatchPropertyValue(batchId, batchPropertyId);
			batchRecord.propertyValues.put(batchPropertyId, batchPropertyValue);
		}
		batchRecords.put(batchId, batchRecord);
		return batchRecord;
	}

	private void handleBatchAdditionEvent(ReportContext reportContext, BatchAdditionEvent batchAdditionEvent) {
		BatchId batchId = batchAdditionEvent.batchId();
		BatchRecord batchRecord = createBatchRecord(reportContext, batchId);
		reportBatch(reportContext, batchRecord);
	}

	private void handleBatchImminentRemovalEvent(ReportContext reportContext,
			BatchImminentRemovalEvent batchImminentRemovalEvent) {
		BatchId batchId = batchImminentRemovalEvent.batchId();
		BatchRecord batchRecord = batchRecords.remove(batchId);
		batchRecord.time = reportContext.getTime();
		reportBatch(reportContext, batchRecord);
	}

	private void handleBatchAmountUpdateEvent(ReportContext reportContext,
			BatchAmountUpdateEvent batchAmountUpdateEvent) {
		BatchId batchId = batchAmountUpdateEvent.batchId();
		BatchRecord batchRecord = batchRecords.get(batchId);
		batchRecord.amount = batchAmountUpdateEvent.currentAmount();
		batchRecord.time = reportContext.getTime();
		reportBatch(reportContext, batchRecord);
	}

	private void handleStageMembershipAdditionEvent(ReportContext reportContext,
			StageMembershipAdditionEvent stageMembershipAdditionEvent) {
		BatchId batchId = stageMembershipAdditionEvent.batchId();
		BatchRecord batchRecord = batchRecords.get(batchId);
		batchRecord.stageId = stageMembershipAdditionEvent.stageId();
		batchRecord.time = reportContext.getTime();
		reportBatch(reportContext, batchRecord);
	}

	private void handleMaterialIdAdditionEvent(ReportContext reportContext,
			MaterialIdAdditionEvent materialIdAdditionEvent) {
		batchPropertyMap.put(materialIdAdditionEvent.materialId(), new LinkedHashSet<>());
	}

	private void handleBatchPropertyDefinitionEvent(ReportContext reportContext,
			BatchPropertyDefinitionEvent batchPropertyDefinitionEvent) {

		BatchPropertyId batchPropertyId = batchPropertyDefinitionEvent.batchPropertyId();
		MaterialId materialId = batchPropertyDefinitionEvent.materialId();

		batchPropertyMap.get(batchPropertyDefinitionEvent.materialId()).add(batchPropertyId);
		for (MaterialsProducerId materialsProducerId : materialsDataManager.getMaterialsProducerIds()) {
			for (StageId stageId : materialsDataManager.getStages(materialsProducerId)) {
				for (BatchId batchId : materialsDataManager.getStageBatches(stageId)) {
					BatchRecord batchRecord = batchRecords.get(batchId);
					if (batchRecord.materialId.equals(materialId)) {
						Object batchPropertyValue = materialsDataManager.getBatchPropertyValue(batchId,
								batchPropertyId);
						batchRecord.time = reportContext.getTime();
						batchRecord.propertyValues.put(batchPropertyId, batchPropertyValue);
						reportBatch(reportContext, batchRecord);
					}
				}
			}
			for (BatchId batchId : materialsDataManager.getInventoryBatches(materialsProducerId)) {
				BatchRecord batchRecord = batchRecords.get(batchId);
				if (batchRecord.materialId.equals(materialId)) {
					Object batchPropertyValue = materialsDataManager.getBatchPropertyValue(batchId, batchPropertyId);
					batchRecord.time = reportContext.getTime();
					batchRecord.propertyValues.put(batchPropertyId, batchPropertyValue);
					reportBatch(reportContext, batchRecord);
				}
			}
		}
	}

	private void handleStageMembershipRemovalEvent(ReportContext reportContext,
			StageMembershipRemovalEvent stageMembershipRemovalEvent) {
		BatchId batchId = stageMembershipRemovalEvent.batchId();
		BatchRecord batchRecord = batchRecords.get(batchId);
		batchRecord.stageId = null;
		batchRecord.time = reportContext.getTime();
		reportBatch(reportContext, batchRecord);
	}

	private void handleBatchPropertyUpdateEvent(ReportContext reportContext,
			BatchPropertyUpdateEvent batchPropertyUpdateEvent) {
		BatchId batchId = batchPropertyUpdateEvent.batchId();
		BatchRecord batchRecord = batchRecords.get(batchId);
		batchRecord.propertyValues.put(batchPropertyUpdateEvent.batchPropertyId(),
				batchPropertyUpdateEvent.currentPropertyValue());
		batchRecord.time = reportContext.getTime();
		reportBatch(reportContext, batchRecord);
	}

	private MaterialsDataManager materialsDataManager;

	public void init(final ReportContext reportContext) {

		reportContext.subscribe(BatchAdditionEvent.class, this::handleBatchAdditionEvent);
		reportContext.subscribe(BatchImminentRemovalEvent.class, this::handleBatchImminentRemovalEvent);
		reportContext.subscribe(BatchAmountUpdateEvent.class, this::handleBatchAmountUpdateEvent);
		reportContext.subscribe(BatchPropertyUpdateEvent.class, this::handleBatchPropertyUpdateEvent);
		reportContext.subscribe(StageMembershipAdditionEvent.class, this::handleStageMembershipAdditionEvent);
		reportContext.subscribe(StageMembershipRemovalEvent.class, this::handleStageMembershipRemovalEvent);
		reportContext.subscribe(BatchPropertyDefinitionEvent.class, this::handleBatchPropertyDefinitionEvent);
		reportContext.subscribe(MaterialIdAdditionEvent.class, this::handleMaterialIdAdditionEvent);

		if (reportContext.stateRecordingIsScheduled()) {
			reportContext.subscribeToSimulationClose(this::recordSimulationState);
		}

		materialsDataManager = reportContext.getDataManager(MaterialsDataManager.class);

		for (MaterialId materialId : materialsDataManager.getMaterialIds()) {
			this.batchPropertyMap.put(materialId, materialsDataManager.getBatchPropertyIds(materialId));
		}

		for (MaterialsProducerId materialsProducerId : materialsDataManager.getMaterialsProducerIds()) {
			for (BatchId inventoryBatchId : materialsDataManager.getInventoryBatches(materialsProducerId)) {
				BatchRecord batchRecord = createBatchRecord(reportContext, inventoryBatchId);
				reportBatch(reportContext, batchRecord);
			}
			for (StageId stageId : materialsDataManager.getStages(materialsProducerId)) {
				for (BatchId stageBatchId : materialsDataManager.getStageBatches(stageId)) {
					BatchRecord batchRecord = createBatchRecord(reportContext, stageBatchId);
					reportBatch(reportContext, batchRecord);
				}
			}
		}

		// release report header
		reportContext.releaseOutput(getReportHeader());
	}

	private void recordSimulationState(ReportContext reportContext) {
		BatchStatusReportPluginData.Builder builder = BatchStatusReportPluginData.builder();
		builder.setReportLabel(reportLabel);
		reportContext.releaseOutput(builder.build());
	}

}
