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
    <link href="css/crawlerMain.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
    </script>
</head>
<body>
<div class="wrapper">

    <h1>Home crawler</h1>
    <form id="mainForm" action="" method="post">
        <div class="row_wrapper">

            <div class="list_wrapper col1">

                <h3>Domain List to Crawl</h3>
                <input id="cbEdumall" type="checkbox" value="edumall" name="domain"/>
                <label for="cbEdumall"> Edumall </label>
                <br/>
                <br/>
                <input id="cbUnica" type="checkbox" value="unica" name="domain">
                <label for="cbUnica"> Unica</label>
                <br/>
                <br/>
            </div>

            <div class="column_wrapper col2">
                <div class="button_list_wapper">
                    <button class="button green" type="submit" name="btAction" value="start">Start</button>
                    <button class="button red" type="submit" name="btAction" value="pause">Pause</button>
                    <button class="button blue" type="submit" name="btAction" value="resume">Resume</button>
                </div>

                <div>
                    <h3>
                        <h3><c:out value="${requestScope.message}" default="No message"/></h3>
                    </h3>
                </div>
            </div>

        </div>

    </form>

</div>
</body>
</html>
