package org.juzu.exoinvitefriend.portlet.commons.services;

import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

import javax.inject.Inject;
import javax.jcr.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by exoplatform on 27/01/15.
 */
public class JCRImpl implements IService {

  private static final Log log = ExoLogger.getLogger(JCRImpl.class);
  private static final String NODE_PROP_EXO_INVITE_FRIEND = "eXoInviteFriendApplication";
  private static final String NODE_PROP_INVITEES = "exo:invitee_emails";
  private static final String NODE_PROP_INVITER_USERNAME = "exo:inviter_username";

  @Inject
  SessionProviderService sessionProviderService;
  @Inject
  NodeHierarchyCreator nodeHierarchyCreator;

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
          invitationNode = homeApp.addNode(invitation.getInviter(),"nt:unstructured");
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
  @Override
  public void sendInvitation(Invitation invitation) {

  }

}
