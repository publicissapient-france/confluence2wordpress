/**
 * Copyright 2011 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package fr.dutra.confluence2wordpress.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

import fr.dutra.confluence2wordpress.core.permissions.PluginPermissionsManager;


public class PluginPermissionsCondition extends BaseConfluenceCondition {

    private PluginPermissionsManager pluginPermissionsManager;

    public PluginPermissionsCondition(PluginPermissionsManager pluginPermissionsManager) {
        this.pluginPermissionsManager = pluginPermissionsManager;
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return pluginPermissionsManager.checkUsagePermission(context.getUser(), context.getPage());
    }

}
