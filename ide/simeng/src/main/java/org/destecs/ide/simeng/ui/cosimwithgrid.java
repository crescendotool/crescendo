package org.destecs.ide.simeng.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

<<<<<<< .mine
import org.eclipse.jface.viewers.Viewer;
=======
import org.destecs.core.simulationengine.IEngineListener;
import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.launcher.Clp20SimLauncher;
import org.destecs.ide.simeng.internal.core.VdmRtBundleLauncher;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
>>>>>>> .r47
import org.eclipse.swt.SWT;
<<<<<<< .mine
import org.eclipse.swt.graphics.Color;
=======
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
>>>>>>> .r47
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
<<<<<<< .mine
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
=======
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
>>>>>>> .r47
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;

public class CoSimStarterView extends ViewPart
{

	private Composite top = null;
	private Button buttonDt = null;
	private Button buttonCt = null;
	private Button buttonFaults = null;
	private Button buttonRun = null;
<<<<<<< .mine

	public CoSimStarterView() {
=======

	public CoSimStarterView()
	{
>>>>>>> .r47
		super();

	}

	enum ArrowDirection
	{
		Up, Down, Right, Left
	}
<<<<<<< .mine

	public void createPartControl(Composite parent) {
=======

	public void createPartControl(Composite parent)
	{
>>>>>>> .r47

		parent.setLayout(new GridLayout(2, false));

<<<<<<< .mine
		// DT Line
		Label dtLabel = new Label(parent, SWT.NONE);
		dtLabel.setText("DT Path:");
=======
		buttonRun = new Button(top, SWT.PUSH);
		buttonRun.setBounds(50, 50, 150, 150);
		buttonRun.setText("RUN");
		buttonRun.pack();
		buttonRun.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				startSimulation();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		Image image = new Image(buttonDt.getDisplay(), this.getClass().getResourceAsStream("VDM.png"));
		buttonDt.setImage(image);
		buttonDt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				try
				{
>>>>>>> .r47

<<<<<<< .mine
		Text nameText = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(gridData);
		nameText.setText("Insert DT model path here");
=======
					PlatformUI.getWorkbench().showPerspective("org.overture.ide.vdmrt.ui.VdmRtPerspective", PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				} catch (WorkbenchException e1)
				{
>>>>>>> .r47

//		Button checkDt = new Button(parent, SWT.PUSH);
//		checkDt.setText("Check");

<<<<<<< .mine
		// CT Line
		Label ctLabel = new Label(parent, SWT.NONE);
		ctLabel.setText("CT Path:");
=======
		image = new Image(buttonCt.getDisplay(), this.getClass().getResourceAsStream("20sim.jpg"));
		buttonCt.setImage(image);
		buttonCt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				try
				{
					Runtime.getRuntime().exec("C:\\Program Files\\20-sim 4.1\\bin\\20sim.exe");
				} catch (IOException e1)
				{
>>>>>>> .r47

		Text ctText = new Text(parent, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		ctText.setLayoutData(gridData);
		ctText.setText("Insert CT model path here");

<<<<<<< .mine
//		Button checkCt = new Button(parent, SWT.PUSH);
//		checkCt.setText("Check");
=======
		image = new Image(buttonFaults.getDisplay(), this.getClass().getResourceAsStream("Fault.png"));
		buttonFaults.setImage(image);
>>>>>>> .r47

<<<<<<< .mine
		Button runButton = new Button(parent,SWT.PUSH);
		runButton.setText("Run CoSim");
		gridData = new GridData();
		//gridData.horizontalSpan = 2;
		runButton.setLayoutData(gridData);
		
		Label warningLabel = new Label(parent, SWT.NONE);
		
		
//		Label sportsLabel = new Label(parent, SWT.NONE);
//		sportsLabel.setText("Sports played:");
//		gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		sportsLabel.setLayoutData(gridData);
//
//		List sportsList = new List(parent, SWT.BORDER | SWT.MULTI);
//		gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		gridData.horizontalAlignment = SWT.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.FILL;
//		gridData.grabExcessVerticalSpace = true;
//		sportsList.setLayoutData(gridData);
//		sportsList.add("Hockey");
//		sportsList.add("Street Hockey");
=======
		top.addListener(SWT.Paint, new Listener()
		{
>>>>>>> .r47

<<<<<<< .mine
=======
			public void handleEvent(Event event)
			{

				event.gc.setLineWidth(4);
				// DT - CT
				event.gc.drawLine(250, 250 + 75, 500, 250 + 75);

				drawArrow(event.gc, ArrowDirection.Right, new Point(500, 250 + 75));
				drawArrow(event.gc, ArrowDirection.Left, new Point(250, 250 + 75));
				// Fault - DT
				event.gc.drawLine(250 - 75, 250, 300, 20 + 75);
				drawArrow(event.gc, ArrowDirection.Down, new Point(250 - 75, 250));
				// Fault - CT
				event.gc.drawLine(500 + 75, 250, 300 + 150, 20 + 75);
				drawArrow(event.gc, ArrowDirection.Down, new Point(500 + 75, 250));
				// Fault - communication
				event.gc.drawLine(300 + 75, 20 + 150, 300 + 75, 250 + 75);

				// event.gc.drawLine(300+75, 20+150 , 300+75+20, 20+150+20);
				// drawArrow(event.gc, ArrowDirection.Up, new Point(300 + 75,20 + 150));
				drawArrow(event.gc, ArrowDirection.Down, new Point(300 + 75, 250 + 75));
			}
		});
>>>>>>> .r47
	}

<<<<<<< .mine
	//
	// buttonDt = new Button(top, SWT.FLAT);
	// buttonDt.setBounds(new Rectangle(100, 250, 150, 150));
	// buttonCt = new Button(top, SWT.FLAT);
	// buttonCt.setBounds(new Rectangle(500, 250, 150, 150));
	// buttonFaults = new Button(top, SWT.FLAT);
	// buttonFaults.setBounds(new Rectangle(300, 20, 150, 150));
	//
	// buttonRun = new Button(top,SWT.PUSH);
	// buttonRun.setBounds(50,50,150,150);
	// buttonRun.setText("RUN");
	// buttonRun.pack();
	//
	// Image image = new Image(buttonDt.getDisplay(), this.getClass()
	// .getResourceAsStream("VDM.png"));
	// buttonDt.setImage(image);
	// buttonDt.addSelectionListener(new
	// org.eclipse.swt.events.SelectionAdapter() {
	// public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
	// {
	// try
	// {
	//
	// PlatformUI.getWorkbench()
	// .showPerspective("org.overture.ide.vdmrt.ui.VdmRtPerspective",
	// PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow());
	// } catch (WorkbenchException e1)
	// {
	//
	// }
	// }
	// });
	//
	// image = new Image(buttonCt.getDisplay(), this.getClass()
	// .getResourceAsStream("20sim.jpg"));
	// buttonCt.setImage(image);
	// buttonCt.addSelectionListener(new
	// org.eclipse.swt.events.SelectionAdapter() {
	// public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
	// {
	// try
	// {
	// Runtime.getRuntime()
	// .exec("C:\\Program Files\\20-sim 4.1\\bin\\20sim.exe");
	// } catch (IOException e1)
	// {
	//
	// }
	// }
	// });
	//
	// image = new Image(buttonFaults.getDisplay(), this.getClass()
	// .getResourceAsStream("Fault.png"));
	// buttonFaults.setImage(image);
	//
	// top.addListener(SWT.Paint, new Listener() {
	//
	// public void handleEvent(Event event)
	// {
	//
	// event.gc.setLineWidth(4);
	// // DT - CT
	// event.gc.drawLine(250, 250 + 75, 500, 250 + 75);
	//
	// drawArrow(event.gc, ArrowDirection.Right, new Point(500, 250 + 75));
	// drawArrow(event.gc, ArrowDirection.Left, new Point(250, 250 + 75));
	// // Fault - DT
	// event.gc.drawLine(250 - 75, 250, 300, 20 + 75);
	// drawArrow(event.gc, ArrowDirection.Down, new Point(250 - 75, 250));
	// // Fault - CT
	// event.gc.drawLine(500 + 75, 250, 300 + 150, 20 + 75);
	// drawArrow(event.gc, ArrowDirection.Down, new Point(500 + 75, 250));
	// // Fault - communication
	// event.gc.drawLine(300 + 75, 20 + 150, 300 + 75, 250 + 75);
	//
	// // event.gc.drawLine(300+75, 20+150 , 300+75+20, 20+150+20);
	// //drawArrow(event.gc, ArrowDirection.Up, new Point(300 + 75,20 + 150));
	// drawArrow(event.gc, ArrowDirection.Down, new Point(300 + 75, 250 + 75));
	// }
	// });
	// }
	//
	//
	// private void drawArrow(GC gc, ArrowDirection dir, Point point)
	// {
	// final int LENGTH = 20;
	// final int WIDTH = 15;
	// switch (dir)
	// {
	// case Up:
	// gc.drawLine(point.x, point.y, point.x + WIDTH, point.y + LENGTH);
	// gc.drawLine(point.x, point.y, point.x - WIDTH, point.y + LENGTH);
	// break;
	//
	// case Down:
	// gc.drawLine(point.x, point.y, point.x + WIDTH, point.y - LENGTH);
	// gc.drawLine(point.x, point.y, point.x - WIDTH, point.y - LENGTH);
	// break;
	//
	// case Right:
	// gc.drawLine(point.x, point.y, point.x - LENGTH, point.y - WIDTH);
	// gc.drawLine(point.x, point.y, point.x - LENGTH, point.y + WIDTH);
	// break;
	// case Left:
	// gc.drawLine(point.x, point.y, point.x + LENGTH, point.y - WIDTH);
	// gc.drawLine(point.x, point.y, point.x + LENGTH, point.y + WIDTH);
	// break;
	// }
	//
	// }

=======
>>>>>>> .r47
<<<<<<< .mine
=======
	private void drawArrow(GC gc, ArrowDirection dir, Point point)
	{
		final int LENGTH = 20;
		final int WIDTH = 15;
		switch (dir)
		{
			case Up:
				gc.drawLine(point.x, point.y, point.x + WIDTH, point.y + LENGTH);
				gc.drawLine(point.x, point.y, point.x - WIDTH, point.y + LENGTH);
				break;

			case Down:
				gc.drawLine(point.x, point.y, point.x + WIDTH, point.y - LENGTH);
				gc.drawLine(point.x, point.y, point.x - WIDTH, point.y - LENGTH);
				break;

			case Right:
				gc.drawLine(point.x, point.y, point.x - LENGTH, point.y - WIDTH);
				gc.drawLine(point.x, point.y, point.x - LENGTH, point.y + WIDTH);
				break;
			case Left:
				gc.drawLine(point.x, point.y, point.x + LENGTH, point.y - WIDTH);
				gc.drawLine(point.x, point.y, point.x + LENGTH, point.y + WIDTH);
				break;
		}

	}

>>>>>>> .r47
	public void setFocus()
	{

	}

	private void startSimulation()
	{
		final String messageViewId = "org.destecs.ide.simeng.ui.views.SimulationMessagesView";
		final String engineViewId = "org.destecs.ide.simeng.ui.views.SimulationEngineView";
		final String simulationViewId = "org.destecs.ide.simeng.ui.views.SimulationView";

		Job runSimulation = null;
		try
		{
			final SimulationEngine engine = new SimulationEngine(new File("C:\\destecs\\workspace\\watertank_new\\watertank.csc"));

			engine.engineListeners.add(new EngineListener(getInfoTableView(engineViewId)));
			engine.messageListeners.add(new MessageListener(getInfoTableView(messageViewId)));
			engine.simulationListeners.add(new SimulationListener(getInfoTableView(simulationViewId)));
			runSimulation = new Job("Simulation")
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					final List<Throwable> exceptions = new Vector<Throwable>();
					class SimulationRunner extends Thread
					{
						public SimulationRunner()
						{
							setDaemon(true);
							setName("Simulation Engine");
						}

						public void run()
						{
							ISafeRunnable runnable = new ISafeRunnable()
							{

								public void run() throws Exception
								{
									runSumulation(engine);

								}

								public void handleException(Throwable e)
								{
									exceptions.add(e);
								}
							};

							SafeRunner.run(runnable);
						};

					}
					
					Thread simulationEngineThread = new SimulationRunner();
					
					simulationEngineThread.start();
					
					while (!simulationEngineThread.isInterrupted() && simulationEngineThread.isAlive())
					{
						sleep(2000);

						if (monitor.isCanceled())
						{
							engine.forceSimulationStop();
						}
					}

					if (exceptions.size() == 0)
					{
						return Status.OK_STATUS;
					} else
					{
						for (Throwable throwable : exceptions)
						{
							throwable.printStackTrace();
						}
						return new Status(IStatus.ERROR, "", "Simulation faild", exceptions.get(0));
					}

				}

				private void sleep(long i)
				{
					try
					{
						Thread.sleep(i);
					} catch (InterruptedException e)
					{
						//Ignore it
					}
					
				}
			};

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		runSimulation.schedule();
	}

	private InfoTableView getInfoTableView(String id)
	{
		IViewPart v;
		try
		{
			v = super.getSite().getPage().showView(id);
			return (InfoTableView) v;
		} catch (PartInitException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	private void runSumulation(final SimulationEngine engine)
			throws ModelPathNotValidException, MalformedURLException,
			InvalidEndpointsExpection, InvalidSimulationLauncher,
			FileNotFoundException
	{
		engine.setDtSimulationLauncher(new VdmRtBundleLauncher(new File("C:\\destecs\\workspace\\watertank_new\\model")));
		engine.setDtModel(new File("C:\\destecs\\workspace\\watertank_new\\model"));
		engine.setDtEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));

		engine.setCtSimulationLauncher(new Clp20SimLauncher());
		engine.setCtModel(new File("C:\\destecs\\workspace\\watertank_new\\WaterTank.emx"));
		engine.setCtEndpoint(new URL("http://localhost:1580"));

		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minLevel", 1.0));
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxLevel", 2.0));

		engine.simulate(shareadDesignParameters, 5);
	}

	private static class EngineListener implements IEngineListener
	{
		private InfoTableView view;

		public EngineListener(InfoTableView view)
		{
			this.view = view;
			this.view.resetBuffer();
		}

		public void info(Simulator simulator, String message)
		{
			List<String> l = new Vector<String>();
			l.add(simulator.toString());
			l.add(message);
			view.setDataList(l);
		}
	}

}
