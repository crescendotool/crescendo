package org.destecs.cosim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

public class CoSimClient {
	public final static String MODEL_BASE_PATH = "C:\\destecs\\workspace\\watertank";

	public final static String _20_sim_Machine = "http://localhost:1580";

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws MalformedURLException,
			InterruptedException {
		String modelPath = MODEL_BASE_PATH;
		if (args.length > 0) {
			modelPath = args[0];
			if (!new File(modelPath).exists()
					|| !new File(modelPath).isDirectory()) {
				System.err.println("Model path is not valid: " + modelPath);
				return;
			}
		}

		// List<D> simulationData =readData(args[1]);

		// Connection to VDM
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		AnnotationClientFactory factory = new AnnotationClientFactory(client);

		ICoSimProtocol dt = (ICoSimProtocol) factory
				.newInstance(ICoSimProtocol.class);

		ProxyICoSimProtocol dtp = new ProxyICoSimProtocol(dt);

		System.out.println("Interface Version: " + dt.getVersion());

		if (!dtp.initialize().success) {
			System.err.println("Initilization error");
			return;
		}
		// Connection to VDM initialized

		// THIS IS JUST DEBUG
//		if (dtp.load(modelPath).success) {
//			List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
//			shareadDesignParameters
//					.add(new SetDesignParametersdesignParametersStructParam(
//							"minLevel", 1.0));
//			shareadDesignParameters
//					.add(new SetDesignParametersdesignParametersStructParam(
//							"maxLevel", 2.0));
//
//			if (dtp.setDesignParameters(shareadDesignParameters).success
//			// && ctp.setDesignParameters(shareadDesignParameters).success
//			) {
//				Double time = 0.0;
//				Double level = 0.0;
//				if (dtp.start().success) {
//					for (int i = 0; i < 1000; i++) {
//						List<StepinputsStructParam> inputs = new
//						 Vector<StepinputsStructParam>();
//						 inputs.add(new StepinputsStructParam("level", level));
//						
//						 List<String> events = new Vector<String>();
//						 // if (time > 10 && eventLoop <= 0)
//						 // {
//						 // events.add("high");
//						 // eventLoop = 5;
//						 // }
//						 // eventLoop--;
//						 StepStruct result = dtp.step(time, inputs, false,
//						 events);
//					}
//
//				}
//
//			}
//
//			return;
//		}

		

		// Connection to 20-sim
		XmlRpcClientConfigImpl _20_sim_Config = new XmlRpcClientConfigImpl();
		_20_sim_Config.setServerURL(new URL(_20_sim_Machine));
		XmlRpcClient _20_sim_Client = new XmlRpcClient();
		_20_sim_Client.setConfig(_20_sim_Config);
		AnnotationClientFactory _20_sim_Factory = new AnnotationClientFactory(
				_20_sim_Client);

		ICoSimProtocol ct = (ICoSimProtocol) _20_sim_Factory
				.newInstance(ICoSimProtocol.class);

		ProxyICoSimProtocol ctp = new ProxyICoSimProtocol(ct);

		// System.out.println("Interface Version: " + dt.getVersion());

		if (!ctp.initialize().success) {
			System.err.println("Initilization error");
			return;
		}

		// Connection to 20-sim initialized

		// List<StepinputsStructParam> ctInputs = new
		// ArrayList<StepinputsStructParam>();
		// ctInputs.add(new StepinputsStructParam("pipi", 3.14));
		//
		// for(int i=0;i<4;i++)
		// ctp.step(new Double(1), ctInputs, true, new ArrayList<String>());

		if (dtp.load(modelPath).success) {
			/*
			 * query the interface for matching / validation
			 */
			printInterface(dtp.queryInterface());

			/*
			 * well we should properly also set the shared design parameters
			 * here This should start the interpreter
			 */
			// List<InitializeSharedParameterStructParam>
			// shareadDesignParameters = new
			// Vector<InitializeSharedParameterStructParam>();
			// shareadDesignParameters.add(new
			// InitializeSharedParameterStructParam("minLevel", 10.0));
			// shareadDesignParameters.add(new
			// InitializeSharedParameterStructParam("maxLevel", 500.0));
			//
			// List<InitializefaultsStructParam> enabledFaults = new
			// Vector<InitializefaultsStructParam>();

			List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
			shareadDesignParameters
					.add(new SetDesignParametersdesignParametersStructParam(
							"minLevel", 1.0));
			shareadDesignParameters
					.add(new SetDesignParametersdesignParametersStructParam(
							"maxLevel", 2.0));

			if (dtp.setDesignParameters(shareadDesignParameters).success
			// && ctp.setDesignParameters(shareadDesignParameters).success
			) {

				if (dtp.start().success && ctp.start().success) {

					// Initial input for DT
					
					Double initTime = 100.0;
					Double time = 0.0;
					Double level = 0.0;
					List<String> events = new Vector<String>();
					List<StepStructoutputsStruct> resultOutput = null;

					List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
					inputs.add(new StepinputsStructParam("level", level));

					StepStruct result = dtp.step(initTime, inputs, false, events);
					
					while (time < 5) {
						// input for CT
						resultOutput = result.outputs;

						inputs = new Vector<StepinputsStructParam>();
						for (StepStructoutputsStruct stepStructoutputsStruct : resultOutput) {

							if (stepStructoutputsStruct.name
									.equals("valveState")) {
								inputs.add(new StepinputsStructParam(
										stepStructoutputsStruct.name,
										stepStructoutputsStruct.value));
							}
						}

						result = ctp.step((result.time)/1000, inputs, false, events);

				
						
						StringBuilder sb = new StringBuilder();

						sb.append("20-sim Requesting time step of = "
								+ result.time + " ");

						for (StepStructoutputsStruct o : result.outputs) {
							sb.append(o.name + " = " + o.value + " ");
						}
						
						resultOutput = result.outputs;

						System.out.println(sb.toString());

						// input for DT
						for (StepStructoutputsStruct stepStructoutputsStruct : resultOutput) {

							if (stepStructoutputsStruct.name
									.equals("level")) {
								inputs.add(new StepinputsStructParam(
										stepStructoutputsStruct.name,
										stepStructoutputsStruct.value));
							}
						}
						

						
						result = dtp.step(((result.time)*1000)+5, inputs, false,
								events);

						sb = new StringBuilder();

						sb.append("VDM Requesting time step of = "
								+ result.time + " ");

						for (StepStructoutputsStruct o : result.outputs) {
							sb.append(o.name + " = " + o.value + " ");
						}

						System.out.println(sb.toString());
						time = (result.time)/1000;
					}

					// int eventLoop = 5;
					// for (int i = 0; i < simulationData.size(); i++)
					// {
					// time = simulationData.get(i).timeS*1000;
					// level = simulationData.get(i).tankTankLevel;
					// /*
					// * Begin stepping
					// */
					// List<StepinputsStructParam> inputs = new
					// Vector<StepinputsStructParam>();
					// inputs.add(new StepinputsStructParam("level", level));
					//
					// List<String> events = new Vector<String>();
					// // if (time > 10 && eventLoop <= 0)
					// // {
					// // events.add("high");
					// // eventLoop = 5;
					// // }
					// // eventLoop--;
					// StepStruct result = dtp.step(time, inputs, false,
					// events);
					//
					// // time = result.time;
					//
					// StringBuilder sb = new StringBuilder();
					//
					// sb.append("VDM Requesting time step of = " + result.time
					// + " ");
					//
					// for (StepStructoutputsStruct o : result.outputs)
					// {
					// sb.append(o.name + " = " + o.value + " ");
					// }
					//
					// System.out.println(sb.toString());

					// level += 0.1;
					// }

					if (!dtp.stop().success) {
						System.err.println("Stop faild");
					}
				} else {
					System.err.println("Initilization faild");
				}
			} else {
				System.err.println("setDesignParameters faild");
			}
		} else {
			System.err.println("Load faild");
		}

		dtp.terminate();

		// if (p.load("C:\\destecs\\workspace\\watertank").success)
		// {
		// /*
		// * query the interface for matching / validation
		// */
		// printInterface(p.queryInterface());
		//
		// /*
		// * well we should properly also set the shared design parameters here
		// This should start the interpreter
		// */
		// List<InitializeSharedParameterStructParam> shareadDesignParameters =
		// new
		// Vector<InitializeSharedParameterStructParam>();
		// shareadDesignParameters.add(new
		// InitializeSharedParameterStructParam("minLevel", 10.0));
		// shareadDesignParameters.add(new
		// InitializeSharedParameterStructParam("maxLevel", 500.0));
		//
		// List<InitializefaultsStructParam> enabledFaults = new
		// Vector<InitializefaultsStructParam>();
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
		// List<StepinputsStructParam> inputs = new
		// Vector<StepinputsStructParam>();
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

	public static String pad(String text, int length) {
		while (text.length() < length) {
			text += " ";
		}
		return text;
	}

	public static void printInterface(QueryInterfaceStruct result) {
		StringBuilder sb = new StringBuilder();

		sb.append("_____________________________\n");
		sb.append("|\tInterface\n");
		sb.append("|----------------------------\n");

		sb.append("|  Shared Design Parameters\n");
		sb.append("|\n");
		if (result.sharedDesignParameters.size() > 0) {
			for (String p : result.sharedDesignParameters) {
				sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
			}
		} else {
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Input Variables\n");
		sb.append("|\n");
		if (result.inputs.size() > 0) {
			for (String p : result.inputs) {
				sb.append("|    " + p /* p.name + " : " + p.value */+ "\n");
			}
		} else {
			sb.append("|    None.\n");
		}
		sb.append("|----------------------------\n");
		sb.append("|  Output Variables\n");
		sb.append("|\n");
		if (result.outputs.size() > 0) {
			for (String p : result.outputs) {
				sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
			}
		} else {
			sb.append("|    None.\n");
		}
		sb.append("_____________________________");
		System.out.println(sb.toString());
	}

	public static List<D> readData(String filename) {
		List<D> data = new Vector<D>();

		File file = new File(filename);
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			boolean isFirst = true;
			// repeat until all lines is read
			while ((text = reader.readLine()) != null) {
				if (isFirst) {
					isFirst = false;
					continue;
				}
				String[] elements = text.split(",");
				data.add(new D(Double.parseDouble(elements[0]), Double
						.parseDouble(elements[1]), Double
						.parseDouble(elements[2]), Double
						.parseDouble(elements[3])));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}

	public static class D {
		// time{s},tank\Tank\level,Control\valvecontrol,Control\count
		public final Double timeS;
		public final Double tankTankLevel;
		public final Double controlValveControl;
		public final Double controlCount;

		public D(Double timeS, Double tankTankLevel,
				Double controlValveControl, Double controlCount) {
			this.timeS = timeS;
			this.controlCount = controlCount;
			this.controlValveControl = controlValveControl;
			this.tankTankLevel = tankTankLevel;
		}

		@Override
		public String toString() {
			return String
					.format("\ntimeS=%1$s, \ntankTankLevel=%2$s, \ncontrolValveControl=%3$s, \ncontrolCount=%4$s",
							timeS, tankTankLevel, controlValveControl,
							controlCount);
		}
	}
}
