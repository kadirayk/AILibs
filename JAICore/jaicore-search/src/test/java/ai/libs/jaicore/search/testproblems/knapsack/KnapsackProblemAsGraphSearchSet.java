package ai.libs.jaicore.search.testproblems.knapsack;

import java.util.Set;

import ai.libs.jaicore.basic.algorithm.ReductionBasedAlgorithmTestProblemSet;
import ai.libs.jaicore.testproblems.knapsack.KnapsackConfiguration;
import ai.libs.jaicore.testproblems.knapsack.KnapsackProblem;
import ai.libs.jaicore.testproblems.knapsack.KnapsackProblemSet;
import jaicore.search.model.other.SearchGraphPath;
import jaicore.search.probleminputs.GraphSearchWithSubpathEvaluationsInput;

public class KnapsackProblemAsGraphSearchSet extends ReductionBasedAlgorithmTestProblemSet<GraphSearchWithSubpathEvaluationsInput<KnapsackConfiguration, String, Double>, SearchGraphPath<KnapsackConfiguration, String>, KnapsackProblem, Set<String>> {

	public KnapsackProblemAsGraphSearchSet() {
		super("Knapsack problem as graph search", new KnapsackProblemSet(), new KnapsackToGraphSearchReducer());
	}
}