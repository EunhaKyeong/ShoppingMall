var categoryCode = $(location).attr('pathname').slice(-1);	//현재 페이지 카테고리 코드
var totalPage = Math.ceil($.cookie("productCnt")/6);	//총 페이지 ex)의류 카테고리에 10개의 상품이 있다면 총 2 페이지가 필요하다.
var buttonHtml = "";
var data;

//버튼 html 생성하는 함수
function createButton(page) {
	var turn = Math.ceil(page/5);	//1페이지~5페이지 : 1턴, 6페이지~10페이지 : 2턴 ...
	buttonHtml = '';	//button관련 html 코드
	
	if (turn!==1) {	//1턴이 아니면 이전버튼 추가
		buttonHtml = '<button id=\"prevButton\" type=\"button\" class=\"btn btn-light\">이전</button>';
	}
	
	if (totalPage>5*turn) {	//해당 턴의 마지막 페이지가(2턴에서 마지막 페이지는 10페이지) 해당 카테고리의 최종 페이지보다 작으면
		for (var p=turn*5-4; p<=5*turn; p++) {
			buttonHtml += '<button type=\"button\" class=\"btn btn-light pageButton\" >' + p + '</button>';
		}
		buttonHtml += '<button id=\"nextButton\" type=\"button\" class=\"btn btn-light\">다음</button>';	//다음 버튼 추가
	} else {	//해당 턴의 마지막 페이지가(2턴에서 마지막 페이지는 10페이지) 해당 카테고리의 최종 페이지이면
		for (var p=turn*5-4; p<=totalPage; p++) {
			buttonHtml += '<button type=\"button\" class=\"btn btn-light pageButton\" >' + p + '</button>';	//최종 페이지까지만 버튼 생성
		}
	}
	
	$("#pageButtonGroup").html(buttonHtml);	//Button div에 html 코드 추가
}

$(window).ready(function() {
	data = {"page":1};	//서버에 전달할 데이터
	
	createButton(1);	//버튼 html 생성하는 함수 호출
	$(".pageButton").first().css({background:"black", color:"white"});	//현재 페이지 버튼에 스타일 변경
	
	pagingProduct(data);	//axios를 활용해 서버에서 해당 페이지의 상품 정보를 가져오기 위한 함수 호출
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
	data = {"page":Number($(".pageButton").last().text())+1};	//서버에 전달할 데이터
	
	createButton(Number($(".pageButton").last().text())+1);	//버튼 html 생성하는 함수 호출
	$(".pageButton").first().css({background:"black", color:"white"});	//현재 페이지 버튼에 스타일 변경
	
	pagingProduct(data);	//axios를 활용해 서버에서 해당 페이지의 상품 정보를 가져오기 위한 함수 호출
});

//이전버튼 클릭했을 때
$(document).on("click", "#prevButton", function() {
	data = {"page":Number($(".pageButton").first().text())-1};	//서버에 전달할 데이터
	
	createButton(Number($(".pageButton").first().text())-1);	//버튼 html 생성하는 함수 호출
	$(".pageButton").last().css({background:"black", color:"white"});	//현재 페이지 버튼에 스타일 변경
	
	pagingProduct(data);		//axios를 활용해 서버에서 해당 페이지의 상품 정보를 가져오기 위한 함수 호출
});	

//axios를 활용해 서버에서 해당 페이지의 상품 정보를 가져와 상품 리스트 목록을 작성하는 함수
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