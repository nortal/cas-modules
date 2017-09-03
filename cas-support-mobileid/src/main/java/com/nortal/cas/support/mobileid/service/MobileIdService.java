package com.nortal.cas.support.mobileid.service;

import com.nortal.cas.support.mobileid.enums.MobileIdStatus;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;
import com.nortal.cas.support.mobileid.model.MobileIdException;

/**
 * Mobile Id autentification services
 * 
 * @author Roman Tekhov
 * @author Deniss Mokijevski
 */
public interface MobileIdService {

  /**
   * Alustab Mobiil-ID põhilist autentimist. Saadab MobileAuthenticate päringu DigiDocService'le ning tagastab saadud <i>challenge</i>
   * koodi, leitud kasutajat ning sessiooni koodi <code>MobileIdAuth</code> objekti kujul.
   * 
   * @param phoneOrIdCode telefoni number või isikukood
   * @param isIdCode true, kui on Eesti isikukood
   */
  MobileIdAuth startMobileId(String phoneOrIdCode, boolean isIdCode) throws MobileIdException;

  /**
   * Saadab DigiDocService'le päringu ning tagastab hetke autentimise staatus.
   * 
   * @param kood
   *          - sessioni -kood
   */
  MobileIdStatus getMobileIdState(Integer kood) throws MobileIdException;

}
