/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.tools.plugins;


import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Says "Hi" to the user.
 * 
 * 
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public abstract class AstGenBaseMojo extends AbstractMojo
{
	/**
	 * The prefix of the generated classes.
	 * 
	 * @parameter
	 * @required
	 */
	protected String name;
	
	/**
	 * The prefix of the generated classes.
	 * 
	 * @parameter
	 * @required
	 */
	protected String xmldefinition;

	/**
	 * The prefix of the generated classes.
	 * 
	 * @parameter
	 * @required
	 */
	protected String packagename;


	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.project.MavenProject project;
	
	/**
	 * Name of the directory into which the jprotocolgenerator should dump the java files.
	 * 
	 * @parameter property="outputDirectory" default-value="${project.build.directory}/generated-sources/jprotocolgenerator"
	 */
	protected File outputDirectory;

//	/**
//	 * default-value="${project.reporting.outputDirectory}"
//	 * 
//	 * @parameter
//	 */
//	private File projectOutputDirectory;
//
//	protected File getProjectOutputDirectory()
//	{
//		if (projectOutputDirectory == null
//				|| projectOutputDirectory.length() == 0)
//		{
//			File output = new File(project.getFile().getParentFile(), "target");
//			if (!output.exists())
//				output.mkdirs();
//
//			return output;
//
//		} else
//			return projectOutputDirectory;
//	}
	
	protected File getProjectJavaSrcDirectory()
	{
		File output = new File(project.getFile().getParentFile(), "src/main/java".replace('/', File.separatorChar));
		return output;
	}
//	protected File getProjectVdmSrcDirectory()
//	{
//		File output = new File(project.getFile().getParentFile(), "src/main/vpp".replace('/', File.separatorChar));
//		return output;
//	}

	protected File getResourcesDir()
	{
		File resources = new File(project.getFile().getParentFile(),
				"src/main/resources".replace('/', File.separatorChar));
		return resources;
	}

//	protected List<File> getGrammas()
//	{
//		List<File> grammas = new Vector<File>();
//		grammas.add(new File(getResourcesDir(), ast));
//		System.out.println("AST file: " + grammas.get(0).getAbsolutePath());
//		return grammas;
//	}

	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

}
