package com.nortal.cas.support.mobileid;

import static org.apache.commons.lang.ArrayUtils.contains;

/**
 * Contains official mobile-ID test numbers. Taken from: https://www.openxades.org/dds_test_phone_numbers.html
 * 
 * Given phone numbers (ID-codes) are meant to test applications with Mobile-ID support (authentication or digital
 * signing) - no need for real phone or SIM-card. Turn your application against test-DigiDocService URL,
 * https://www.openxades.org:8443, with servicename parameter "Testimine" and use these phone numbers to get different
 * responses from the service, depending on the usage situation. Certificates related to test-numbers are issued from
 * TEST-SK certification authority (CA).
 * 
 * <pre>
 * Phone number 	Id Code 	Country 	Description 	Sample scenario: mobile authentication
 * 37200007 	14212128025 	EE 	Successful authentication, signing 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status USER_AUTHENTICATED
 * 37200002 	14212128020 	EE 	Mobile-ID functionality is not ready yet, try again after awhile 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status MID_NOT_READY
 * 37200001 	38002240211 	EE 	Mobile-ID is not activated 	MobileAuthenticate: errorcode 303
 * 37200009 	14212128027 	EE 	Mobile-ID user certificates are revoked or suspended 	MobileAuthenticate: errorcode 302
 * 37200004 	14212128022 	EE 	User canceled authentication 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status USER_CANCEL
 * 37200008 	14212128026 	EE 	Phone is not in coverage area 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status PHONE_ABSENT
 * 37200003 	14212128021 	EE 	Sending authentication request to phone failed 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status SENDING_ERROR
 * 37200006 	14212128024 	EE 	SIM application error 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status SIM_ERROR
 * 37200005 	14212128023 	EE 	Service techical error 	MobileAuthenticate: status OK, GetMobileAuthenticateStatus: status INTERNAL_ERROR
 * </pre>
 * 
 * @author Allar Saarnak
 */
public class MobileIdTestNumbers {
	
	private static final String[] testMobileNumbers = {
		"37200001", 
		"37200002", 
		"37200003", 
		"37200004", 
		"37200005", 
		"37200006", 
		"37200007", 
		"37200008", 
		"37200009"
	};
	private static final String[] testIdCodes = {
		"14212128020",
		"14212128021",
		"14212128022",
		"14212128023",
		"14212128024",
		"14212128025",
		"14212128026",
		"14212128027",
		"38002240211"
	};
	
	public static boolean isTestPhoneNumber(String phoneNumber){
		return contains(testMobileNumbers, phoneNumber);
	}

	public static boolean isTestIdCode(String idCode){
		return contains(testIdCodes, idCode);
	}

}
