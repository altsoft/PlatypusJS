/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

/**
 *
 * @author mg
 */
public class VSpinner extends JSpinner implements HasEmptyText, HasEditable {

    protected SpinnerDoubleModel model;

    public VSpinner() {
        super(new SpinnerDoubleModel(0.0d, 0.0d, 100.0d, 1.0d));
        model = (SpinnerDoubleModel) super.getModel();
        model.addValueChangeListener(valueChangedAlerter);
    }

    protected PropertyChangeListener valueChangedAlerter = (PropertyChangeEvent evt) -> {
        firePropertyChange(HasValue.VALUE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
    };

    public final void setModel(SpinnerDoubleModel aModel) {
        if (model != null) {
            model.removeValueChangeListener(valueChangedAlerter);
        }
        model = aModel;
        super.setModel(model);
        if (model != null) {
            model.addValueChangeListener(valueChangedAlerter);
        }
    }

    @Override
    public Double getValue() {
        return (Double) getModel().getValue();
    }

    public void setValue(Double aValue) {
        super.setValue(aValue);
    }

    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(HasValue.VALUE_PROP_NAME, listener);
    }

    @Override
    public void requestFocus() {
        if (getEditor() instanceof NumberEditor) {
            JFormattedTextField ftf = ((NumberEditor) getEditor()).getTextField();
            ftf.requestFocus();
        }
    }

    @Override
    public boolean requestFocus(boolean temporary) {
        if (getEditor() instanceof NumberEditor) {
            JFormattedTextField ftf = ((NumberEditor) getEditor()).getTextField();
            return ftf.requestFocus(temporary);
        }
        return super.requestFocus(temporary);
    }

    @Override
    public void commitEdit() throws ParseException {
        if (getModel().getValue() != null) {
            super.commitEdit();
        }
    }

    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
        JComponent editor = getEditor();
        editor.setOpaque(aValue);
        if (editor instanceof NumberEditor) {
            ((NumberEditor) editor).getTextField().setOpaque(aValue);
        }
    }

    protected String emptyText;

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
    }

    @Override
    public boolean getEditable() {
        JComponent editor = getEditor();
        return ((NumberEditor) editor).getTextField().isEditable();
    }

    @Override
    public void setEditable(boolean aValue) {
        JComponent editor = getEditor();
        ((NumberEditor) editor).getTextField().setEditable(aValue);
    }

    public Double getMin() {
        return model.getMin();
    }

    public void setMin(Double aValue) {
        model.setMin(aValue);
    }

    public Double getMax() {
        return model.getMax();
    }

    public void setMax(Double aValue) {
        model.setMax(aValue);
    }

    public double getStep() {
        return model.getStep();
    }

    public void setStep(double aValue) {
        model.setStep(aValue);
    }

    public String getText() {
        JComponent editor = getEditor();
        return ((NumberEditor) editor).getTextField().getText();
    }

    public void setText(String aValue) throws Exception {
        JComponent editor = getEditor();
        ((NumberEditor) editor).getTextField().setText(aValue != null ? aValue : "");
        ((NumberEditor) editor).getTextField().commitEdit();
    }
}