/**
 *   Copyright 2017 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.cas.support.mobileid.controller;

import static com.nortal.cas.messages.Message.getMessage;
import static com.nortal.cas.support.esteid.validator.IdCodeValidator.isValidIdCode;
import static com.nortal.cas.support.mobileid.MobileId.NUMBER_TOO_LONG;
import static com.nortal.cas.support.mobileid.MobileId.ONLY_DIGITS_ALLOWED;
import static com.nortal.cas.support.mobileid.MobileId.PLEASE_WAIT;
import static com.nortal.cas.support.mobileid.MobileId.UNKNOWN;
import static com.nortal.cas.support.mobileid.MobileId.valueOf;
import static com.nortal.cas.support.mobileid.MobileIdTestNumbers.isTestIdCode;
import static com.nortal.cas.support.mobileid.MobileIdTestNumbers.isTestPhoneNumber;
import static com.nortal.cas.support.mobileid.enums.MobileIdStatus.EXPIRED_TRANSACTION;
import static com.nortal.cas.support.mobileid.enums.MobileIdStatus.OK;
import static com.nortal.cas.support.mobileid.enums.MobileIdStatus.OUTSTANDING_TRANSACTION;
import static com.nortal.cas.support.mobileid.enums.MobileIdStatus.USER_AUTHENTICATED;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nortal.cas.messages.Message;
import com.nortal.cas.support.mobileid.enums.MobileIdFault;
import com.nortal.cas.support.mobileid.enums.MobileIdStatus;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;
import com.nortal.cas.support.mobileid.model.MobileIdException;
import com.nortal.cas.support.mobileid.service.MobileIdService;

@Controller
public class MobileIdController {
  private static final Logger log = LoggerFactory.getLogger(MobileIdController.class);

  private static final String ACTION_STATUS_CHECK = "m_id_status";
  private static final String ACTION_LOGIN = "m_id_login";

  public static final String MOBILE_AUTH_SESSION_KEY = "mobileAuthentication";
  private static final String STATUS_CHECK_SESSION_KEY = "sessionCode";
  private static final String STATUS_CHECK_DELAY_KEY = "attemptDelay";
  private static final String PHONE_COUNTRY_PREFIX = "372";

  private MobileIdService mobileIdService;
  private MessageSource messageSource;
  /**Disable in production*/
  private boolean isTestNumbersEnabled;
  /**The delay in seconds between mobileId checks.*/
  private int mIdCheckDelay = 5;
  /**The number of maximum mobileId checks. */
	private int mIdCheckCount = 20;

	@RequestMapping(value="/mobileIdAuth", method=POST)
  public void mobileIdAuth(Locale locale, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    
	setCharacterEncoding(req, resp, "UTF-8");

    String phoneOrId = req.getParameter("phonenumber").replaceAll("\\s","");
    
    boolean isIdCode = isValidIdCode(phoneOrId) || (isTestNumbersEnabled && isTestIdCode(phoneOrId));
    boolean isTestPhoneNumber = isTestNumbersEnabled && isTestPhoneNumber(phoneOrId);
    
    if(!isIdCode && !isTestPhoneNumber){
    	phoneOrId = fixPhoneNumberPrefix(phoneOrId);
	    if (!isNumeric(phoneOrId) || isBlank(phoneOrId)) {
	    	queryFailed(resp, locale, phoneOrId, new Message(ONLY_DIGITS_ALLOWED));
	    	return;
	    }
	    else if (phoneOrId.length() > 11) {
	    	queryFailed(resp, locale, phoneOrId, new Message(NUMBER_TOO_LONG));
	    	return;
	    }
    }

    HttpSession sess = req.getSession();
    String action = req.getParameter("action");
    log.debug("Received post with action: " + action);
    if (ACTION_LOGIN.equalsIgnoreCase(action)) {

      log.info("Mobile authentication start for: " + phoneOrId);
      sess.removeAttribute(MOBILE_AUTH_SESSION_KEY);
      MobileIdAuth mobileIdAuthentication = doStartMobileIdAuthentication(phoneOrId, resp, isIdCode, locale);
      sess.setAttribute(MOBILE_AUTH_SESSION_KEY, mobileIdAuthentication);

    } else if (ACTION_STATUS_CHECK.equalsIgnoreCase(action)) {
      log.debug("Mobile authentication status check");

      MobileIdAuth runningAuth = (MobileIdAuth) sess.getAttribute(MOBILE_AUTH_SESSION_KEY);
      if (runningAuth == null) {
        log.error("Cannot check status if auth is not started. Starting new auth for number: " + phoneOrId);
        MobileIdAuth mobileIdAuthentication = doStartMobileIdAuthentication(phoneOrId, resp, isIdCode, locale);
        sess.setAttribute(MOBILE_AUTH_SESSION_KEY, mobileIdAuthentication);
        return;
      } else if (req.getAttribute(STATUS_CHECK_SESSION_KEY) != null
          && !req.getAttribute(STATUS_CHECK_SESSION_KEY).equals(runningAuth.getSesscode())) {
        log.error("Last succesful authentication start does not match with current status check. Phone: " + phoneOrId);
        sess.removeAttribute(MOBILE_AUTH_SESSION_KEY);
        return;
      }

      doCheckMobileIdAuthentication(runningAuth, resp, locale);

      if (USER_AUTHENTICATED.equals(runningAuth.getStatus())) {
        log.info("Mobile id authentication was a success for phone " + phoneOrId);
      }

    } else {
      throw new ServletException("Not supported action");
    }
  }

