package contracteditor.editor.core;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;

import org.overture.ide.ui.editor.syntax.VdmColorProvider;

public class DestecsContractSourceViewerConfiguration extends
		VdmSourceViewerConfiguration
{
	private String[] commentingPrefix = new String[]{"#"};
	public final static String SINGLELINE_COMMENT = "__vdm_singleline_comment";
	@Override
	protected ITokenScanner getVdmCodeScanner()
	{
		return new DestecsContractCodeScanner(new VdmColorProvider());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
	{
		return null;
	}
	
	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer,
			String contentType) {
		if(contentType.equals(IDocument.DEFAULT_CONTENT_TYPE)){
			return commentingPrefix; 
		}
		if(contentType.equals(SINGLELINE_COMMENT)){
			return commentingPrefix;
		}
		
		return super.getDefaultPrefixes(sourceViewer, contentType);
	}

}
