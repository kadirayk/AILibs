package ai.libs.jaicore.search.util;

import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.libs.jaicore.basic.algorithm.events.AlgorithmEvent;
import ai.libs.jaicore.graphvisualizer.events.graph.GraphInitializedEvent;
import ai.libs.jaicore.graphvisualizer.events.graph.NodeAddedEvent;
import ai.libs.jaicore.graphvisualizer.events.graph.NodeTypeSwitchEvent;
import ai.libs.jaicore.search.core.interfaces.AOptimalPathInORGraphSearch;
import ai.libs.jaicore.search.model.travesaltree.Node;
import ai.libs.jaicore.search.model.travesaltree.NodeExpansionDescription;
import ai.libs.jaicore.search.probleminputs.GraphSearchInput;
import ai.libs.jaicore.search.structure.graphgenerator.NodeGoalTester;
import ai.libs.jaicore.search.structure.graphgenerator.SingleRootGenerator;

public class GraphSanityChecker<N, A> extends AOptimalPathInORGraphSearch<GraphSearchInput<N, A>, N, A, Double> {

	private Logger logger = LoggerFactory.getLogger(GraphSanityChecker.class);
	private String loggerName;

	private SanityCheckResult sanityCheckResult;
	private final int maxNodesToExpand;
	private boolean detectCycles = true;
	private boolean detectDeadEnds = true;

	public GraphSanityChecker(final GraphSearchInput<N, A> problem, final int maxNodesToExpand) {
		super(problem);
		this.maxNodesToExpand = maxNodesToExpand;
	}

	@Override
	public AlgorithmEvent nextWithException() throws InterruptedException  {
		switch (this.getState()) {
		case CREATED:
			return this.activate();
		case ACTIVE:
			int expanded = 0;
			Stack<Node<N, ?>> open = new Stack<>();
			N root = ((SingleRootGenerator<N>) this.getGraphGenerator().getRootGenerator()).getRoot();
			NodeGoalTester<N> goalTester = (NodeGoalTester<N>) this.getGraphGenerator().getGoalTester();
			open.push(new Node<>(null, root));
			this.post(new GraphInitializedEvent<N>(this.getId(), root));
			while (!open.isEmpty() && expanded < this.maxNodesToExpand) {
				Node<N, ?> node = open.pop();
				if (!node.isGoal()) {
					this.post(new NodeTypeSwitchEvent<>(this.getId(), node, "or_closed"));
				}
				expanded++;
				List<NodeExpansionDescription<N, A>> successors = this.getGraphGenerator().getSuccessorGenerator().generateSuccessors(node.getPoint());
				if (this.detectDeadEnds && successors.isEmpty() && !node.isGoal()) {
					this.sanityCheckResult = new DeadEndDetectedResult<N>(node.getPoint());
					break;
				}
				for (NodeExpansionDescription<N, A> successor : successors) {
					if (this.detectCycles && node.externalPath().contains(successor.getTo())) {
						List<N> path = node.externalPath();
						path.add(successor.getTo());
						this.sanityCheckResult = new CycleDetectedResult<N>(path, node.getPoint());
						break;
					}
					Node<N, ?> newNode = new Node<>(node, successor.getTo());
					newNode.setGoal(goalTester.isGoal(newNode.getPoint()));
					open.add(newNode);
					this.post(new NodeAddedEvent<N>(this.getId(), node.getPoint(), successor.getTo(), newNode.isGoal() ? "or_solution" : "or_open"));
				}
				if (this.sanityCheckResult != null) {
					break;
				}
				if (expanded % 100 == 0 || expanded == this.maxNodesToExpand) {
					this.logger.debug("Expanded {}/{} nodes.", expanded, this.maxNodesToExpand);
				}
			}
			this.shutdown();
			return this.terminate();

		default:
			throw new IllegalStateException("Cannot do anything in state " + this.getState());
		}
	}

	public SanityCheckResult getSanityCheck() {
		return this.sanityCheckResult != null ? this.sanityCheckResult : new GraphSeemsSaneResult();
	}

	@Override
	public String getLoggerName() {
		return this.loggerName;
	}

	@Override
	public void setLoggerName(final String name) {
		this.logger.info("Switching logger from {} to {}", this.logger.getName(), name);
		this.loggerName = name;
		this.logger = LoggerFactory.getLogger(name);
		this.logger.info("Activated logger {} with name {}", name, this.logger.getName());
		super.setLoggerName(this.loggerName + "._orgraphsearch");
	}

	public boolean isDetectCycles() {
		return this.detectCycles;
	}

	public void setDetectCycles(final boolean detectCycles) {
		this.detectCycles = detectCycles;
	}

	public boolean isDetectDeadEnds() {
		return this.detectDeadEnds;
	}

	public void setDetectDeadEnds(final boolean detectDeadEnds) {
		this.detectDeadEnds = detectDeadEnds;
	}
}