package de.upb.crc901.automl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.upb.crc901.mlplan.core.AbstractMLPlanBuilder;
import de.upb.crc901.mlplan.core.MLPlan;
import de.upb.crc901.mlplan.gui.outofsampleplots.WekaClassifierSolutionCandidateRepresenter;
import jaicore.basic.TimeOut;
import jaicore.graphvisualizer.events.recorder.AlgorithmEventHistory;
import jaicore.graphvisualizer.events.recorder.AlgorithmEventHistoryRecorder;
import jaicore.graphvisualizer.events.recorder.AlgorithmEventHistorySerializer;
import jaicore.graphvisualizer.events.recorder.property.AlgorithmEventPropertyComputer;
import jaicore.graphvisualizer.plugin.nodeinfo.NodeDisplayInfoAlgorithmEventPropertyComputer;
import jaicore.graphvisualizer.plugin.nodeinfo.NodeInfoAlgorithmEventPropertyComputer;
import jaicore.graphvisualizer.plugin.solutionperformanceplotter.ScoredSolutionCandidateInfoAlgorithmEventPropertyComputer;
import jaicore.ml.WekaUtil;
import jaicore.planning.hierarchical.algorithms.forwarddecomposition.graphgenerators.tfd.TFDNodeInfoGenerator;
import jaicore.search.gui.plugins.rollouthistograms.RolloutInfoAlgorithmEventPropertyComputer;
import jaicore.search.model.travesaltree.JaicoreNodeInfoGenerator;
import weka.core.Instances;

public class MLPlanAlgorithmEventHistorySerializationTest {

	@Test
	public void testSerializationAndDeserializationOfAlgorithmEventHistoryOfMLPlan() throws Exception {
		/* load data for segment dataset and create a train-test-split */
		File file = new File("testrsc/car.arff");
		Instances data = new Instances(new FileReader(file));
		data.setClassIndex(data.numAttributes() - 1);
		List<Instances> split = WekaUtil.getStratifiedSplit(data, 0, .7f);

		/* initialize mlplan with a tiny search space, and let it run for 30 seconds */
		AbstractMLPlanBuilder builder = AbstractMLPlanBuilder.forWeka();
		builder.withNodeEvaluationTimeOut(new TimeOut(30, TimeUnit.SECONDS));
		builder.withCandidateEvaluationTimeOut(new TimeOut(10, TimeUnit.SECONDS));
		builder.withTimeOut(new TimeOut(90, TimeUnit.SECONDS));
		builder.withNumCpus(2);

		MLPlan mlplan = new MLPlan(builder, split.get(0));
		mlplan.setPortionOfDataForPhase2(0f);
		mlplan.setLoggerName("mlplan");

		NodeInfoAlgorithmEventPropertyComputer nodeInfoAlgorithmEventPropertyComputer = new NodeInfoAlgorithmEventPropertyComputer();
		List<AlgorithmEventPropertyComputer> algorithmEventPropertyComputers = Arrays.asList(nodeInfoAlgorithmEventPropertyComputer,
				new NodeDisplayInfoAlgorithmEventPropertyComputer<>(new JaicoreNodeInfoGenerator<>(new TFDNodeInfoGenerator())), new RolloutInfoAlgorithmEventPropertyComputer(nodeInfoAlgorithmEventPropertyComputer),
				new ScoredSolutionCandidateInfoAlgorithmEventPropertyComputer(new WekaClassifierSolutionCandidateRepresenter()));

		AlgorithmEventHistoryRecorder recorder = new AlgorithmEventHistoryRecorder(algorithmEventPropertyComputers);
		mlplan.registerListener(recorder);

		mlplan.call();

		AlgorithmEventHistorySerializer eventHistorySerializer = new AlgorithmEventHistorySerializer();
		String serializedAlgorithmEventHistory = eventHistorySerializer.serializeAlgorithmEventHistory(recorder.getHistory());

		AlgorithmEventHistory deserializedAlgorithmEventHistory = new AlgorithmEventHistorySerializer().deserializeAlgorithmEventHistory(serializedAlgorithmEventHistory);

		assertEquals(recorder.getHistory(), deserializedAlgorithmEventHistory);
	}
}