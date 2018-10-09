<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: zml85
  Date: 2017/12/5
  Time: 16:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="/static/css/bootstrap.min.css">
    <style>
        .btn {
            margin-top: 35px;
            width: 400px;
        }
        .clock {
            margin-top: 40px;
            font-size: 24px;
        }

    </style>
</head>
<body>

    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <img src="http://ozp6wbrw7.bkt.clouddn.com/${product.productImage}?imageView2/1/w/200/h/200/q/75">
            </div>
            <div class="col-md-8">
                <h4>${product.productName}</h4>
                <p>${product.productTitle}</p>
               <h3 class="text-danger">抢购价：￥${product.productPrice}<small style="text-decoration:line-through">市场价：￥${product.marketPrice}</small></h3>

                <c:choose>
                    <c:when test="${product.productInventory == 0}">
                        <button class="btn btn-default btn-lg" disabled>已售完</button>
                    </c:when>
                    <c:when test="${product.end}">
                        <button class="btn btn-default btn-lg" disabled>已结束</button>
                    </c:when>
                    <c:when test="${product.start and not product.end}">
                        <button id="secKillBtn" class="btn btn-lg btn-danger">立即抢购</button>
                    </c:when>
                    <c:otherwise>
                        <button id="secKillBtn" class="btn btn-lg btn-danger" disabled>等待抢购</button>
                        <div class="clock">距离抢购时间：<span id="clock">xx分xx秒</span></div>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>
        <div class="row">
            <div class="col-md-offset-4 col-md-8">
                <div>
                    ${product.productDesc}
                </div>
            </div>
        </div>

    </div>

    <script src="/static/js/jquery.min.js"></script>
    <script src="/static/js/moment.js"></script>
    <script src="/static/js/jquery.countdown.min.js"></script>
    <script src="/static/js/layer/layer.js"></script>
    <script>
        $(function () {
            $("#clock").countdown(${product.startTimeTs},function(event) {
                $(this).html(event.strftime('%D 天 %H小时%M分钟%S秒'));
            }).on("finish.countdown",function () {
                $("#secKillBtn").text("立即抢购").removeAttr("disabled");
            });
        });


        $("#secKillBtn").click(function () {

            $.get("/product/seckill/${product.id}").done(function (resp) {

                if (resp.state == 'success'){
                    layer.msg("抢购成功");
                }else {
                    layer.msg(resp.message);
                }

            }).error(function () {
                    layer.msg("服务器异常，请稍后");
            });

        });



    </script>
</body>
</html>
