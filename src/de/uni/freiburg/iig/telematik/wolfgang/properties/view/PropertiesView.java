package de.uni.freiburg.iig.telematik.wolfgang.properties.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.TreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.graphic.component.RestrictedTextField;
import de.invation.code.toval.graphic.component.event.RestrictedTextFieldListener;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sepia.graphic.GraphicalPTNet;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.NodeGraphics;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.PTGraphics;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Dimension;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Fill;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Line;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Position;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.wolfgang.icons.IconFactory;
import de.uni.freiburg.iig.telematik.wolfgang.menu.AbstractToolBar;
import de.uni.freiburg.iig.telematik.wolfgang.menu.PNEditingModeListener;
import de.uni.freiburg.iig.telematik.wolfgang.menu.AbstractToolBar.Mode;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.PNProperties.PNComponent;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.EditorForFirstColumn;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.EditorForPropertiesFieldColumn;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.JTableRenderer;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.PNCellEditor;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.PNPropertiesTreeUI;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.PNTreeNode;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.PNTreeNodeRenderer;
import de.uni.freiburg.iig.telematik.wolfgang.properties.view.tree.PNTreeNodeType;

public class PropertiesView extends JTree implements PNPropertiesListener {



	private static final long serialVersionUID = -23504178961013201L;

	protected PNProperties properties = null;
	private final Image propertiesImage = IconFactory.getIconImageFixSize("edit_properties");
	Color bgcolor = UIManager.getColor("Panel.background");

	// The Three basic ParentNodes of the tree
	PNTreeNode placesNode = new PNTreeNode("Places", PNTreeNodeType.PLACES);
	PNTreeNode transitionsNode = new PNTreeNode("Transitions", PNTreeNodeType.TRANSITIONS);
	PNTreeNode arcsNode = new PNTreeNode("Arcs", PNTreeNodeType.ARCS);

	private PNTreeNode root;
	private DefaultTreeModel treeModel;

	private AbstractToolBar editorToolbar;

	private boolean selectNodeCall;

	public AbstractToolBar getEditorToolbar() {
		return editorToolbar;
	}

	public void setEditorToolbar(AbstractToolBar editorToolbar) {
		this.editorToolbar = editorToolbar;
	}

	public PropertiesView(PNProperties properties) {
		Validate.notNull(properties);
		this.properties = properties;
		// expand all nodes in the tree to be visible
		for (int i = 0; i < getRowCount(); i++) {
			expandRow(i);
		}
	}

