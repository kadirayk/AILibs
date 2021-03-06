package ai.libs.jaicore.graphvisualizer.plugin.solutionperformanceplotter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.libs.jaicore.basic.algorithm.events.AlgorithmEvent;
import ai.libs.jaicore.basic.algorithm.events.ScoredSolutionCandidateFoundEvent;
import ai.libs.jaicore.graphvisualizer.events.gui.GUIEvent;
import ai.libs.jaicore.graphvisualizer.plugin.ASimpleMVCPluginController;
import ai.libs.jaicore.graphvisualizer.plugin.controlbar.ResetEvent;
import ai.libs.jaicore.graphvisualizer.plugin.timeslider.GoToTimeStepEvent;

public class SolutionPerformanceTimelinePluginController extends ASimpleMVCPluginController<SolutionPerformanceTimelinePluginModel, SolutionPerformanceTimelinePluginView> {

	private Logger logger = LoggerFactory.getLogger(SolutionPerformanceTimelinePluginController.class);

	public SolutionPerformanceTimelinePluginController(final SolutionPerformanceTimelinePluginModel model, final SolutionPerformanceTimelinePluginView view) {
		super(model, view);
	}

	@Override
	public void handleGUIEvent(final GUIEvent guiEvent) {
		if (guiEvent instanceof ResetEvent || guiEvent instanceof GoToTimeStepEvent) {
			this.getModel().clear();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleAlgorithmEventInternally(final AlgorithmEvent algorithmEvent) {
		if (algorithmEvent instanceof ScoredSolutionCandidateFoundEvent) {
			this.logger.debug("Received solution event {}", algorithmEvent);
			ScoredSolutionCandidateFoundEvent<?,?> event = (ScoredSolutionCandidateFoundEvent<?,?>)algorithmEvent;
			if (!(event.getScore() instanceof Number)) {
				this.logger.warn("Received SolutionCandidateFoundEvent, but the score is of type {}, which is not a number.", event.getScore().getClass().getName());
				return;
			}
			this.logger.debug("Adding solution to model and updating view.");
			this.getModel().addEntry((ScoredSolutionCandidateFoundEvent<?, ? extends Number>)event);
		} else {
			this.logger.trace("Received and ignored irrelevant event {}", algorithmEvent);
		}
	}

}
