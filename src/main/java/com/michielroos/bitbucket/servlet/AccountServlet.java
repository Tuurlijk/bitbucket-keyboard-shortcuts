package com.michielroos.bitbucket.servlet;

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
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AccountServlet extends AbstractServlet {
    private final UserService userService;

    public AccountServlet(SoyTemplateRenderer soyTemplateRenderer, ActiveObjects ao, UserService userService) {
        super(soyTemplateRenderer, ao);
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get userSlug from path
        String pathInfo = req.getPathInfo();

        String userSlug = pathInfo.substring(1); // Strip leading slash
        Person user = userService.getUserBySlug(userSlug);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        render(resp, "plugin.example.account", ImmutableMap.<String, Object>of("user", user));
    }
}
