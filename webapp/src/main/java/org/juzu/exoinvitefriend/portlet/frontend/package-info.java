/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Application(defaultController = JuZExoInviteFriendFrontendApplication.class)
@Portlet(name = "eXoInviteFriendFrontendPortlet")
@Bindings({
  @Binding(value = SessionProviderService.class), @Binding(value = NodeHierarchyCreator.class),
  @Binding(value = IdentityManager.class),
  @Binding(value = MailService.class),
  @Binding(value = IService.class,implementation = JCRImpl.class)
})
@WebJars(@WebJar("jquery"))
@Scripts({
  @Script(id="jquery",value = "jquery/1.10.2/jquery.js"),
  @Script(value = "javascripts/exoinvitefriend.js",depends = "jquery")
})
@Stylesheets(@Stylesheet("css/exoinvitefriend.css"))
@Assets("*")
package org.juzu.exoinvitefriend.portlet.frontend;

import juzu.Application;
import juzu.plugin.asset.*;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import juzu.plugin.webjars.WebJar;
import juzu.plugin.webjars.WebJars;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.social.core.manager.IdentityManager;
import org.juzu.exoinvitefriend.portlet.commons.services.IService;
import org.juzu.exoinvitefriend.portlet.commons.services.JCRImpl;