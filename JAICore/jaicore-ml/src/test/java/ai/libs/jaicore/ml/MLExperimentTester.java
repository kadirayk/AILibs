package ai.libs.jaicore.ml;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;
import org.junit.Test;

import ai.libs.jaicore.basic.SQLAdapter;
import ai.libs.jaicore.experiments.ExperimentDBEntry;
import ai.libs.jaicore.experiments.ExperimentRunner;
import ai.libs.jaicore.experiments.IExperimentIntermediateResultProcessor;
import ai.libs.jaicore.experiments.IExperimentSetConfig;
import ai.libs.jaicore.experiments.IExperimentSetEvaluator;
import ai.libs.jaicore.experiments.databasehandle.ExperimenterSQLHandle;
import ai.libs.jaicore.experiments.exceptions.ExperimentDBInteractionFailedException;
import ai.libs.jaicore.experiments.exceptions.ExperimentEvaluationFailedException;
import ai.libs.jaicore.experiments.exceptions.IllegalExperimentSetupException;
import ai.libs.jaicore.experiments.exceptions.IllegalKeyDescriptorException;
import ai.libs.jaicore.ml.evaluation.evaluators.weka.SingleRandomSplitClassifierEvaluator;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Test for the experiment package in the context of ml experiments.
 *
 * @author fmohr, mwever
 */
public class MLExperimentTester implements IExperimentSetEvaluator {

	private static final ISpecificMLExperimentConfig config = ConfigCache.getOrCreate(ISpecificMLExperimentConfig.class);
	private boolean conductedExperiment = false;

	@Override
	public void evaluate(final ExperimentDBEntry experimentEntry,
			final IExperimentIntermediateResultProcessor processor) throws ExperimentEvaluationFailedException {
		try {
			if (this.config.getDatasetFolder() == null || (!this.config.getDatasetFolder().exists())) {
				throw new IllegalArgumentException(
						"config specifies invalid dataset folder " + this.config.getDatasetFolder());
			}
			Map<String, String> description = experimentEntry.getExperiment().getValuesOfKeyFields();
			Classifier c = AbstractClassifier.forName(description.get("classifier"), null);
			Instances data = new Instances(new BufferedReader(new FileReader(
					new File(this.config.getDatasetFolder() + File.separator + description.get("dataset") + ".arff"))));
			data.setClassIndex(data.numAttributes() - 1);
			int seed = Integer.valueOf(description.get("seed"));

			System.out.println(c.getClass().getName());
			Map<String, Object> results = new HashMap<>();
			SingleRandomSplitClassifierEvaluator eval = new SingleRandomSplitClassifierEvaluator(data);
			eval.setSeed(seed);
			double loss = eval.evaluate(c);

			results.put("loss", loss);
			processor.processResults(results);
			this.conductedExperiment = true;
		} catch (Exception e) {
			throw new ExperimentEvaluationFailedException(e);
		}
	}

	@Test
	public void testExperimentRunnerForMLExperiment() throws ExperimentDBInteractionFailedException, IllegalExperimentSetupException {
		ExperimentRunner runner = new ExperimentRunner(config, new MLExperimentTester(), new ExperimenterSQLHandle(config));
		runner.randomlyConductExperiments(true);
		assertTrue(this.conductedExperiment);
	}
}
