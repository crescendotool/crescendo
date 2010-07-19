package org.destecs.cosim;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.xmlrpc.extensions.AnnotationClientFactory;
import org.destecs.protocol.ICoSimProtocol;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;

public class CoSimClient
{
	public final static String MODEL_BASE_PATH = "C:\\destecs\\workspace\\watertank";

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws MalformedURLException,
			InterruptedException
	{
		String modelPath = MODEL_BASE_PATH;
		if(args.length>0)
		{
			modelPath = args[0];
			if(!new File(modelPath).exists() || !new File(modelPath).isDirectory())
			{
				System.err.println("Model path is not valid: "+ modelPath);
				return;
			}
		}
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		AnnotationClientFactory factory = new AnnotationClientFactory(client);

		ICoSimProtocol dt = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);

		ProxyICoSimProtocol p = new ProxyICoSimProtocol(dt);

		System.out.println("Interface Version: " + dt.getVersion());

		if (!p.initialize().success)
		{
			System.err.println("Initilization error");
			return;
		}

		if (p.load(modelPath).success)
		{
			/*
			 * query the interface for matching / validation
			 */
			printInterface(p.queryInterface());

			/*
			 * well we should properly also set the shared design parameters here This should start the interpreter
			 */
			// List<InitializeSharedParameterStructParam> shareadDesignParameters = new
			// Vector<InitializeSharedParameterStructParam>();
			// shareadDesignParameters.add(new InitializeSharedParameterStructParam("minLevel", 10.0));
			// shareadDesignParameters.add(new InitializeSharedParameterStructParam("maxLevel", 500.0));
			//
			// List<InitializefaultsStructParam> enabledFaults = new Vector<InitializefaultsStructParam>();

			List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
			shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minLevel", 10.0));
			shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxLevel", 500.0));

			if (p.setDesignParameters(shareadDesignParameters).success)
			{

				if (p.start().success)
				{

					Double time = 0.0;
					Double level = 0.0;
					int eventLoop = 5;
					for (int i = 0; i < 100; i++)
					{
						/*
						 * Begin stepping
						 */
						List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
						inputs.add(new StepinputsStructParam("level", level));

						List<String> events = new Vector<String>();
						if (time > 10 && eventLoop <= 0)
						{
							events.add("high");
							eventLoop=5;
						}
						eventLoop--;
						StepStruct result = p.step(time, inputs, false, events);

						time = result.time;

						StringBuilder sb = new StringBuilder();

						sb.append("Step: Time = " + time + " ");

						for (StepStructoutputsStruct o : result.outputs)
						{
							sb.append(o.name + " = " + o.value + " ");
						}

						System.out.println(sb.toString());

						level += 0.1;
					}
					if (!p.stop().success)
					{
						System.err.println("Stop faild");
					}
				} else
				{
					System.err.println("Initilization faild");
				}
			} else
			{
				System.err.println("setDesignParameters faild");
			}
		} else
		{
			System.err.println("Load faild");
		}
		
		p.terminate();

		// if (p.load("C:\\destecs\\workspace\\watertank").success)
		// {
		// /*
		// * query the interface for matching / validation
		// */
		// printInterface(p.queryInterface());
		//
		// /*
		// * well we should properly also set the shared design parameters here This should start the interpreter
		// */
		// List<InitializeSharedParameterStructParam> shareadDesignParameters = new
		// Vector<InitializeSharedParameterStructParam>();
		// shareadDesignParameters.add(new InitializeSharedParameterStructParam("minLevel", 10.0));
		// shareadDesignParameters.add(new InitializeSharedParameterStructParam("maxLevel", 500.0));
		//
		// List<InitializefaultsStructParam> enabledFaults = new Vector<InitializefaultsStructParam>();
		// if (p.initialize(shareadDesignParameters, enabledFaults).success)
		// {
		//
		// Double time = 0.0;
		// Double level = 0.0;
		// for (int i = 0; i < 1000; i++)
		// {
		// /*
		// * Begin stepping
		// */
		// List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
		// inputs.add(new StepinputsStructParam("level", level));
		//
		// StepStruct result = p.step(time, inputs, false);
		//
		// time = result.time;
		//
		// StringBuilder sb = new StringBuilder();
		//
		// sb.append("Step: Time = " + time + " ");
		//
		// for (StepStructoutputsStruct o : result.outputs)
		// {
		// sb.append(o.name + " = " + o.value + " ");
		// }
		//
		// System.out.println(sb.toString());
		//
		// level += 0.1;
		// }
		// if(!p.stop().success)
		// {
		// System.err.println("Stop faild");
		// }
		// } else
		// {
		// System.err.println("Initilization faild");
		// }
		// } else
		// {
		// System.err.println("Load faild");
		// }
	}

	public static String pad(String text, int length)
	{
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}

	public static void printInterface(QueryInterfaceStruct result)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("_____________________________\n");
		sb.append("|\tInterface\n");
		sb.append("|----------------------------\n");

		sb.append("|  Shared Design Parameters\n");
		sb.append("|\n");
		if (result.sharedDesignParameters.size() > 0)
		{
			for (String p : result.sharedDesignParameters)
			{
				sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Input Variables\n");
		sb.append("|\n");
		if (result.inputs.size() > 0)
		{
			for (String p : result.inputs)
			{
				sb.append("|    " + p /* p.name + " : " + p.value */+ "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Output Variables\n");
		sb.append("|\n");
		if (result.outputs.size() > 0)
		{
			for (String p : result.outputs)
			{
				sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
			}
		} else
		{
			sb.append("|    None.\n");
		}
		sb.append("_____________________________");
		System.out.println(sb.toString());
	}

}
