package org.destecs.protocol.exceptions;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class SimulationException extends Exception implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String internalStackTrace;

	public SimulationException(String message, Throwable cause)
	{
		super(message, cause);
		internalStackTrace =stack2string(cause);
	}

	public SimulationException(String message)
	{
		super(message);
	}
	
	String getInternalStackTrace()
	{
		return this.internalStackTrace;
	}

	public static String stack2string(Throwable e) {
		  try {
		    StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
		    return "------\r\n" + sw.toString() + "------\r\n";
		  }
		  catch(Exception e2) {
		    return "bad stack2string";
		  }
	}
}
