/*
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
/**
 * @author Priit Liivak
 * @author Allar Saarnak
 */
MobileIdStatus = {
		cancelled: false
};

function startMobileIdAuthentication() {
	var phonenumber = $('#phonenumber').val();
	
	if (phonenumber) {
		phonenumber = jQuery.trim(phonenumber).replace(/\s+/g, '');
	}
	
	if (!phonenumber || !$.isNumeric(phonenumber)){
		$('#mIdNumberRequired').show();
	}
	else {
		$('#mIdNumberRequired').hide();
		$("#mIdMessageTextError").hide();
		$('#mobileIdCancel').show();
		startMobileIdAuth($('#messageAfterClick').val());
	}
	
	return false;
}

function hideMessage() {
	$('#mobileId_message').hide();
	$('#mobileId_actions').show();
}
function showMessage(content) {
	if (content!==undefined) {
		var dialogContainer = $('#mobileId_message');
		dialogContainer.find('#mIdMessageText').html(content);
		//Make sure that message is visible
		dialogContainer.show();
		$('#mobileId_actions').hide();
	}
}

function cancelMobileIdAuth() {
	MobileIdStatus.cancelled=true;
	hideMessage();
}

function startMobileIdAuth(initialMsg) {
	MobileIdStatus.cancelled = false;
	
	showMessage(initialMsg);

	$.ajax({
		type : "POST",
		url : "mobileIdAuth",
		data : {
			action : "m_id_login",
			phonenumber : $("#phonenumber").val()
		},
		success : mobileIdStartAuthenticationResponse,
		error : mobileIdError,
		dataType : "json",
		cache : false,
		global : false
	});

	return;
}

function mobileIdStartAuthenticationResponse(data, textStatus, XMLHttpRequest) {
	if(MobileIdStatus.cancelled){
		MobileIdStatus.cancelled=false;
		return;
	}
	showMessage(data.message);

	var attemptDelay = data.attemptDelay;
	
	if (data.startStatusCheck) {
		setTimeout(function() {
			$.ajax({
				type : "POST",
				url : "mobileIdAuth",
				data : {
					action : "m_id_status",
					phonenumber : $("#phonenumber").val(),
					sessionCode : data.sessionCode
				},
				success : mobileIdStartAuthenticationResponse,
				error : mobileIdError,
				dataType : "json",
				cache : false,
				global : false
			});
		}, attemptDelay * 1000);
	} else if (data.doCheckCertificate) {
		document.forms['mobileIdLoginForm'].submit();
	}
}

function mobileIdError(event, request, settings) {
	$('#mobileIdCancel').hide();
	$('#mIdMessageTextError').show();
	$('#mobileId_message').show();
	$('#mobileId_actions').show();
}