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
