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
package com.nortal.cas.support.mobileid.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Mobiil id päringu olekute koodid.
 * 
 * @author <a href="mailto:laurit@webmedia.ee">Lauri Tulmin</a> 22.06.2007
 */
public enum MobileIdStatus {

  OK ("OK"), //päring korras
  OUTSTANDING_TRANSACTION ("OUTSTANDING_TRANSACTION"), //autenimine alles toimub
  USER_AUTHENTICATED ("USER_AUTHENTICATED"), //isik tuvastatud
  NOT_VALID ("NOT_VALID"), //toiming on lõppenud, kuid kasutaja poolt tekitatud signatuur ei ole kehtiv.
  EXPIRED_TRANSACTION ("EXPIRED_TRANSACTION"), //sessioon on aegunud
  USER_CANCEL ("USER_CANCEL"), //kasutaja katkestas
  MID_NOT_READY ("MID_NOT_READY"), //Mobiil-ID funktsionaalsus ei ole veel kasutatav, proovida mõne aja pärast uuesti
  PHONE_ABSENT ("PHONE_ABSENT"), //telefon ei ole levis
  SENDING_ERROR ("SENDING_ERROR"), //Muu sõnumi saatmise viga (telefon ei suuda sõnumit vastu võtta, sõnumikeskus häiritud)
  SIM_ERROR ("SIM_ERROR"), //SIM rakenduse viga
  INTERNAL_ERROR ("INTERNAL_ERROR"); //teenuse tehniline viga

  private static final Map<String, MobileIdStatus> tyybid = new HashMap<String, MobileIdStatus>();
  static {
    for (MobileIdStatus c : MobileIdStatus.values()) {
      tyybid.put(c.getKood(), c);
    }
  }
  
  private final String kood;
  
  private MobileIdStatus(String kood) {
    this.kood = kood;
  }

  public String getKood() {
    return kood;
  }

  public String toString() {
    return kood;
  }

  /**
   * Leia antud oleku koodile vastav olek.
   * 
   * @param kood
   * @return MobiilidStaatus või null kui antud koodile vastavat olekut ei leitud.
   */
  public static MobileIdStatus getStaatus(String kood) {
    return tyybid.get(kood);
  } 

}
