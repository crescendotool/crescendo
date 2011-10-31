package org.destecs.ide.ui.editor.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import org.destecs.core.contract.IVariable;
import org.destecs.core.contract.Variable;
import org.destecs.ide.core.resources.DestecsModel;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.ui.editor.core.DestecsDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

;
public class VdmLinkCompletionProcessor implements IContentAssistProcessor
{
	final IContextInformationValidator ctxtInfoVlidator = new ContextInformationValidator(this);
	DestecsModel model = null;
final List<String> groups = Arrays.asList(new String[]{"input","output","event","sdp","model"});
	Map<String, List<String>> vdmMetadata = new Hashtable<String, List<String>>();
//http://www.ibm.com/developerworks/opensource/library/os-ecca/
	// http://www.50001.com/language/javaside/lec/java_ibm/Equipping%20SWT%20%BE%D6%C7%C3%B8%AE%C4%C9%C0%CC%BC%C7%BF%A1%20content%20assistants%20%C3%DF%B0%A1%C7%CF%B1%E2%20%28%BF%B5%B9%AE%29.htm
	// http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/editors_jface.htm
	public VdmLinkCompletionProcessor()
	{
		// vdmMetadata.put("levelSensor", Arrays.asList(new String[] { "level",
		// "isLevelSensorBroken" }));
		// vdmMetadata.put("valveActuator", Arrays.asList(new String[] {
		// "valveState", "isValveBroken" }));
		// vdmMetadata.put("Controller", Arrays.asList(new String[] { "minLevel",
		// "maxLevel" }));

	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset)
	{
		// System.out.println("computeCompletionProposals");

		// Retrieve current document
		IDocument doc = viewer.getDocument();

		// Retrieve current selection range
		Point selectedRange = viewer.getSelectedRange();

		List<ICompletionProposal> propList = new ArrayList<ICompletionProposal>();

		if (selectedRange.y > 0)
		{
			// try
			// {
			//
			// // Retrieve selected text
			// String text = doc.get(selectedRange.x, selectedRange.y);
			//
			// // Compute completion proposals
			// computeStyleProposals(text, selectedRange, propList);
			//
			// } catch (BadLocationException e)
			// {
			//
			// }
		} else
		{ // Retrieve qualifier
			QualifierInfo qualifier = getQualifier(doc, offset);
			// System.out.println(qualifier);
			// Compute completion proposals
			computeStructureProposals(qualifier, offset, propList, viewer);
		}

		// Create completion proposal array
		ICompletionProposal[] proposals = new ICompletionProposal[propList.size()];

		// and fill with list elements
		propList.toArray(proposals);

		// Return the proposals
		return proposals;

	}

	class QualifierInfo
	{
		public StringBuffer pre = new StringBuffer();
		public boolean dot;
		public StringBuffer proposal = new StringBuffer();
		public boolean postEqual;
		public String group= new String();

		public void add(char c)
		{
			if (!dot)
				proposal.append(c);
			else
				pre.append(c);
		}

		public void reverse()
		{
			pre = pre.reverse();
			proposal = proposal.reverse();
		}
		
		public void checkGroup()
		{
			for (String g : groups)
			{
				if(proposal.toString().startsWith(g))
				{
					group = g;
					proposal.delete(0, g.length());
				}
			}
		}

	}

	private QualifierInfo getQualifier(IDocument doc, int documentOffset)
	{

		// Use string buffer to collect characters
//		StringBuffer buf = new StringBuffer();
		QualifierInfo info = new QualifierInfo();
		while (true)
		{
			try
			{

				// Read character backwards
				char c = doc.getChar(--documentOffset);

				if (c == '=')
				{
					info.postEqual = true;
					break;
				}

				if (c == ';' || c == '\n' || c == '\t')// haracter.isWhitespace(c))
					break;

				if (c == '.')
				{
					info.dot = true;
				}

				info.add(c);

			} catch (BadLocationException e)
			{

				// Document start reached, no tag found
				return info;
			}
		}
		info.reverse();
		return info;
	}

