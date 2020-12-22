package org.cytoscape.analyzer.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoveDupEdgesTaskFactory extends AbstractTaskFactory {

	public RemoveDupEdgesTaskFactory() {
	}

  @Override
  public TaskIterator createTaskIterator() {
    return new TaskIterator(new RemoveDupEdgesTask());
  }

}
