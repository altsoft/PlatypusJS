/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersRowset;
import com.eas.client.model.script.RowsetHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.NativeJavaHostObject;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class ApplicationPlatypusParametersEntity extends ApplicationPlatypusEntity implements ApplicationParametersEntity {

    protected List<Change> changeLog = new ArrayList<>();// dummy change log. No entries expected here

    public ApplicationPlatypusParametersEntity(ApplicationPlatypusModel aModel) {
        super();
        model = aModel;
        entityId = ApplicationModel.PARAMETERS_ENTITY_ID;
        executed = true;
    }

    @Override
    public List<Change> getChangeLog() throws Exception {
        return changeLog;
    }

    @Override
    public boolean isRowsetPresent() {
        return true;
    }

    @Override
    public Rowset getRowset() throws Exception {
        execute();
        return super.getRowset();
    }

    @Override
    public Fields getFields() {
        return model.getParameters();
    }

    @Override
    public void validateQuery() throws Exception {
        execute();
    }

    @Override
    protected boolean isTagValid(String aTagName) {
        return true;
    }

    @Override
    public ApplicationPlatypusParametersEntity copy() throws Exception {
        ApplicationPlatypusParametersEntity copied = new ApplicationPlatypusParametersEntity(model);
        assign(copied);
        return copied;
    }

    @Override
    public void accept(ModelVisitor<ApplicationPlatypusEntity> visitor) {
        if(visitor instanceof ApplicationModelVisitor<?>)
        {
            ((ApplicationModelVisitor<?>)visitor).visit(this);
        }
    }


    @Override
    public String getQueryId() {
        return null;
    }

    @Override
    public PlatypusQuery getQuery() {
        return null;
    }

    @Override
    public String getTableDbId() {
        return null;
    }

    @Override
    public void setTableDbId(String tableDbId) {
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void setTableName(String aTableName) {
    }

    @Override
    public Long getEntityId() {
        return ApplicationModel.PARAMETERS_ENTITY_ID;
    }

    @Override
    public Scriptable defineProperties() throws Exception {
        if (model.getScriptThis() != null && model.getScriptThis() instanceof ScriptableObject) {
            ScriptableRowset<ApplicationPlatypusEntity> sRowset = new ScriptableRowset<>((ApplicationPlatypusEntity)this);
            NativeJavaHostObject modelPublished = model.getPublished();
            sRowsetWrap = new RowsetHostObject<>(sRowset, modelPublished);
            // deprecated
            /* global parameters names */
            Fields md = sRowset.getFields();
            sRowset.createScriptableFields();
            ScriptableObject moduleThis = (ScriptableObject) model.getScriptThis();
            if (md != null) {
                for (int i = 1; i <= md.getFieldsCount(); i++) {
                    Field field = md.get(i);
                    String fName = field.getName();
                    if (fName != null && !fName.isEmpty()) {
                        /* var m = new Module1(); m.param1 */
                        moduleThis.defineProperty(fName, sRowset.getScriptableField(fName), ScriptableRowset.getValueScriptableFieldMethod, ScriptableRowset.setValueScriptableFieldMethod, 0);
                    }
                }
            }
            /* predefined and user parameters names
            /* predefined */
            moduleThis.defineProperty(Model.PARAMETERS_SCRIPT_NAME, sRowsetWrap, ScriptableObject.READONLY);
            /* user */
            String dsName = getName();
            if (dsName != null && !dsName.isEmpty()) {
                moduleThis.defineProperty(dsName, sRowsetWrap, ScriptableObject.READONLY);
            }
            //
            modelPublished.defineProperty(Model.PARAMETERS_SCRIPT_NAME, sRowsetWrap);
            return sRowsetWrap;
        }
        return null;
    }

    @Override
    public boolean execute() {
        if (rowset == null) {
            rowset = new ParametersRowset((Parameters) model.getParameters());
            rowset.addRowsetListener(this);
        }
        return rowset != null;
    }

    @Override
    protected boolean internalExecute(boolean refresh) {
        executing = false;
        executed = true;
        return true;
    }

    @Override
    protected void refreshRowset() throws Exception {
        // no op
    }

}
