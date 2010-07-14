package org.destecs.cosim;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.protocol.ICoSimProtocol;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.InitializeSharedParameterStructParam;
import org.destecs.protocol.structs.InitializefaultsStructParam;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructInputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructOutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructSharedParameterStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destetcs.core.xmlrpc.extensions.AnnotationClientFactory;

public class CoSimClient
{

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws MalformedURLException,
			InterruptedException
	{
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		AnnotationClientFactory factory = new AnnotationClientFactory(client);

		ICoSimProtocol dt = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);

		ProxyICoSimProtocol p = new ProxyICoSimProtocol(dt);

		// if (p.load("C:\\overture\\runtime-ide.product\\HomeAutomationRT").success)
		if (p.load("C:\\destecs\\workspace\\watertank").success)
		{
			/*
			 * query the interface for matching / validation
			 */
			printInterface(p.queryInterface());

			/*
			 * well we should properly also set the shared design parameters here This should start the interpreter
			 */
			List<InitializeSharedParameterStructParam> shareadDesignParameters = new Vector<InitializeSharedParameterStructParam>();
			shareadDesignParameters.add(new InitializeSharedParameterStructParam("minLevel", 10.0));
			shareadDesignParameters.add(new InitializeSharedParameterStructParam("maxLevel", 500.0));

			List<InitializefaultsStructParam> enabledFaults = new Vector<InitializefaultsStructParam>();
			p.initialize(shareadDesignParameters, enabledFaults);

			

			// for (long time : steps)
			// {
			// System.out.println("\nStep..:\n\n"
			// + p.step(new Double(time), inputs, false));
			// Thread.sleep(1000);
			// }
			Double time = 0.0;
			Double level = 0.0;
			for (int i = 0; i < 1000; i++)
			{
				/*
				 * Begin stepping
				 */
				List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
				inputs.add(new StepinputsStructParam("level", level));
				
				StepStruct result = p.step(time, inputs, false);

				time = result.time;

				StringBuilder sb = new StringBuilder();

				sb.append("Step: Time = " + time + " ");

				for (StepStructoutputsStruct o : result.outputs)
				{
					sb.append(o.name + " = " + o.value+ " ");
				}

				System.out.println(sb.toString());
				
				level +=0.1;
			}

		}

		// Object resultF = p.queryFaults();
		// System.out.println("\nqueryFaults..:\n\n " + resultF);

		// ICoSimProtocol dt = (ICoSimProtocol)
		// factory.newInstance(Thread.currentThread().getContextClassLoader(),ICoSimProtocol.class,"PREFIX");

		// System.out.println("GetVersion: "+dt.GetVersion());
		//		
		// Map<String, List<Map<String, Object>>> interfaceDefinition = dt.QueryInterface();
		// System.out.println("QueryInterface: "+interfaceDefinition);

		// System.out.println(dt.getVersion());

	}

	public static String pad(String text, int length)
	{
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}

	static long[] steps = { 0, 4, 8, 34, 126, 146, 150, 154, 180, 272, 292,
			296, 999999999 };

	public static void printInterface(QueryInterfaceStruct result)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("_____________________________\n");
		sb.append("|\tInterface\n");
		sb.append("|----------------------------\n");

		sb.append("|  Shared Design Parameters\n");
		sb.append("|\n");
		if (result.SharedParameter.size() > 0)
		{
			for (QueryInterfaceStructSharedParameterStruct p : result.SharedParameter)
			{
				sb.append("|    " + p.name + " : " + p.value + "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Input Variables\n");
		sb.append("|\n");
		if (result.Inputs.size() > 0)
		{
			for (QueryInterfaceStructInputsStruct p : result.Inputs)
			{
				sb.append("|    " + p.name + " : " + p.value + "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Output Variables\n");
		sb.append("|\n");
		if (result.Outputs.size() > 0)
		{
			for (QueryInterfaceStructOutputsStruct p : result.Outputs)
			{
				sb.append("|    " + p.name + " : " + p.value + "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("_____________________________");
		System.out.println(sb.toString());
	}

}
