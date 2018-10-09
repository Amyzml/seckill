<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--
  Created by IntelliJ IDEA.
  User: zml85
  Date: 2017/12/5
  Time: 11:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>商品抢购</title>
</head>
<link rel="stylesheet" href="/static/css/bootstrap.css">
<body>

<a href="/product/add" class="btn btn-success navbar-btn pull-right"><i class="fa fa-plus"></i> 添加商品</a>

    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">商品抢购</h3>
        </div>
        <div class="panel-body">
            <c:forEach items="${productList}" var="product">
                <div class="row">
                    <div class="col-md-2">
                        <img src="http://ozp6wbrw7.bkt.clouddn.com/${product.productImage}?imageView2/1/w/200/h/200/q/75">
                    </div>

                    <div class="col-md-10">
                        <h3><a href="/product/${product.id}">${product.productName}</a> </h3>
                        <h4 style="margin-top: 25px" class="text-danger">抢购价: ￥${product.productPrice}</h4>
                        <h4 style="margin-top: 35px">开始时间：<fmt:formatDate value="${product.startTime}" pattern="YYYY-MM-dd HH:mm"/></h4>
                    </div>
                </div>
            </c:forEach>
        </div>


    </div>

<script>
    function strUnique(){
        var str = "abc, abcd, abc, abcde, abcd, abcde";
        var ret = [];
        str.replace(/[^,]+/g, function($1, $2){
            (str.indexOf($1) == $2) && ret.push($1);
        });
        alert(ret);
        return ret;
    }

    strUnique();
    console.log(strUnique() + " .... ")

</script>
</body>
</html>
