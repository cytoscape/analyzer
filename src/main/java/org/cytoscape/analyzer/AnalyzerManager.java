package org.cytoscape.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

import javax.swing.JOptionPane;

public class AnalyzerManager implements SessionLoadedListener {

	final private TaskManager<?, ?> taskManager;
	final private CyServiceRegistrar registrar; 
	final private HashMap<String, String> settings;
	final private CySwingApplication application;
	private AvailableCommands availableCommands;

	private ResultsPanel resultsPanel;

	public AnalyzerManager(final CyServiceRegistrar reg, CySwingApplication desktop) {
		registrar = reg;
		this.taskManager = registrar.getService(TaskManager.class);
		this.availableCommands = registrar.getService(AvailableCommands.class);
		application = desktop;
		resultsPanel = new ResultsPanel(this);
		settings = new HashMap<String, String>();
	}


	private boolean isCyplotInstalled() {
        return availableCommands.getNamespaces().contains("cyplot");
    }

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		unregisterResultsPanel();
		
	}
	public void makeDegreeHisto() {
		if (NetworkAnalyzer.verbose) 	System.out.println("makeDegreeHisto");
		CommandExecutorTaskFactory commandTF = registrar.getService(CommandExecutorTaskFactory.class);
		TaskManager<?,?> taskManager = registrar.getService(TaskManager.class);
		Map<String, Object> args = new HashMap<>();
		if (!isCyplotInstalled()){
		int response = JOptionPane.showConfirmDialog(null, "Do you want to install cyPlot app to use this functionality?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			Map<String, Object> argsApp = new HashMap<>();
			argsApp.put("app","cyplot");
			TaskIterator installCyPlot = commandTF.createTaskIterator("apps","install",argsApp, null);
			taskManager.execute(installCyPlot, new TaskObserver() {
			@Override
			public void taskFinished(ObservableTask task) {}
			public void allFinished(FinishStatus finishStatus) {
				args.put("xCol","Degree");
				TaskIterator ti = commandTF.createTaskIterator("cyplot","histogram",args, null);
				taskManager.execute(ti);
			 		}
				}
				);
			} else {
				return;
			}	
		} else {
			args.put("xCol","Degree");
			TaskIterator ti = commandTF.createTaskIterator("cyplot","histogram",args, null);
			taskManager.execute(ti);
		}
	}

	
	public void makeBetweenScatter() {
		if (NetworkAnalyzer.verbose) 	System.out.println("makeBetweenScatter");
		CommandExecutorTaskFactory commandTF = registrar.getService(CommandExecutorTaskFactory.class);
		TaskManager<?,?> taskManager = registrar.getService(TaskManager.class);
		Map<String, Object> args = new HashMap<>();
		if (!isCyplotInstalled()){
		int response = JOptionPane.showConfirmDialog(null, "Do you want to install cyPlot app to use this functionality?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			Map<String, Object> argsApp = new HashMap<>();
			argsApp.put("app","cyplot");
			TaskIterator installCyPlot = commandTF.createTaskIterator("apps","install",argsApp, null);
			taskManager.execute(installCyPlot, new TaskObserver() {
			@Override
			public void taskFinished(ObservableTask task) {}
			public void allFinished(FinishStatus finishStatus) {
				args.put("xCol","Degree");
				args.put("yCol","BetweennessCentrality");
				TaskIterator ti = commandTF.createTaskIterator("cyplot","scatter",args, null);
				taskManager.execute(ti);
			 			}
					}
				);
			} else {
				return;
			}
		} else {
			args.put("xCol","Degree");
			args.put("yCol","BetweennessCentrality");
			TaskIterator ti = commandTF.createTaskIterator("cyplot","scatter",args, null);
			taskManager.execute(ti);
		}
	}


	public void makeClosenessClusterScatter() {
		if (NetworkAnalyzer.verbose) 	System.out.println("makeClosenessClusterScatter");
		CommandExecutorTaskFactory commandTF = registrar.getService(CommandExecutorTaskFactory.class);
		TaskManager<?,?> taskManager = registrar.getService(TaskManager.class);
		Map<String, Object> args = new HashMap<>();
//		args.put("url",url);
		args.put("x","ClosenessCentrality");
		args.put("y","BetweennessCentrality");
		TaskIterator ti = commandTF.createTaskIterator("cychart","scatter",args, null);
		taskManager.execute(ti);
		
	}

	// create and register the results panel, 
	// and listen for network change events, so we always show the current network stats
	boolean isRegistered = false;
	public void registerResultsPanel()
	{
		if (isRegistered) return;
		registerService(resultsPanel, CytoPanelComponent.class, new Properties());
		registerService(resultsPanel, SetCurrentNetworkListener.class, new Properties());
		CytoPanel panel = application.getCytoPanel(CytoPanelName.EAST);
		panel.setState(CytoPanelState.DOCK);
		isRegistered = true;
		resultsPanel.enableButtons(true);
	}
	
	// when a session closes, unregister the panel.
	public void unregisterResultsPanel()
	{
		if (!isRegistered) return;
		unregisterService(resultsPanel, CytoPanelComponent.class);
		unregisterService(resultsPanel, SetCurrentNetworkListener.class);
		resultsPanel.enableButtons(false);
		isRegistered = false;
	}
	
	public String getSetting(String setting) {		return settings.get(setting);	}

	public void setSetting(String setting, double value) {		setSetting(setting, String.valueOf(value));	}
	public void setSetting(String setting, int value) 	{		setSetting(setting, String.valueOf(value));	}
	public void setSetting(String setting, boolean value) {		setSetting(setting, String.valueOf(value));	}
	public void setSetting(String setting, String value) { 		settings.put(setting, value);	}

	public void executeCommand(String namespace, String command, Map<String, Object> args, boolean synchronous) {
		executeCommand(namespace, command, args, null, synchronous);
	}

	public void executeCommand(String namespace, String command, Map<String, Object> args) {
		executeCommand(namespace, command, args, null, false);
	}

	public void executeCommand(String namespace, String command, Map<String, Object> args, 
	                           TaskObserver observer, boolean synchronous) {
	}

	public void executeTasks(TaskIterator tasks) {
		taskManager.execute(tasks);
	}

	public void executeTasks(TaskIterator tasks, TaskObserver observer) {
		taskManager.execute(tasks, observer);
	}

	public void executeTasks(TaskFactory factory) {
		taskManager.execute(factory.createTaskIterator());
	}

	public void executeTasks(TaskFactory factory, TaskObserver observer) {
		taskManager.execute(factory.createTaskIterator(), observer);
	}

	public <S> S getService(Class<S> serviceClass) {
		return registrar.getService(serviceClass);
	}

	public <S> S getService(Class<S> serviceClass, String filter) {
		return registrar.getService(serviceClass, filter);
	}

	public void registerService(Object service, Class<?> serviceClass, Properties props) {
		registrar.registerService(service, serviceClass, props);
	}

	public void unregisterService(Object service, Class<?> serviceClass) {
		registrar.unregisterService(service, serviceClass);
	}



}
