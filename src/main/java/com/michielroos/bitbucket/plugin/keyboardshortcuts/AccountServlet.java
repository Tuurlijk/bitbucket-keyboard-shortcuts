package com.michielroos.bitbucket.plugin.keyboardshortcuts;

/**
 * Copyright notice
 *
 * ⓒ 2016 ℳichiel ℛoos <michiel@michielroos.com>
 * All rights reserved
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.user.Person;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.michielroos.bitbucket.plugin.keyboardshortcuts.ao.ShortcutOverride;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import net.java.ao.Query;

import java.io.PrintWriter;
import java.util.*;

import static com.google.common.base.Preconditions.*;

public class AccountServlet extends HttpServlet {
    private final ActiveObjects ao;
    private final UserManager userManager;
    private final TemplateRenderer renderer;

    public AccountServlet(ActiveObjects ao, TemplateRenderer renderer, UserManager userManager) {
        this.ao = checkNotNull(ao);
        this.renderer = checkNotNull(renderer);
        this.userManager = checkNotNull(userManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserKey userKey = userManager.getRemoteUserKey();
        final Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("user", userManager.getRemoteUser());

        if (userKey == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ao.executeInTransaction(new TransactionCallback<Void>() {
            public Void doInTransaction() {
                ShortcutOverride[] overrides = ao.find(ShortcutOverride.class, Query.select().where("USER_KEY = ?", userKey.getStringValue()));
                velocityParams.put("overrides", overrides);
                return null;
            }
        });

        response.setContentType("text/html;charset=utf-8");
        renderer.render("templates/accountSettings.vm", velocityParams, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final Map parameters = request.getParameterMap();
        final UserKey userKey = userManager.getRemoteUserKey();

        ao.delete(ao.find(ShortcutOverride.class, Query.select().where("USER_KEY = ?", userKey.getStringValue())));

        Map parms = request.getParameterMap();
        for (Iterator iterator = parms.entrySet().iterator(); iterator.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) iterator.next();
            final String[] parameterParts = entry.getKey().toString().split("_");

            if (request.getParameter((String) entry.getKey()).equals("on")) {
                ao.executeInTransaction(new TransactionCallback<ShortcutOverride>() {
                    public ShortcutOverride doInTransaction() {
                        final ShortcutOverride shortcutOverride = ao.create(ShortcutOverride.class);
                        shortcutOverride.setUserKey(userKey.getStringValue());
                        shortcutOverride.setContext(parameterParts[0]);
                        shortcutOverride.setShortcut(parameterParts[1]);
                        shortcutOverride.setPropertyKey(parameterParts[0] + "_" + parameterParts[1]);
                        shortcutOverride.setEnabled(true);
                        shortcutOverride.save();
                        return shortcutOverride;
                    }
                });
            }
        }
        response.sendRedirect("admin");
    }
}
