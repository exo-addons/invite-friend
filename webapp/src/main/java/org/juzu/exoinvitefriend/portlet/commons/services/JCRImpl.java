package org.juzu.exoinvitefriend.portlet.commons.services;

import com.google.caja.util.Json;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.manager.IdentityManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by exoplatform on 27/01/15.
 */
public class JCRImpl implements IService {

  private static final Log log = ExoLogger.getLogger(JCRImpl.class);
  private static final String NODE_PROP_EXO_INVITE_FRIEND = "eXoInviteFriendApplication";
  private static final String NODE_PROP_INVITEES = "exo:invitee_emails";
  private static final String NODE_PROP_INVITER_USERNAME = "exo:inviter_username";

  private static final String EMAIL_ERROR_FORMAT = "email_error_format";
  private static final String EMAIL_ERROR_EXISTS = "email_error_exists";
  private static final String EMAIL_ERROR_EXO_CHECK = "email_error_exo_check";
  private static final String EMAIL_ERROR_MKTO_CHECK = "email_error_mkto_check";
  private static final String Email_VALID_OK = "email_valid_ok";
  private static final String INVITER_ERROR_NOT_EXISTS = "inviter_error_not_exists";

  private EmailService emailService;

  @Inject
  SessionProviderService sessionProviderService;
  @Inject
  NodeHierarchyCreator nodeHierarchyCreator;

  @Inject
  IdentityManager identityManager;
  @Inject
  MailService mailService;
  @Inject
  OrganizationService organizationService;

  @PostConstruct
  public void init(){
    emailService = new EmailService(identityManager,mailService);
  }
  private Node getOrCreateAppHome() throws Exception {
    Node appHome = null;
    SessionProvider sProvider = sessionProviderService.getSystemSessionProvider(null);
    Node publicApp = nodeHierarchyCreator.getPublicApplicationNode(sProvider);
    try {
      if(publicApp.hasNode(NODE_PROP_EXO_INVITE_FRIEND))
        appHome = publicApp.getNode(NODE_PROP_EXO_INVITE_FRIEND);
      else{
        appHome = publicApp.addNode(NODE_PROP_EXO_INVITE_FRIEND, "nt:unstructured");
        publicApp.getSession().save();
      }
    } catch (Exception e) {
      log.error("ERR eXo Invite Friend cannot get home folder");
    }
    return appHome;
  }
  private void setNodeProperties(Node aNode,Invitation invitation) throws RepositoryException {
    Set<String> invitee_emails = invitation.getInvitee_emails();
    aNode.setProperty(NODE_PROP_INVITEES,invitee_emails.toArray(new String[invitee_emails.size()]));
  }
  private Invitation transferNode2Account(Node node) throws RepositoryException {
    if (null == node)
      return null;
    Invitation invitation = new Invitation();
    invitation.setInviter(node.getName());
    PropertyIterator iter = node.getProperties("exo:*");
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(NODE_PROP_INVITEES)){
        Set<String> invitee_emails = new HashSet<String>();
        for (Value email:p.getValues()){
          invitee_emails.add(email.getString());
        }
        invitation.setInvitee_emails(invitee_emails);
      }
    }
    if (invitation.validate())
      return invitation;
    else
      return null;
  }

  @Override
  public Invitation storeInvitation(Invitation invitation) {
    if (!invitation.validate())
      return null;
    Node homeApp = null;
    try {
      homeApp = this.getOrCreateAppHome();
      Node invitationNode = null;
      if (null != homeApp){
        if (homeApp.hasNode(invitation.getInviter())){
          invitationNode = homeApp.getNode(invitation.getInviter());
          Invitation existingInvitation = this.transferNode2Account(invitationNode);
          Set<String> invitee_emails = existingInvitation.getInvitee_emails();
          Set<String> new_invitee_emails = invitation.getInvitee_emails();
          for (String email:invitee_emails){
            if (!new_invitee_emails.contains(email)){
              new_invitee_emails.add(email);
            }
          }
          invitation.setInvitee_emails(new_invitee_emails);
        }else{
          invitationNode = homeApp.addNode(invitation.getInviter(), "nt:unstructured");
        }
        if (null != invitationNode){
          log.info("exo invite friend=> store data successfully");
          this.setNodeProperties(invitationNode,invitation);
          homeApp.save();
          return invitation;
        }else{
          log.error("ERR exo invite friend: cannot store invitation");
        }
      }
    } catch (Exception e) {
      log.error("ERR exo invite friend: cannot store invitation");
      e.getMessage();
    }
    return null;
  }
  private Map<String, String> processCheckEXOEmail(String email){
    Map<String, String> result = new HashMap<String, String>();
    result.put("result",Email_VALID_OK);
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Pattern pattern;
    Matcher matcher;
    pattern = Pattern.compile(EMAIL_PATTERN);
    matcher = pattern.matcher(email);
    if( email == null || "".equals(email) || !matcher.matches()) {
      result.put("result", EMAIL_ERROR_FORMAT);
      result.put("msg","Please enter a valid email address.");
    }else{
      Query query = new Query();
      query.setEmail(email);
      ListAccess<User> listUser;
      try {
        listUser = organizationService.getUserHandler().findUsersByQuery(query);
        if(listUser != null && listUser.getSize() != 0){
          result.put("result", EMAIL_ERROR_EXISTS);
          result.put("msg","This email is already created");
        }
      } catch (Exception e) {
        result.put("result", EMAIL_ERROR_EXO_CHECK);
        result.put("msg","The service is not available. Please try again later");
        log.error("======================ERROR check email for invitation =======================",e);
      }
    }
    return result;
  }
  @Override
  public JSONObject sendInvitation(String inviter, String invitee, String invitationUrl) {
    JSONObject result = new JSONObject();
    Map<String, String> emailFormat = this.processCheckEXOEmail(invitee);
    String emailResult = emailFormat.get("result");
    String emailMsg = emailFormat.get("msg");
    if(Email_VALID_OK.equals(emailResult)){
      if (null == invitationUrl || "".equals(invitationUrl)){
        invitationUrl = System.getProperty("exo.base.url");
      }
/*      String senderEmailEncoded = emailInvitee+"-"+currentIdentity.getProfile().getEmail();
      senderEmailEncoded = Base64.encode(senderEmailEncoded) ;
      String strUrl = uriInfo.getRequestUri().getScheme()+"://"+uriInfo.getRequestUri().getHost()+":"+uriInfo.getRequestUri().getPort()+"/portal/intranet/register/";
      URI location = URI.create(strUrl+"?invitedemail="+emailInvitee+"&referreremail="+senderEmailEncoded);
      result.put("url",location.toURL().toString());*/
      emailMsg = "sent successfully";
      this.emailService.sendInvitation(inviter,invitee,invitationUrl);
    }
    try {
      result.put("result",emailResult);
      result.put("msg",emailMsg);
    } catch (JSONException e) {
      log.error("ERR exo-invite-friend error: generate json object");
      result = null;
    }
    return result;

  }

}
