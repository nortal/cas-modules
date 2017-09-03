package com.nortal.cas.support.mobileid.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Mobiil id vigade koodid.
 * 
 * @author <a href="mailto:laurit@webmedia.ee">Lauri Tulmin</a> 22.06.2007
 */
public enum MobileIdFault {

  //teenust kasutava kliendi põhjustatud vead
  UNKNOWN ("100"), //Teenuse üldine veasituatsioon
  INVALID_PHONE_NUMBER ("101"), //Sisendparameetrid mittekorrektsel kujul
  MISSING_PARAMETERS ("102"), //Mõni kohustuslik sisendparameeter on määramata
  RESTRICTED ("103"), //Ligipääs antud meetodile antud parameetritega piiratud
  //teenusesisesed vead
  FAULT ("200"), //Teenuse üldine viga
  USER_CERTIFICATE_MISSING ("201"), //Kasutaja sertifikaat puudub
  UNABLE_TO_VERIFY_CERTIFICATE ("202"), //Kasutaja sertifikaadi kehtivus ei ole võimalik kontrollida
  //lõppkasutaja ja tema telefoniga seotud vead
  PHONE_FAULT ("300"), //Kasutajaga telefoniga seotud üldine viga
  NOT_MOBILE_ID_CLIENT ("301"), //Pole Mobiil-ID kasutaja
  INVALID_CERTIFICATE ("302"), //Kasutaja sertifikaat ei kehti (OCSP vastus REVOKED)
  UNKNOWN_CERTIFICATE ("303"); //Kasutaja sertifikaadi olek teadmata (OCSP vastus UNKNOWN)

  private static final Map<String, MobileIdFault> tyybid = new HashMap<String, MobileIdFault>();
  static {
    for (MobileIdFault c : MobileIdFault.values()) {
      tyybid.put(c.getKood(), c);
    }
  }

  private final String kood;

  private MobileIdFault(String code) {
    this.kood = code;
  }

  public String getKood() {
    return kood;
  }

  /**
   * @param kood
   * @return antud koodile vastav viga või MobiilidViga.UNKNOWN kui antud koodile vastavat viga ei leitud.
   */
  public static MobileIdFault getViga(String kood) {
    MobileIdFault viga = tyybid.get(kood);
    return viga != null ? viga : UNKNOWN;
  } 

}
