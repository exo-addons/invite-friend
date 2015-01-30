package org.juzu.exoinvitefriend.portlet.commons.services;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.juzu.exoinvitefriend.portlet.commons.models.Invitation;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by exoplatform on 27/01/15.
 */
public class EmailService {

  private static final Log log = ExoLogger.getLogger(EmailService.class);
  String remoteUrl = "";
  MailService exoMailService;
  IdentityManager identityManager;

  final String email_invitation_template="/html/email_invitation_template.html";
  private String DNS;
  public  EmailService(IdentityManager identityManager,MailService mailService){

    this.identityManager = identityManager;
    this.exoMailService = mailService;
    this.remoteUrl = System.getProperty("EXO_DEPLOYMENT_URL");
    if(null == remoteUrl || "".equals(remoteUrl)){
      remoteUrl = "http://community.exoplatform.com";
    }
    this.setDNS(System.getProperty("exo.base.url"));
  }
  private String generateSubject(Identity identity){
    return identity.getProfile().getFullName()+" has invited you to join "+this.getDNS();
  }
  private String generateBody(Identity identity){

    if (null != identity){
      Map<String, String> props = new HashMap<String, String>();
      props.put("DNS",this.getDNS());
      props.put("DNS_URL",this.remoteUrl);
      props.put("inviter_fullname",identity.getProfile().getFullName());
      return this.getBodyByTemplate(email_invitation_template,props);
    }
    return null;
  }
  private String getBodyByTemplate(String fileTemplate, Map<String, String> templateProperties) {
    InputStream is = this.getClass().getResourceAsStream(fileTemplate);
    String body = null;
    try {
      body = this.resolveTemplate(is, templateProperties);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    return body;
  }

  private String resolveTemplate(InputStream is, Map<String, String> properties) throws FileNotFoundException {
    Scanner scanner = new Scanner(is);
    StringBuilder sb = new StringBuilder();
    try {
      while (scanner.hasNextLine()) {
        sb.append(scanner.nextLine()).append(System.getProperty("line.separator"));
      }
    } finally {
      scanner.close();
    }
    String templateContent = sb.toString();
    if (templateContent != null) {
      for (Map.Entry<String, String> property : properties.entrySet()) {
        templateContent = templateContent.replace("${" + property.getKey() + "}", property.getValue());
      }
    }
    return templateContent;
  }
  public void sendInvitation(String inviter,String invitee_email){
    Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,inviter,true);
    if (null != identity){
      String sender = identity.getProfile().getFullName()+" | "+this.getDNS();
      String subject = this.generateSubject(identity);
      String body = this.generateBody(identity) ;
      if (null != body){
        try {
          log.info(" subject "+subject);
          log.info(" body "+body);
          exoMailService.sendMessage(sender,invitee_email,subject,body);
        } catch (Exception e) {
          log.error("exo invite friend => cannot send invitation");
          e.getMessage();
        }
      }
    }
  }

  public String getDNS() {
    return DNS;
  }

  public void setDNS(String DNS) {
    this.DNS = DNS;
  }
}
