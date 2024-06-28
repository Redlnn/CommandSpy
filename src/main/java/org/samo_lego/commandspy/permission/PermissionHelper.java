package org.samo_lego.commandspy.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

/**
 * Permission checker.
 *
 * In its own class since we do not want to depend
 * on it fully, but use it just if luckperms mod is loaded.
 */
public class PermissionHelper {

    /**
     * Checks permission for player using Lucko's
     * permission API.
     */
    public static boolean checkPermission(CommandSource source, String permission) {
        return Permissions.check(source, permission, false);
    }

    /**
     * Checks permission for player using Lucko's
     * permission API.
     */
    public static boolean checkPermission(Entity entity, String permission) {
        return Permissions.check(entity, permission, false);
    }
}
