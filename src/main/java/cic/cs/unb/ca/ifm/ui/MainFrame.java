package cic.cs.unb.ca.ifm.ui;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.flow.ui.FlowMonitorPane;
import cic.cs.unb.ca.flow.ui.FlowOfflinePane;
import cic.cs.unb.ca.flow.ui.FlowVisualPane;
import cic.cs.unb.ca.guava.Event.FlowVisualEvent;
import cic.cs.unb.ca.guava.GuavaMgr;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import com.google.common.eventbus.Subscribe;
import swing.common.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 7419600803861028585L;

	private FlowOfflinePane offLinePane;
	private FlowMonitorPane monitorPane;
	private FlowVisualPane visualPane;
	private ListComponents listComponents;


	public MainFrame() throws HeadlessException {
		super("CICFlowMeter");
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().getInsets().set(5, 5, 5, 5);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("CIC_Logo.png")));
		
		setMinimumSize(new Dimension(700,500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                dispose();
                System.gc();
            }
        });

		initMenu();
		
		offLinePane = new FlowOfflinePane();
        monitorPane = new FlowMonitorPane();
        visualPane = new FlowVisualPane();
        getContentPane().add(monitorPane,BorderLayout.CENTER);

        GuavaMgr.getInstance().getEventBus().register(this);

		setVisible(true);
	}
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(arg0 -> {
            FlowMgr.getInstance().destroy();
            System.exit(EXIT_ON_CLOSE);
        });
		mnFile.add(itemExit);
		
		JMenu mnNetwork = new JMenu("NetWork");
		menuBar.add(mnNetwork);
		
		JMenuItem itemOffline = new JMenuItem("Offline");
		itemOffline.addActionListener(e -> SwingUtils.setBorderLayoutPane(getContentPane(),offLinePane,BorderLayout.CENTER));
		mnNetwork.add(itemOffline);
		
		JMenuItem itemRealtime = new JMenuItem("Realtime");
		itemRealtime.addActionListener(e -> SwingUtils.setBorderLayoutPane(getContentPane(),monitorPane,BorderLayout.CENTER));
		mnNetwork.add(itemRealtime);

        /*JMenu mnAction = new JMenu("Action");
        menuBar.add(mnAction);

        JMenuItem itemVisual = new JMenuItem("Visual");
        itemVisual.addActionListener(actionEvent -> SwingUtils.setBorderLayoutPane(getContentPane(),visualPane,BorderLayout.CENTER));
        mnAction.add(itemVisual);*/

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem itemOutputColumns = new JMenuItem("Output Columns");

		listComponents = new ListComponents();

		listComponents.setSaveActionListener(columns -> {
					saveSelectedColumns(columns);
					listComponents.setVisible(false);
					return null;
				}
		);

		itemOutputColumns.addActionListener(e -> {
			List<FlowFeature> featureColumns = FlowMgr.getInstance().getFeatureColumns();
			List<ListComponents.Column> columns = featureColumns.stream().map(flowFeature -> new ListComponents.Column(flowFeature, flowFeature.getName()))
					.collect(Collectors.toList());
			listComponents.setSelectedColumns(columns);
			listComponents.setVisible(true);
		});
		mnSettings.add(itemOutputColumns);

        JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(e -> AboutDialog.show(MainFrame.this));
		mnHelp.add(itemAbout);
	}

	private void saveSelectedColumns(java.util.List<ListComponents.Column> selectedColumns) {
		java.util.List<FlowFeature> selectedFeatures = selectedColumns.stream()
				.map(ListComponents.Column::getFeature)
				.collect(Collectors.toList());
		FlowMgr.getInstance().setFeatureColumns(selectedFeatures);
	}

	@Subscribe
    public void listenGuava(FlowVisualEvent evt) {
        System.out.println("file: " + evt.getCsv_file().getPath());

        if (visualPane == null) {
            visualPane = new FlowVisualPane(evt.getCsv_file());
        } else {
            visualPane.visualFile(evt.getCsv_file());
        }

        SwingUtils.setBorderLayoutPane(getContentPane(), visualPane, BorderLayout.CENTER);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GuavaMgr.getInstance().getEventBus().unregister(this);
    }
}
