package de.uni.freiburg.iig.telematik.wolfgang.menu.toolbars;

import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.wolfgang.editor.component.PNEditorComponent;

public class NodeToolBar extends JToolBar {

	private static final long serialVersionUID = -6491749112943066366L;
	protected NodePalettePanel palettePanel = null;

	public NodeToolBar(final PNEditorComponent pnEditor, int orientation) throws PropertyException, IOException {
		super(orientation);
		Validate.notNull(pnEditor);
		setFloatable(false);
		add(getPalettePanel());
		
	}
	
	private JPanel getPalettePanel() throws ParameterException, PropertyException, IOException {
		if (palettePanel == null) {
			palettePanel = new NodePalettePanel();
		}
		return palettePanel;
	}

}
