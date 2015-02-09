/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.SessionRequestHandler;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.server.PlatypusServer;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;

/**
 *
 * @author pk
 */
public class LogoutRequestHandler extends SessionRequestHandler<LogoutRequest, LogoutRequest.Response> {

    public LogoutRequestHandler(PlatypusServerCore aServer, LogoutRequest aRequest) {
        super(aServer, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<LogoutRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        getServerCore().getSessionManager().remove(aSession.getId());
        ((PlatypusServer)getServerCore()).getPrincipals().remove(aSession.getId());
        if (onSuccess != null) {
            onSuccess.accept(new LogoutRequest.Response());
        }
    }
}
