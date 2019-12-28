$(function() {
	//地址管理点击删除时的提示
	$(".add-del").click(function() {
			if (confirm("确定删除这个地址吗？")) {
				location.href = "address.html";
			}
		})
		//点击设置为默认地址时的效果
	$(".add-def").click(function() {
		$(".add-def").show();
		$(this).hide();
	})
	$(".add-def:eq(0)").hide();
})