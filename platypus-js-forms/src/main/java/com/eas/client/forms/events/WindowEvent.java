/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WindowEvent extends Event<java.awt.event.WindowEvent> {

    protected WindowEvent(java.awt.event.WindowEvent aEvent) {
        super(aEvent);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }
}
