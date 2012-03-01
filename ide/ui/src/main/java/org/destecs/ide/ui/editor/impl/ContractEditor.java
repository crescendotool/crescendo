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

import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;

public class ContractEditor extends AbstractDestecsEditor
{
	public static class DestecsContractSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		
		@Override
		protected ITokenScanner getCodeScaner(DestecsColorProvider colorProvider)
		{
			return new DestecsContractCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new ContractReconcilingStrategy();
		}

	}
	
	public static class ContractReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("rawtypes")
		@Override
		protected ParserWrapper getParser()
		{
			return new ContractParserWrapper();
		}
	}

	public static class DestecsContractCodeScanner extends BaseCodeScanner
	{

		public DestecsContractCodeScanner(DestecsColorProvider provider)
		{
			super(provider);
		}

		@Override
		protected String[] getCommentWords()
		{
			return new String[] {  };
		}

		@Override
		protected String[] getKeywords()
		{
			return new String[] { "contract", "end", "sdp","shared_design_parameter",
					"real", "monitored", "controlled", "event", "bool",
					"false", "true", "matrix","array" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "true", "false", "real", "bool" };
		}

	}

	public ContractEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		configuration = getContractSourceViewerConfiguration();
		setSourceViewerConfiguration(configuration);
	}

	public DestecsContractSourceViewerConfiguration getContractSourceViewerConfiguration()
	{
		return new DestecsContractSourceViewerConfiguration();
	}

}
