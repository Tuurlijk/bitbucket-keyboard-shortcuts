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
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import static com.google.common.base.Preconditions.*;

public abstract class AbstractServlet extends HttpServlet {
    protected final SoyTemplateRenderer soyTemplateRenderer;
    protected final ActiveObjects ao;

    public AbstractServlet(SoyTemplateRenderer soyTemplateRenderer, ActiveObjects ao) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.ao = checkNotNull(ao);
    }

    protected void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(
                    resp.getWriter(),
                    "com.michielroos.bitbucket-keyboard-shortcuts:keyboard-settings-web-resource",
                    templateName,
                    data
            );
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }
}
