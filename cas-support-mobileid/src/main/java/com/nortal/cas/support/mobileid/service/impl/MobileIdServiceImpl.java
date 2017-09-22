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
package com.nortal.cas.support.mobileid.service.impl;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nortal.cas.support.mobileid.enums.MobileIdFault;
import com.nortal.cas.support.mobileid.enums.MobileIdStatus;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;
import com.nortal.cas.support.mobileid.model.MobileIdException;
import com.nortal.cas.support.mobileid.service.MobileIdService;

import ee.sk.www.digidocservice.digidocservice_2_3_wsdl.DigiDocServiceStub;
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
public class MobileIdServiceImpl implements MobileIdService {
  private static final Logger log = LoggerFactory.getLogger(MobileIdServiceImpl.class);

  private String serviceName;
  private String serviceUrl;
  
  public MobileIdAuth startMobileId(String phone, String idCode) throws MobileIdException {
    DigiDocServiceStub digiDocServiceStub = null;

    try {
      digiDocServiceStub = new DigiDocServiceStub(serviceUrl);
    } catch (AxisFault e) {
      log.error("Digidoc query failed: ", e);
      throw new MobileIdException(MobileIdFault.UNKNOWN, e);
    }

    MobileAuthenticateDocument authenticateDocument = getMobileAuthenticateDocument(phone, idCode);
    
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
      if(phone != null){
    	  answer.setPhoneNo(phone);
      }
      return answer;
    } catch (RemoteException e) {
      log.error("Mobile id authentication failed: ", e.getCause());
      throw new MobileIdException(MobileIdFault.getViga(e.getMessage()), e);
    }
  }

private MobileAuthenticateDocument getMobileAuthenticateDocument(String phone, String idCode) {
	MobileAuthenticateDocument authenticateDocument = MobileAuthenticateDocument.Factory.newInstance();
    MobileAuthenticate mobileAuthenticate = authenticateDocument.addNewMobileAuthenticate();
    mobileAuthenticate.setCountryCode("EE");
    mobileAuthenticate.setLanguage("EST");
    mobileAuthenticate.setReturnCertData(true);
    mobileAuthenticate.setMessagingMode("asynchClientServer");
    mobileAuthenticate.setServiceName(serviceName);
    if(idCode != null){
    	mobileAuthenticate.setIDCode(idCode);
    }
    if (phone != null){
    	mobileAuthenticate.setPhoneNo(phone);
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

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }
}
