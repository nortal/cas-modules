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
import static com.nortal.cas.support.mobileid.MobileId.NUMBER_TOO_LONG;
import static com.nortal.cas.support.mobileid.MobileId.ONLY_DIGITS_ALLOWED;
import static com.nortal.cas.support.mobileid.MobileId.PLEASE_WAIT;
import static com.nortal.cas.support.mobileid.MobileId.UNKNOWN;
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

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nortal.cas.messages.Message;
import com.nortal.cas.support.esteid.validator.IdCodeValidator;
import com.nortal.cas.support.mobileid.MobileId;
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
	/** Disable in production */
	private boolean isTestNumbersEnabled;
	/** The delay in seconds between mobileId checks. */
	private int mIdCheckDelay = 5;
	/** The number of maximum mobileId checks. */
	private int mIdCheckCount = 20;

	private boolean allowPhoneOrId = true;

	private static class PhoneIdCode {
		public PhoneIdCode(String phone, String idCode) {
			this.phone = phone;
			this.idCode = idCode;
		}

		String phone;
		String idCode;

		@Override
		public String toString() {
			return "phone: " + phone + ", id: " + idCode;
		}
	}

	private static class MobileIdControllerException extends Exception {
		private static final long serialVersionUID = 1L;
		private Message msg;

		public MobileIdControllerException(Message msg) {
			this.msg = msg;
		}

		public Message getMsg() {
			return msg;
		}
	}

	private void fail(MobileId mobileId) throws MobileIdControllerException {
		throw new MobileIdControllerException(new Message(mobileId));
	}

	private PhoneIdCode getPhoneIdCode(HttpServletRequest req) {
		String phone = req.getParameter("phonenumber");
		String idCode = req.getParameter("idcode");
		
		if (StringUtils.isBlank(phone)) {
			phone = null;
		}
		
		if (StringUtils.isBlank(idCode)) {
			idCode = null;
		}

		if (phone == null || idCode == null) {
			String phoneOrId = phone == null ? idCode : phone;
			if (isValidIdCode(phoneOrId)) {
				idCode = phoneOrId;
				phone = null;
			} else {
				phone = phoneOrId;
				idCode = null;
			}
		}

		if (phone != null) {
			phone = phone.replaceAll("\\s", "");
			phone = fixPhoneNumberPrefix(phone);
		}

		return new PhoneIdCode(phone, idCode);
	}

	private void checkPhoneIdCode(PhoneIdCode phoneOrId) throws MobileIdControllerException {
		String phone = phoneOrId.phone;
		String idCode = phoneOrId.idCode;

		if ((phone == null && idCode == null) || ((phone == null || idCode == null) && !allowPhoneOrId)) {
			fail(MobileId.FAULT_MISSING_PARAMETERS);
		}

		if (idCode != null && !isValidIdCode(idCode)) {
			fail(MobileId.INVALID_ID_CODE);
		}

		if (phone != null) {
			if (!isTestNumbersEnabled || !isTestPhoneNumber(phone)) {
				if (!isNumeric(phone) || isBlank(phone)) {
					fail(ONLY_DIGITS_ALLOWED);
				} else if (phone.length() > 11) {
					fail(NUMBER_TOO_LONG);
				}
			}
		}
	}

	@RequestMapping(value = "/mobileIdAuth", method = POST)
	public void mobileIdAuth(Locale locale, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		setCharacterEncoding(req, resp, "UTF-8");

		PhoneIdCode phoneOrId = getPhoneIdCode(req);

		try {
			checkPhoneIdCode(phoneOrId);

			MobileIdAuth auth = processAction(req, phoneOrId);
			if (auth != null) {
				if (OK.equals(auth.getStatus())) {
					log.info("Mobile id authentication for " + phoneOrId + " is started");
					pleaseWait(auth, resp, locale);
				} else if (OUTSTANDING_TRANSACTION.equals(auth.getStatus())) {
					log.info("Mobile id authentication for " + phoneOrId + " is continued");
					pleaseWait(auth, resp, locale);
				} else if (USER_AUTHENTICATED.equals(auth.getStatus())) {
					userAuthenticated(resp, locale, phoneOrId);
				} else {
					fail(MobileId.valueOf(auth.getStatus()));
				}
			}
		} catch (MobileIdException e) {
			handleError(new Message(MobileId.valueOf(e.getError())), locale, resp, phoneOrId);
		} catch (MobileIdControllerException e) {
			handleError(e.getMsg(), locale, resp, phoneOrId);
		}
	}

	private MobileIdAuth processAction(HttpServletRequest req, PhoneIdCode phoneOrId)
			throws ServletException, MobileIdException {
		HttpSession sess = req.getSession();
		String action = req.getParameter("action");
		log.debug("Received post with action: " + action);
		if (ACTION_LOGIN.equalsIgnoreCase(action)) {
			log.info("Mobile authentication start for: " + phoneOrId);
			sess.removeAttribute(MOBILE_AUTH_SESSION_KEY);
			return doStartMobileIdAuthentication(phoneOrId, sess);
		} else if (ACTION_STATUS_CHECK.equalsIgnoreCase(action)) {
			log.debug("Mobile authentication status check");
			MobileIdAuth runningAuth = (MobileIdAuth) sess.getAttribute(MOBILE_AUTH_SESSION_KEY);
			if (runningAuth == null) {
				log.error("Cannot check status if auth is not started. Starting new auth for: " + phoneOrId);
				return doStartMobileIdAuthentication(phoneOrId, sess);
			} else if (req.getAttribute(STATUS_CHECK_SESSION_KEY) != null
					&& !req.getAttribute(STATUS_CHECK_SESSION_KEY).equals(runningAuth.getSesscode())) {
				log.error("Last succesful authentication start does not match with current status check for: "
						+ phoneOrId);
				sess.removeAttribute(MOBILE_AUTH_SESSION_KEY);
				// XXX Do nothing then???
				return null;
			} else {
				return doCheckMobileIdAuthentication(runningAuth);
			}
		} else {
			throw new ServletException("Not supported action");
		}
	}

	private void handleError(Message message, Locale locale, HttpServletResponse resp, PhoneIdCode phoneOrId)
			throws ServletException {
		log.error("MobileID query failed code - for number " + phoneOrId + " is: " + message);
		Map<String, Object> sessionParams = new HashMap<String, Object>();
		putMessage(sessionParams, locale, message);
		sessionParams.put("startStatusCheck", false);
		writeJson(resp, sessionParams);
	}

	private void pleaseWait(MobileIdAuth auth, HttpServletResponse response, Locale locale) throws ServletException {
		Map<String, Object> sessionParams = new HashMap<String, Object>();
		putMessage(sessionParams, locale, new Message(PLEASE_WAIT, auth.getChallengeId()));
		putStartStatusCheck(sessionParams, auth, locale);
		writeJson(response, sessionParams);
	}

	private void userAuthenticated(HttpServletResponse response, Locale locale, PhoneIdCode phoneOrId)
			throws ServletException {
		log.info("Mobile id authentication was a success for " + phoneOrId);
		Map<String, Object> sessionParams = new HashMap<String, Object>();
		// If authentication is ok then check certificate and stop periodic queries
		sessionParams.put("doCheckCertificate", true);
		sessionParams.put("startStatusCheck", false);
		writeJson(response, sessionParams);
	}

	private boolean isValidIdCode(String idCode) {
		return IdCodeValidator.isValidIdCode(idCode) || (isTestNumbersEnabled && isTestIdCode(idCode));
	}

	private String fixPhoneNumberPrefix(String phoneOrId) {
		if (phoneOrId.startsWith("+")) {
			phoneOrId = phoneOrId.substring(1);
		}
		return phoneOrId.startsWith(PHONE_COUNTRY_PREFIX) ? phoneOrId : PHONE_COUNTRY_PREFIX + phoneOrId;
	}

	private void setCharacterEncoding(HttpServletRequest req, HttpServletResponse resp, String characterEncoding) {
		try {
			req.setCharacterEncoding(characterEncoding);
		} catch (UnsupportedEncodingException e) {
			log.error("Unsupported encoding: " + characterEncoding);
		}
		resp.setCharacterEncoding(characterEncoding);
	}

	private MobileIdAuth doCheckMobileIdAuthentication(MobileIdAuth auth) throws MobileIdException {
		int statusCheckAttempt = auth.getStatusCheckAttempt();
		log.info("Continuing authentication for session: " + auth.getSesscode() + " phone: " + auth.getPhoneNo()
				+ " attempt:" + statusCheckAttempt);

		if (mIdCheckCount <= statusCheckAttempt) {
			auth.setStatus(EXPIRED_TRANSACTION);
		} else {
			auth.registerStatusCheckAttempt();
			MobileIdStatus result = mobileIdService.getMobileIdState(auth.getSesscode());
			if (result == null) {
				throw new MobileIdException(MobileIdFault.UNKNOWN, null);
			}
			auth.setStatus(result);

			log.debug("Session: " + auth.getSesscode() + " status: " + result.name());
		}

		return auth;
	}

	private MobileIdAuth doStartMobileIdAuthentication(PhoneIdCode phoneOrId, HttpSession session)
			throws MobileIdException {
		MobileIdAuth result = mobileIdService.startMobileId(phoneOrId.phone, phoneOrId.idCode);

		if (OK.equals(result.getStatus())) {
			session.setAttribute(MOBILE_AUTH_SESSION_KEY, result);
		}

		return result;
	}

	private void putStartStatusCheck(Map<String, Object> sessionParams, MobileIdAuth result, Locale locale) {
		sessionParams.put("startStatusCheck", true);
		sessionParams.put(STATUS_CHECK_DELAY_KEY, mIdCheckDelay);
		sessionParams.put(STATUS_CHECK_SESSION_KEY, result.getSesscode());
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

	public void setAllowPhoneOrId(boolean allowPhoneOrId) {
		this.allowPhoneOrId = allowPhoneOrId;
	}
}
