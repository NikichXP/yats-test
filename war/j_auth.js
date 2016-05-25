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
			url: 'https://beta-dot-nctc-yats.appspot.com/_ah/api/user/v1/auth',
			data: authData,
			success: function(resData) {
				if (resData.accessLevel != 'FAIL')
				{
					setCookie("sesToken", resData.sessionId);
					setCookie("sesTO", resData.timeout);
					setCookie("accessLevel", resData.accessLevel);
					location.reload();
				}
				else alert ('Вы ввели неправильные данные');
			},
		});		
	});
	
});
