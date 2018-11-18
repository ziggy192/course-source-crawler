<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ziggy192
  Date: 11/4/18
  Time: 3:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="course" value="${requestScope.course}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Course Detail</title>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <link rel="stylesheet" href="css/detail.css" type="text/css"/>
    <script>
        function downloadPDF(courseId) {
            var urlRequest = location.origin + "/resources/course/pdf/" + courseId;
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.responseType = "blob";
            xmlHttpRequest.open("GET", urlRequest, true);
            xmlHttpRequest.onreadystatechange = function (event) {
                if (xmlHttpRequest.readyState == 4) {
                    if (xmlHttpRequest.status == 200) {
                        var response = event.target.response;
                        var file = new Blob([response], {type: "application/pdf"});

                        var fileUrl = window.URL.createObjectURL(file);

                        var a = document.createElement("a");
                        a.style = "display: none";
                        document.body.appendChild(a);
                        //Create a DOMString representing the blob
                        //and point the link element towards it
                        a.href = fileUrl;
                        a.target = '_blank';
                        a.download = 'courseDetail.pdf';
                        a.click();




                    } else {
                        console.error("Error on downloadPDF() | url=" + urlRequest + " | status=" + xmlHttp.statusText);
                    }
                }
            };
            xmlHttpRequest.send(null);
        }
    </script>
</head>
<body>


<div class="wrapper">
    <div class="header fixed_header">
        <div class="app_name">
            <a href="search">
                <span>COURSE SOURCE</span>
            </a>
        </div>
        <div class="search_bar">
            <form id="form_search" class="form_search" action="search" autocomplete="off" method="get">

                <input id="query_input" class="input_search" name="query" type="text"
                       placeholder="Search for courses.."
                       value="${param.query}"/>
                <input id="query" name="query" type="hidden" value="${param.query}"/>
            </form>
        </div>

    </div>
    <div class="main">
        <div class="floating_content_wrapper">


            <div class="img_course_in_floating">
                <!--todo check if have VideoURL or not-->
                <c:choose>
                    <c:when test="${course.previewVideoUrl != null && !course.previewVideoUrl.isEmpty()}">
                        <iframe src="${course.previewVideoUrl}"
                                allowfullscreen frameborder="0" height="100%" width="100%">
                        </iframe>
                    </c:when>
                    <c:otherwise>
                        <img src="${course.imageUrl}">
                    </c:otherwise>
                </c:choose>

                <!--<img src="//d303ny97lru840.cloudfront.net/k-57b67684047c99584fc4a66e/20170605-/slide1.png"/>-->

            </div>

            <div class="floating_content_detail">
                <div class="btn_wrapper">
                    <a class="btn_green" href="${course.sourceUrl}" target="_blank">
                        Go to source
                    </a>
                </div>
                <div class="props">
                    <div class="prop_item">
                        <span class="pull_left bold">Domain</span>
                        <span class="pull_right">${requestScope.domain.name}</span>
                    </div>
                    <div class="prop_item">
                        <span class="pull_left bold">Category</span>
                        <span class="pull_right">${requestScope.category.name}</span>
                    </div>
                    <div class="prop_item">
                        <span class="pull_left bold">$ Cost</span>
                        <span class="pull_right">${course.cost}</span>
                    </div>
                    <div class="prop_item">
                        <span class="pull_left bold">Video length</span>
                        <span class="pull_right">${requestScope.durationString}</span>
                    </div>
                    <div class="prop_item">
                        <span class="pull_left bold">Rating</span>
                        <span class="pull_right">${course.rating}</span>
                    </div>
                    <div class="prop_item">
                        <span class="pull_left bold">Rating number </span>
                        <span class="pull_right">${course.ratingNumber}</span>
                    </div>

                </div>
                <div class="btn_wrapper">
                    <button class="button blue" type="button" onclick="downloadPDF(${course.id})">Download PDF</button>
                </div>

            </div>
        </div>
        <div class="content_course">
            <div class="course_intro">
                <span>${course.name}</span>
            </div>
            <div class="content_course_nav">

                <ul>
                    <li>
                        <a class="nav_item" href="#overview_tab">Overview</a>

                    </li>
                    <li>
                        <a class="nav_item" href="#syllabus_tab">Syllabus</a>

                    </li>
                    <li>
                        <a class="nav_item" href="#author_tab">Teacher</a>

                    </li>
                </ul>
            </div>

            <div id="overview_tab" class="content_item">
                <h1>Tổng quan khoá học</h1>
                <c:out value="${course.overviewDescription}" escapeXml="false"/>
            </div>
            <div id="syllabus_tab" class="content_item">
                <h1>Giáo trình </h1>

                <c:out value="${course.syllabus}" escapeXml="false"/>
            </div>
            <div id="author_tab" class="content_item">
                <h1>Giới thiệu giảng viên</h1>


                <div class="author_wrapper">

                    <div class="author_basic_info">

                        <div class="img_author">

                            <img src="${course.authorImageUrl}"/>
                        </div>

                        <div class="author_name">${course.author}</div>
                    </div>

                    <div class="author_description">
                        <c:out value="${course.authorDescription}" escapeXml="false"/>
                    </div>
                </div>

            </div>
        </div>
    </div>


</div>

</body>
</html>