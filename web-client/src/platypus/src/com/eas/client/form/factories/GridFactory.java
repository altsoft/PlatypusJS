package com.eas.client.form.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cyberneko.html.filters.Identity;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.BooleanRowValueConverter;
import com.eas.client.converters.DateRowValueConverter;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.ObjectKeyProvider;
import com.eas.client.form.Publisher;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.combo.ComboLabelProvider;
import com.eas.client.form.grid.RowsetPositionSelectionHandler;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.ModelGridColumn;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.rows.RowChildrenFetcher;
import com.eas.client.form.grid.rows.RowsetDataProvider;
import com.eas.client.form.grid.selection.MultiRowSelectionModel;
import com.eas.client.form.grid.selection.SingleRowSelectionModel;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.PlatypusComboBox;
import com.eas.client.form.published.widgets.PlatypusDateField;
import com.eas.client.form.published.widgets.PlatypusFormattedTextField;
import com.eas.client.form.published.widgets.PlatypusSpinnerField;
import com.eas.client.form.published.widgets.PlatypusTextArea;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

public class GridFactory {

	//
	public static final int ONE_FIELD_ONE_QUERY_TREE_KIND = 1;
	public static final int FIELD_2_PARAMETER_TREE_KIND = 2;
	public static final int SCRIPT_PARAMETERS_TREE_KIND = 3;

	protected Element gridTag;

	protected int treeKind = ONE_FIELD_ONE_QUERY_TREE_KIND;
	protected ModelElementRef unaryLinkField;
	protected ModelElementRef param2GetChildren;
	protected ModelElementRef paramSourceField;
	protected Model model;
	protected Entity rowsSource;
	protected ModelGrid grid;
	// columns
	protected List<ComboLabelProvider> comboLabelProviders = new ArrayList<>();
	protected List<ModelGridColumn<?>> publishedColumns = new ArrayList<>();

	protected Set<Entity> rowsetsOfInterestHosts = new HashSet<Entity>();

	public GridFactory(Element aTag, Model aModel) {
		super();
		gridTag = aTag;
		model = aModel;
	}

	public ModelGrid getModelGrid() {
		return grid;
	}

	protected void cleanup() {
		rowsSource = null;
	}

	public void process() throws Exception {
		cleanup();
		Element rowsColumnsTag = Utils.getElementByTagName(gridTag, "rowsColumnsDesignInfo");
		int rowsHeaderType = Utils.getIntegerAttribute(rowsColumnsTag, "rowsHeaderType", ModelGrid.ROWS_HEADER_TYPE_USUAL);
		int fixedRows = Utils.getIntegerAttribute(rowsColumnsTag, "fixedRows", 0);
		int fixedColumns = Utils.getIntegerAttribute(rowsColumnsTag, "fixedColumns", 0);
		int rowsHeight = Utils.getIntegerAttribute(gridTag, "rowsHeight", 20);
		boolean rowLines = Utils.getBooleanAttribute(gridTag, "showHorizontalLines", true);
		boolean columnLines = Utils.getBooleanAttribute(gridTag, "showVerticalLines", true);
		boolean showOddRowsInOtherColor = Utils.getBooleanAttribute(gridTag, "showOddRowsInOtherColor", true);
		boolean editable = Utils.getBooleanAttribute(gridTag, "editable", true);
		final boolean deletable = Utils.getBooleanAttribute(gridTag, "deletable", true);
		final boolean insertable = Utils.getBooleanAttribute(gridTag, "insertable", true);
		final String generalCellFunctionName = rowsColumnsTag.getAttribute("generalRowFunction");
		ModelElementRef rowsModelElement = new ModelElementRef(Utils.getElementByTagName(rowsColumnsTag, "rowsDatasource"), model);
		rowsSource = rowsModelElement.entity;
		rowsetsOfInterestHosts.add(rowsSource);

		Element treeTag = Utils.getElementByTagName(gridTag, "treeDesignInfo");
		unaryLinkField = new ModelElementRef(Utils.getElementByTagName(treeTag, "unaryLinkField"), model);
		treeKind = Utils.getIntegerAttribute(treeTag, "treeKind", ONE_FIELD_ONE_QUERY_TREE_KIND);
		param2GetChildren = new ModelElementRef(Utils.getElementByTagName(treeTag, "param2GetChildren"), model);
		paramSourceField = new ModelElementRef(Utils.getElementByTagName(treeTag, "paramSourceField"), model);

		grid = new ModelGrid(); 
		grid.setRowsSource(rowsSource);
		// Selection models & service column configuration
		grid.setRowsHeaderType(rowsHeaderType);
		// Columns multi level structure
		NodeList nodesWithColumns = gridTag.getChildNodes();
		for (int i = 0; i < nodesWithColumns.getLength(); i++) {
			if ("column".equalsIgnoreCase(nodesWithColumns.item(i).getNodeName())) {
				assert nodesWithColumns.item(i) instanceof Element : "Column must be a tag";
				Element columnTag = (Element) nodesWithColumns.item(i);
				processColumn(columnTag, 0);
			}
		}
		// Data plain and tree(optional) layers
		if (isTreeConfigured()) {
		} else {
		}
		/*
		editing.setEditable(editable);
		editing.setDeletable(deletable);
		editing.setInsertable(insertable);
		*/
		// row lines ?
		// column lines ?
		for (Entity entity : rowsetsOfInterestHosts) {
			grid.addUpdatingTriggerEntity(entity);
		}
		for (ComboLabelProvider labelProvider : comboLabelProviders) {
			labelProvider.setOnLabelCacheChange(grid.getCrossUpdaterAction());
		}
	}

