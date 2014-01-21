/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * QuerySettingsVisualPanel.java
 *
 * Created on 25.03.2011, 11:54:03
 */
package com.eas.designer.application.query.templates;

import com.eas.designer.application.utils.DatabaseConnectionComboBoxModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class QuerySettingsVisualPanel extends javax.swing.JPanel {

    protected NewQueryWizardSettingsPanel panel;
    protected String datasourceName;
    protected DefaultComboBoxModel<String> schemasModel;

    /** Creates new form QuerySettingsVisualPanel */
    public QuerySettingsVisualPanel(NewQueryWizardSettingsPanel aWizardStep) {
        initComponents();
        panel = aWizardStep;
        txtConnection.setModel(new DatabaseConnectionComboBoxModel());
        txtConnection.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConnection conn = (DatabaseConnection)txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
            }
        
        });
        txtConnection.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent e) {
                DatabaseConnection conn = (DatabaseConnection)txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
            }
        });
    }

    public void refreshControls() throws Exception {
        DatabaseConnection conn = ConnectionManager.getDefault().getConnection(datasourceName);
        txtConnection.setSelectedItem(conn);
    }

    public boolean valid(WizardDescriptor wd) throws Exception {
        String lconnectionName = (String) wd.getProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME);
        if (lconnectionName != null && !panel.connectionExist(lconnectionName)) {
            wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(QuerySettingsVisualPanel.class, "nonConnectionFile"));
            return false;
        }
        wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, NbBundle.getMessage(QuerySettingsVisualPanel.class, "readyForNextStep"));
        return true;
    }

    void store(WizardDescriptor wd) throws Exception {
        wd.putProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME, datasourceName);
    }

    void read(WizardDescriptor wd) throws Exception {
        datasourceName = (String) wd.getProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME);
        refreshControls();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConnection = new javax.swing.JLabel();
        btnApplicationConnection = new javax.swing.JToggleButton();
        txtConnection = new javax.swing.JComboBox();

        lblConnection.setText(org.openide.util.NbBundle.getMessage(QuerySettingsVisualPanel.class, "QuerySettingsVisualPanel.lblConnection.text")); // NOI18N

        btnApplicationConnection.setText(org.openide.util.NbBundle.getMessage(QuerySettingsVisualPanel.class, "QuerySettingsVisualPanel.btnApplicationConnection.text")); // NOI18N
        btnApplicationConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplicationConnectionActionPerformed(evt);
            }
        });

        txtConnection.setEditable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConnection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtConnection, 0, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnApplicationConnection)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(btnApplicationConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(266, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplicationConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplicationConnectionActionPerformed
        try {
            datasourceName = null;            
            refreshControls();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnApplicationConnectionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnApplicationConnection;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JComboBox txtConnection;
    // End of variables declaration//GEN-END:variables

}
