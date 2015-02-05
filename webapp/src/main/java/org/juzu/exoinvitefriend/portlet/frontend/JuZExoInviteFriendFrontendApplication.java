package org.juzu.exoinvitefriend.portlet.frontend;

import juzu.*;
import juzu.bridge.portlet.JuzuPortlet;
import juzu.plugin.ajax.Ajax;
import juzu.request.RequestContext;
import juzu.request.SecurityContext;
import org.exoplatform.portal.pom.config.tasks.PreferencesTask;
import org.json.JSONObject;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;
import org.juzu.exoinvitefriend.portlet.commons.services.IService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
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
  private final static String INVITATION_URL = "invitationUrl";
  @View
  public Response index(RequestContext context){
    if (PortletMode.EDIT.equals(context.getProperty(JuzuPortlet.PORTLET_MODE))){
      Boolean enableStoreData = Boolean.valueOf(portletPreferences.getValue(ENABLE_STORE_DATA,"true"));
      String invitationUrl = portletPreferences.getValue(INVITATION_URL,"");
      return editModeTpl.with().set(ENABLE_STORE_DATA,enableStoreData).set(INVITATION_URL,invitationUrl).ok();
    }
    return indexTpl.ok();
  }
  @Ajax
  @Resource
  public Response sendInvitation(SecurityContext securityContext, String email){
    String currentUserName = securityContext.getUserPrincipal().getName();
    String invitationUrl = portletPreferences.getValue(INVITATION_URL,"");
    JSONObject result = iService.sendInvitation(currentUserName, email,invitationUrl);
    if (null != result)
      return Response.ok(result.toString());
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response storeInvitation(SecurityContext securityContext,String email){
    Boolean enableStoreData = Boolean.valueOf(portletPreferences.getValue(ENABLE_STORE_DATA,"true"));
    if (enableStoreData){
      String currentUserName = securityContext.getUserPrincipal().getName();
      if(null != iService.storeInvitation(this.valuateInvitation(currentUserName,email)))
        return Response.ok("ok");
    }
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

  @Action
  public Response saveSettings(String enableStoreData,String invitationUrl) throws ReadOnlyException, IOException, ValidatorException {
    portletPreferences.setValue(ENABLE_STORE_DATA,enableStoreData);
    portletPreferences.setValue(INVITATION_URL,invitationUrl);
    portletPreferences.store();
    return JuZExoInviteFriendFrontendApplication_.index().with(JuzuPortlet.PORTLET_MODE, PortletMode.VIEW);
  }

}