	private boolean isLazyTreeConfigured() {
		return param2GetChildren.isCorrect() && param2GetChildren.field != null && paramSourceField.isCorrect() && paramSourceField.field != null;
	}

	private boolean isTreeConfigured() throws Exception {
		return rowsSource != null && unaryLinkField.isCorrect() && unaryLinkField.field != null && (treeKind == ONE_FIELD_ONE_QUERY_TREE_KIND || treeKind == FIELD_2_PARAMETER_TREE_KIND);
	}

	private HeaderGroupConfig processColumn(Element aTag, int deepness) throws Exception {
		// plain settings
		String name = aTag.getAttribute("name");
		String title = aTag.getAttribute("title");
		boolean visible = Utils.getBooleanAttribute(aTag, "visible", true);
		boolean enabled = Utils.getBooleanAttribute(aTag, "enabled", true);
		boolean substitute = Utils.getBooleanAttribute(aTag, "substitute", false);
		boolean plain = Utils.getBooleanAttribute(aTag, "plain", true);
		int width = Utils.getIntegerAttribute(aTag, "width", 50);

		Element cellTag = null;
		ModelElementRef modelElement = null;
		Element controlTag = null;
		// veers
		ModelElementRef columnsDatasource = null;
		ModelElementRef columnsDisplayField = null;
		ModelElementRef cellsDatasource = null;
		// style
		Element headerEasFontTag = null;
		Element styleTag = null;
		int childrenCount = 0;
		int subgroupsCount = 0;
		List<HeaderGroupConfig> subGroups = new ArrayList<HeaderGroupConfig>();
		NodeList columnNodes = aTag.getChildNodes();
		Integer _currentLeavesCount = null;
		for (int c = 0; c < columnNodes.getLength(); c++) {
			if ("cellDesignInfo".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				cellTag = (Element) columnNodes.item(c);
			else if ("datamodelElement".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				modelElement = new ModelElementRef((Element) columnNodes.item(c), model);
			else if ("controlInfo".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				controlTag = (Element) columnNodes.item(c);
			// veers
			else if ("columnsDatasource".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				columnsDatasource = new ModelElementRef((Element) columnNodes.item(c), model);
			else if ("columnsDisplayField".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				columnsDisplayField = new ModelElementRef((Element) columnNodes.item(c), model);
			else if ("cellsDatasource".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				cellsDatasource = new ModelElementRef((Element) columnNodes.item(c), model);
			// style
			else if ("headerEasFont".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				headerEasFontTag = (Element) columnNodes.item(c);
			else if ("style".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				styleTag = (Element) columnNodes.item(c);
			else if ("column".equalsIgnoreCase(columnNodes.item(c).getNodeName())) {
				if (_currentLeavesCount == null)
					_currentLeavesCount = currentLeavesCount;
				childrenCount++;
				HeaderGroupConfig subGroup = processColumn((Element) columnNodes.item(c), deepness + 1);
				subGroups.add(subGroup);
				if (subGroup != null)// leaf
					subgroupsCount++;
			}
		}

		if (childrenCount > 0) {
			assert _currentLeavesCount != null;
			HeaderGroupConfig group = new HeaderGroupConfig((title != null && !title.isEmpty()) ? title : name);
			groups.add(group);
			group.setRow(deepness);
			group.setColumn(_currentLeavesCount);
			group.setRowspan(1);
			int colSpan = 0;
			for (HeaderGroupConfig subGroup : subGroups) {
				if (subGroup == null)
					colSpan++;
				else
					colSpan += subGroup.getColspan();
			}
			group.setColspan(colSpan);
			return group;
		} else {
			currentLeavesCount++;
			ColumnConfig<Row, ?> cc = configureColumn(name, title, width, modelElement, aTag, controlTag);
			if (cc != null) {
				cc.setHidden(!visible);
			}
			return null;
		}
	}

	private void bindCallback(final com.google.gwt.cell.client.Cell.Context context, final PublishedCell cellToRender) {
		cellToRender.setDisplayCallback(new Runnable() {
			@Override
			public void run() {
				final TableCellElement parent = (TableCellElement) grid.getView().getCell(context.getIndex(), context.getColumn());
				if (parent != null) {
					// rendering
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					SafeHtmlBuilder lsb = new SafeHtmlBuilder();
					String toRender1 = cellToRender.getDisplay();
					if (toRender1 == null)
						lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
					else
						lsb.append(SafeHtmlUtils.fromString(toRender1));
					PublishedStyle styleToRender = grid.complementPublishedStyle(cellToRender.getStyle());
					ControlsUtils.renderDecorated(lsb, styleToRender, sb);
					// detemine where to apply rendered html
					XElement cellInnerElement = null;
					if (grid.getView() instanceof PlatypusTreeGridView) {
						PlatypusTreeGridView pTreeView = (PlatypusTreeGridView) grid.getView();
						String cellInnerClass = pTreeView.getFirstCellTextSelector();
						// The cellInnerClass is already with dot prefix
						cellInnerElement = parent.<XElement> cast().selectNode(cellInnerClass);
						if (cellInnerElement == null) {
							cellInnerClass = grid.getView().getAppearance().styles().cellInner();
							cellInnerElement = parent.<XElement> cast().selectNode("." + cellInnerClass);
						}
					} else {
						String cellInnerClass = grid.getView().getAppearance().styles().cellInner();
						cellInnerElement = parent.<XElement> cast().selectNode("." + cellInnerClass);
					}
					if (cellInnerElement != null)
						cellInnerElement.setInnerSafeHtml(sb.toSafeHtml());
				}
			}
		});
	}

	private ColumnConfig<Row, ?> configureColumn(String aName, String aTitle, int aWidth, final ModelElementRef aColModelElement, Element aColumnTag, Element aControlTag) throws Exception {
		boolean readonly = Utils.getBooleanAttribute(aColumnTag, "readonly", false);
		final String cellFunctionName = aColumnTag.getAttribute("cellFunction");
		final String selectFunctionName = aColumnTag.getAttribute("selectFunction");
		boolean selectOnly = Utils.getBooleanAttribute(aColumnTag, "selectOnly", false);
		boolean fixed = Utils.getBooleanAttribute(aColumnTag, "fixed", false);

		if (aColModelElement != null && aColModelElement.isCorrect()) {
			if (aColModelElement.entity != rowsSource)
				rowsetsOfInterestHosts.add(aColModelElement.entity);
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant((aTitle != null && !aTitle.isEmpty()) ? aTitle : aName);
			String controlInfoName = aControlTag.getAttribute("classHint");
			if (rowsSource != null && aColModelElement.entity != null && aColModelElement.field != null) {
				if ("DbSchemeDesignInfo".equalsIgnoreCase(controlInfoName)) {
				} else if ("DbGridDesignInfo".equalsIgnoreCase(controlInfoName)) {
				} else if ("DbMapDesignInfo".equalsIgnoreCase(controlInfoName)) {
				} else if ("DbLabelDesignInfo".equalsIgnoreCase(controlInfoName)) {
					String formatPattern = aControlTag.getAttribute("format");
					int formatType = Utils.getIntegerAttribute(aControlTag, "valueType", ObjectFormat.MASK);
					final ObjectFormat format = new ObjectFormat(formatType, formatPattern);
					final ModelGridObjectColumn column = new ModelGridObjectColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));
					PlatypusColumnConfig<Row, Object> cc = new PlatypusColumnConfig<Row, Object>(new ValueGridColumn<Object>(rowsSource, aColModelElement, new ObjectRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					cc.setCell(new AbstractCell<Object>() {

						@Override
						public void render(final com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
							try {
								PublishedStyle styleToRender = null;
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								String toRender = format.format(value);
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, toRender);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
									if (cellToRender.getDisplay() != null)
										toRender = cellToRender.getDisplay();
								}
								if (toRender == null)
									lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
								else
									lsb.append(SafeHtmlUtils.fromString(toRender));
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception e) {
								sb.append(SafeHtmlUtils.fromString(e.getMessage()));
							}
						}
					});

					PlatypusFormattedTextField tf = new PlatypusFormattedTextField(format);
					PlatypusAdapterCellField<Object> resComp = new PlatypusAdapterCellField<Object>(tf, column);
					JavaScriptObject published = Publisher.publishColumnEditor(tf, resComp);
					tf.setData(PlatypusWindow.PUBLISHED_DATA_KEY, published);
					resComp.setPublishedField(published);
					resComp.setEditable(!readonly);
					resComp.setSelectOnly(selectOnly);
					cc.setEditor(resComp);
					column.setEditor(resComp);
					column.setColumnConfig(cc);
					leaves.add(cc);
					oColumns.add(cc);
					return cc;
				} else if ("DbTextDesignInfo".equalsIgnoreCase(controlInfoName)) {
					final ModelGridTextColumn column = new ModelGridTextColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));
					PlatypusColumnConfig<Row, String> cc = new PlatypusColumnConfig<Row, String>(new ValueGridColumn<String>(rowsSource, aColModelElement, new StringRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					cc.setCell(new AbstractCell<String>() {

						@Override
						public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
							try {
								PublishedStyle styleToRender = null;
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								String toRender = value;
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, value);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
									if (cellToRender.getDisplay() != null)
										toRender = cellToRender.getDisplay();
								}
								if (toRender == null)
									lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
								else
									lsb.append(SafeHtmlUtils.fromString(toRender));
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception ex) {
								Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
							}
						}
					});

					PlatypusTextArea ta = new PlatypusTextArea();
					PlatypusAdapterCellField<String> resComp = new PlatypusAdapterCellField<String>(ta, column);
					JavaScriptObject published = Publisher.publishColumnEditor(ta, resComp);
					ta.setData(PlatypusWindow.PUBLISHED_DATA_KEY, published);
					resComp.setPublishedField(published);
					resComp.setEditable(!readonly);
					resComp.setSelectOnly(selectOnly);
					cc.setEditor(resComp);
					column.setEditor(resComp);
					column.setColumnConfig(cc);
					leaves.add(cc);
					sColumns.add(cc);
					return cc;
				} else if ("DbDateDesignInfo".equalsIgnoreCase(controlInfoName)) {
					final ModelGridDateColumn column = new ModelGridDateColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));
					PlatypusColumnConfig<Row, Date> cc = new PlatypusColumnConfig<Row, Date>(new ValueGridColumn<Date>(rowsSource, aColModelElement, new DateRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					String dateFormat = aControlTag.getAttribute("dateFormat");
					if (dateFormat != null)
						dateFormat = ControlsUtils.convertDateFormatString(dateFormat);
					final DateTimeFormat dtFormat = dateFormat != null ? DateTimeFormat.getFormat(dateFormat) : DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL);
					cc.setCell(new AbstractCell<Date>() {

						@Override
						public void render(com.google.gwt.cell.client.Cell.Context context, Date value, SafeHtmlBuilder sb) {
							try {
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								PublishedStyle styleToRender = null;
								String toRender = value != null ? dtFormat.format(value) : null;
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, toRender);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
									if (cellToRender.getDisplay() != null)
										toRender = cellToRender.getDisplay();
								}
								if (toRender == null)
									lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
								else
									lsb.append(SafeHtmlUtils.fromString(toRender));
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception ex) {
								Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
							}
						}
					});
					final PlatypusDateField df = new PlatypusDateField(dtFormat);
					PlatypusAdapterCellField<Date> resComp = new PlatypusAdapterCellField<Date>(df, column);
					JavaScriptObject published = Publisher.publishColumnEditor(df, resComp);
					df.setData(PlatypusWindow.PUBLISHED_DATA_KEY, published);
					resComp.setPublishedField(published);
					resComp.setEditable(!readonly);
					resComp.setSelectOnly(selectOnly);
					cc.setEditor(resComp);
					column.setEditor(resComp);
					column.setColumnConfig(cc);
					leaves.add(cc);
					dColumns.add(cc);
					return cc;
				} else if ("DbImageDesignInfo".equalsIgnoreCase(controlInfoName)) {
				} else if ("DbCheckDesignInfo".equalsIgnoreCase(controlInfoName)) {
					final ModelGridColumn column = new ModelGridColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));
					PlatypusColumnConfig<Row, Boolean> cc = new PlatypusColumnConfig<Row, Boolean>(new ValueGridColumn<Boolean>(rowsSource, aColModelElement, new BooleanRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					// There are some nightmare with null values and script
					// boolean value(NOT Boolean !).
					// If you want use published version of the editor, you
					// should solve this problem.
					CheckBoxCell cell = new CheckBoxCell() {

						@Override
						public void render(com.google.gwt.cell.client.Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
							try {
								PublishedStyle styleToRender = null;
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, null);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
								}
								CheckBoxCellOptions options = new CheckBoxCellOptions();
								options.setName(name);

								// radios must have a name for ie6 and ie7
								if (name == null && (GXT.isIE6() || GXT.isIE7())) {
									name = XDOM.getUniqueId();
								}

								options.setReadonly(isReadOnly());
								options.setDisabled(isDisabled());
								options.setBoxLabel(getBoxLabel());
								String checkBoxId = XDOM.getUniqueId();

								String nameParam = options.getName() != null ? " name='" + options.getName() + "' " : "";
								String disabledParam = options.isDisabled() ? " disabled=true" : "";
								String readOnlyParam = options.isReadonly() ? " readonly" : "";
								String idParam = " id=" + checkBoxId;
								String typeParam = " type=checkbox";
								String checkedParam = value != null && value ? " checked" : "";

								lsb.appendHtmlConstant("<input " + typeParam + nameParam + disabledParam + readOnlyParam + idParam + checkedParam + " />");
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception ex) {
								Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
							}
						}
					};
					cell.setReadOnly(readonly);
					cell.setWidth(cc.getWidth());
					cc.setCell(cell);
					column.setColumnConfig(cc);
					leaves.add(cc);
					bColumns.add(cc);
					return cc;
				} else if ("DbSpinDesignInfo".equalsIgnoreCase(controlInfoName)) {
					final ModelGridSpinColumn column = new ModelGridSpinColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));
					PlatypusColumnConfig<Row, Double> cc = new PlatypusColumnConfig<Row, Double>(new ValueGridColumn<Double>(rowsSource, aColModelElement, new DoubleRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					cc.setCell(new NumberCell<Double>() {

						@Override
						public void render(com.google.gwt.cell.client.Cell.Context context, Number value, SafeHtmlBuilder sb) {
							try {
								PublishedStyle styleToRender = null;
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								String toRender = value != null ? NumberFormat.getDecimalFormat().format(value) : null;
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, toRender);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
									if (cellToRender.getDisplay() != null)
										toRender = cellToRender.getDisplay();
								}
								if (toRender == null)
									lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
								else
									lsb.append(SafeHtmlUtils.fromString(toRender));
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception ex) {
								Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
							}
						}

					});
					final PlatypusSpinnerField sf = new PlatypusSpinnerField(new SpinnerFieldCell<Double>(new NumberPropertyEditor.DoublePropertyEditor()));
					if (aControlTag.hasAttribute("min"))
						sf.setMinValue(Double.valueOf(aControlTag.getAttribute("min")));
					else
						sf.setMinValue(-Double.MAX_VALUE);
					if (aControlTag.hasAttribute("max"))
						sf.setMaxValue(Double.valueOf(aControlTag.getAttribute("max")));
					else
						sf.setMaxValue(Double.MAX_VALUE);
					if (aControlTag.hasAttribute("step"))
						sf.setIncrement(Double.valueOf(aControlTag.getAttribute("step")));

