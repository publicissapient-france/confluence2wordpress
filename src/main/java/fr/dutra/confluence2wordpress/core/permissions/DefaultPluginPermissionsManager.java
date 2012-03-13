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
package fr.dutra.confluence2wordpress.core.permissions;

import java.util.List;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

import fr.dutra.confluence2wordpress.core.settings.PluginSettingsManager;


public class DefaultPluginPermissionsManager implements PluginPermissionsManager {

    private UserManager salUserManager;

    private PluginSettingsManager pluginSettingsManager;

    public DefaultPluginPermissionsManager(PluginSettingsManager pluginSettingsManager, UserManager salUserManager) {
        this.pluginSettingsManager = pluginSettingsManager;
        this.salUserManager = salUserManager;
    }

    @Override
    public boolean checkUsagePermission(User user, ContentEntityObject page) {
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

    private boolean checkPage(ContentEntityObject page) {
        if(page == null){
            return false;
        }
        String pageSpaceKey;
        if(page instanceof Draft){
            pageSpaceKey = ((Draft)page).getDraftSpaceKey();
        } else {
            pageSpaceKey = ((SpaceContentEntityObject) page).getSpaceKey();
        }
        List<String> spaceKeys = pluginSettingsManager.getAllowedConfluenceSpaceKeysAsList();
        if(spaceKeys == null || spaceKeys.isEmpty()){
            return true;
        }
        for (String spaceKey : spaceKeys) {
            if(pageSpaceKey.equals(spaceKey)){
                return true;
            }
        }
        return false;
    }


}
