package org.juzu.exoinvitefriend.portlet.commons.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by exoplatform on 27/01/15.
 */
public class Utils {

  public static Boolean validateEmail(String email){
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Pattern pattern;
    Matcher matcher;
    pattern = Pattern.compile(EMAIL_PATTERN);
    matcher = pattern.matcher(email);
    if( email == null || "".equals(email) || !matcher.matches()) {
      return false;
    }
    return false;
  }

}
