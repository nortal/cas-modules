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

import com.nortal.cas.support.mobileid.enums.MobileIdFault;

/**
 * Mobiilid autentimine ebaõnnestus.
 * 
 * @author <a href="mailto:laurit@webmedia.ee">Lauri Tulmin</a> 22.06.2007
 */
public class MobileIdException extends Exception {
  private static final long serialVersionUID = 1L;

  private MobileIdFault error;

  public MobileIdException(MobileIdFault error, Throwable t) {
    super(t);
    this.error = error;
  }

  public MobileIdFault getError() {
    return error;
  }

}
