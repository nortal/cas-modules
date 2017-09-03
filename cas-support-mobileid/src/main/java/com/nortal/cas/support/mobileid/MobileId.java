package com.nortal.cas.support.mobileid;

import com.nortal.cas.messages.Message;
import com.nortal.cas.messages.MessageInterface;
import com.nortal.cas.support.mobileid.enums.MobileIdFault;
import com.nortal.cas.support.mobileid.enums.MobileIdStatus;

/**
 * Mobile-ID login message code holder. <br/>
 * Used to create {@link Message} and find Mobile-ID login message text. 
 * @author Allar Saarnak
 */
public enum MobileId implements MessageInterface {
	UNKNOWN("login.mobile_id.error.unknown"),
	PLEASE_WAIT("login.mobile_id.pleaseWait"),
	ONLY_DIGITS_ALLOWED("login.mobile_id.rm.error.canContainOnlyDigits"),
	NUMBER_TOO_LONG("login.mobile_id.rm.error.numberIsTooLong"),
	
	//response
	STATUS_OK ("login.mobile_id.error.OK"), //päring korras
	STATUS_OUTSTANDING_TRANSACTION ("login.mobile_id.error.OUTSTANDING_TRANSACTION"), //autenimine alles toimub
	STATUS_USER_AUTHENTICATED ("login.mobile_id.error.USER_AUTHENTICATED"), //isik tuvastatud
	STATUS_NOT_VALID ("login.mobile_id.error.NOT_VALID"), //toiming on lõppenud, kuid kasutaja poolt tekitatud signatuur ei ole kehtiv.
	STATUS_EXPIRED_TRANSACTION ("login.mobile_id.error.EXPIRED_TRANSACTION"), //sessioon on aegunud
	STATUS_USER_CANCEL ("login.mobile_id.error.USER_CANCEL"), //kasutaja katkestas
	STATUS_MID_NOT_READY ("login.mobile_id.error.MID_NOT_READY"), //Mobiil-ID funktsionaalsus ei ole veel kasutatav, proovida mõne aja pärast uuesti
	STATUS_PHONE_ABSENT ("login.mobile_id.error.PHONE_ABSENT"), //telefon ei ole levis
	STATUS_SENDING_ERROR ("login.mobile_id.error.SENDING_ERROR"), //Muu sõnumi saatmise viga (telefon ei suuda sõnumit vastu võtta, sõnumikeskus häiritud)
	STATUS_SIM_ERROR ("login.mobile_id.error.SIM_ERROR"), //SIM rakenduse viga
	STATUS_INTERNAL_ERROR ("login.mobile_id.error.INTERNAL_ERROR"), //teenuse tehniline viga
	
	//exception - teenust kasutava kliendi põhjustatud vead
	FAULT_UNKNOWN ("login.mobile_id.error.100"), //Teenuse üldine veasituatsioon
	FAULT_INVALID_PHONE_NUMBER ("login.mobile_id.error.101"), //Sisendparameetrid mittekorrektsel kujul
	FAULT_MISSING_PARAMETERS ("login.mobile_id.error.102"), //Mõni kohustuslik sisendparameeter on määramata
	FAULT_RESTRICTED ("login.mobile_id.error.103"), //Ligipääs antud meetodile antud parameetritega piiratud (näiteks kasutatav  ServiceName ei ole teenuse pakkuja juures registreeritud.
	//teenusesisesed vead
	FAULT_FAULT ("login.mobile_id.error.200"), //Teenuse üldine viga
	FAULT_USER_CERTIFICATE_MISSING ("login.mobile_id.error.201"), //Kasutaja sertifikaat puudub
	FAULT_UNABLE_TO_VERIFY_CERTIFICATE ("login.mobile_id.error.202"), //Kasutaja sertifikaadi kehtivus ei ole võimalik kontrollida
	//lõppkasutaja ja tema telefoniga seotud vead
	FAULT_PHONE_FAULT ("login.mobile_id.error.300"), //Kasutajaga telefoniga seotud üldine viga
	FAULT_NOT_MOBILE_ID_CLIENT ("login.mobile_id.error.301"), //Pole Mobiil-ID kasutaja
	FAULT_INVALID_CERTIFICATE ("login.mobile_id.error.302"), //Kasutaja sertifikaat ei kehti (OCSP vastus REVOKED)
	FAULT_UNKNOWN_CERTIFICATE ("login.mobile_id.error.303"); //Kasutaja sertifikaadi olek teadmata (OCSP vastus UNKNOWN)

	private String messageCode;

	private MobileId(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessageCode() {
		return messageCode;
	}
	
	public static MobileId valueOf(MobileIdStatus status){
		return valueOf("STATUS_" + status.name());
	}

	public static MobileId valueOf(MobileIdFault fault){
		return valueOf("FAULT_" + fault.name());
	}
}
