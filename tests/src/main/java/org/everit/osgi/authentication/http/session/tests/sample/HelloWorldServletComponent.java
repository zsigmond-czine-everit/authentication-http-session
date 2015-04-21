/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.osgi.authentication.http.session.tests.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.AuthenticationContext;
import org.everit.osgi.authentication.http.session.AuthenticationSessionAttributeNames;

@Component(name = "HelloWorldServletComponent", metatype = true, configurationFactory = true,
    policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Properties({
    @Property(name = "authenticationSessionAttributeNames.target"),
    @Property(name = "authenticationContext.target")
})
@Service(value = Servlet.class)
public class HelloWorldServletComponent extends HttpServlet {

  private static final long serialVersionUID = -5545883781165913751L;

  @Reference(bind = "setAuthenticationContext")
  private AuthenticationContext authenticationContext;

  @Reference(bind = "setAuthenticationSessionAttributeNames")
  private AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException,
      IOException {
    long currentResourceId = authenticationContext.getCurrentResourceId();
    StringBuilder sb = null;
    if (currentResourceId == 1) {
      sb = new StringBuilder();
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for (StackTraceElement stackTraceElement : stackTrace) {
        sb.append("\tat ").append(stackTraceElement).append("\n");
      }
    }

    HttpSession httpSession = req.getSession();
    long newResourceId = new Random().nextLong();
    httpSession.setAttribute(authenticationSessionAttributeNames.authenticatedResourceId(),
        newResourceId);

    resp.setContentType("text/plain");
    PrintWriter out = resp.getWriter();
    out.print(currentResourceId + ":" + newResourceId);
    if (sb != null) {
      out.print(":\n === Server stackrace for analizing Filter chain and Servlet invocations ===\n"
          + sb.toString().replaceAll(":", "-->")
          + " === Server stacktrace END ===\n");
    }
  }

  public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
    this.authenticationContext = authenticationContext;
  }

  public void setAuthenticationSessionAttributeNames(
      final AuthenticationSessionAttributeNames authenticationSessionAttributeNames) {
    this.authenticationSessionAttributeNames = authenticationSessionAttributeNames;
  }

}
