package org.juzu.exoinvitefriend.portlet.commons.services;

import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

/**
 * Created by exoplatform on 27/01/15.
 */
public interface IService {

  public void sendInvitation(Invitation invitation);
  public Invitation storeInvitation(Invitation invitation);

}

