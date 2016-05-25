$(function(){
	var count;	
	var $resDiv = $('#div-right');
	var dateObj = new Date();
	
	var authSesToken = getCookie("sesToken");
	var authSesTO = getCookie("sesTO");

	var ordersList = {};
	var tokenJson = {
		token: authSesToken,
	};
		
	
	if (authSesToken != null)
		if(authSesTO - dateObj < 0)
			window.location = "auth.html";

	//check if session ok every 60 secs
	setInterval(function() 
	{ 
		if (getCookie("sesToken") == null) window.location = "auth.html";
		else 
			$.ajax({
				type: 'GET',
				url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/checkToken',
				data: { token: getCookie("sesToken")},
				success: function(resData) { 
					if (resData.value == false) window.location = "auth.html";
				},
			});		
	}, 60000);
	
	//get user name	
	$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/getname',
		data: tokenJson,
		success: function(resData) {
			$('#p-greeting').append("<h3>Добро пожаловать, " + resData.name + "!</h3>");
		},	
	});	
	//dynamic table of orders
	$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/allOrders',
		data: tokenJson,
		success: function(resData) { 
			for (var i = 0; i < resData.items.length; i++)
			{
				$('#table-orders').append("<tr class='tr-order'>" +
						"<td>" + (i + 1) + "</td>" +
						"<td>" + resData.items[i].id + "</td>" +
						"<td>" + resData.items[i].clientName + "</td>" +
						"<td>" + resData.items[i].productName + "</td>" +
						"<td>" + resData.items[i].creatorName + "</td>" +
						"<td>" + resData.items[i].paid + "</td>" +
					"</tr>");	
			};
			$('#li-open-orders').append(" " + i);
			var k = 0;
			for (var i = 0; i < resData.items.length; i++)
				if (resData.items[i].donePaid == false) k++;
			$('#li-done-orders').append(" " + k);
		},
	});	
	//debug button to get token	
	$('#menu-showses').on('click', function(){
		$resDiv.append("TokenId = " + getCookie("sesToken") + "; tokenTO = " + getCookie("sesTO"));	
	});
	//log out
	$('#menu-logout').on('click', function(){
		deleteCookie("sesToken");	
		deleteCookie("sesTO");	
		window.location = "auth.html";
	});
	//hide order edit block
	$('#opacity').on('click', function(){
		$(this).css('display', 'none');	
		
		$('#order-edit-form').css('display', 'none');
		
		$('#order-edit-payment').css('display', 'none');
		
		$('#client-list').css('display', 'none');
		
		$('#product-list').css('display', 'none');
		
		$('#lists-container').css('display', 'none');
		
		$('#order-create').css('display', 'none');
		
		$('#client-menu').css('display', 'none');
		$('#client-menu').css('height', '322px');
		$('#client-menu-create').html("Добавить клиента");
		
		$('#order-create-file-upload').css('display', 'none');
		$('#order-create-file-upload').html("");
	});
	//hide lists block
	$('#lists-container').on('click', function(){
		$(this).css('display', 'none');	
		$('#client-list').css('display', 'none');
		$('#product-list').css('display', 'none');
		$('#lists-container').css('display', 'none');
	});
	//get client name from table
	$('#table-client-list').on("click", "td.td-client-list", function() {
		var chosenClientId = $(this).children('input.inner-client-input-1').val();
		$('#order-client-name').html($(this).html())
		$('#client-list').css('display', 'none');
		$('#lists-container').css('display', 'none');
		
		$('#input-order-client-id').val(chosenClientId);
	});
	//get product name from table
	$('#table-product-list').on("click", "td.td-product-list", function() {
		var chosenProduct = $(this).html();
		var chosenProductId = $(this).children('input.inner-product-input-1').val();
		var chosenProductPrice = $(this).children('input.inner-product-input-2').val();
		
		$('#order-product-name').html(chosenProduct);
		$('#order-product-price').html(chosenProductPrice);
		$('#input-order-product-id').val(chosenProductId);
		$('#product-list').css('display', 'none');
		$('#lists-container').css('display', 'none');
	});
	//show client names table		
	$('#order-client-name').on('click', function(){
		$('#table-client-list').html("<tbody></tbody>");
		
		$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/getMyClients',
		data: tokenJson,
		success: function(resData) {
			var imax = resData.items.length;
			var count = 0;
			for (var i = 0; i < resData.items.length; i++)
			{
				if (count == imax) break;
				$('#table-client-list tbody').append("<tr id='tr-client-list-" + (i + 1) + "'>");
				for (var j = 0; j < 5; j++)
				{
					$('#tr-client-list-' + (i + 1)).append(
					"<td class='td-client-list'>" + resData.items[count].name + " " + resData.items[count].surname + 
					"<input type='hidden' class='inner-client-input-1' value=" + resData.items[count].id + ">" +
					"</td>"
					);
					count++;
					if (count == imax) break;
				}
				$('#table-client-list').append("</tr>");				
			}
			$('#client-list').css('display', 'block');
			$('#lists-container').css('display', 'block');
		},
		});	
	});
	
	//show products table
	$('#order-product-name').on('click', function(){
		$('#table-product-list').html("<tbody></tbody>");
		
		$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/allProducts',
		data: tokenJson,
		success: function(resData) {
			var imax = resData.items.length;
			var count = 0;
			for (var i = 0; i < resData.items.length; i++)
			{
				if (count == imax) break;
				$('#table-product-list tbody').append("<tr id='tr-product-list-" + (i + 1) + "'>");
				for (var j = 0; j < 5; j++)
				{
					$('#tr-product-list-' + (i + 1)).append(
					"<td class='td-product-list'>" + 
					resData.items[count].title + 
					"<input type='hidden' class='inner-product-input-1' value=" + resData.items[count].id + ">" +
					"<input type='hidden' class='inner-product-input-2' value=" + resData.items[count].defaultPrice + ">" 
					+ "</td>"
					);
					count++;
					if (count == imax) break;
				}
				$('#table-product-list tbody').append("</tr>");				
			}
			$('#product-list').css('display', 'block');
			$('#lists-container').css('display', 'block');
		},
		});	
	});
	
	//open payment edit menu
	$('#table-orders').on("click", "tr.tr-order", function() {
		
		var rowIndex = $(this).index() - 1;
		
		$('#table-order-payment').html("<tbody>" + 
			"<tr id='table-order-payment-header'>" + 
			"<td>Клиент</td>" + 
			"<td>Продукт</td>" +
			"<td>Стоимость</td>" +
			"<td>Внесенная оплата</td>" +
			"<td>Осталось</td>" +
		"</tbody>");
		
		$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/allOrders',
		data: tokenJson,
		success: function(resData) { 
			$('#order-payment-text-block').html("<H2>Редактирование заказа #" + resData.items[rowIndex].id + "</H2>");
			
			$('#table-order-payment tbody').append("<tr>" + 
				"<td>" + resData.items[rowIndex].clientName + "</td>" + 
				"<td>" + resData.items[rowIndex].productName + "</td>" +
				"<td>" + resData.items[rowIndex].price + "</td>" + 
				"<td>" + resData.items[rowIndex].paid + "</td>" + 
				"<td>" + (resData.items[rowIndex].price - resData.items[rowIndex].paid) + "</td>" + 
			+ "</tr>");
			
			$('#input-order-Id').val(resData.items[rowIndex].id);
			$('#opacity').css('display', 'block');
			$('#order-edit-payment').css('display', 'block');
		},
		});	
	});
	//Send order payment
	$('#order-edit-send').on('click', function(){

		if ($('#input-paid').val() != "")	
			var payment = Math.round($('#input-paid').val() * 100) / 100;
		else var payment = 0;
		
		var paymentData = {
			token: authSesToken,
			orderid: $('#input-order-Id').val(),
			paid: payment,
		};
		
		$.ajax({
			type: 'GET',
			url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/editorder',
			data: paymentData,
			success: location.reload(),	
		});		

	});
	//Delete order
	$('#order-edit-del').on('click', function(){

		var delData = {
			token: authSesToken,
			orderid: $('#input-order-Id').val(),
		};
		
		$.ajax({
			type: 'GET',
			url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/cancelOrder',
			data: delData,
			success: location.reload(),	
		});		

	});
	
	//open order creation menu
	$('#menu-create-order').on('click', function() {
		
		$('#order-create-text-block').html("<H2>Создание заказа</H2>");
		
		$('#opacity').css('display', 'block');
		$('#order-create').css('display', 'block');
		$('#order-create-file-form').html("Приложить файлы");
	});
	
	//order creation file uploading form
	$('#order-create-file-form').on('click', function() {
		var k = 1;
		if ($('#order-create-file-upload').css('display') == 'block')
		{
			$('#order-create-file-upload').css('display', 'none');
			$('#order-create-file-upload').html("");
			$('#order-create-file-form').html("Приложить файлы");
		}
		else
		{
			/* for (var i = 1; i <= k; i++)
			{
				$('#order-create-file-upload').append("<br /><input type='file' id='myFile-" + i +"'>");
			}	 */
			$('#order-create-file-upload').css('display', 'block');
			$('#order-create-file-form').html("Cпрятать");
		}		
	});
	
	//send new order
	$('#my_form').on('submit', function(e) {
		var $that = $(this);
		
		var pID = $('#input-order-product-id').val();
		var cID = $('#input-order-client-id').val();
		var paidSum = $('#create-input-paid').val();
		
		if (!$.isNumeric(paidSum)) paidSum = 0;
		if (!$.isNumeric(pID)) alert ('Заполните обязательные поля');
		else if (!$.isNumeric(cID)) alert ('Заполните обязательные поля');
		else
		{
			var orderData = {
				token: authSesToken,
				productid: pID,
				clientid: cID,
				paid: paidSum,
			};
			
			$.ajax({
				type: 'GET',
				url: 'https://beta-dot-nctc-yats.appspot.com/_ah/api/order/v1/createorder',
				data: orderData,
			});	
			
			$.ajax({
				type: 'GET',
				url: 'https://beta-dot-nctc-yats.appspot.com/_ah/api/user/v1/getBlobPath',
				success: function (resData) {
					formData = new FormData($that.files[0]); 
					$.ajax({
						url: resData.value,
						type: 'POST',
						contentType: false, 
						processData: false, 
						data: formData,
					});
				},
			});
		};
	});
	$('#order-create-send').on('click', function() {
		
		var pID = $('#input-order-product-id').val();
		var cID = $('#input-order-client-id').val();
		var paidSum = $('#create-input-paid').val();
		
		if (!$.isNumeric(paidSum)) paidSum = 0;
		if (!$.isNumeric(pID)) alert ('Заполните обязательные поля');
		else if (!$.isNumeric(cID)) alert ('Заполните обязательные поля');
		else
		{
			var orderData = {
				token: authSesToken,
				productid: pID,
				clientid: cID,
				paid: paidSum,
			};
			
			$.ajax({
				type: 'GET',
				url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/order/v1/createorder',
				data: orderData,
				/* success: location.reload(),	 */
			});	
			
			$.ajax({
			type: 'GET',
			url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/getBlobPath',
			success: function(resData) { 
<<<<<<< HEAD
				var clData = new FormData();  
				
				clData.append('myFile', $('myFile-1[name=txtfilePath]').val());
=======
				var clData = new FormData();    
				clData.append('myFile', $('#myFile-1').val());
>>>>>>> parent of c9edbd0... das sas
				clData.append('token', getCookie("sesToken"));
				
				$.ajax({
					type: 'POST',
					url: resData.value,
					data: clData,
					processData: false,
					contentType: false,
					/* success: location.reload(),	 */
				});	
			},
			});	
		};	
			
	});
	
	//open client menu
	$('#menu-client-open').on("click", function() {
				
		$('#table-client-menu').html("<tbody></tbody>");
		$('#client-menu-text-block').html("<H2>Мои клиенты</H2>");
		$('#client-new').css('display', 'none');
		
		$.ajax({
		type: 'GET',
		url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/getMyClients',
		data: tokenJson,
		success: function(resData) {
			var imax = resData.items.length;
			var count = 0;
			for (var i = 0; i < resData.items.length; i++)
			{
				if (count == imax) break;
				$('#table-client-menu tbody').append("<tr id='tr-client-menu-" + (i + 1) + "'>");
				for (var j = 0; j < 5; j++)
				{
					$('#tr-client-menu-' + (i + 1)).append(
					"<td class='td-client-menu'>" + resData.items[count].name + " " + resData.items[count].surname + 
					"<input type='hidden' class='inner-client-menu-input-1' value=" + resData.items[count].id + ">" +
					"</td>"
					);
					count++;
					if (count == imax) break;
				}
				$('#table-client-menu').append("</tr>");				
			}
			$('#client-menu').css('display', 'block');
			$('#opacity').css('display', 'block');
		},
		});	
	});
	
	//client creation menu
	$('#client-menu-create').on('click', function() {
		
		$('#client-menu-create').html("Принять");
		
		if ($('#client-new').css('display') == 'block')
		{
			$('#client-new').css('display', 'none');
			$('#client-menu-create').html("Добавить клиента");
			$('#client-menu').css('height', '322px');
			
			var clData = new FormData();    
			clData.append('token', authSesToken);
			clData.append('mail', $('#client-new-email').val());
			clData.append('name', $('#client-new-name').val());
			clData.append('surname', $('#client-new-surname').val());
			clData.append('phone', $('#client-new-phone').val());
			clData.append('pass', '');
			
			$.ajax({
			type: 'POST',
			url: 'https://beta-dot-eduportal-1277.appspot.com/_ah/api/user/v1/createuser',
			data: clData,
			processData: false,
			contentType: false,
			success: location.reload(),	
			});	
		}
		else 
		{
			$('#client-new').css('display', 'block');
			$('#client-menu').css('height', '637px');
			$('#client-menu-dismiss').css('display', 'block');
		}
		
	});
	//cancel client creation
	$('#client-menu-dismiss').on('click', function() {

		$('#client-new').css('display', 'none');
		$('#client-menu-create').html("Добавить клиента");
		$('#client-menu').css('height', '322px');	
		$('#client-menu-dismiss').css('display', 'none');		
	});
});

function checkBool(data)
{
	if (data == true)
		return "Да";
	else return "Нет";
};

