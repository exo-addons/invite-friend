package org.juzu.exoinvitefriend.portlet.frontend;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.request.SecurityContext;

import javax.inject.Inject;

/**
 * Created by exoplatform on 23/01/15.
 */
public class JuZFrontendApplication {

  @Inject
  @Path("index.gtmpl")
  org.juzu.exoinvitefriend.portlet.frontend.templates.index indexTpl;

  @View
  public Response index(SecurityContext securityContext){
    String currentUserName = securityContext.getUserPrincipal().getName();

    return Response.ok("i am "+currentUserName);
  }

}
