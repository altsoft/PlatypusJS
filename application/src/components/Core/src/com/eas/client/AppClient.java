/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.login.PrincipalHost;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.Request;
import java.util.List;
import javax.security.auth.login.LoginException;

/**
 * Interface intended to extend Client interface with application servers's
 * features access. Features like server modules (session beans), using
 * connection, etc.
 *
 * @author pk, mg
 * @see Client
 */
public interface AppClient extends Client, PrincipalHost {

    public String getUrl();
    
    /**
     * Returns PlatypusQuery instance, containing fields and parameters
     * description. It returned without sql text and main table.
     *
     * @return PlatypusQuery instance.
     */
    @Override
    public PlatypusQuery getAppQuery(String aQueryId) throws Exception;

    /**
     * Logs in to application server.
     *
     * <p>The returned value indicates the session id which is designated to
     * this session from now on.</p>
     *
     * <p>On unsuccessful login it throws
     * <code>FailedLoginException</code>.</p>
     *
     * @param aUserName the user name to log in with.
     * @param aPassword the password to log in with.
     * @return the session id.
     * @throws javax.security.auth.login.LoginException
     */
    public String login(String aUserName, char[] aPassword) throws LoginException;

    /**
     * Logs out current session from the appliction server.
     *
     * @throws Exception
     */
    public void logout() throws Exception;

    public List<Change> getChangeLog();

    /**
     * execute request
     *
     * @param aRequest request
     * @throws java.lang.Exception
     */
    public void executeRequest(Request aRequest) throws Exception;

    /**
     * Creates and returns new data flow provider, setted up, according to
     * information about query.
     *
     * @param aEntityId - Entity application element identifier.
     * @param aExpectedFields
     * @return New data flow provider instance.
     * @throws java.lang.Exception
     */
    public FlowProvider createFlowProvider(String aEntityId, Fields aExpectedFields) throws Exception;

    /**
     * Commits all previous calls to executeUpdate and enqueueRowsetUpdate
     * methods.
     *
     * @throws Exception
     * @return Affected in this transaction rows count from commited calls to
     * enqueueUpdate, not to enqueueRowsetUpdate. Number of affected rows by
     * rowset's changes you can take from the rowset directly.
     * @see #enqueueRowsetUpdate(com.bearsoft.rowset.Rowset)
     */
    public int commit() throws Exception;

    /**
     * Requests the application server to create session bean (server module).
     *
     * @param moduleName Name of created module. It's considered, that module id
     * is resolved through some registry.
     * @return The created module name or null if creation has failed. The
     * returned module name is used in further work to call server module's
     * methods.
     * @throws Exception
     */
    public String createServerModule(String moduleName) throws Exception;

    /**
     * Requests the application server to destroy session bean.
     *
     * @param moduleName Modeule name of the session bean being destroyed.
     * @throws Exception
     */
    public void disposeServerModule(String moduleName) throws Exception;

    /**
     * Requests the application server to call session bean method.
     *
     * @param moduleId String identifier (name) of the session bean been called.
     * @param methodName Name of the method to call.
     * @param arguments Argument array to be passed to session bean's method
     * when calling it.
     * @return Marshalled result of the ben's method call.
     * @throws Exception
     */
    public Object executeServerModuleMethod(String moduleId, String methodName, Object... arguments) throws Exception;

    /**
     * Метод предназначен для исполнения DML запросов (insert, uddate, delete и
     * хранимых процедур).
     *
     * @param entityId Идентификатор сущности в базе данных приложения.
     * @param params Параметры передаваемые в запрос для исполнения.
     * @throws Exception
     */
    public void enqueueUpdate(String entityId, Parameters params) throws Exception;

}
