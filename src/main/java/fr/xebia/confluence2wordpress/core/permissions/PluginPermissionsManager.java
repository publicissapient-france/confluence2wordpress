package fr.xebia.confluence2wordpress.core.permissions;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.user.User;

public interface PluginPermissionsManager {

    boolean checkUsagePermission(User user, SpaceContentEntityObject page);

    boolean checkConfigurationPermission(User user);

}