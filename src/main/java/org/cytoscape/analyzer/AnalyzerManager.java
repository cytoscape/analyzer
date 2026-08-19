package org.cytoscape.analyzer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

public class AnalyzerManager implements SessionLoadedListener {

	final private TaskManager<?, ?> taskManager;
	final private CyServiceRegistrar registrar; 
	final private CySwingApplication application;
	private AvailableCommands availableCommands;
	
	private final Properties props;

	private AboutDialog aboutDialog;
	private ResultsPanel resultsPanel;

	public AnalyzerManager(CyServiceRegistrar reg, CySwingApplication desktop) {
		registrar = reg;
		taskManager = registrar.getService(TaskManager.class);
		availableCommands = registrar.getService(AvailableCommands.class);
		application = desktop;
		props = loadProperties("/analyzer.properties");
		
		resultsPanel = new ResultsPanel(this);
		resultsPanel.getAboutButton().addActionListener(e -> showAboutDialog());
		resultsPanel.getCloseButton().addActionListener(e -> unregisterResultsPanel());
	}

	private boolean isCyplotInstalled() {
        return availableCommands.getNamespaces().contains("cyplot");
    }

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		unregisterResultsPanel();
	}
	
	/**
	 * @param degreeColumn Node column to plot: <i>Degree</i> for undirected results, or the
	 *        <i>Indegree</i>/<i>Outdegree</i> the user picked for directed ones.
	 */
	public void makeDegreeHisto(String degreeColumn) {
		if (NetworkAnalyzer.verbose) 	System.out.println("makeDegreeHisto: " + degreeColumn);
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
			@Override
			public void allFinished(FinishStatus finishStatus) {
				args.put("xCol",degreeColumn);
				TaskIterator ti = commandTF.createTaskIterator("cyplot","histogram",args, null);
				taskManager.execute(ti);
			 		}
				}
				);
			} else {
				return;
			}	
		} else {
			args.put("xCol",degreeColumn);
			TaskIterator ti = commandTF.createTaskIterator("cyplot","histogram",args, null);
			taskManager.execute(ti);
		}
	}
	
	/**
	 * @param degreeColumn Node column for the x axis: <i>Degree</i> for undirected results, or the
	 *        <i>Indegree</i>/<i>Outdegree</i> the user picked for directed ones.
	 */
	public void makeBetweenScatter(String degreeColumn) {
		if (NetworkAnalyzer.verbose) 	System.out.println("makeBetweenScatter: " + degreeColumn);
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
			@Override
			public void allFinished(FinishStatus finishStatus) {
				args.put("xCol",degreeColumn);
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
			args.put("xCol",degreeColumn);
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

	public void registerResultsPanel() {
		if (isRegistered)
			return;
		
		registrar.registerAllServices(resultsPanel);
		resultsPanel.update();
		var panel = application.getCytoPanel(CytoPanelName.EAST);
		panel.setState(CytoPanelState.DOCK);
		isRegistered = true;
	}
	
	// when a session closes, unregister the panel.
	public void unregisterResultsPanel() {
		if (!isRegistered)
			return;
		
		registrar.unregisterAllServices(resultsPanel);
		isRegistered = false;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
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
	
	/**
	 * Returns true if the current operating system is Mac OS X.
	 * @return true if the current OS is Mac OS X and false otherwise.
	 */
	public static boolean isMac() {
		return System.getProperty("os.name").startsWith("Mac OS X");
	}
	
	private void showAboutDialog() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> showAboutDialog());
			return;
		}
		
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(SwingUtilities.getWindowAncestor(resultsPanel), this, registrar);
			
			if (isMac()) // Workaround for bug: https://bugs.openjdk.java.net/browse/JDK-8182638
				aboutDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowActivated(WindowEvent evt) {
						evt.getWindow().toFront();
					}
				});
		}

		if (!aboutDialog.isVisible()) {
			aboutDialog.setLocationRelativeTo(null);
			aboutDialog.setVisible(true);
		}
	}

	private static Properties loadProperties(String name) {
		var props = new Properties();

		try {
			var in = CyActivator.class.getResourceAsStream(name);

			if (in != null) {
				props.load(in);
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return props;
	}
}
