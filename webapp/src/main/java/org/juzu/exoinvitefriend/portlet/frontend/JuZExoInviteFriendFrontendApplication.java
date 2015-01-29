package org.juzu.exoinvitefriend.portlet.frontend;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.bridge.portlet.JuzuPortlet;
import juzu.plugin.ajax.Ajax;
import juzu.request.RequestContext;
import juzu.request.SecurityContext;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;
import org.juzu.exoinvitefriend.portlet.commons.services.IService;

import javax.inject.Inject;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by exoplatform on 23/01/15.
 */
public class JuZExoInviteFriendFrontendApplication {

  @Inject
  PortletPreferences portletPreferences;
  @Inject
  IService iService;
  @Inject
  @Path("index.gtmpl")
  org.juzu.exoinvitefriend.portlet.frontend.templates.index indexTpl;
  @Inject
  @Path("editMode.gtmpl")
  org.juzu.exoinvitefriend.portlet.frontend.templates.editMode editModeTpl;
  private final static String ENABLE_STORE_DATA = "enableStoreData";
  @View
  public Response index(RequestContext context){
    Boolean enableStoreData = Boolean.valueOf(portletPreferences.getValue(ENABLE_STORE_DATA,"true"));
    if (PortletMode.EDIT.equals(context.getProperty(JuzuPortlet.PORTLET_MODE))){
      return editModeTpl.with().set(ENABLE_STORE_DATA,enableStoreData).ok();
    }
    return indexTpl.ok();
  }
  @Ajax
  @Resource
  public Response sendInvitation(SecurityContext securityContext, String email){
    String currentUserName = securityContext.getUserPrincipal().getName();
    iService.sendInvitation(currentUserName,email);
    return Response.ok("ok");
  }
  @Ajax
  @Resource
  public Response storeInvitation(SecurityContext securityContext,String email){
    String currentUserName = securityContext.getUserPrincipal().getName();
    if(null != iService.storeInvitation(this.valuateInvitation(currentUserName,email)))
      return Response.ok("ok");
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response saveEditMode(String enableStoreData){
    try {
      portletPreferences.setValue(ENABLE_STORE_DATA,enableStoreData);
      portletPreferences.store();
      return Response.ok("ok");
    } catch (ReadOnlyException e) {
      e.printStackTrace();
    } catch (ValidatorException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Response.ok("nok");
  }
  private Invitation valuateInvitation(String inviter, String invitee){
    Invitation invitation = new Invitation();
    invitation.setInviter(inviter);
    Set<String> invitee_emails = new HashSet<String>();
    invitee_emails.add(invitee);
    invitation.setInvitee_emails(invitee_emails);
    return invitation;
  }
}