					PlatypusAdapterCellField<Double> resComp = new PlatypusAdapterCellField<Double>(sf, column);
					JavaScriptObject published = Publisher.publishColumnEditor(sf, resComp);
					sf.setData(PlatypusWindow.PUBLISHED_DATA_KEY, published);
					resComp.setPublishedField(published);
					resComp.setEditable(!readonly);
					resComp.setSelectOnly(selectOnly);
					cc.setEditor(resComp);
					column.setEditor(resComp);
					column.setColumnConfig(cc);
					leaves.add(cc);
					nColumns.add(cc);
					return cc;
				} else if ("DbComboDesignInfo".equalsIgnoreCase(controlInfoName)) {
					final ModelGridObjectColumn column = new ModelGridObjectColumn(aName);
					publishedColumns.add(column);
					handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));

					final ModelElementRef valueRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "valueField"), model);
					final ModelElementRef displayRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "displayField"), model);
					rowsetsOfInterestHosts.add(valueRef.entity);
					rowsetsOfInterestHosts.add(displayRef.entity);
					final boolean list = Utils.getBooleanAttribute(aControlTag, "list", true);
					final ComboLabelProvider labelProvider = new ComboLabelProvider();
					labelProvider.setTargetValueRef(aColModelElement);
					labelProvider.setLookupValueRef(valueRef);
					labelProvider.setDisplayValueRef(displayRef);
					comboLabelProviders.add(labelProvider);
					PlatypusColumnConfig<Row, Object> cc = new PlatypusColumnConfig<Row, Object>(new ValueGridColumn<Object>(rowsSource, aColModelElement, new ObjectRowValueConverter()), aWidth,
					        sb.toSafeHtml(), readonly, fixed);
					cc.setCell(new AbstractCell<Object>() {

						@Override
						public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
							try {
								PublishedStyle styleToRender = null;
								SafeHtmlBuilder lsb = new SafeHtmlBuilder();
								String toRender = labelProvider.getLabel(value);
								PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(),
								        column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context, aColModelElement, toRender);
								if (cellToRender != null) {
									styleToRender = cellToRender.getStyle();
									if (cellToRender.getDisplay() != null)
										toRender = cellToRender.getDisplay();
								}
								if (toRender == null)
									lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
								else
									lsb.append(SafeHtmlUtils.fromString(toRender));
								styleToRender = grid.complementPublishedStyle(styleToRender);
								ControlsUtils.renderDecorated(lsb, styleToRender, sb);
								if (cellToRender != null) {
									bindCallback(context, cellToRender);
								}
							} catch (Exception e) {
								sb.append(SafeHtmlUtils.fromString(e.getMessage()));
							}
						}
					});
					ListStore<Object> cbStore = new ListStore<Object>(new ObjectKeyProvider());
					RowsetDataProvider filler = new RowsetDataProvider(cbStore);
					if(valueRef.entity != null){
						filler.setRowset(valueRef.entity.getRowset());
					}
					if(displayRef.entity != null){
						Rowset displayRowset = displayRef.entity.getRowset();
						if(displayRowset != null)
							displayRowset.addRowsetListener(filler);
					}

					final PlatypusComboBox cb = new PlatypusComboBox(new ComboBoxCell<Object>(cbStore, labelProvider));
					cb.setTypeAhead(true);
					cb.setTriggerAction(TriggerAction.ALL);
					cb.getCell().setHideTrigger(!list);
					cb.setEditable(false);
					PlatypusAdapterCellField<Object> resComp = new PlatypusAdapterCellField<Object>(cb, column);
					JavaScriptObject published = Publisher.publishColumnEditor(cb, resComp);
					cb.setData(PlatypusWindow.PUBLISHED_DATA_KEY, published);
					resComp.setPublishedField(published);
					resComp.setEditable(!readonly);
					resComp.setSelectOnly(selectOnly);
					cb.setReadOnly(!list);
					cc.setEditor(resComp);
					column.setEditor(resComp);
					column.setColumnConfig(cc);
					leaves.add(cc);
					oColumns.add(cc);
					return cc;
				}
			}
		} else {
			// Cell calculated only by cell function
			final ModelGridTextColumn column = new ModelGridTextColumn(aName);
			publishedColumns.add(column);
			handlersResolvers.add(new ColumnHandlersResolver(model.getModule(), cellFunctionName, selectFunctionName, column));

			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant((aTitle != null && !aTitle.isEmpty()) ? aTitle : aName);
			PlatypusColumnConfig<Row, String> cc = new PlatypusColumnConfig<Row, String>(new ValueGridColumn<String>(rowsSource, aColModelElement, new StringRowValueConverter()), aWidth,
			        sb.toSafeHtml(), readonly, fixed);
			cc.setCell(new AbstractCell<String>() {

				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
					try {
						String toRender = value;
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						PublishedCell cellToRender = calcContextPublishedCell(column.getEventsThis(), column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(),
						        context, aColModelElement, value);
						if (cellToRender != null) {
							styleToRender = cellToRender.getStyle();
							if (cellToRender.getDisplay() != null)
								toRender = cellToRender.getDisplay();
						}
						if (toRender == null)
							lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
						else
							lsb.append(SafeHtmlUtils.fromString(toRender));
						styleToRender = grid.complementPublishedStyle(styleToRender);
						ControlsUtils.renderDecorated(lsb, styleToRender, sb);
						if (cellToRender != null) {
							bindCallback(context, cellToRender);
						}
					} catch (Exception ex) {
						Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			});
			leaves.add(cc);
			column.setColumnConfig(cc);
			return cc;
		}
		return null;
	}

	public PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject cellFunction, com.google.gwt.cell.client.Cell.Context context, ModelElementRef aColModelElement,
	        String aDisplay) throws Exception {
		if (cellFunction != null) {
			Row renderedRow = renderedRow(context);
			if (renderedRow != null) {
				Object data = aColModelElement != null ? Utils.toJs(renderedRow.getColumnObject(aColModelElement.getColIndex())) : null;
				PublishedCell cell = Publisher.publishCell(data, aDisplay);
				Object[] rowIds = renderedRow.getPKValues();
				if (rowIds != null) {
					for (int i = 0; i < rowIds.length; i++)
						rowIds[i] = Utils.toJs(rowIds[i]);
				}
				Boolean res = Utils.executeScriptEventBoolean(
				        aThis,
				        cellFunction,
				        JsEvents.publishOnRenderEvent(aThis, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? Utils.toJsArray(rowIds) : rowIds[0]) : null, null,
				                Entity.publishRowFacade(renderedRow, rowsSource), cell));
				if (res != null && res) {
					return cell;
				}
			}
		}
		return null;
	}

	protected Row renderedRow(com.google.gwt.cell.client.Cell.Context context) {
		Object key = context.getKey();
		return store.findModelWithKey(key != null ? key.toString() : null);
	}
}
