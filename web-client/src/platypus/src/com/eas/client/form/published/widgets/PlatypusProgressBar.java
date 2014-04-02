package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.progress.ProgressBar;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;

public class PlatypusProgressBar extends ProgressBar implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled;
	protected String name;
	protected JavaScriptObject published;
	
	protected String text;
	protected TextFormatter formatter = new TextFormatter() {

		@Override
		public String getText(ProgressBar bar, Double curProgress) {
			return text;
		}
	};

	public PlatypusProgressBar() {
		super();
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
    public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu; 
    }

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT && menu != null) {
							menu.showRelativeTo(PlatypusProgressBar.this);
						}
					}

				}, ClickEvent.getType());
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		enabled = aValue;
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public String getText() {
		return text;
	}

	public void setText(String aValue) {
		if (text == null && aValue != null || !text.equals(aValue)) {
			text = aValue;
			if (text != null) {
				setTextFormatter(formatter);
			} else {
				setTextFormatter(null);
			}
			
		}
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "value", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setValue(Ljava/lang/Double;)(aValue);
			}
		});
		Object.defineProperty(published, "minimum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getMinProgress()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setMinProgress(D)(aValue);
			}
		});
		Object.defineProperty(published, "maximum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getMaxProgress()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setMaxProgress(D)(aValue);
			}
		});
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setText(Ljava/lang/String;)('' + aValue);
			}
		});
	}-*/;
}
