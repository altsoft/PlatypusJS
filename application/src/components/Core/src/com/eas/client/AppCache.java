/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.metadata.ApplicationElement;

/**
 *
 * @author mg
 */
public interface AppCache {

    public String getApplicationPath();
    
    public String translateScriptPath(String aName) throws Exception;
    
    public boolean isActual(String aName, long aTxtContentLength, long aTxtCrc32) throws Exception;
    
    /**
     * Tests if an arbitrary application query object (only query) is in the cache.
     * @param aName Query application object id from meta database
     * @return True if query is in the cache, false otherwise
     * @throws Exception
     */
    public boolean containsKey(String aName) throws Exception;

    public ApplicationElement get(String aName) throws Exception;

    public void put(String aId, ApplicationElement aAppElement) throws Exception;

    public void addListener(CacheListener<String> aListener) throws Exception;

    public void removeListener(CacheListener<String> aListener) throws Exception;

    /**
     * Removes an application element from the cache.
     * @param aName Identifier of the removed element.
     * @throws Exception
     */
    public void remove(String aName) throws Exception;

    /**
     * Clears all cached data. I.e. tables metadata, application elements and information about indexes.
     * @throws java.lang.Exception
     */
    public void clear() throws Exception;
    
}
