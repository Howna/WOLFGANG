package de.uni.freiburg.iig.telematik.wolfgang.actions.zoom;

import java.awt.event.ActionEvent;
import java.io.IOException;

import com.mxgraph.view.mxGraphView;

import de.invation.code.toval.properties.PropertyException;
import de.uni.freiburg.iig.telematik.wolfgang.actions.AbstractPNEditorAction;
import de.uni.freiburg.iig.telematik.wolfgang.editor.component.PNEditorComponent;
import de.uni.freiburg.iig.telematik.wolfgang.editor.properties.EditorProperties;
import de.uni.freiburg.iig.telematik.wolfgang.icons.IconFactory;

public class ZoomInAction extends AbstractPNEditorAction {

	private static final long serialVersionUID = 7450908146578160638L;
	private mxGraphView view;
	private double currentZoom;
	private double newZoom;

	public ZoomInAction(PNEditorComponent editor) throws PropertyException, IOException {
		super(editor, "Zoom In", IconFactory.getIcon("zoom_in"));
		view = getEditor().getGraphComponent().getGraph().getView();

	}

	@Override
	protected void doFancyStuff(ActionEvent e) throws Exception {
		currentZoom = view.getScale();
		currentZoom = Math.round(currentZoom*100)/100.0; 
		if (currentZoom <= 4 - EditorProperties.getInstance().getDefaultZoomStep()) {
			newZoom = currentZoom + EditorProperties.getInstance().getDefaultZoomStep();
			newZoom = Math.round(newZoom*100)/100.0;
			getEditor().getGraphComponent().zoomTo(newZoom, getEditor().getGraphComponent().isCenterZoom());
		}
		
		switch(getEditor().getEditorToolbar().getMode()){
		case EDIT:
			break;
		case PLAY:
			getEditor().getGraphComponent().removeCellOverlays();
			getEditor().getGraphComponent().highlightEnabledTransitions();
			break;
		default:
			break;
		}
	}

}