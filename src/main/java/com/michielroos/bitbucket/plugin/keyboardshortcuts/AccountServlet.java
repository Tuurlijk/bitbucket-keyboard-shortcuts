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
import com.atlassian.templaterenderer.TemplateRenderer;
import com.michielroos.bitbucket.plugin.keyboardshortcuts.ao.ShortcutOverride;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import static com.google.common.base.Preconditions.*;

public class AccountServlet extends HttpServlet {
    private final ActiveObjects ao;
    private final UserService userService;
    private final TemplateRenderer renderer;

    public AccountServlet(ActiveObjects ao, TemplateRenderer renderer, UserService userService) {
        this.ao = checkNotNull(ao);
        this.renderer = renderer;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get userSlug from path
        String pathInfo = request.getPathInfo();

        String userSlug = pathInfo.substring(1); // Strip leading slash
        Person user = userService.getUserBySlug(userSlug);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            public Void doInTransaction() {
                for (ShortcutOverride shortcutOverride : ao.find(ShortcutOverride.class)) // (2)
                {
//                    w.printf("<li><%2$s> %s </%2$s></li>", shortcutOverride.getContext(), shortcutOverride.isEnabled() ? "strike" : "strong");
                }
                return null;
            }
        });

        response.setContentType("text/html;charset=utf-8");
//        renderer.render("templates/accountSettings.vm", ImmutableMap.<String, Object>of("user", user)), response.getWriter());
        renderer.render("templates/accountSettings.vm", response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {
        final String description = req.getParameter("task");
        ao.executeInTransaction(new TransactionCallback<ShortcutOverride>() // (1)
        {
            public ShortcutOverride doInTransaction() {
                final ShortcutOverride shortcutOverride = ao.create(ShortcutOverride.class); // (2)
                shortcutOverride.setDescription(description); // (3)
                shortcutOverride.save(); // (4)
                return shortcutOverride;
            }
        });
        response.sendRedirect("admin");
    }
}
