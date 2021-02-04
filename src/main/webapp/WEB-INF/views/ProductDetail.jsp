<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored = "false" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>

<!DOCTYPE html>
<html>

<head>

  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
<script
  src="https://code.jquery.com/jquery-3.5.1.js"
  integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc="
  crossorigin="anonymous"></script>
  <script src="<%=request.getContextPath() %>/resources/ProductDetail/js/reply.js"></script>
        
  <title>Shop Item - Start Bootstrap Template</title>

  <!-- Bootstrap core CSS -->
  <link href="<%=request.getContextPath() %>/resources/ProductDetail/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Custom styles for this template -->
  <link href="<%=request.getContextPath() %>/resources/ProductDetail/css/shop-item.css" rel="stylesheet">

</head>

<body>

  <jsp:include page="/WEB-INF/views/common/nav.jsp"></jsp:include>

  <!-- Page Content -->
  <div class="container">

    <div class="row">

      <div class="col-lg-3">
        <h1 class="my-4">Shop Name</h1>
        <div class="list-group">
          <a href="#" class="list-group-item active">Category 1</a>
          <a href="#" class="list-group-item">Category 2</a>
          <a href="#" class="list-group-item">Category 3</a>
        </div>
      </div>
      <!-- /.col-lg-3 -->

      <div class="col-lg-9">

		<form role="form" method="post" autocomplete="off" enctype = "multipart/form-data">
		
		<input type="hidden" name="product_code" value="${ProductById.product_code}" />
		
        <div class="card mt-4">
           <c:if test="${ProductById.thumbnail_url != 'none.png'}">
              <img class="card-img-top" src="${ProductById.thumbnail_url}" alt=""></c:if>
           <c:if test="${ProductById.thumbnail_url == 'none.png'}">
              <img class="card-img-top" src="https://www.namdokorea.com/site/jeonnam/tour/images/noimage.gif" alt="https://www.namdokorea.com/site/jeonnam/tour/images/noimage.gif"></c:if>
          <div class="card-body">
            <h3 class="card-title">${ProductById.product_name }</h3>
            <h4>${ProductById.product_price }원</h4>
            <p class="card-text">제조사 : ${ProductById.product_manufacturer }</p>
            <p class="card-text">판매사 : ${ProductById.product_seller }</p>
            <p class="card-text">판매자 : ${ProductById.customerName }</p>
            <p class="card-text">포인트 : ${ProductById.product_point }</p>
            <p class="card-text">재고 : ${ProductById.product_stock }</p>
            <p class="card-text">카테고리 : ${ProductById.category_name }</p>
            <c:if test = "${AverageScore == 0}">
            	<span class = "test_warning">댓글로 평점을 남겨주세요!!</span></c:if>
            <c:if test = "${AverageScore == 1}">
            	<span class = "test_warning">평점 : &#9733;&#9734;&#9734;&#9734;&#9734;</span></c:if>
           	<c:if test = "${AverageScore == 2}">
           	  	<span class = "test_warning">평점 : &#9733;&#9733;&#9734;&#9734;&#9734;</span></c:if>
           	<c:if test = "${AverageScore == 3}">
           	    <span class = "test_warning">평점 : &#9733;&#9733;&#9733;&#9734;&#9734;</span></c:if>
            <c:if test = "${AverageScore == 4}">
              	<span class = "test_warning">평점 : &#9733;&#9733;&#9733;&#9733;&#9734;</span></c:if>
            <c:if test = "${AverageScore == 5}">
              	<span class = "test_warning">평점 : &#9733;&#9733;&#9733;&#9733;&#9733;</span></c:if>
            <c:if test = "${customerType == 2 && ProductById.customerName == customerName && ProductById.product_seller == CompanyName}">
            <div>
	            <button type="button" id="modify_Btn" class="btn btn-warning">수정</button>
				<button type="button" id="delete_Btn" class="btn btn-danger">삭제</button>
            </div>
            </c:if>
          </div>
        </div>
        <!-- /.card -->
        <!-- 상품end -->
        
        
        <script>
	        var formObj = $("form[role='form']");
	        
	        $("#modify_Btn").click(function(){
	         formObj.attr("action", "/ProductModify/${product_code}");
	         formObj.attr("method", "get")
	         formObj.submit();
	        });
	        
	        $("#delete_Btn").click(function(){    
	      	var con = confirm("정말로 삭제하시겠습니까?");
	      	
	      	if(con) {  
	         	formObj.attr("action", "/Delete");
	         	formObj.submit();}
	        });
        </script>
		</form>
		<!-- 댓글/대댓글 -->
        <div class="card card-outline-secondary my-4">
          <div class="card-header">
            Product Reviews
            <button id = "replyBtn">New Reply</button>
          </div>
          <div class="chat">
            <!-- ajax reply -->
          </div>
          <div class = "panel-footer"></div>
        </div>
        <!-- /.card -->
        
        <!-- Modal -->
      <div class="modal fade" id="modal" tabindex="-1" role="dialog"
        aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal"
                aria-hidden="true">&times;</button>
              <h4 class="modal-title" id="myModalLabel">REPLY MODAL</h4>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label>Reply</label> 
                <input class="form-control" name='review_comment' value='New Reply!!!!'>
              </div>      
              <div class="form-group">
                <label>Replyer</label> 
                <input class="form-control" name='customer_name' value='replyer'>
              </div>
              <div class="form-group">
                <label>DATE</label> 
                <input class="form-control" name='review_date' value='2018-01-01 13:13'>
              </div>
              <div class="form-group">
                <label>SCORE</label> 
                <input class="form-control" name='review_score' value='2018-01-01 13:13'>
              </div>
      
            </div>
			<div class="modal-footer">
			        <button id='modalModifyBtn' type="button" class="btn btn-warning">Modify</button>
			        <button id='modalRemoveBtn' type="button" class="btn btn-danger">Remove</button>
			        <button id='modalRegisterBtn' type="button" class="btn btn-primary">Register</button>
			        <button id='modalCloseBtn' type="button" class="btn btn-default">Close</button>
			      </div>          </div>
			          <!-- /.modal-content -->
			        </div>
			        <!-- /.modal-dialog -->
			      </div>
			      <!-- /.modal -->
        
        <script>
        
        $(document).ready(function() {
        	
        	var product_code = '<c:out value = "${ProductById.product_code}"/>';
        	var replyUL = $(".chat");
        	
        	showList(1);
        	
        	function showList(page) {
        		
        		replyService.getList({product_code : product_code, page : page || 1}, function(replyCount, list) {
        			
        			if(page == -1) {
        				pageNum = Math.ceil(replyCount/10.0);
        				showList(pageNum);
        				return;
        			}
        			
        			var str = "";
        			
        			if(list == null || list.length == 0) {
        				replyUL.html("");
        				
        				return;
        			}
        			
        			for(var i=0, len = list.length || 0; i<len; i++) {
        				str += "<p data-rno = '" + list[i].review_code + "'>" + list[i].review_comment + "</p>";
        				str += "<small class = 'review_text'>" + replyService.displayTime(list[i].review_date) + " posted by " + list[i].customer_name + " score : " + list[i].review_score + "</small><hr>";
        			}
        			replyUL.html(str);
        			showReplyPage(replyCount);
        		});
        	}
        	
        	var pageNum = 1;
        	var replyPageFooter = $(".panel-footer");
        	
        	function showReplyPage(ReplyCount) {
        		
        		var endNum = Math.ceil(pageNum / 10.0) * 10;
        		var startNum = endNum - 9;
        		
        		var prev = startNum != 1;
        		var next = false;
        		
        		if(endNum * 10 >= ReplyCount) {
        			endNum = Math.ceil(ReplyCount/10.0);
        		}
        		
        		if(endNum * 10 < ReplyCount) {
        			next = true;
        		}
        		
        		var str = "<ul class = 'pagination pull-right'>";
        		
        		if(prev) {
        			
        			str += "<li class = 'page-item'><a class = 'page-link' href = '" + (startNum -1) + "'>Previous</a></li>";
        		}
        		
        		for(var i = startNum; i<=endNum; i++) {
        			
        			var active = pageNum == i? "active" : "";
        			
        			str += "<li class = 'page-item " + active + "'><a class = 'page-link' href = '" + i + "'>" + i + "</a></li>";
        		}
        		
        		if(next) {
        			
        			str += "<li class = 'page-item'><a class = 'page-link' href = '" + (endNum + 1) + "'>Next</a></li>";
        		}
        		
        		str += "</ul></div>";
        		
        		replyPageFooter.html(str);
        	}
        	
        	replyPageFooter.on("click", "li a", function(e) {
        		
        		e.preventDefault();
        		
        		var targetPageNum = $(this).attr("href");
        		
        		pageNum = targetPageNum;
        		
        		showList(pageNum);
        	});
        	
        	var modal = $(".modal");
        	var modalInputReply = modal.find("input[name = 'review_comment']");
        	var modalInputReplyer = modal.find("input[name = 'customer_name']");
        	var modalInputReplyDate = modal.find("input[name = 'review_date']");
        	var modalInputReplyScore = modal.find("input[name = 'review_score']");
        	
        	var modalModifyBtn = $("#modalModifyBtn");
        	var modalRemoveBtn = $("#modalRemoveBtn");
        	var modalRegisterBtn = $("#modalRegisterBtn");
        	
        	var getOrderCode = '<c:out value = "${getOrderCode}"/>';
        	var getCustomerCode = '<c:out value = "${customerCode}"/>';
        	
        	console.log(getOrderCode);
        	console.log(getCustomerCode);
        	
        	$("#modalCloseBtn").on("click", function(e){
            	
            	modal.modal('hide');
            });
        	
        	$("#replyBtn").on("click", function(e) {

        			if(${customerCode != null}) { //로그인 여부
        				if(${customerType == 1}) { // 구매자타입
	            			if(${CustomerReply == 1}) { // 이사람이 물건을 산 사람인가
	            				if(${OrderCodeIsDone == 1}) {
		            				modal.find("input").val("");
		                        	modalInputReplyDate.closest("div").hide();
		                        	modalInputReplyer.val("${customerName}").attr("readonly", "readonly");
		                        	modal.find("button[id != 'modalCloseBtn']").hide();
		
		                        	modalRegisterBtn.show();
		                        		
		                        	$(".modal").modal("show");
	            				} else {
	            					confirm("배송을 받으신 후에 댓글을 남길 수 있습니다.");
	            				}
	            			} else {
	            				confirm("물건을 산 사람만 댓글을 남길 수 있습니다.");
	            			}
        				} else { //판매자 타입
        					if(${ProductById.customerName == customerName}) { //판매자 본인
        						modal.find("input").val("");
	                        	modalInputReplyDate.closest("div").hide();
	                        	modalInputReplyer.val("${customerName}").attr("readonly", "readonly");
	                        	modal.find("button[id != 'modalCloseBtn']").hide();
	
	                        	modalRegisterBtn.show();
	                        		
	                        	$(".modal").modal("show");
        					} else {
        						confirm("이 물건의 판매자가 아닙니다.");
        					}
        				}
            		} else {
            			var con = confirm("로그인이 필요합니다. 로그인페이지로 이동하시겠습니까?");
            			
            			if(con) {
            				location.href = "/login";
            			}
            		}
        	});
        	
        	$(".chat").on("click", "p", function(e) {
        		
        		var review_code = $(this).data("rno");
        		
        		if(${customerCode != null}) { //로그인 여부
    				if(${customerType == 1}) { // 구매자타입
            			if(${CustomerReply == 1}) { // 이사람이 물건을 산 사람인가
            				if(${OrderCodeIsDone == 1}) {
            					replyService.get(review_code, function(reply) {
            	        			
            	        			modalInputReply.val(reply.review_comment);
            	        			modalInputReplyer.val(reply.customer_name).attr("readonly", "readonly");
            	        			modalInputReplyDate.val(replyService.displayTime(reply.review_date)).attr("readonly", "readonly");
            	        			modalInputReplyScore.val(reply.review_score);
            	        			modal.data("review_code", reply.review_code);
            	        			
            	        			modal.find("button[id != 'modalCloseBtn']").hide();
            	        			modalModifyBtn.show();
            	        			modalRemoveBtn.show();
            	        			
            	        			$(".modal").modal("show");
            	        		});
            				}
            			}
    				} else { //판매자 타입
    					if(${ProductById.customerName == customerName}) { //판매자 본인
    						replyService.get(review_code, function(reply) {
    		        			
    		        			modalInputReply.val(reply.review_comment).attr("readonly", "readonly");
    		        			modalInputReplyer.val(reply.customer_name).attr("readonly", "readonly");
    		        			modalInputReplyDate.val(replyService.displayTime(reply.review_date)).attr("readonly", "readonly");
    		        			modalInputReplyScore.val(reply.review_score).attr("readonly", "readonly");;
    		        			modal.data("review_code", reply.review_code);
    		        			
    		        			modal.find("button[id != 'modalCloseBtn']").hide();
    		        			modalRemoveBtn.show();
    		        			
    		        			$(".modal").modal("show");
    		        		});
    					}
    				}
        		}
        	});
        	
        	modalModifyBtn.on("click", function(e) {
        		
        		var reply = {
        				
        				review_code : modal.data("review_code"),
        				review_comment : modalInputReply.val(),
        				review_score : modalInputReplyScore.val()
        		};
        		
        		replyService.update(reply, function(result) {
        			
        			alert(result);
        			modal.modal("hide");
        			showList(pageNum);
        		});
        	});
        	
        	modalRemoveBtn.on("click", function(e) {
        		
        		var review_code = modal.data("review_code");
        		
        		replyService.remove(review_code, function(result) {
        			
        			alert(result);
        			modal.modal("hide");
        			showList(pageNum);
        		});
        	});
        	
        	modalRegisterBtn.on("click", function(e) {
					
				var reply = {
					review_comment : modalInputReply.val(),
					review_score : modalInputReplyScore.val(),
					product_code : parseInt(product_code),
					order_code : getOrderCode,
					customer_code : getCustomerCode
				};
				
				        		
				replyService.add(reply, function(result) {
				        			
					alert(result);
					        			
					modal.find("input").val("");
					modal.modal("hide");
					        			
					showList(1);
				});
			});
		});
        
        
        
        </script>
		</div>
	
    </div>

  </div>
  <!-- /.container -->

  <jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
  
  <!-- Bootstrap core JavaScript -->
  <script src="<%=request.getContextPath() %>/resources/ProductDetail/vendor/jquery/jquery.min.js"></script>
  <script src="<%=request.getContextPath() %>/resources/ProductDetail/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>

</html>