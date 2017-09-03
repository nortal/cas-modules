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
