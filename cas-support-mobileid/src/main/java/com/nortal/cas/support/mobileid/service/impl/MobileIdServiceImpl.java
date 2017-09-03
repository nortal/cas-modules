package com.nortal.cas.support.mobileid.service.impl;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nortal.cas.support.mobileid.enums.MobileIdFault;
import com.nortal.cas.support.mobileid.enums.MobileIdStatus;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;
import com.nortal.cas.support.mobileid.model.MobileIdException;
import com.nortal.cas.support.mobileid.service.MobileIdService;

import ee.sk.digidocservice.digidocservice_2_3_wsdl.DigiDocServiceStub;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.GetMobileAuthenticateStatusDocument;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.GetMobileAuthenticateStatusDocument.GetMobileAuthenticateStatus;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.GetMobileAuthenticateStatusResponseDocument;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.MobileAuthenticateDocument;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.MobileAuthenticateDocument.MobileAuthenticate;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.MobileAuthenticateResponseDocument;
import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.MobileAuthenticateResponseDocument.MobileAuthenticateResponse;

/**
 * @author Deniss Mokijevski
 * @author Maksim Boiko (max@webmedia.ee)
 */
public class MobileIdServiceImpl implements MobileIdService, ApplicationContextAware {
  private static final Logger log = LoggerFactory.getLogger(MobileIdServiceImpl.class);

  private String serviceName;
  private String serviceUrl;
  
  public MobileIdAuth startMobileId(String phoneOrId, boolean isIdCode) throws MobileIdException {
    DigiDocServiceStub digiDocServiceStub = null;

    try {
      digiDocServiceStub = new DigiDocServiceStub(serviceUrl);
    } catch (AxisFault e) {
      log.error("Digidoc query failed: ", e);
      throw new MobileIdException(MobileIdFault.UNKNOWN, e);
    }

    MobileAuthenticateDocument authenticateDocument = getMobileAuthenticateDocument(phoneOrId, isIdCode);
    
    try {
      
      MobileAuthenticateResponseDocument response = digiDocServiceStub.mobileAuthenticate(authenticateDocument);
      MobileAuthenticateResponse mobileAuthenticateResponse = response.getMobileAuthenticateResponse();
      MobileIdAuth answer = new MobileIdAuth();
      answer.setCertificateData(mobileAuthenticateResponse.getCertificateData());
      answer.setChallengeId(mobileAuthenticateResponse.getChallengeID());
      answer.setSesscode(mobileAuthenticateResponse.getSesscode());

      String statusString = mobileAuthenticateResponse.getStatus();
      answer.setStatus(MobileIdStatus.valueOf(statusString));

      answer.setUserGivenname(mobileAuthenticateResponse.getUserGivenname());
      answer.setUserSurname(mobileAuthenticateResponse.getUserSurname());
      answer.setIdCode(mobileAuthenticateResponse.getUserIDCode());
      if(!isIdCode){
    	  answer.setPhoneNo(phoneOrId);
      }
      return answer;
    } catch (RemoteException e) {
      log.error("Mobile id authentication failed: ", e.getCause());
      throw new MobileIdException(MobileIdFault.getViga(e.getMessage()), e);
    }
  }

private MobileAuthenticateDocument getMobileAuthenticateDocument(String phoneOrId, boolean isIdCode) {
	MobileAuthenticateDocument authenticateDocument = MobileAuthenticateDocument.Factory.newInstance();
    MobileAuthenticate mobileAuthenticate = authenticateDocument.addNewMobileAuthenticate();
    mobileAuthenticate.setCountryCode("EE");
    mobileAuthenticate.setLanguage("EST");
    mobileAuthenticate.setReturnCertData(true);
    mobileAuthenticate.setMessagingMode("asynchClientServer");
    mobileAuthenticate.setServiceName(serviceName);
    if(isIdCode){
    	mobileAuthenticate.setIDCode(phoneOrId);
    }
    else{
    	mobileAuthenticate.setPhoneNo(phoneOrId);
    }
	return authenticateDocument;
}

  public MobileIdStatus getMobileIdState(Integer sessCode) throws MobileIdException {
    DigiDocServiceStub digiDocServiceStub = null;
    try {
      digiDocServiceStub = new DigiDocServiceStub(serviceUrl);
    } catch (AxisFault e) {
      throw new MobileIdException(MobileIdFault.UNKNOWN, e);
    }

    GetMobileAuthenticateStatusDocument statusDocument = GetMobileAuthenticateStatusDocument.Factory.newInstance();
    GetMobileAuthenticateStatus mobileAuthenticateStatus = statusDocument.addNewGetMobileAuthenticateStatus();
    mobileAuthenticateStatus.setSesscode(sessCode);
    try {

      GetMobileAuthenticateStatusResponseDocument authenticateStatus = digiDocServiceStub.getMobileAuthenticateStatus(statusDocument);
      String statusString = authenticateStatus.getGetMobileAuthenticateStatusResponse().getStatus();
      return MobileIdStatus.valueOf(statusString);
    } catch (RemoteException e) {
      throw new MobileIdException(MobileIdFault.getViga(e.getMessage()), e);
    }
  }

  public void setApplicationContext(ApplicationContext arg0) throws BeansException {
    // This method makes sure that current service gets initialized before servlet that uses it...
    log.info("Initializing mobileIdService");
//FIXME Is this still needed in Weblogic 12c ? "In Weblogic projects make sure that https protocol uses Weblogic SSLSocketFactory."
//FIXME Should we set socket factory for the Protocol object ?
//  Protocol.registerProtocol("https", new Protocol("https", new WeblogicSocketFactory(), 443));
//  Protocol.registerProtocol("https", new Protocol("https", new SSLProtocolSocketFactory(), 8443));
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }
}
