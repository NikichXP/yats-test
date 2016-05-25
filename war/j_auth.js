$(function(){
		
	var sesTimeOut;
	var sID;
	
	var today = new Date();
	var dateObj = Date.parse(today);
	
	var authSesToken = getCookie("sesToken");
	var authSesTO = getCookie("sesTO");
	
	if (authSesToken != null)
		if(authSesTO - dateObj > 0)
			window.location = "workspace.html";
	
	
	
	$('#div-send-button').on('click', function(){
		
		var authData = {
			login: $('#login').val(),
			pass: $('#pass').val(),
		};
		
		$.ajax({
			type: 'GET',
			url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/auth',
			data: authData,
			success: function(resData) {
				sTO = resData.timeout;
				sID = resData.sessionId;
				setCookie("sesToken", sID);
				setCookie("sesTO", sTO);
				window.location = "workspace.html";
			},
			error: $('#div-res').html('<p>Authorisation faild</p>')
		});		

	});
	
});
