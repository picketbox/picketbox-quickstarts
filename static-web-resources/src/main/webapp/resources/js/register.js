var getHost = function() {
	var port = '8080';
	var hname = window.location.hostname;

	if (hname.indexOf("rhcloud.com") == -1) {
		return 'http://' + hname + ":" + port + appContext;
	} else {
		return 'http://' + hname + appContext;
	}
};

$(document).ready(function() {

	if (!$('#register-btn')) {
		return;
	}
	
	$('#register-btn').click(function(e) {
		var jqxhr = $.ajax(appContext + '/services/register', {
			contentType: "application/json",
            dataType:'json',
            data:JSON.stringify({userName:$('#username').val(),password:$('#password').val(),confirmPassword:$('#confirmPassword').val(),
            	firstName:$('#firstName').val(),lastName:$('#lastName').val(),email:$('#email').val()}),
            type:'POST', 
            success:function (data) {
                if (data.status.indexOf('Success') > -1) {
                	alert("Registration Successful. Please login..");
                	window.location = getHost() + "/login.jsp";
                } else if (data.status.length == 0){
                	$('#register-msg').text("Registration failed. Try again ...");
                } else {
                	$('#register-msg').text(data.status);
                }
            },
			error : function(xhr, ajaxOptions, thrownError) {
				alert("Oops. An error occured when processing your request.");
			}
        });
        
		return false; // prevents submit of the form
	});
	
	$('#username').on('focusout', (function(e) {
		var jqxhr = $.ajax(appContext + '/services/checkUsername', {
			contentType : "application/json",
			dataType : 'json',
			data : JSON.stringify({
				userName : $('#username').val()
			}),
			type : 'POST',
			success : function(data) {
				if (data.status) {
					$('#register-msg').text(data.status);
				} else {
					$('#register-msg').text('');
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				alert("Oops. An error occured when processing your request.");
			}
		});
	}));
});