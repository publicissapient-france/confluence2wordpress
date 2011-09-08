package fr.xebia.confluence2wordpress.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;


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
