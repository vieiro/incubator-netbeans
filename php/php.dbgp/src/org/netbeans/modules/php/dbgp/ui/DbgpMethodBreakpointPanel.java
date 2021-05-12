/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.php.dbgp.ui;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.modules.php.dbgp.breakpoints.FunctionBreakpoint;
import org.netbeans.modules.php.dbgp.breakpoints.FunctionBreakpoint.Type;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author den
 */
public class DbgpMethodBreakpointPanel extends JPanel implements Controller {
    private static final long serialVersionUID = 4967178020173186468L;
    private static final String EMPTY_METHOD_NAME = "LBL_EmptyMethodName"; // NOI18N
    private static final String METHOD_RETURN = NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "LBL_MethodReturn"); // NOI18N
    private static final String METHOD_CALL = NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "LBL_MethodCall"); // NOI18N

    public DbgpMethodBreakpointPanel() {
        initComponents();
    }

    @Override
    public boolean cancel() {
        return true;
    }

    @Override
    public boolean ok() {
        String methodName = myMethodName.getText();
        if (methodName == null || methodName.trim().length() == 0) {
            String msg = NbBundle.getMessage(DbgpMethodBreakpointPanel.class, EMPTY_METHOD_NAME);
            NotifyDescriptor descr = new NotifyDescriptor.Message(msg);
            DialogDisplayer.getDefault().notify(descr);
            return false;
        }
        /*
         *  TODO : check methods name that debugger engine is accepted
         *  and set filtering here for this strings.
         *  Usual method names should be regegxp
         *  [a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*
         *  by PHP spec.
         *  But there can be also Class member method names. I don't know
         *  how debugger engine accept such methods ( if it distinguish
         *  class member methods from functions in file at all ).
         */
        MethodType type = (MethodType) METHOD_TYPES_MODEL.getSelectedItem();
        FunctionBreakpoint breakpoint = new FunctionBreakpoint(type.getType(), methodName);
        DebuggerManager.getDebuggerManager().addBreakpoint(breakpoint);
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        myMethodLbl = new javax.swing.JLabel();
        myStopOnLbl = new javax.swing.JLabel();
        myMethodName = new javax.swing.JTextField();
        myStopType = new javax.swing.JComboBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "LBL_Settings"))); // NOI18N

        myMethodLbl.setLabelFor(myMethodName);
        org.openide.awt.Mnemonics.setLocalizedText(myMethodLbl, org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "LBL_MethodName")); // NOI18N

        myStopOnLbl.setLabelFor(myStopType);
        org.openide.awt.Mnemonics.setLocalizedText(myStopOnLbl, org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "LBL_StopOn")); // NOI18N

        myMethodName.setText("");

        myStopType.setModel(METHOD_TYPES_MODEL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myMethodLbl)
                    .addComponent(myStopOnLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myStopType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myMethodName, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(myMethodLbl)
                    .addComponent(myMethodName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(myStopOnLbl)
                    .addComponent(myStopType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        myMethodLbl.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "DbgpMethodBreakpointPanel.myMethodLbl.AccessibleContext.accessibleName")); // NOI18N
        myMethodLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "DbgpMethodBreakpointPanel.myMethodLbl.AccessibleContext.accessibleDescription")); // NOI18N
        myStopOnLbl.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "DbgpMethodBreakpointPanel.myStopOnLbl.AccessibleContext.accessibleName")); // NOI18N
        myStopOnLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "DbgpMethodBreakpointPanel.myStopOnLbl.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(DbgpMethodBreakpointPanel.class, "A11_SettingsName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel myMethodLbl;
    private javax.swing.JTextField myMethodName;
    private javax.swing.JLabel myStopOnLbl;
    private javax.swing.JComboBox myStopType;
    // End of variables declaration//GEN-END:variables
    private static final ComboBoxModel METHOD_TYPES_MODEL = new DefaultComboBoxModel(MethodType.values());

    private enum MethodType {
        CALL(Type.CALL),
        RETURN(Type.RETURN);

        MethodType(Type type) {
            myType = type;
        }

        Type getType() {
            return myType;
        }

        @Override
        public String toString() {
            if (this == CALL) {
                return METHOD_CALL;
            } else if (this == RETURN) {
                return METHOD_RETURN;
            } else {
                assert false;
                return null;
            }
        }

        private final Type myType;

    }

}
