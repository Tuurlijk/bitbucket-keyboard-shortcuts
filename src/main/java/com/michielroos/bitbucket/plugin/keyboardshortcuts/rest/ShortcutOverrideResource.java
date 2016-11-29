package com.michielroos.bitbucket.plugin.keyboardshortcuts.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

/**
 * Copyright notice
 *
 * ⓒ 2016 ℳichiel ℛoos <michiel@michielroos.com>
 * All rights reserved
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.michielroos.bitbucket.plugin.keyboardshortcuts.ao.ShortcutOverride;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/")
public class ShortcutOverrideResource {
    private final ActiveObjects ao;
    private final UserManager userManager;

    public ShortcutOverrideResource(ActiveObjects ao, UserManager userManager) {
        this.ao = checkNotNull(ao);
        this.userManager = checkNotNull(userManager);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        final UserKey userKey = userManager.getRemoteUserKey();
        if (userKey == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        ShortcutOverride[] overrides = ao.find(ShortcutOverride.class, Query.select().where("USER_KEY = ?", userKey.getStringValue()));

        List<String> jsonLines = new ArrayList<String>();
        String json = "";

        for (ShortcutOverride override : overrides) {
            String line = "{"
                    + "\"context\": \"" + override.getContext() + "\","
                    + "\"enabled\": \"" + override.isEnabled() + "\","
                    + "\"propertyKey\": \"" + override.getPropertyKey() + "\","
                    + "\"shortcut\": \"" + override.getShortcut() + "\""
                    + "}";
            jsonLines.add(line);
        }
        json = StringUtils.join(jsonLines, ',');

        return Response.ok("{\"status\": \"ok\", \"overrides\": [" + json + "]}").build();
    }
}
