package org.cytoscape.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.LookAndFeelUtil;
import org.cytoscape.util.swing.OpenBrowser;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private static final String LOGO = "/img/logo.png";
	public static final String APP_URL = "https://apps.cytoscape.org/apps/networkanalyzer";

	private final String version;
	private final String buildDate;

	private JLabel logoLabel;
	private JEditorPane mainContainer;
	private JPanel buttonPanel;

	private final CyServiceRegistrar registrar;

	public AboutDialog(Window owner, AnalyzerManager manager, CyServiceRegistrar registrar) {
		super(owner, "About NetworkAnalyzer", ModalityType.APPLICATION_MODAL);
		this.registrar = registrar;
		
		version = manager.getProperty("project.version");
		buildDate = manager.getProperty("buildDate");

		getContentPane().setBackground(Color.WHITE);
		
		getContentPane().add(getLogoLabel(), BorderLayout.NORTH);
		getContentPane().add(getMainContainer(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		
		pack();
		setResizable(false);
	}
	
	private JLabel getLogoLabel() {
		if (logoLabel == null) {
			// 1. Load the original image icon
	        var originalIcon = new ImageIcon(getClass().getResource(LOGO));
	        // 2. Calculate 50% of original dimensions
	        int newWidth = originalIcon.getIconWidth() / 2;
	        int newHeight = originalIcon.getIconHeight() / 2;
	        // 3. Scale the image smoothly
	        var scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	        var scaledIcon = new ImageIcon(scaledImage);
	        // 4. Create label with HTML text and explicit dimensions
	        logoLabel = new JLabel(scaledIcon);
	        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
		}
		
		return logoLabel;
	}

	private JEditorPane getMainContainer() {
		if (mainContainer == null) {
			mainContainer = new JEditorPane();
			mainContainer.setMargin(new Insets(10, 10, 10, 10));
			mainContainer.setEditable(false);
			mainContainer.setEditorKit(new HTMLEditorKit());
			mainContainer.addHyperlinkListener(new HyperlinkAction());

			mainContainer.setText(
					"<html><body style='font-family:Arial,Helvetica,sans-serif;'>"
					+ "<p align='center'>"
					+ "<span style='font-size:small;'><b>version " + version + "</b>"
					+ " (" + buildDate + ")<BR>"
					+ "A Cytoscape App</span>"
					+ "</p><BR>"
					
					+ "<hr size='4' noshade>"
					
					+ "<p align='left' style='font-size:small;'>"
					+ "NetworkAnalyzer performs analysis of biological networks and calculates network topology parameters<BR>"
					+ "including the diameter of a network, the average number of neighbors, and the number of connected pairs of nodes.<BR>"
					+ "It also computes the distributions of more complex network parameters such as node degrees, average clustering<BR>"
					+ "coefficients, topological coefficients, and shortest path lengths.<BR>"
					+ "</p>"
					
					+ "<p align='left' style='font-size:small;'>App Homepage:<BR><a href='" + APP_URL + "'>" + APP_URL + "</a></p><BR>"
					
					+ "<hr size='4' noshade>"
					
					+ "<p style='font-size:small'>If you use this app in your research, please cite:</p>"
					+ "<p style='font-family:Courier New,monospace;font-size:small'>"
					+ "Assenov Y, Ramírez F, Schelhorn SE, Lengauer T, Albrecht M<BR>"
					+ "<a href='https://pubmed.ncbi.nlm.nih.gov/18006545/'>Computing topological parameters of biological networks</a><BR>"
					+ "<i>Bioinformatics. 2008;24(2):282-284</i><BR>"
					
					+ "</p><BR>"
					+ "</body></html>"
			);
			
			mainContainer.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_ENTER:
						case KeyEvent.VK_ESCAPE:
							dispose();
							break;
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
				}
			});
		}

		return mainContainer;
	}
	
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			JButton okBtn = new JButton(new AbstractAction("Close") {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			buttonPanel = LookAndFeelUtil.createOkCancelPanel(null, okBtn);
			buttonPanel.setOpaque(false);
			buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")));
			
			LookAndFeelUtil.setDefaultOkCancelKeyStrokes(getRootPane(), okBtn.getAction(), okBtn.getAction());
			getRootPane().setDefaultButton(okBtn);
		}
		
		return buttonPanel;
	}
	
	private class HyperlinkAction implements HyperlinkListener {
		
		@Override
		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				registrar.getService(OpenBrowser.class).openURL(event.getURL().toString());
		}
	}
}