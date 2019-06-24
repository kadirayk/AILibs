package ai.libs.jaicore.ml.cache;

import com.fasterxml.jackson.annotation.JsonProperty;

import ai.libs.jaicore.ml.core.dataset.IOrderedDataset;

/**
 * Instruction to track a fold-based subset computation for a {@link ReproducibleInstances} object.
 * The original dataset is split into folds using the specified fold technique, e.g., 10-fold cross validation or a simple stratified split.
 * Then, a subset of the created folds is joined as to determine the desired subset of the original set.
 *
 * For example, to obtain the training data in a 5-fold cross validation if the test data corresponds to the 3rd fold, the foldTechnique is "<method for cross validation>(5, <seed>)", and the outIndices are "0,1,3,4"
 *
 * @author fmohr, jnowack
 *
 */
public abstract class FoldBasedSubsetInstruction<I, D extends IOrderedDataset<I>> extends Instruction<I, D> {

	/** Constant string to identify this instruction. */
	public static final String COMMAND_NAME = "buildSubsetViaFolds";

	protected final int[] outIndices;

	/**
	 * Constructor to create a split Instruction that can be converted into json.
	 *
	 * @param foldTechnique
	 *            method used to compute the folds
	 * @param outIndex
	 *            index of the portion to use in the following
	 */
	public FoldBasedSubsetInstruction(@JsonProperty("foldTechnique") final String foldTechnique, @JsonProperty("outIndices") final int... outIndices) {
		this.command = COMMAND_NAME;
		this.parameters.put("foldTechnique", "" + foldTechnique);
		this.parameters.put("outIndices", outIndices);
		this.outIndices = outIndices;
	}
}
