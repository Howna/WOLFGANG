package de.uni.freiburg.iig.telematik.wolfgang.editor.component;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JToolBar;

import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.sepia.graphic.GraphicalIFNet;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.IFNetGraphics;
import de.uni.freiburg.iig.telematik.sepia.petrinet.AbstractFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNetFlowRelation;
import de.uni.freiburg.iig.telematik.wolfgang.exception.EditorToolbarException;
import de.uni.freiburg.iig.telematik.wolfgang.graph.IFNetGraph;
import de.uni.freiburg.iig.telematik.wolfgang.graph.IFNetGraphComponent;
import de.uni.freiburg.iig.telematik.wolfgang.graph.PNGraphComponent;
import de.uni.freiburg.iig.telematik.wolfgang.menu.AbstractToolBar;
import de.uni.freiburg.iig.telematik.wolfgang.menu.CPNToolBar;
import de.uni.freiburg.iig.telematik.wolfgang.menu.popup.EditorPopupMenu;
import de.uni.freiburg.iig.telematik.wolfgang.menu.popup.TransitionPopupMenu;
import de.uni.freiburg.iig.telematik.wolfgang.properties.IFNetProperties;
import de.uni.freiburg.iig.telematik.wolfgang.properties.PNProperties;

public class IFNetEditorComponent extends AbstractIFNetEditorComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5600305366471054461L;
	
	public IFNetEditorComponent(){
		super();
	}

	public IFNetEditorComponent(GraphicalIFNet netContainer) {
		super(netContainer);
	}

	public IFNetEditorComponent(GraphicalIFNet netContainer, boolean askForLayout) {
		super(netContainer, askForLayout);
	}

	public IFNetEditorComponent(GraphicalIFNet netContainer, LayoutOption layoutOption) {
		super(netContainer, layoutOption);
	}
	
	@Override
	protected GraphicalIFNet createNetContainer() {
		return new GraphicalIFNet(new IFNet(), new IFNetGraphics());
	}
	
	@Override
	public GraphicalIFNet getNetContainer() {
		return (GraphicalIFNet) super.getNetContainer();
	}

	@Override
	protected IFNetProperties getPNProperties() {
		return (IFNetProperties) super.getPNProperties();

	}



	@Override
	protected PNGraphComponent createGraphComponent() {
		return new IFNetGraphComponent(new IFNetGraph(getNetContainer(), getPNProperties()));

	}

	@Override
	public EditorPopupMenu getPopupMenu() {
		try {
			return new EditorPopupMenu(this);
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TransitionPopupMenu getTransitionPopupMenu() {
				try {
					return new TransitionPopupMenu(this);
				} catch (ParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PropertyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

	}

	@Override
	protected PNProperties createPNProperties() {
		return new IFNetProperties(getNetContainer());
	}
	
	@Override
	protected AbstractToolBar createNetSpecificToolbar() throws EditorToolbarException {
		return new CPNToolBar(this, JToolBar.HORIZONTAL);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected String getArcConstraint(AbstractFlowRelation relation) {
		// TODO: Do something
		return null;
	}

}