private void queryFailed(HttpServletResponse resp, Locale locale,
		String phoneOrId, Message message) throws ServletException {
	  log.error("Query failed with MessageCode " + message.getCode() + " for phoneOrId " + phoneOrId);
	  Map<String, Object> sessionParams = new HashMap<String, Object>();
	  putMessage(sessionParams, locale, message);
	  sessionParams.put("startStatusCheck", false);
	  writeJson(resp, sessionParams);
	  return;
}

private String fixPhoneNumberPrefix(String phoneOrId) {
    if (phoneOrId.startsWith("+")) {
        phoneOrId = phoneOrId.substring(1);
    }
	return phoneOrId.startsWith(PHONE_COUNTRY_PREFIX) ? 
			phoneOrId : 
			PHONE_COUNTRY_PREFIX + phoneOrId;
}

private void setCharacterEncoding(HttpServletRequest req,
		HttpServletResponse resp, String characterEncoding) {
	try {
      req.setCharacterEncoding(characterEncoding);
    } catch (UnsupportedEncodingException e) {
      log.error("Unsupported encoding: " +characterEncoding);
    }
    resp.setCharacterEncoding(characterEncoding);
}

  private void doCheckMobileIdAuthentication(MobileIdAuth auth, HttpServletResponse response, Locale locale) throws ServletException {
    int statusCheckAttempt = auth.getStatusCheckAttempt();
    log.info("Continuing authentication for session: " + auth.getSesscode() + " phone: " + auth.getPhoneNo() + " attempt:"
        + statusCheckAttempt);
    String phoneNumber = auth.getPhoneNo();

    Map<String, Object> sessionParams = new HashMap<String, Object>();

	if (mIdCheckCount <= statusCheckAttempt) {
      auth.setStatus(EXPIRED_TRANSACTION);
      putMessage(sessionParams, locale, auth.getStatus());
      sessionParams.put("startStatusCheck", false);
      writeJson(response, sessionParams);
      return;
    }

    try {
      auth.registerStatusCheckAttempt();
      MobileIdStatus result = mobileIdService.getMobileIdState(auth.getSesscode());
      if (result == null) {
        throw new MobileIdException(MobileIdFault.UNKNOWN, null);
      }
      auth.setStatus(result);
      log.debug("Session: " + auth.getSesscode() + " status: " + result.name());
    } catch (MobileIdException e) {
      // Query failed
      log.error("MobileID query failed code - for number " + phoneNumber + " is: " + e.getError());
      putMessage(sessionParams, locale, e.getError());
      sessionParams.put("startStatusCheck", false);
      writeJson(response, sessionParams);
      return;
    }

    if (OUTSTANDING_TRANSACTION.equals(auth.getStatus())) {
      // If transaction is still running
      log.info("Mobile id authentication for " + phoneNumber + " is continued");
      putMessage(sessionParams, locale, new Message(PLEASE_WAIT, auth.getChallengeId()));
      putStartStatusCheck(sessionParams, auth, locale);
    } else if (USER_AUTHENTICATED.equals(auth.getStatus())) {
      // If authentication is ok then check certificate and stop periodic queries
      sessionParams.put("doCheckCertificate", true);
      sessionParams.put("startStatusCheck", false);
    } else {
      // If status check response was error
      log.error("MobileID error - for number " + phoneNumber + " is: " + auth.getStatus());
      sessionParams.put("startStatusCheck", false);
      putMessage(sessionParams, locale, auth.getStatus());
    }

    writeJson(response, sessionParams);
  }

  private MobileIdAuth doStartMobileIdAuthentication(String phoneOrId, HttpServletResponse response, boolean isIdCode, Locale locale) throws ServletException {
    Map<String, Object> sessionParams = new HashMap<String, Object>();

    MobileIdAuth result = null;
    try {
      result = mobileIdService.startMobileId(phoneOrId, isIdCode);
    } catch (MobileIdException e) {
      // Query failed
      log.error("MobileID query failed code - for number " + phoneOrId + " is: " + e.getError());
      putMessage(sessionParams, locale, e.getError());
      sessionParams.put("startStatusCheck", false);
      writeJson(response, sessionParams);
      return null;
    }

    if (OK.equals(result.getStatus())) {
      log.info("Mobile id authentication for " + phoneOrId + " has started");
      putMessage(sessionParams, locale, new Message(PLEASE_WAIT, result.getChallengeId()));
      putStartStatusCheck(sessionParams, result, locale);
    } else {
      log.error("MobileID error code - for number " + phoneOrId + " is: " + result.getStatus());
      putMessage(sessionParams, locale, result.getStatus());
      sessionParams.put("startStatusCheck", false);
    }

    writeJson(response, sessionParams);
    return result;
  }

  private void putStartStatusCheck(Map<String, Object> sessionParams, MobileIdAuth result, Locale locale) {
    sessionParams.put("startStatusCheck", true);
    sessionParams.put(STATUS_CHECK_DELAY_KEY, mIdCheckDelay);
    sessionParams.put(STATUS_CHECK_SESSION_KEY, result.getSesscode());
  }

  private void putMessage(Map<String, Object> sessionParams, Locale locale, MobileIdStatus status) {
    putMessage(sessionParams, locale, new Message(valueOf(status)));
  }

  private void putMessage(Map<String, Object> sessionParams, Locale locale, MobileIdFault error) {
    putMessage(sessionParams, locale, new Message(valueOf(error)));
  }

  private void putMessage(Map<String, Object> sessionParams, Locale locale, Message message) {
    String translatedMessage = messageSource.getMessage(message, locale);
    if (isBlank(translatedMessage) || message.getCode().equals(translatedMessage)) {
      log.error("No message for code: " + message.getCode());
      translatedMessage = getMessage(messageSource, locale, UNKNOWN);
    }
    sessionParams.put("message", translatedMessage);
  }

  @SuppressWarnings("unchecked")
  private void writeJson(HttpServletResponse response, Map<String, Object> sessionParams) throws ServletException {
    try {
      JSONObject json = new JSONObject();
      json.putAll(sessionParams);
      response.setContentType("application/json");
      response.setHeader("Cache-Control", "no-cache");

      if (log.isTraceEnabled()) {
        log.trace("Responding with: " + json.toJSONString());
      }

      response.getWriter().write(json.toJSONString());
    } catch (IOException e) {
      throw new ServletException("Could not create json.", e);
    }
  }
  
  public void setIsTestNumbersEnabled(boolean isTestNumbersEnabled) {
		this.isTestNumbersEnabled = isTestNumbersEnabled;
	}

  public void setMobileIdService(MobileIdService mobileIdService) {
  	this.mobileIdService = mobileIdService;
  }

  public void setMessageSource(MessageSource messageSource) {
  	this.messageSource = messageSource;
  }
  
  public void setmIdCheckDelay(int mIdCheckDelay) {
		this.mIdCheckDelay = mIdCheckDelay;
	}

	public void setmIdCheckCount(int mIdCheckCount) {
		this.mIdCheckCount = mIdCheckCount;
	}
}
