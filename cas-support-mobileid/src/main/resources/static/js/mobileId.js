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