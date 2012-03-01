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
package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;

public class ScenarioEditor extends AbstractDestecsEditor
{
	
	
	public static class ScenarioSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		@Override
		protected ITokenScanner getCodeScaner(DestecsColorProvider colorProvider)
		{
			return new ScenarioCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new ScenarioReconcilingStrategy();
		}

	}
	
	public static class ScenarioReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("rawtypes")
		@Override
		protected ParserWrapper getParser()
		{
			return new ScenarioParserWrapper();
		}
	}

	public static class ScenarioCodeScanner extends BaseCodeScanner
	{

		public ScenarioCodeScanner(DestecsColorProvider provider)
		{
			super(provider);
		}

		@Override
		protected String[] getCommentWords()
		{
			return new String[] {};
		}

		@Override
		protected String[] getKeywords()
		{
			return new String[] { "DE","CT" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "true", "false"};
		}

	}

	public ScenarioEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}
	

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		configuration = getScenarioSourceViewerConfiguration();
		setSourceViewerConfiguration(configuration);
	}
	
	public ScenarioSourceViewerConfiguration getScenarioSourceViewerConfiguration()
	{
		return new ScenarioSourceViewerConfiguration();
	}

}
