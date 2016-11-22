package com.michielroos.bitbucket.plugin.keyboardshortcuts.ao;

/**
 * Copyright notice
 *
 * ⓒ 2016 ℳichiel ℛoos <michiel@michielroos.com>
 * All rights reserved
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ShortcutOverride extends Entity {
    String getUserKey();

    void setUserKey(String userKey);

    String getShortcut();

    void setShortcut(String shortcut);

    String getContext();

    void setContext(String context);

    String getDescription();

    void setDescription(String description);

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
