package org.destecs.ide.libraries.wizard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.destecs.ide.libraries.store.LibStore;
import org.destecs.ide.libraries.store.Library;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class LibrarySelection extends Composite
{

	private Label label = null;

	Map<Button, Library> libMap = new HashMap<Button, Library>();

	Button buttonLinkedLibs;
	boolean showTitle = false;

	public LibrarySelection(Composite parent, int style)
	{
		super(parent, style);
		initialize();
	}

	public LibrarySelection(Composite parent, int style, boolean showTitle)
	{
		super(parent, style);
		this.showTitle = showTitle;
		initialize();
	}

	private void initialize()
	{
		if (showTitle)
		{
			label = new Label(this, SWT.NONE);
			label.setText("Select libraries to include");
		}
		LibStore store = new LibStore();
		Set<Library> libs = store.getLibraries();

		Group libGroup = createGroup(this, "Libraries");
		for (Library library : libs)
		{
			Button b = new Button(libGroup, SWT.CHECK);
			b.setText(library.name + " - "+library.version);
			b.setToolTipText(library.description);
			libMap.put(b, library);
		}

		buttonLinkedLibs = new Button(createGroup(this, "Configuration"), SWT.CHECK);
		buttonLinkedLibs.setText("Used linked libraries");
		buttonLinkedLibs.setToolTipText("This creates symbolic links to the libraries");
		buttonLinkedLibs.setSelection(true);

		setSize(new Point(300, 200));
		setLayout(new GridLayout());
	}

	private Group createGroup(Composite comp, String name)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText(name);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		return group;
	}

	public Set<Library> getSelectedLibs()
	{
		Set<Library> selectedLibs = new HashSet<Library>();
		for (Entry<Button, Library> entry : libMap.entrySet())
		{
			if (entry.getKey().getSelection())
			{
				selectedLibs.add(entry.getValue());
			}
		}
		return selectedLibs;
	}

	public boolean useLinkedLibs()
	{
		return buttonLinkedLibs.getSelection();
	}

}
