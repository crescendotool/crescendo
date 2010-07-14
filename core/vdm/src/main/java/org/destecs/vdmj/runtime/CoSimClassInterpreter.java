package org.destecs.vdmj.runtime;

import org.destecs.vdmj.scheduler.CoSimResourceScheduler;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.runtime.ClassInterpreter;



public class CoSimClassInterpreter extends ClassInterpreter
{

	public CoSimClassInterpreter(ClassList classes) throws Exception
	{
		super(classes);
		scheduler = new CoSimResourceScheduler();
	}

}