	private void computeStructureProposals(QualifierInfo qualifier,
			int documentOffset, List<ICompletionProposal> propList,
			ITextViewer viewer)
	{
qualifier.checkGroup();
		
		int qlen = qualifier.proposal.toString().trim().length();
		
		if(qualifier.pre.toString().trim().length()==0 && qualifier.group.isEmpty() && !qualifier.postEqual)
		{
			addGroups(documentOffset,qualifier,propList);
			return;
		}
		
		List<String> prop = new Vector<String>();
		if (qualifier.pre.length() == 0 && !qualifier.postEqual)
		{
			if (model == null)
			{
				if (viewer.getDocument() instanceof DestecsDocument)
				{
					IProject project = ((DestecsDocument) viewer.getDocument()).getFile().getProject();
					IDestecsProject p = (IDestecsProject) project.getAdapter(IDestecsProject.class);
					model = p.getModel();
					loadVdmMetadata(p);
				}
			}

			if (model != null && model.getContract() != null)
			{

				for (String text : model.getContract().getEvents())
				{
					if (qualifier.proposal.toString().trim().length() == 0
							|| text.startsWith(qualifier.proposal.toString()))
					{
						// Derive cursor position
						int cursor = text.length();

						IContextInformation contextInfo = new ContextInformation(null, text);
						// Construct proposal
						CompletionProposal proposal = new CompletionProposal(text, documentOffset
								- qlen, qlen, cursor, null, text, contextInfo, text
								+ "-Event declared in contract");

						// and add to result list
						propList.add(proposal);
					}
				}

				for (IVariable var : model.getContract().getMonitoredVariables())
				{

					String text = var.getName();
					if (qualifier.proposal.toString().trim().length() == 0
							|| text.startsWith(qualifier.proposal.toString()))
					{
						// Derive cursor position
						int cursor = text.length();

						IContextInformation contextInfo = new ContextInformation(null, text);
						// Construct proposal
						CompletionProposal proposal = new CompletionProposal(text, documentOffset
								- qlen, qlen, cursor, null, text, contextInfo, text
								+ "-Monitored variable declared in contract. "
								+ var.getDataType());

						// and add to result list
						propList.add(proposal);
					}
				}

				for (IVariable var : model.getContract().getControlledVariables())
				{

					String text = var.getName();
					if (qualifier.proposal.toString().trim().length() == 0
							|| text.startsWith(qualifier.proposal.toString()))
					{
						// Derive cursor position
						int cursor = text.length();

						IContextInformation contextInfo = new ContextInformation(null, text);
						// Construct proposal
						CompletionProposal proposal = new CompletionProposal(text, documentOffset
								- qlen, qlen, cursor, null, text, contextInfo, text
								+ "-Monitored variable declared in contract. "
								+ var.getDataType());

						// and add to result list
						propList.add(proposal);
					}
				}

			}
		}

		if (qualifier.postEqual && !qualifier.dot)
		{
			for (String text : vdmMetadata.keySet())
			{
				if (qualifier.proposal.toString().trim().length() == 0
						|| text.startsWith(qualifier.proposal.toString()))
				{
					int cursor = text.length();
					IContextInformation contextInfo = new ContextInformation(null, text);
					// Construct proposal
					CompletionProposal proposal = new CompletionProposal(text, documentOffset
							- qlen, qlen, cursor, null, text, contextInfo, text
							+ "-instance variable in System");
					// and add to result list
					propList.add(proposal);
				}
			}
		}
		if (qualifier.postEqual && qualifier.dot)
		{
			// String prefix = qualifier.substring(0, qualifier.indexOf('.')).trim();
			String pre = qualifier.pre.substring(0, qualifier.pre.length() - 1);
			if (vdmMetadata.containsKey(pre))
			{
				prop.addAll(vdmMetadata.get(pre));
			}
		}

		for (String text : prop)
		{
			if (qualifier.proposal.toString().trim().length() == 0
					|| text.startsWith(qualifier.proposal.toString()))
			{
				// Derive cursor position
				int cursor = text.length();
				// Construct proposal
				CompletionProposal proposal = new CompletionProposal(text, documentOffset
						- qlen, qlen, cursor);

				// and add to result list
				propList.add(proposal);
			}
		}
	}

	private void addGroups(int documentOffset,
			QualifierInfo qualifier, List<ICompletionProposal> propList)
	{
		int qlen = qualifier.proposal.length();
		for (String text : groups)
		{
			if (qualifier.proposal.length() == 0
					|| text.startsWith(qualifier.proposal.toString()))
			{
				// Derive cursor position
				int cursor = text.length();

				IContextInformation contextInfo = new ContextInformation(null, text);
				// Construct proposal
				CompletionProposal proposal = new CompletionProposal(text, documentOffset
						- qlen, qlen, cursor, null, text, contextInfo, text
						+ "-Event declared in contract");

				// and add to result list
				propList.add(proposal);
			}
		}
	}

	private void loadVdmMetadata(IDestecsProject p)
	{
		try
		{
			Properties props = new Properties();
			IFile file = p.getVdmModelFolder().getFile(".metadata");

			if (file.exists())
			{
				props.load(file.getContents());

				for (Entry<Object, Object> entry : props.entrySet())
				{
					vdmMetadata.put(entry.getKey().toString(), Arrays.asList(entry.getValue().toString().split(",")));
				}
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset)
	{
		System.out.println("computeContextInformation");
		IContextInformation[] result = new IContextInformation[5];
		for (int i = 0; i < result.length; i++)
			result[i] = new ContextInformation(MessageFormat.format("private", new Object[] {
					new Integer(i), new Integer(offset) }), MessageFormat.format("private", new Object[] {
					new Integer(i), new Integer(offset - 5),
					new Integer(offset + 5) }));
		return result;
	}

	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '.', '(' };// return null;//return new char[] { '=','\n' };
	}

	public char[] getContextInformationAutoActivationCharacters()
	{
		return new char[] { '#' };
	}

	public IContextInformationValidator getContextInformationValidator()
	{
		// return ctxtInfoVlidator;
		return new ContextInformationValidator(this);
	}

	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
