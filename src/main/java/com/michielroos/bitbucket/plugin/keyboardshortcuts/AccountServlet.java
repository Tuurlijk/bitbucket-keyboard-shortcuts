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
import java.util.HashMap;
import java.util.Map;

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
        final PrintWriter w = response.getWriter();

        w.write("<ol>");

        if (userKey == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            public Void doInTransaction() {
                for (ShortcutOverride shortcutOverride : ao.find(ShortcutOverride.class, Query.select().where("USER_KEY = ?", userKey.getStringValue()))) // (2)
                {
                    System.out.printf("<li><%2$s> %s </%2$s></li>", shortcutOverride.getUserKey(), shortcutOverride.isEnabled() ? "strike" : "strong");
                    w.printf("<li><%2$s> %s </%2$s></li>", shortcutOverride.getUserKey(), shortcutOverride.isEnabled() ? "strike" : "strong");
                }
                return null;
            }
        });
        System.out.printf("strong");
        System.out.println("lalala");
        w.write("</ol>");


        Map<String, Object> velocityParams = new HashMap<String,Object>();

        velocityParams.put("user", userManager.getRemoteUser());

        UserProfile user = userManager.getRemoteUser();

//        user.getFullName()

        response.setContentType("text/html;charset=utf-8");
//        renderer.render("templates/accountSettings.vm", ImmutableMap.<String, Object>of("user", user)), response.getWriter());
        renderer.render("templates/accountSettings.vm", velocityParams, response.getWriter());
//        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String description = request.getParameter("pullRequestA");
        final UserKey userKey = userManager.getRemoteUserKey();

        System.out.printf("description: %1$s", description);

        ao.executeInTransaction(new TransactionCallback<ShortcutOverride>() // (1)
        {
            public ShortcutOverride doInTransaction() {
                final ShortcutOverride shortcutOverride = ao.create(ShortcutOverride.class); // (2)
                shortcutOverride.setUserKey(userKey.getStringValue()); // (3)
                shortcutOverride.setDescription(description); // (3)
                shortcutOverride.save(); // (4)
                return shortcutOverride;
            }
        });
        response.sendRedirect("admin");
    }
}
