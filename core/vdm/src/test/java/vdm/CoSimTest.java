package vdm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.Links;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.vdm.SimulationManager;
import org.destecs.vdmj.VDMCO;
import org.junit.Test;
import org.overture.config.Settings;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.scheduler.SystemClock.TimeUnit;

public class CoSimTest
{
	@Test
	public void test() throws RemoteSimulationException
	{
		SimulationManager.getInstance().initialize();

		VDMCO.replaceNewIdentifier.clear();

		Settings.prechecks = true;
		Settings.postchecks = true;
		Settings.invchecks = true;
		Settings.dynamictypechecks = true;
		Settings.measureChecks = true;
		boolean disableRtLog = false;
		boolean disableCoverage = false;
		boolean disableOptimization = false;

		Settings.usingCmdLine = true;
		Settings.usingDBGP = false;

		List<File> specfiles = new Vector<File>();

		for (File file : new File("src/test/resources/periodic-watertank".replace('/', File.separatorChar)).listFiles())
		{
			if (file.getName().endsWith(".vdmrt"))
				specfiles.add(file);
		}

		File linkFile = new File("src/test/resources/periodic-watertank/vdm.link".replace('/', File.separatorChar));
		File baseDirFile = new File(".");
		
		Links links =null;
		VdmLinkParserWrapper linksParser = new VdmLinkParserWrapper();
		try
		{
			links= linksParser.parse(linkFile);
		} catch (IOException e)
		{
			throw new RemoteSimulationException("Faild to parse vdm links",e);
		}// Links.load(linkFile);

		if (links == null || linksParser.hasErrors())
		{
			throw new RemoteSimulationException("Faild to parse vdm links");
		}

		SimulationManager.getInstance().load(specfiles, links, new File("."), baseDirFile, disableRtLog, disableCoverage, disableOptimization);

		List<Map<String, Object>> parameters = new Vector<Map<String, Object>>();

		Map<String, Object> pMax = new HashMap<String, Object>();
		pMax.put("name", "maxlevel");
		pMax.put("value", new Double[] { 5.0 });
		pMax.put("size", new Integer[] { 1 });
		parameters.add(pMax);

		Map<String, Object> pMin = new HashMap<String, Object>();
		pMin.put("name", "minlevel");
		pMin.put("value", new Double[] { 2.0 });
		pMin.put("size", new Integer[] { 1 });
		parameters.add(pMin);

		SimulationManager.getInstance().setDesignParameters(parameters);

		Double time = (double) 0;
		SimulationManager.getInstance().start(time.longValue());

		Double level = 10.0;
		while (time < 5)
		{
			List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
			inputs.add(new StepinputsStructParam("level", Arrays.asList(new Integer[] { 1 }), Arrays.asList(new Double[] { level })));

			long timeTmp =SystemClock.timeToInternal(TimeUnit.seconds, time);
			StepStruct res = SimulationManager.getInstance().step(timeTmp, inputs, new Vector<String>());

			res.time = SystemClock.internalToTime(TimeUnit.seconds, res.time.longValue());
			// System.levelSensor.level
			// System.valveActuator.valveState

			System.out.println(String.format("Running from: %.6f to %.6f", time, res.time));
			for (StepStructoutputsStruct output : res.outputs)
			{
				System.out.println(String.format("\tOutput %s = %s", output.name, output.value.get(0)));
			}
			level -= 1;

			time = res.time;
		}

		SimulationManager.getInstance().stopSimulation();

	}
}
