<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: zml85
  Date: 2017/12/5
  Time: 11:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>添加商品</title>
    <link rel="stylesheet" href="/static/js/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="/static/js/editer/styles/simditor.css" />
    <link rel="stylesheet" href="/static/css/bootstrap.min.css">
</head>

<body>

    <div class="container">

        <form method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label>商品名称</label>
                <input type="text" name="productName" class="form-control">
            </div>
            <div class="form-group">
                <label>商品副标题</label>
                <input type="text" name="productTitle" class="form-control">
            </div>
            <div class="form-group">
                <label>图片</label>
                <input type="file" name="image" class="form-control">
            </div>
            <div class="form-group">
                <label>库存</label>
                <input type="text" name="productInventory" class="form-control">
            </div>
            <div class="form-group">
                <label>抢购价</label>
                <input type="text" name="productPrice" class="form-control">
            </div>
            <div class="form-group">
                <label>市场价</label>
                <input type="text" name="marketPrice" class="form-control">
            </div>
            <div class="form-group">
                <label>开始时间</label>
                <input type="text" name="sTime" class="form-control timePick">
            </div>
            <div class="form-group">
                <label>结束时间</label>
                <input type="text" name="eTime" class="form-control timePick">
            </div>
            <div class="form-group">
                <label>商品描述</label>
               <textarea name="productDesc" id="editor" class="form-control"></textarea>
            </div>

            <div class="form-group">
                <button class="btn btn-success">保存</button>
            </div>

        </form>
    </div>

    <script src="/static/js/jquery.min.js"></script>
    <script src="/static/js/bootstrap.min.js"></script>
    <script src="/static/js/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
    <script src="/static/js/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>
    <script src="/static/js/editer/scripts/module.js"></script>
    <script src="/static/js/editer/scripts/hotkeys.js"></script>
    <script src="/static/js/editer/scripts/uploader.js"></script>
    <script src="/static/js/editer/scripts/simditor.js"></script>

<script>
    $(function () {
        var timepicker = $('.timePick').datetimepicker({
            format: "yyyy-mm-dd hh:ii",
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true
        });
        var editor = new Simditor({
            textarea: $('#editor')
            //optional options
        });
    });
</script>
</body>
</html>