	/**
	 * Configures the tree, and adds the trees basic nodes {@link #placesNode},
	 * {@link #transitionsNode} and {@link #arcsNode} <br>
	 * and assigns the renderer and and editor.
	 * 
	 * @throws IOException
	 * @throws PropertyException
	 *             @
	 */
	public void setUpGUI() throws PropertyException, IOException {
		String netName = properties.getNetContainer().getPetriNet().getName();
		add(new JLabel(netName));
		root = new PNTreeNode(netName, PNTreeNodeType.ROOT);

		root.add(placesNode);
		root.add(transitionsNode);
		root.add(arcsNode);

		treeModel = new DefaultTreeModel(root);
		this.setModel(treeModel);
		setInvokesStopCellEditing(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setRootVisible(false);
		// Color bgcolor = WolfgangProperties.getInstance().getGridColor();
		this.setBackground(bgcolor);

		// Set Editor for Property Fields
		JTextField txt = new JTextField();
		txt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		PNCellEditor editor = new PNCellEditor(txt);
		setCellEditor(editor);
		setEditable(true);
		setRowHeight(0);
		PNTreeNodeRenderer renderer = new PNTreeNodeRenderer();
		renderer.setTextSelectionColor(Color.BLACK);
		setCellRenderer(renderer);
		// Add PN information
		List<String> placeNames = properties.getPlaceNames();
		Collections.sort(placeNames);
		for (String placeName : placeNames) {
			insertPlaceNode(placeName);
		}

		List<String> transitionNames = properties.getTransitionNames();
		Collections.sort(transitionNames);
		for (String transitionName : transitionNames) {
			insertTransitionNode(transitionName);
		}

		List<String> arcNames = properties.getArcNames();
		Collections.sort(arcNames);
		for (String arcName : arcNames) {
			insertArcNode(arcName);
		}
		for (int i = 0; i < getRowCount(); i++) {
			expandRow(i);
		}
		deselect();

	}

	// Creates PropertiesFields of for the given Name

	private PNTreeNode createFields(String nodeName, PNComponent pnProperty, PNTreeNodeType nodeType) throws PropertyException, IOException {
		PNTreeNode node = new PNTreeNode(nodeName, nodeType);
		Set<PNProperty> propertiesSet = null;
		switch (pnProperty) {
		case ARC:
			propertiesSet = properties.getArcProperties();
			break;
		case PLACE:
			propertiesSet = properties.getPlaceProperties();
			break;
		case TRANSITION:
			propertiesSet = properties.getTransitionProperties();
			break;
		}
		List<PNProperty> list = new ArrayList<PNProperty>(propertiesSet);
		Collections.sort(list);
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(2);
		for (PNProperty property : list) {
			PropertiesField field = null;
			if (!property.equals(PNProperty.PLACE_CAPACITY)) {
				field = new PropertiesField(pnProperty, nodeName, properties.getValue(pnProperty, nodeName, property), property);
				field.addListener(field);

			} else {
				field = new PropertiesField(pnProperty, nodeName, properties.getValue(pnProperty, nodeName, property), property) {

					@Override
					public void setText(String t) {
						if (t.equals("-1"))
							super.setText("\u221E");
						else
							super.setText(t);
					}

				};
				field.addListener(field);
				field.setEditable(false);
				field.setBackground(bgcolor);

			}

			boolean showArcWeight = true;
			switch (property) {
			case ARC_WEIGHT:
				if (properties instanceof CPNProperties || properties instanceof IFNetProperties)
					showArcWeight = false;
				break;
			case PLACE_LABEL:
				node.setTextField(field);
				break;
			case TRANSITION_LABEL:
				node.setTextField(field);
				break;
			default:
				break;
			}

			if (showArcWeight)
				tableModel.addRow(new Object[] { property + ":", field });
			
		}

		// Order of Properties corresponds to Order of PropertiesClass

		final PNPropertiesTable table = new PNPropertiesTable(tableModel);
		editorToolbar.addEditingModeListener(table);

		// Color bgcolor = Color.LIGHT_GRAY;

		table.setBackground(bgcolor);

		TableColumnModel colModel = table.getColumnModel();
		TableColumn col1 = colModel.getColumn(1);
		col1.setCellRenderer(new JTableRenderer());
		int col1Width = 60;
		col1.setWidth(col1Width);
		col1.setPreferredWidth(col1Width);
		col1.setMinWidth(col1Width);
		col1.setMaxWidth(col1Width);
		col1.setCellEditor(new EditorForPropertiesFieldColumn());
		TableColumn col0 = colModel.getColumn(0);
		int col0Width = 60;
		col0.setWidth(col0Width);
		col0.setPreferredWidth(col0Width);
		col0.setMinWidth(col0Width);
		col0.setMaxWidth(col0Width);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		col0.setCellRenderer(rightRenderer);
		col0.setCellEditor(new EditorForFirstColumn());

		table.setPreferredScrollableViewportSize(table.getPreferredSize());

		Object key = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke("ENTER"));
		final Action action = table.getActionMap().get(key);
		Action custom = new AbstractAction("wrap") {
			// Currently no reaction on ENTER
			@Override
			public void actionPerformed(ActionEvent e) {

				// Default behaviour on Enter
				// int row = table.getSelectionModel().getLeadSelectionIndex();
				// if (row == table.getRowCount() - 1) {
				// // do custom stuff
				// // return if default shouldn't happen or call default after
				// return;
				// }
				// action.actionPerformed(e);
			}

		};
		table.getActionMap().put(key, custom);

		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		// table.repaint();
		table.setGridColor(bgcolor);

		node.add(new PNTreeNode(table, PNTreeNodeType.LEAF));
		return node;
	}

	
	private class PNPropertiesTable extends JTable implements PNEditingModeListener {
	

		public PNPropertiesTable(DefaultTableModel tableModel) {
			super(tableModel);
		}

