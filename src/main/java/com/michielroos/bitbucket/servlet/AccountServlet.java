package com.michielroos.bitbucket.servlet;

/**
 * Copyright notice
 *
 * ⓒ 2016 ℳichiel ℛoos <michiel@michielroos.com>
 * All rights reserved
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

import com.atlassian.bitbucket.user.Person;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.templaterenderer.TemplateRenderer;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AccountServlet extends HttpServlet {
    private final UserService userService;
    private final TemplateRenderer renderer;

    public AccountServlet(TemplateRenderer renderer, UserService userService) {
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

        response.setContentType("text/html;charset=utf-8");
        renderer.render("templates/admin.vm", response.getWriter());
    }
}
