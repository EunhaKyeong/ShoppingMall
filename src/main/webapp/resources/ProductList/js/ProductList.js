var categoryCode = $(location).attr('pathname').slice(-1);	//현재 페이지 카테고리 코드
var totalPage = Math.ceil($.cookie("productCnt")/6);	//총 페이지 ex)의류 카테고리에 10개의 상품이 있다면 총 2 페이지가 필요하다.
var buttonHtml = "";
var data;

$(window).ready(function() {
	data = {"page":1};	//서버에 전달할 데이터
	
	if (totalPage>5) {
		for (var page=1; page<=5; page++) {
			buttonHtml += "<button type=\"button\" class=\"btn btn-light pageButton\" index=\"" + page + "\">" + page + "</button>";
		}
		buttonHtml += "<button id=\"nextButton\" type=\"button\" class=\"btn btn-light\">다음</button>";
	} else {
		for (var page=1; page<=totalPage; page++) {
			buttonHtml += "<button type=\"button\" class=\"btn btn-light pageButton\" index=\"" + page + "\">" + page + "</button>";
		}
	}
	$("#pageButtonGroup").html(buttonHtml);
	$(".pageButton").first().css({background:"black", color:"white"});
	
	pagingProduct(data);
});

//페이지버튼 클릭했을 때
$(document).on("click", ".pageButton", function() {
	$(".pageButton").css({background:"#F8F9FA", color:"black"});
	$(this).css({background:"black", color:"white"});
	
	data = {"page":Number($(this).text())};
	pagingProduct(data);
});

//다음페이지 클릭했을 때
$(document).on("click", "#nextButton", function() {
	data = {"page":Number($(".pageButton").last().text())+1};
				
	buttonHtml = "<button id=\"prevButton\" type=\"button\" class=\"btn btn-light\">이전</button>";
	if (totalPage>=data.page+5) {
		for (var page=data.page; page<data.page+5; page++) {
			buttonHtml += "<button type=\"button\" class=\"btn btn-light pageButton\" index=\"" + page + "\">" + page + "</button>";
		}
		buttonHtml += "<button id=\"nextButton\" type=\"button\" class=\"btn btn-light\">다음</button>";
	} else {
		for (var page=data.page; page<=totalPage; page++) {
			buttonHtml += "<button type=\"button\" class=\"btn btn-light pageButton\" index=\"" + page + "\">" + page + "</button>";
		}
	}
	$("#pageButtonGroup").html(buttonHtml);
	$(".pageButton").first().css({background:"black", color:"white"});
	
	pagingProduct(data);
});

//이전버튼 클릭했을 때
$(document).on("click", "#prevButton", function() {
	data = {"page":Number($(".pageButton").first().text())-1};
				
	buttonHtml = "";
	if (data.page-5!=0) {
		buttonHtml = "<button id=\"prevButton\" type=\"button\" class=\"btn btn-light\">이전</button>";
	}
	for (var page=data.page-4; page<=data.page; page++) {
		buttonHtml += "<button type=\"button\" class=\"btn btn-light pageButton\" index=\"" + page + "\">" + page + "</button>";
	}
	buttonHtml += "<button id=\"nextButton\" type=\"button\" class=\"btn btn-light\">다음</button>";
	$("#pageButtonGroup").html(buttonHtml);
	$(".pageButton").last().css({background:"black", color:"white"});
	
	pagingProduct(data);		
});

function pagingProduct(data) {
	axios.post("/ProductList/paging/" + categoryCode, data).then(function(res) {
		var html = "";
		var imageSrc = "";
		for (var i=0; i<res.data.length; i++) {
			var productStockHtml = "";
			if (res.data[i].thumbnail_url=='none.png') {
				imageSrc = "https://www.namdokorea.com/site/jeonnam/tour/images/noimage.gif";
			}
			else {
				imageSrc = res.data[i].thumbnail_url;
			}
			if (res.data[i].product_stock==0) {
				productStockHtml = "<p class=\"card-text\" style=\"color:red\"> 품절된 상품입니다. </p>";
			}
			html += "<div class=\"col-lg-4 col-md-6 mb-4\">";
			html += "<div class=\"card h-100\">";
			html += "<a href=\"/ProductDetail/" + res.data[i].product_code + "\"><img class=\"card-img-top\" src=" + imageSrc + " alt=\"\"></a>";
			html += "<div class=\"card-body\">";
			html += "<h4 class=\"card-title\">";
			html += "<a href=\"/ProductDetail/" + res.data[i].product_code + "\">" + res.data[i].product_name + "</a>";
			html += "</h4>";
			html += "<h5>" + res.data[i].product_price + "원</h5>";
			html += productStockHtml;
			html += "<p class=\"card-text\">" + res.data[i].product_manufacturer + "</p>";
			html += "</div>";
			html += "<div class=\"card-footer\">" + res.data[i].product_score + "</div>";
			html += "</div>";
			html += "</div>";
		}
				
		$("#productRow").html(html);
		
		//ProductList 페이지에서 뒤로가기 버튼을 눌렀을 때 이전 페이지의 history를 기억하기 위해 pushState() 사용.(Detail 페이지에서 뒤로가기 버튼 누른거랑 다른거)
		var buttonHtml = $("#pageButtonGroup").html();
		history.pushState({"productHtml":html, "buttonHtml":buttonHtml}, null, null);
		
		//상품 상세 정보에서 뒤로 가기 버튼
		$('#productRow').find('a').each(function(index) {
			$(this).click(function(e) {
				
			});
		});
		
	}).catch(function(error) {
		alert("오류가 발생했습니다!");
		if (error.response) {
	      	// 요청이 이루어졌으며 서버가 2xx의 범위를 벗어나는 상태 코드로 응답했습니다.
	      	console.log(error.response.data);
	      	console.log(error.response.status);
	      	console.log(error.response.headers);
	     } else if (error.request) {
	      	// 요청이 이루어 졌으나 응답을 받지 못했습니다.
	      	console.log(error.request);
	     } else {
	      	// 오류를 발생시킨 요청을 설정하는 중에 문제가 발생했습니다.
	      	console.log('Error', error.message);
	     }
	     console.log(error.config);
	});
}

//ProductList 페이지에서 뒤로가기버튼을 눌렀을 때(Detail 페이지에서 뒤로가기 버튼 누른거랑 다른거)
$(window).on('popstate',function(){
	data = history.state;
	$("#productRow").html(data.productHtml);
	$("#pageButtonGroup").html(data.buttonHtml);
});