		@Override
		public void editingModeChanged(Mode mode) {
			switch (mode) {
			case EDIT:
				setEnabled(true);
				break;
			case PLAY:
				setEnabled(false);
				break;
			default:
				break;

			}
			
		}

	}
	/**
	 * This method is called each time a value is changed within one of the
	 * textfields.
	 * 
	 * @param fieldType
	 * @param name
	 * @param property
	 * @param oldValue
	 * @param newValue
	 */
	protected void propertiesFieldValueChanged(PNComponent fieldType, String name, PNProperty property, String oldValue, String newValue) {
		try {
			properties.setValue(this, fieldType, name, property, newValue);
		} catch (ParameterException e1) {
			switch (fieldType) {
			case PLACE:
			case TRANSITION:
			case ARC:
				setPropertiesFieldValue(name, property, oldValue);
			}
		}
	}

	@Override
	public void propertyChange(PNPropertyChangeEvent event) {
		if (event.getSource() != this) {
			switch (event.getFieldType()) {
			case PLACE:
			case TRANSITION:
			case ARC:
				setPropertiesFieldValue(event.getName(), event.getProperty(), event.getNewValue().toString());
				repaint();
				break;
			}
		}
	}

	/**
	 * @param name
	 * @param property
	 * @param oldValue
	 */
	protected void setPropertiesFieldValue(String name, PNProperty property, String oldValue) {
		PNTreeNode child = (PNTreeNode) findTreeNodeByName((DefaultMutableTreeNode) getModel().getRoot(), name).getFirstChild();
		int i = 0;
		for (i = 0; i <= child.getTable().getRowCount(); i++) {
			if (child.getTable().getValueAt(i, 0).toString().equals(property + ":"))
				break;
		}
		((JTextField) child.getTable().getValueAt(i, 1)).setText(oldValue);
	}

	public class PropertiesField extends RestrictedTextField implements RestrictedTextFieldListener, PNEditingModeListener {

		private static final long serialVersionUID = -2791152505686200734L;
		private PNComponent type = null;
		private PNProperty property = null;
		private String name = null;

