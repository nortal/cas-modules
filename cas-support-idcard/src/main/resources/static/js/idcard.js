function getEsteidCertificate(event) {
	event.preventDefault();
	var request = jQuery.ajax({
        url:      'idlogin',
        type:     'GET',
    });
	request.done(function(msg){
		$('#idCardLoginForm').submit();
	});
}