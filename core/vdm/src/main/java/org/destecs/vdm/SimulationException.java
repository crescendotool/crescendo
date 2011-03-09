package org.destecs.vdm;

import java.io.Serializable;

public class SimulationException extends Exception implements Serializable
{
//	String internalStackTrace;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimulationException(String message, Throwable cause)
	{
		super(message, cause);
//		internalStackTrace =stack2string(this);
	}

	public SimulationException(String message)
	{
		super(message);
	}

//	public static String stack2string(Exception e) {
//		  try {
//		    StringWriter sw = new StringWriter();
//		    PrintWriter pw = new PrintWriter(sw);
//		    e.printStackTrace(pw);
//		    return "------\r\n" + sw.toString() + "------\r\n";
//		  }
//		  catch(Exception e2) {
//		    return "bad stack2string";
//		  }
//	}
}
