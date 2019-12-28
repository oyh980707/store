$(function() {
	//用户点击登录按钮时
	$("#loginBtn").click(function() {
			//如果用户没有勾选自动登录
			if (!$("#auto").prop("checked")) {
				//清空cookie中的内容
				$.cookie("isAutoLogin", "false", {
					expire: -1
				});
				$.cookie("username", "", {
					expires: -1
				});
				$.cookie("password", "", {
					expires: -1
				});
			} else {
				//如果用户勾选了自动登录
				//获得用户名密码
				var vusername = $("#username").val();
				var vpassword = $("#password").val();
				//存入cookie
				//expires: 7 表示存储一个带7天期限的cookie
				$.cookie("isAutoLogin", "true", {
					expires: 7
				});
				$.cookie("username", vusername, {
					expires: 7
				});
				$.cookie("password", vpassword, {
					expires: 7
				});
			}
		})
		//页面加载时运行的代码
		//判断是否有自动登录的内容
	if ($.cookie("isAutoLogin") == "true") {
		//如果是自动登录，将cookie中的信息自动填写到用户名和密码框中
		$("#auto").prop("checked", true);
		$("#username").val($.cookie("username"));
		$("#password").val($.cookie("password"));
	}

});