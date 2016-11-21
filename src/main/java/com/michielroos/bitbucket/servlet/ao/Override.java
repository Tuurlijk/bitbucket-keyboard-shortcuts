package com.michielroos.bitbucket.servlet.ao;

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
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

@Preload
public interface Override extends Entity {
    boolean isEnabled();
    String getContext();
    String getShortcut();

    void setEnabled(boolean enabled);
    void setContext(String context);
    void setShortcut(String shortcut);
}