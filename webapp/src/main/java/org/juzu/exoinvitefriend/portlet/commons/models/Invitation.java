package org.juzu.exoinvitefriend.portlet.commons.models;

import java.util.Set;

/**
 * Created by exoplatform on 27/01/15.
 */
public class Invitation {

  private String inviter;
  private Set<String> invitee_emails;
  public Invitation(){}

  public String getInviter() {
    return inviter;
  }

  public void setInviter(String inviter) {
    this.inviter = inviter;
  }

  public Set<String> getInvitee_emails() {
    return invitee_emails;
  }

  public void setInvitee_emails(Set<String> invitee_emails) {
    this.invitee_emails = invitee_emails;
  }
  public Boolean validate(){
    if (null == this.getInviter() || "".equals(this.getInviter()))
      return false;
    if (null == this.getInvitee_emails() || this.getInvitee_emails().size() == 0)
      return false;
    return true;
  }
}