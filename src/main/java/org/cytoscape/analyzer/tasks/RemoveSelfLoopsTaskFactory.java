package org.cytoscape.analyzer.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoveSelfLoopsTaskFactory extends AbstractTaskFactory {

	public RemoveSelfLoopsTaskFactory() {
	}

  @Override
  public TaskIterator createTaskIterator() {
    return new TaskIterator(new RemoveSelfLoopsTask());
  }

}
