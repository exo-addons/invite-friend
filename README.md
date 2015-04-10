This add-on allows to send an invitation to join your platform instance
-----------
   **Compatible version**
   - PLF 4.1.x

   **exo-invite-friend addon currently has some main functions**

   - Send invitation to join on your website via email address.
   - Store invitation informations like inviter, email invitee, date.
   - Edit mode: In this mode, you can enable/disable the data saving settings and you also can set the invitation url.
   Normally the invitation should be the register url.

   **What needs to be configured in platform package**
   - Edit tomcat/gatein/conf/configuration.properties file:
   set "gatein.email.domain.url" for your website url (e.g: gatein.email.domain.url=https://intranet.mycompany.com )
   set "exo.notifications.portalname" for your website name (e.g: exo.notifications.portalname=eXo)

   **How to apply this addon to your website**
   - Clone project: git clone https://github.com/exo-addons/invite-friend.git
   - Go to invite-friend directory
   - Compile project: mvn clean install
   - Go to webapp/target directory, copy web application named "exo-invite-friend-webapp.war" and put it into tomcat/webapps
   - Run tomcat
   - Signin to platform
   - Go to Administration -> Applications -> Add portlet named "Juzu eXo Invite Friend" to a category
   - Put the portlet named "Juzu eXo Invite Friend" into where you want

  -----------
  **By reusing exo-invite-friend addon, you also can develop another portlet that allows to see the invitation ports according to your own needs **

 -----------
