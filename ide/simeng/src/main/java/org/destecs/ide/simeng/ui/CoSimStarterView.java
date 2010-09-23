package org.destecs.ide.simeng.ui;

import java.io.IOException;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;

public class CoSimStarterView extends ViewPart {

	private Composite top = null;
	private Button buttonDt = null;
	private Button buttonCt = null;
	private Button buttonFaults = null;
	private Button buttonRun = null;
	
	public CoSimStarterView() {
		super();
		
	}

	enum ArrowDirection {
		Up, Down, Right, Left
	}
	
	public void createPartControl(Composite parent) {
		top = new Composite(parent, SWT.NONE);
		// TODO Auto-generated method stub

		buttonDt = new Button(top, SWT.FLAT);
		buttonDt.setBounds(new Rectangle(100, 250, 150, 150));
		buttonCt = new Button(top, SWT.FLAT);
		buttonCt.setBounds(new Rectangle(500, 250, 150, 150));
		buttonFaults = new Button(top, SWT.FLAT);
		buttonFaults.setBounds(new Rectangle(300, 20, 150, 150));

		buttonRun = new Button(top,SWT.PUSH);
		buttonRun.setBounds(50,50,150,150);
		buttonRun.setText("RUN");
		buttonRun.pack();
		
		Image image = new Image(buttonDt.getDisplay(), this.getClass()
				.getResourceAsStream("VDM.png"));
		buttonDt.setImage(image);
		buttonDt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				try
				{

					PlatformUI.getWorkbench()
							.showPerspective("org.overture.ide.vdmrt.ui.VdmRtPerspective",
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow());
				} catch (WorkbenchException e1)
				{

				}
			}
		});

		image = new Image(buttonCt.getDisplay(), this.getClass()
				.getResourceAsStream("20sim.jpg"));
		buttonCt.setImage(image);
		buttonCt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				try
				{
					Runtime.getRuntime()
							.exec("C:\\Program Files\\20-sim 4.1\\bin\\20sim.exe");
				} catch (IOException e1)
				{

				}
			}
		});

		image = new Image(buttonFaults.getDisplay(), this.getClass()
				.getResourceAsStream("Fault.png"));
		buttonFaults.setImage(image);

		top.addListener(SWT.Paint, new Listener() {

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
				//drawArrow(event.gc, ArrowDirection.Up, new Point(300 + 75,20 + 150));
				drawArrow(event.gc, ArrowDirection.Down, new Point(300 + 75, 250 + 75));
			}
		});
	}


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

	public void setFocus() {

	}

}
