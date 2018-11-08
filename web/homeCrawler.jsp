<%--
  Created by IntelliJ IDEA.
  User: ziggy192
  Date: 11/8/18
  Time: 4:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Home Cralwer</title>
    <script type="text/javascript">
        function submit(btActionName) {
            var form = document.getElementById("mainForm");


            form.submit();
        }
    </script>
</head>
<body>
<h1>Home crawler</h1>
<form id="mainForm" action="" method="post">
    <h3>Domain List to Crawl</h3>
    <input id="cbEdumall" type="checkbox" value="edumall" name="domain"/>
    <label for="cbEdumall"> Edumall </label>
    <br/>
    <input id="cbUnica" type="checkbox" value="unical" name="domain">
    <label for="cbUnica"> Unica</label>
    <br/>

    <div>
        <button type="submit" name="btAction" value="start">Start</button>
        <button type="submit" name="btAction" value="pause">Pause</button>
        <button type="submit" name="btAction" value="resume">Resume</button>
        <button type="submit" name="btAction" value="stop">Stop</button>
    </div>

    <div>
        <h3><c:out value="${requestScope.message}" default="No message"/></h3>
    </div>

</form>
</body>
</html>
