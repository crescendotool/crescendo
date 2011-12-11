package org.destecs.ide.debug.octave;

import java.io.File;

public class OctaveFactory
{

	public static String createResultScript(String name, File deCsvFile,
			File ctCsvFile)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("## Author: Kenneth Lausdahl\n");
		sb.append("1;\n");
		sb.append("function result = readResult ()\n");

		sb.append("\n");

		sb.append("de = "+ (deCsvFile==null?"\"\"":"csvread(\""+deCsvFile.getName()+"\")")+";\n");
		sb.append("ct = "+ (ctCsvFile==null?"\"\"":"csvread(\""+ctCsvFile.getName()+"\")")+";\n");
		sb.append("\n");
		sb.append("result = struct(\"name\", \""+name+"\",\n");
		sb.append("\"de\",de,\n");
		sb.append("\"ct\",ct\n");
		sb.append(");\n");
		sb.append("\n");
		sb.append("endfunction\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("## Load last result automatically\n");
		sb.append("lastrun = readResult();\n");

		return sb.toString();
	}
}
