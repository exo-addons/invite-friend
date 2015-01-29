package org.juzu.exoinvitefriend.portlet.commons.services;

import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

/**
 * Created by exoplatform on 27/01/15.
 */
public interface IService {

  public void sendInvitation(String inviter, String invitee);
  public Invitation storeInvitation(Invitation invitation);

}

