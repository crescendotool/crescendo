package org.destecs.ide.modelmanagement;

import java.io.FileWriter;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowLogCommandHandler extends AbstractHandler {

	public ShowLogCommandHandler(){
		
	}

	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		try{
			 IStructuredSelection  selection  =  (IStructuredSelection)  HandlerUtil.
			 getCurrentSelectionChecked(event);
			 final  IContainer  c  =  (IContainer)  selection.getFirstElement();
			 
			 //System.out.println("ShowLogCommandHandler");		 
			 //System.out.println(c);
			 

			 //possible format
			 FileWriter fw = new FileWriter("C:\\XiaochenPhD\\Development Log.csv");
	         fw.write("Development Log\r\n");
			 fw.write("Revision,Actions,Author,Date,Message,Auto-detected Changes,Exploration Point,Imporatant Change,Tag as Baseline\r\n");
	         fw.write("6,edit,zng,02:18:23 March 8 2011,fixed a bug,,,,\r\n");
	         fw.write("5,edit,niy,12:20:51 March 6 2011,model clean up,,,,\r\n");
	         fw.write("4,edit,zng,19:39:25 March 6 2011,added important detial,,Yes,Yes,Yes\r\n");
	         fw.write(",,,,,> 2 changes in WaterTank (type: CT Mainmodel),,,\r\n");
	         fw.write(",,,,,   > 1 changes in Controller (type: CT Submodel),,,\r\n");
	         fw.write(",,,,,      > 1 changes in Controller2 (type: CT Submodel-Equation),,,\r\n");
	         fw.write(",,,,,         > (type: CT parameter) is changed,,,\r\n");
	         fw.write(",,,,,      > 1 changes in Tank (type: CT Submodel),,,\r\n");
	         fw.write(",,,,,         > (type: CT C) has been added,,,\r\n");
	         fw.write("3,edit,bnk,10:40:33 March 5 2011,controller2 is changed,,,,\r\n");
	         fw.write(",,,,,> 2 changes in WaterTank (type: CT Mainmodel),,,\r\n");
	         fw.write(",,,,,   > 2 changes in Controller (type: CT Submodel),,,\r\n");
	         fw.write(",,,,,      > 2 changes in Controller2 (type: CT Submodel-Equation),,,\r\n");
	         fw.write(",,,,,         > (type: CT code) has been changed,,,\r\n");
	         fw.write(",,,,,         > (type: CT parameter) has been changed,,,\r\n");
	         fw.write("2,edit,zng,13:52:27 March 5 2011,added a submodel,,,\r\n");
	         fw.write(",,,,,> 1 changes in WaterTank (type: CT Mainmodel),,,\r\n");
	         fw.write(",,,,,   > 1 changes in Controller (type: CT Submodel),,,\r\n");
	         fw.write(",,,,,      > Controller2 (type: CT Submodel-Equation) has been added. ,,,\r\n");
	         fw.write("1,add,zng,22:10:03 March 4 2011,top-level model,,,,Yes\r\n");
	         fw.close();
			 
	         System.out.println("Development log file is generated at: C:\\XiaochenPhD\\Development Log.csv");
	         
			}catch(Exception e)
			{e.printStackTrace();}
		return null;
	}



}
