<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored = "false" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
	
<script
  src="https://code.jquery.com/jquery-3.5.1.js"
  integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc="
  crossorigin="anonymous"></script>
  
  <title>Shop Homepage - Start Bootstrap Template</title>

  <!-- Bootstrap core CSS -->
  <link href="<%=request.getContextPath() %>/resources/ProductList/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  
  <!-- Custom styles for this template -->
  <link href="<%=request.getContextPath() %>/resources/common/css/shop-homepage.css" rel="stylesheet">
  <link href="<%=request.getContextPath() %>/resources/common/css/common.css" rel="stylesheet">
  <link rel="stylesheet" href="<%=request.getContextPath() %>/resources/ProductList/css/productList.css">

</head>

<body>

  <jsp:include page="/WEB-INF/views/common/nav.jsp"></jsp:include>

  <!-- Page Content -->
  <div id="container" class="container">

    <div class="row">

      <div class="col-lg-3 nav-col-lg-3">

        <h1 class="my-4 nav-title">Shop Name</h1>
        <div id="categories" class="list-group">
	        <c:forEach items="${ categories }" var="category">
	          	<a href="/ProductList/${ category.category_code }" class="list-group-item">${ category.category_name }</a>
	        </c:forEach>
        </div>

      </div>
      <!-- /.col-lg-3 -->

      <div class="col-lg-9 main-col-lg-9">
        
        <!-- 상품 -->
        <div id="productRow" class="row">

        </div>
        <!-- 상품end -->
        
        <!-- /.row -->
		<div class="row">
			<div class="col-md-12 text-center">
				<div id="pageButtonGroup" class="btn-group me-2" role="group" aria-label="First group">
					
				</div>
			</div>
		</div>
		<!-- /.row -->
      </div>
      <!-- /.col-lg-9 -->

    </div>
    <!-- /.row -->

  </div>
  <!-- /.container -->

  <jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

  <!-- Bootstrap core JavaScript -->
  <script src="<%=request.getContextPath() %>/resources/ProductList/vendor/jquery/jquery.min.js"></script>
  <script src="<%=request.getContextPath() %>/resources/ProductList/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <!-- JavaScript -->
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
  <script src="<%=request.getContextPath() %>/resources/ProductList/js/ProductList.js"></script>
</body>

</html>