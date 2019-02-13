package java.org.cytoscape.analyzer;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class ShowResultsPanelTaskFactory extends AbstractTaskFactory {
	final AnalyzerManager manager;

	public ShowResultsPanelTaskFactory(final AnalyzerManager manager) {
		super();
		this.manager = manager;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ShowResultsPanelTask(manager));
	}

	public boolean isReady() {
		return true;
	}

}