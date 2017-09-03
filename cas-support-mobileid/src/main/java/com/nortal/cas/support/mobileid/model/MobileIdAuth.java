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
package com.nortal.cas.support.mobileid.model;

import java.io.Serializable;

import com.nortal.cas.support.mobileid.enums.MobileIdStatus;


/**
 * Mobile id authentication object.
 * 
 * @author <a href="mailto:laurit@webmedia.ee">Lauri Tulmin</a> 22.06.2007
 */
public class MobileIdAuth implements Serializable {
  private static final long serialVersionUID = 1L;

  private int sesscode = 0;
  private int statusCheckAttempt=0;
  private String phoneNo = null;
  private MobileIdStatus status;
  private String certificateData;
  private String signature;
  private String challengeId;
  private String idCode;
  private String userGivenname = null;
  private String userSurname = null;


  public String getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(String challengeId) {
    this.challengeId = challengeId;
  }

  public String getIdCode() {
    return idCode;
  }

  public void setIdCode(String idCode) {
    this.idCode = idCode;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public int getSesscode() {
    return sesscode;
  }

  public void setSesscode(int sesscode) {
    this.sesscode = sesscode;
  }
  
  public int getStatusCheckAttempt() {
    return statusCheckAttempt;
  }
  
  public void registerStatusCheckAttempt() {
    statusCheckAttempt++;
  }
  
  public String getCertificateData() {
    return certificateData;
  }

  public void setCertificateData(String certificateData) {
    this.certificateData = certificateData;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public MobileIdStatus getStatus() {
    return status;
  }

  public void setStatus(MobileIdStatus status) {
    this.status = status;
  }

  public void setStatus(String status) {
    setStatus(MobileIdStatus.getStaatus(status));
  }

  public String getUserGivenname() {
    return userGivenname;
  }

  public void setUserGivenname(String userGivenname) {
    this.userGivenname = userGivenname;
  }

  public String getUserSurname() {
    return userSurname;
  }

  public void setUserSurname(String userSurname) {
    this.userSurname = userSurname;
  }
  
  @Override
  public String toString() {
    return "Phone: "+phoneNo+" session: "+sesscode+" status:"+status;
  }


}