		public PropertiesField(PNComponent type, String name, String text, PNProperty property) {
			super(property.getRestriction(), text);
			this.type = type;
			this.property = property;
			this.name = name;
			setOpaque(true);
			// Color bgcolor =
			// WolfgangProperties.getInstance().getBackgroundColor();

			setBackground(bgcolor);
			editorToolbar.addEditingModeListener(this);
			this.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						stopEditing();
						// clearSelection();
					}
				}

			});

		}

		protected PropertiesField getPropertyField() {
			return this;

		}

		public PNProperty getPNProperty() {
			return property;
		}

		@Override
		public void valueChanged(String oldValue, String newValue) {
			propertiesFieldValueChanged(type, name, property, oldValue, newValue);
		}

		@Override
		public void setBorder(Border border) {
			// Remove Border from Textfield
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (isEditable())
				g.drawImage(propertiesImage, 45, 2, null);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
		}

		@Override
		public void editingModeChanged(Mode mode) {
			switch (mode) {
			case EDIT:
				if(!this.property.equals(PNProperty.PLACE_CAPACITY))
				setEditable(true);
				break;
			case PLAY:
				setEditable(false);
				break;
			default:
				break;
				
			}
			setBackground(bgcolor);
			

		}

	}

	// CURRENTLY NOT IN USE

	@Override
	public void componentAdded(PNComponent component, String name) {
		// try {
		switch (component) {
		case PLACE:
			try {
				insertPlaceNode(name);
			} catch (PropertyException | IOException e1) {
				throw new RuntimeException(e1);
			}
			break;
		case TRANSITION:
			try {
				insertTransitionNode(name);
			} catch (PropertyException | IOException e1) {
				throw new RuntimeException(e1);
			}
			break;
		case ARC:
			try {
				insertArcNode(name);
			} catch (PropertyException | IOException e) {
				throw new RuntimeException(e);
			}
			break;
		}
	}

	private void insertArcNode(String name) throws PropertyException, IOException {
		treeModel.insertNodeInto(createFields(name, PNComponent.ARC, PNTreeNodeType.ARC), arcsNode, getNextIndex(properties.getArcNames(), name));
	}

	private void insertTransitionNode(String name) throws PropertyException, IOException {
		treeModel.insertNodeInto(createFields(name, PNComponent.TRANSITION, PNTreeNodeType.TRANSITION), transitionsNode, getNextIndex(properties.getTransitionNames(), name));
	}

	private void insertPlaceNode(String name) throws PropertyException, IOException {
		treeModel.insertNodeInto(createFields(name, PNComponent.PLACE, PNTreeNodeType.PLACE), placesNode, getNextIndex(properties.getPlaceNames(), name));
	}

	private int getNextIndex(List<String> list, String name) {
		int i = 0;
		for (String p : list) {
			if (list.get(i).equals(name))
				return i;
			i++;
		}
		return i;
	}

	@Override
	public void componentRemoved(PNComponent component, String name) {
		DefaultMutableTreeNode comp = findTreeNodeByName((DefaultMutableTreeNode) getModel().getRoot(), name);
		if (comp != null)
			treeModel.removeNodeFromParent(comp);
	}

	public void selectNode(String name) {
		selectNodeCall = true;
		DefaultMutableTreeNode node = findTreeNodeByName((DefaultMutableTreeNode) getModel().getRoot(), name);

		if (node != null) {
			PNTreeNode firstChild = (PNTreeNode) ((PNTreeNode) node).getChildAt(0);
			TreePath propPath = new TreePath(firstChild.getPath());
			deselect(); // collapses all unfolded paths before currently
						// selected node is expanded
			setSelectionPath(new TreePath(node.getPath()));
			collapsePath(propPath);

		} else {
			deselect();
		}
	}

	public void deselect() {
		for (int i = getRowCount(); i >= 0; i--) {
			collapseRow(i);
		}
	}

	@Override
	protected void fireValueChanged(final TreeSelectionEvent e) {
		if (this.isValid()&&!selectNodeCall)
			super.fireValueChanged(e);
		else {
			super.clearSelection();
		}
		selectNodeCall = false;

	}

	private DefaultMutableTreeNode findTreeNodeByName(DefaultMutableTreeNode root, String name) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(name)) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(0);
				return node;
			}
		}
		return null;
	}

	public static void main(String[] args) throws PropertyException, IOException {
		PTNet ptNet = createPTNet();
		PTGraphics ptNetGraphics = createPTNetGraphics(ptNet);
		GraphicalPTNet netContainer = new GraphicalPTNet(ptNet, ptNetGraphics);
		JPanel pnlPv = new JPanel();
		PropertiesView propertiesView = new PropertiesView(new PTProperties(netContainer));
		propertiesView.setUpGUI();
		pnlPv.add(propertiesView);
		new DisplayFrame(pnlPv, true);

	}

	private static PTNet createPTNet() throws ParameterException {
		PTNet ptnet = null;

		// Create places
		Set<String> places = new HashSet<>();
		places.add("p0");
		places.add("p1");
		places.add("p2");
		places.add("p3");

		// create transitions
		Set<String> transitions = new HashSet<>();
		transitions.add("t0");
		transitions.add("t1");

		// create the the token colors used in the initial marking
		PTMarking marking = new PTMarking();
		marking.set("p0", 1);

		// create the P/T-net with all tokens in P0
		ptnet = new PTNet(places, transitions, marking);

		// Add the flow relation
		ptnet.addFlowRelationPT("p0", "t0");
		ptnet.addFlowRelationTP("t0", "p1");
		ptnet.addFlowRelationPT("p1", "t1");
		ptnet.addFlowRelationTP("t1", "p3");
		ptnet.addFlowRelationTP("t1", "p2");

		return ptnet;
	}

	private static PTGraphics createPTNetGraphics(PTNet ptnet) throws ParameterException {
		PTGraphics ptnetGraphics = new PTGraphics();
		Map<String, NodeGraphics> placeGraphics = new HashMap<>();
		Map<String, NodeGraphics> transistionGraphics = new HashMap<>();
		for (AbstractPNNode p : ptnet.getPlaces()) {
			NodeGraphics nodeGraphics = new NodeGraphics(new Position(10, 10), new Dimension(20, 20), new Fill(), new Line());
			placeGraphics.put(p.getName(), nodeGraphics);
		}
		ptnetGraphics.setPlaceGraphics(placeGraphics);
		for (AbstractPNNode p : ptnet.getTransitions()) {
			NodeGraphics nodeGraphics = new NodeGraphics(new Position(30, 30), new Dimension(40, 40), new Fill(), new Line());
			transistionGraphics.put(p.getName(), nodeGraphics);
		}
		ptnetGraphics.setTransitionGraphics(transistionGraphics);

		return ptnetGraphics;
	}

	@Override
	/**
	 * Notification from the <code>UIManager</code> that the L&F has changed.
	 * Replaces the current UI object with the latest version from the
	 * <code>UIManager</code>.
	 *
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		super.updateUI();
		setUI((TreeUI) new PNPropertiesTreeUI());

	}


}
