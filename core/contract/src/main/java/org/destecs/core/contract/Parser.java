package org.destecs.core.contract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.destecs.core.contract.Variable.DataType;
import org.destecs.core.contract.Variable.VariableType;

public class Parser
{
	public final static String LEX_CONTRACT = "contract";
	public final static String LEX_DESIGN_PARAMETER = "design_parameter";
	public final static String LEX_REAL = "real";
	public final static String LEX_BOOL = "bool";
	public final static String LEX_MONITORED = "monitored";
	public final static String LEX_CONTROLLED = "controlled";
	public final static String LEX_EVENT = "event";
	public final static String LEX_END = "end";
	public final static String LEX_ASSIGNMENT = ":=";
	public final static String LEX_SEMICOLON = ";";

	private File file;
	BufferedReader reader = null;

	public Parser(File file)
	{
		this.file = file;
	}

	private String nextLine() throws IOException
	{
		String text = null;
		while ((text = reader.readLine()) != null)
		{
			text = text.trim();
			if (text.length() > 0
					&& (text.startsWith(LEX_CONTRACT)
							|| text.startsWith(LEX_EVENT+" ")
							|| text.startsWith(LEX_CONTROLLED)
							|| text.startsWith(LEX_MONITORED)
							|| text.startsWith(LEX_DESIGN_PARAMETER+" ") || text.startsWith(LEX_END)))
			{
				return text;
			}
		}
		return text;
	}

	public Contract parse()
	{
		try
		{
			reader = new BufferedReader(new FileReader(file));
			Contract contract = new Contract();

			String line = nextLine();

			if (line.startsWith(LEX_CONTRACT + " "))
			{
				contract.name = line.substring(line.indexOf(' ')).trim();
			}
			while ((line = nextLine()) != null)
			{
				if (line.startsWith(LEX_END))
				{
					return contract;
				}
				if (line.startsWith(LEX_EVENT))
				{
					contract.events.add(line.substring(line.indexOf(' '), line.length() - 1).trim());
				}
				if (line.startsWith(LEX_DESIGN_PARAMETER)
						|| line.startsWith(LEX_CONTROLLED)
						|| line.startsWith(LEX_MONITORED))
				{
					Variable var = new Variable();

					if (line.startsWith(LEX_DESIGN_PARAMETER))
					{
						var.type = VariableType.SharedDesignParameter;
					}
					if (line.startsWith(LEX_CONTROLLED))
					{
						var.type = VariableType.Controlled;
					}
					if (line.startsWith(LEX_MONITORED))
					{
						var.type = VariableType.Monitored;
					}

					line = line.substring(line.indexOf(' ')).trim();

					if (line.startsWith(LEX_REAL))
					{
						var.dataType = DataType.real;
					}
					if (line.startsWith(LEX_BOOL))
					{
						var.dataType = DataType.bool;
					}

					line = line.substring(line.indexOf(' ')).trim();

					var.name = line.substring(0,line.indexOf(' ')).trim();

					String value = line.substring(line.indexOf(LEX_ASSIGNMENT)+LEX_ASSIGNMENT.length(), line.indexOf(LEX_SEMICOLON)).trim();

					if (var.dataType == DataType.bool)
					{
						var.value = Boolean.parseBoolean(value);
					}
					if (var.dataType == DataType.real)
					{
						var.value = Double.parseDouble(value);
					}
					contract.variables.add(var);
				}
			}

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		System.out.println(new Parser(new File(args[0])).parse().toString());
	}
}

// contract watertank
//
// design_parameters
//
// design_parameter real maxlevel := 3.0;
// design_parameter real minlevel := 2.0;
//
// variables
//
// monitored real level := 0.0;
//
// controlled bool valve := false;
//
//
// events
//
// event high;
// event low;
//
// end watertank