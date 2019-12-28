$(function() {
	/*商品列表，鼠标移入时加阴影、移出移除阴影*/
	$(".goods-panel").hover(function() {
		$(this).css("box-shadow", "0px 0px 8px #888888");
	}, function() {
		$(this).css("box-shadow", "");
	})
	//加入收藏时，♥的实心空心切换
	$(".add-fav").toggle(function() {
		$(this).html("<span class='fa fa-heart-o'></span>加入收藏");
	}, function() {
		$(this).html("<span class='fa fa-heart'></span>取消收藏");
	})
})