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

package org.netbeans.modules.autoupdate.services;

import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.modules.autoupdate.updateprovider.ModuleItem;

/**
 *
 * @author Jiri Rechtacek
 */
public class KitModuleUpdateElementImpl extends ModuleUpdateElementImpl {
    
    public KitModuleUpdateElementImpl (ModuleItem item, String providerName) {
        super (item, providerName);
    }
    
    @Override
    public UpdateManager.TYPE getType () {
        if (getUpdateUnit () == null) {
            return UpdateManager.TYPE.KIT_MODULE;
        }
        return getUpdateUnit ().getType ();
    }
    
}
