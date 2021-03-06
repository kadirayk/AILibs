package ai.libs.jaicore.ml.evaluation.evaluators.weka.factory;

import ai.libs.jaicore.basic.algorithm.exceptions.AlgorithmException;
import ai.libs.jaicore.ml.core.dataset.sampling.inmemory.ASamplingAlgorithm;
import ai.libs.jaicore.ml.core.dataset.sampling.inmemory.factories.interfaces.ISamplingAlgorithmFactory;
import ai.libs.jaicore.ml.core.dataset.util.StratifiedSplit;
import ai.libs.jaicore.ml.core.dataset.weka.WekaInstance;
import ai.libs.jaicore.ml.core.dataset.weka.WekaInstances;
import ai.libs.jaicore.ml.evaluation.evaluators.weka.ExtrapolatedSaturationPointEvaluator;
import ai.libs.jaicore.ml.evaluation.evaluators.weka.IClassifierEvaluator;
import ai.libs.jaicore.ml.learningcurve.extrapolation.LearningCurveExtrapolationMethod;
import weka.core.Instances;

public class ExtrapolatedSaturationPointEvaluatorFactory implements IClassifierEvaluatorFactory {

	private int[] anchorpoints;
	private ISamplingAlgorithmFactory<WekaInstances<Object>, ? extends ASamplingAlgorithm<WekaInstances<Object>>> subsamplingAlgorithmFactory;
	private double trainSplitForAnchorpointsMeasurement;
	private LearningCurveExtrapolationMethod extrapolationMethod;

	public ExtrapolatedSaturationPointEvaluatorFactory(final int[] anchorpoints,
			final ISamplingAlgorithmFactory<WekaInstances<Object>, ? extends ASamplingAlgorithm<WekaInstances<Object>>> subsamplingAlgorithmFactory,
					final double trainSplitForAnchorpointsMeasurement, final LearningCurveExtrapolationMethod extrapolationMethod) {
		super();
		this.anchorpoints = anchorpoints;
		this.subsamplingAlgorithmFactory = subsamplingAlgorithmFactory;
		this.trainSplitForAnchorpointsMeasurement = trainSplitForAnchorpointsMeasurement;
		this.extrapolationMethod = extrapolationMethod;
	}

	@Override
	public IClassifierEvaluator getIClassifierEvaluator(final Instances dataset, final long seed) throws ClassifierEvaluatorConstructionFailedException {
		try {
			StratifiedSplit<WekaInstance<Object>, Object, WekaInstances<Object>> split = new StratifiedSplit<>(new WekaInstances<>(dataset), seed);
			split.doSplit(0.7);
			WekaInstances<Object> train = split.getTrainingData();
			WekaInstances<Object> test = split.getTestData();
			return new ExtrapolatedSaturationPointEvaluator<>(this.anchorpoints, this.subsamplingAlgorithmFactory, train,
					this.trainSplitForAnchorpointsMeasurement, this.extrapolationMethod, seed, test);
		} catch (ClassNotFoundException | AlgorithmException e) {
			throw new ClassifierEvaluatorConstructionFailedException(e);
		}
	}

}
