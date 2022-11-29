package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.guava.GuavaMgr;
import cic.cs.unb.ca.ifm.ui.ListComponents;
import cic.cs.unb.ca.ifm.ui.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class App {
	public static final Logger logger = LoggerFactory.getLogger(App.class);
	public static void init() {
		FlowMgr.getInstance().init();
		GuavaMgr.getInstance().init();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			logger.warn("Unable to instantiate system look and feel", e);
		}
		
		EventQueue.invokeLater(() -> {
            try {
                init();
                new MainFrame();
            } catch (Exception e) {
				//logger.debug(e.getMessage());
				logger.error("Error" ,e);
            }
        });
	}
}
