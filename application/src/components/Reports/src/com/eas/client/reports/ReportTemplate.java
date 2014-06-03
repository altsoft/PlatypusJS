/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.report.Report;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.model.application.ApplicationModel;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 * TODO Create factory for ReportTemplate descendants as new formats will be added.  
 * @author mg
 */
public class ReportTemplate implements HasPublished{

    protected byte[] template;
    protected ApplicationModel<?, ?, ?, ?> model;
    protected JSObject scriptData;
    protected String format;
    protected String name;
    private static JSObject publisher;
    protected Object published;
    
    public ReportTemplate(byte[] aTemplate, ApplicationModel<?, ?, ?, ?> aModel, String aFormat, String aName) {
        super();
        template = aTemplate;
        model = aModel;
        format = aFormat;
        name = aName;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    private static final String GENERATEREPORT_JSDOC = ""
            + "/**\n"
            + " * Generate report from template.\n"
            + " */";

    @ScriptFunction(jsDoc = GENERATEREPORT_JSDOC)
    public Report generateReport() throws Exception {
        if (template != null) {
            ExelTemplate reportTemplate = new ExelTemplate(model, scriptData, format);
            reportTemplate.setTemplate(new CompactBlob(template));
            byte[] data = reportTemplate.create();
            return new Report(data, format, name);
        }
        return null;
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
    
}