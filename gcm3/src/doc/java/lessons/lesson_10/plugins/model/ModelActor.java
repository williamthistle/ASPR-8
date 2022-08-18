package lessons.lesson_10.plugins.model;

import lessons.lesson_10.plugins.disease.DiseaseDataManager;
import lessons.lesson_10.plugins.policy.PolicyDataManager;
import nucleus.ActorContext;

public final class ModelActor {

	public void init(ActorContext actorContext) {
		DiseaseDataManager diseaseDataManager = actorContext.getDataManager(DiseaseDataManager.class);
		actorContext.releaseOutput("Model Actor initializing");
		String tab = "\t";
		actorContext.releaseOutput(tab+"r0 = " + diseaseDataManager.getR0());
		actorContext.releaseOutput(tab+"asymptomatic days = " + diseaseDataManager.getAsymptomaticDays());
		actorContext.releaseOutput(tab+"symptomatic days = " + diseaseDataManager.getSymptomaticDays());		
		PolicyDataManager policyDataManager = actorContext.getDataManager(PolicyDataManager.class);
		actorContext.releaseOutput(tab+"school closing infection rate = "+policyDataManager.getSchoolClosingInfectionRate());
		actorContext.releaseOutput(tab+"distribute vaccine locally = "+policyDataManager.distributeVaccineLocally());
	}
}
