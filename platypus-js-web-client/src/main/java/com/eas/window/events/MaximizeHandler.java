/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window.events;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author mg
 * @param <T> Type being maximized
 */
public interface MaximizeHandler<T> extends EventHandler {

    public void onMaximize(MaximizeEvent<T> anEvent);
}
