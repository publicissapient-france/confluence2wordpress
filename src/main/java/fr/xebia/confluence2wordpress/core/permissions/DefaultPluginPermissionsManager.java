package fr.xebia.confluence2wordpress.core.permissions;

import java.util.List;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;


public class DefaultPluginPermissionsManager implements PluginPermissionsManager {

    private UserManager salUserManager;

    private PluginSettingsManager pluginSettingsManager;

    public DefaultPluginPermissionsManager(PluginSettingsManager pluginSettingsManager, UserManager salUserManager) {
        this.pluginSettingsManager = pluginSettingsManager;
        this.salUserManager = salUserManager;
    }

    @Override
    public boolean checkUsagePermission(User user, SpaceContentEntityObject page) {
        return checkUser(user) && checkPage(page);
    }

    @Override
    public boolean checkConfigurationPermission(User user) {
        if(user == null){
            return false;
        }
        return salUserManager.isAdmin(user.getName());
    }

    private boolean checkUser(User user) {
        if(user == null){
            return false;
        }
        List<String> groupNames = pluginSettingsManager.getAllowedConfluenceGroupsAsList();
        if(groupNames == null || groupNames.isEmpty()){
            return true;
        }
        for (String groupName : groupNames) {
            if(salUserManager.isUserInGroup(user.getName(), groupName)){
                return true;
            }
        }
        return false;
    }

    private boolean checkPage(SpaceContentEntityObject page) {
        if(page == null){
            return false;
        }
        List<String> spaceKeys = pluginSettingsManager.getAllowedConfluenceSpaceKeysAsList();
        if(spaceKeys == null || spaceKeys.isEmpty()){
            return true;
        }
        for (String spaceKey : spaceKeys) {
            if(page.getSpaceKey().equals(spaceKey)){
                return true;
            }
        }
        return false;
    }


}
