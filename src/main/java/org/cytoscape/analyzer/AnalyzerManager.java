package  java.org.cytoscape.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;


public class AnalyzerManager {

	final TaskManager<?, ?> taskManager;

	final CyServiceRegistrar registrar; 
	final HashMap<String, String> settings;

	public AnalyzerManager(final CyServiceRegistrar registrar) {
		this.registrar = registrar;
		this.taskManager = registrar.getService(TaskManager.class);
		settings = new HashMap<String, String>();
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