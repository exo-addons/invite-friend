package org.juzu.exoinvitefriend.portlet.commons.services;

import org.json.JSONObject;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

import java.util.Map;

/**
 * Created by exoplatform on 27/01/15.
 */
public interface IService {

  public JSONObject sendInvitation(String inviter, String invitee, String invitationUrl);
  public Invitation storeInvitation(Invitation invitation);

}

