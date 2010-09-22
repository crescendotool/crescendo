package contracteditor.editor.core;

import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;



public class DestecsContractEditor extends VdmEditor {
	
	public DestecsContractEditor() {
		super();
	}

	@Override
	public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration() {
		return new DestecsContractSourceViewerConfiguration();
	}
	
	
}
