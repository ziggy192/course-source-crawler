<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ziggy192
  Date: 11/4/18
  Time: 3:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search</title>
    <link href="css/main.css" rel="stylesheet" type="text/css"/>
    <link href="css/search.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="js/test.js"></script>
    <script type="text/javascript" src="js/utils.js"></script>

    <script type="text/javascript">


        function getArrowChar(order) {
            var arrow_up = '&#x21e7;';
            var arrow_down = '&#x21e9;';


            switch (order) {
                case 'down':
                    return arrow_down;
                default:
                    return arrow_up;

            }
        }

        function changeUrlAddress(urlPath) {
            var hostPath = "";
            window.history.replaceState("", "Title", urlPath);


        }

        var xslHttp;
        var xsltProcessor = new XSLTProcessor();

        function requestXslByXmlHttpRequest() {

            loadXml(location.origin + "/xsl/search.xsl"
                , function (xmlHttpObject)
            {
                xsdHttp = xmlHttpObject;
                render(xmlHttp, xsdHttp);
            }
        )
            ;
            xsltProcessor.importStylesheet(xslDoc);
        }

        function render(xmlHttp, xslHttp) {
            if (xmlHttp
                && xmlHttp.readyState == 4
                && xmlHttp.status == 200
                && xslHttp
                && xslHttp.readyState == 4
                && xslHttp.status == 200) {


                var xmlDoc = xmlHttp.responseXML;
                var xslDoc = xslHttp.responseXML;


                // debugger;
                xsltProcessor = new XSLTProcessor();
                xsltProcessor.importStylesheet(xslDoc);

                //used to add to html document
                var resultDocument = xsltProcessor.transformToFragment(xmlDoc, document);
                console.log(resultDocument);
                document.getElementById("xmlResult").appendChild(resultDocument);


                //set search result text
                document.getElementById("query_result").innerText = document.getElementById("query").value;


                //loadmore button
                var btnLoadmore = document.getElementById('btn_load_more');

                var elementCount = xmlHttp.responseXML.documentElement.childElementCount;

                if (elementCount == 0) {
                    //display no ressult:
                    document.getElementById("msg_no_result").style.display = 'block';
                }
                else {
                    document.getElementById("msg_no_result").style.display = 'none';
                }
                if (elementCount < 20) {
                    btnLoadmore.style.display = 'none';
                } else {
                    btnLoadmore.style.display = 'block';
                }
            }

        }

        function onFormSubmit(event) {
            window.scrollTo(0, 0);

            document.getElementById('query').value = document.getElementById('query_input').value;
            updateData();

            event.preventDefault();

        }

        function onBodyLoad() {


            // handle form submit event

            var form = document.getElementById("form_search");
            form.onsubmit = onFormSubmit;

            updateData();


        }

        function loadXml(url, handlerWhenSucess) {
            var xmlHttp = getXmlHttpObject();
            xmlHttp.open("GET", url, true);
            // xmlHttp.responseType = 'document';
            xmlHttp.onreadystatechange = function () {
                if (xmlHttp.readyState == 4) {
                    if (xmlHttp.status == 200) {

                        console.log('url=' + url + '\n response=' + xmlHttp.response);
                        console.log('url=' + url + '\n responsexml=' + xmlHttp.responseXML);
                        console.log('url=' + url + '\n resonseXMLCount=' + xmlHttp.responseXML.documentElement.childElementCount)
                        handlerWhenSucess(xmlHttp);

                    }
                    else {
                        console.error("Error on loadXml() | url=" + url + " | status=" + xmlHttp.statusText);
                    }
                }
            };
            xmlHttp.send(null);


        }

        function updateData() {
            //remove all children in table
            var xmlResult = document.getElementById("xmlResult");
            while (xmlResult.firstChild) {
                xmlResult.removeChild(xmlResult.firstChild);
            }


            currentPage = 1;


            sendRequest();


        }

        function getParamMap() {
            var query = document.getElementById("query").value;

            // var sort = document.getElementById("sort").value;
            var sort = getOrderStringFromAttributes();


            var categoryIdList = [];
            document.getElementsByName("categoryId").forEach(function (categoryElement, key) {
                if (categoryElement.checked) {
                    categoryIdList.push(categoryElement.value);
                }

            });
            var domainIdList = [];
            document.getElementsByName("domainId").forEach(function (domainElement, key) {
                if (domainElement.checked) {
                    domainIdList.push(domainElement.value);
                }
            });

            var paramMap = new Map();
            paramMap.set("query", query);
            if (sort && sort.length > 0) {

                paramMap.set("sort", sort);
            }

            if (categoryIdList && categoryIdList.length > 0) {

                paramMap.set("categoryId", categoryIdList);
            }
            if (domainIdList && domainIdList.length > 0) {

                paramMap.set("domainId", domainIdList);
            }
            paramMap.set("page", currentPage);
            return paramMap;
        }

        function sendRequest() {

            var paramMap = getParamMap();


            var xmlResultUrl = buildUrl(location.origin + "/resources/course", paramMap);


            //todo search param url
            var currentUrl = "search";


            console.log('currentUrl=' + currentUrl);
            var paramedUrl = buildUrl(currentUrl, paramMap);


            window.history.replaceState("", "", paramedUrl);
            console.log('pramedUrl=' + paramedUrl);

            console.log("sendRequest|url=" + xmlResultUrl);

            var xslHttp;
            var xmlHttp;
            loadXml(xmlResultUrl
                , function (xmlHttpObject) {
                    xmlHttp = xmlHttpObject;

                    render(xmlHttp, xslHttp);
                });


            loadXml(location.origin + "/xsl/search.xsl"
                , function (xmlHttpObject) {
                    xslHttp = xmlHttpObject;
                    render(xmlHttp, xslHttp);
                });


        }

        var currentPage = 1;

        function loadMore() {

            currentPage++;
            sendRequest();

        }


        function getOrderStringFromAttributes() {
            var sort = 'name';
            var order = 'up';
            // debugger;
            var orderElements = document.getElementsByClassName('order_column');
            for (var i = 0; i < orderElements.length; i++) {
                var orderElement = orderElements[i];
                if (orderElement.getAttribute('data-current') == 'true') {
                    sort = orderElement.getAttribute('data-column');
                    order = orderElement.getAttribute('data-order');
                    break;
                }
            }


            return sort + '-' + order;
        }


        function sortClicked(element) {


            //change sort eelment value based on element
            console.log(element);
            // debugger;
            var isCurrent = element.getAttribute('data-current');
            var order = element.getAttribute('data-order');
            var choosedColumn = element.getAttribute('data-column');
            // debugger;

            if (isCurrent && isCurrent == 'true') {
                if (order == 'up') {
                    order = 'down';
                } else {
                    order = 'up';
                }
            } else {
                order = 'up';
            }


            //set sort values to comlumn
            var orderElements = document.getElementsByClassName('order_column');
            for (var i = 0; i < orderElements.length; i++) {
                var orderElement = orderElements[i];
                // debugger;

                if (orderElement.getAttribute('data-column') == choosedColumn) {
                    orderElement.setAttribute('data-order', order);
                    orderElement.setAttribute('data-current', 'true');
                    orderElement.getElementsByClassName('order_sign')[0].innerHTML = getArrowChar(order);
                } else {
                    orderElement.setAttribute('data-order', 'up');
                    orderElement.setAttribute('data-current', 'false');

                    orderElement.getElementsByClassName('order_sign')[0].innerHTML = '';
                }
            }


            //submit request to server
            updateData();

        }


    </script>
</head>
<body onload="onBodyLoad()">

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
    <div class="main" style="flex-direction: column;">
        <h1>Search result for "<span id="query_result"></span>"</h1>

        <div class="search_wrapper">
            <div class="filter_col">
                <div class="single_filter">
                    <div class="filter_name">
                        <span>Domain</span>
                    </div>
                    <div class="filter_list">
                        <ul>
                            <c:forEach var="domain" items="${requestScope.domains}">
                                <li class="filter_item">
                                    <c:set scope="page" var="isChoosed" value="false"/>
                                    <c:forEach items="${requestScope.domainParams}" var="domainParam">
                                        <c:if test="${domainParam==domain.id}">
                                            <c:set scope="page" var="isChoosed" value="true"/>
                                        </c:if>
                                    </c:forEach>

                                    <input type="checkbox" name="domainId"
                                            <c:if test="${isChoosed!=null && isChoosed=='true'}">
                                                checked
                                            </c:if>
                                           value="${domain.id}"/>

                                    <span>${domain.name}</span>
                                </li>
                            </c:forEach>
                            <%----%>
                            <%--<li class="filter_item">--%>
                            <%--<input type="checkbox" name="domainId" value="3"/> <span>Domain 3</span>--%>
                            <%--</li>--%>
                        </ul>
                    </div>
                </div>
                <div class="single_filter">
                    <div class="filter_name">
                        <span>Category</span>
                    </div>
                    <div class="filter_list">
                        <ul>
                            <c:forEach var="category" items="${requestScope.categories}">
                                <li class="filter_item">
                                    <c:set scope="page" var="isChoosed" value="false"/>
                                    <c:forEach items="${requestScope.categoryParams}" var="categoryParam">
                                        <c:if test="${categoryParam==category.id}">

                                            <c:set scope="page" var="isChoosed" value="true"/>
                                        </c:if>
                                    </c:forEach>

                                    <input type="checkbox" name="categoryId"
                                            <c:if test="${isChoosed!=null && isChoosed=='true'}">
                                                checked
                                            </c:if>
                                           value="${category.id}"/>

                                    <span>${category.name}</span>
                                </li>
                            </c:forEach>
                            <%--<li class="filter_item">--%>
                            <%--<input type="checkbox" name="categoryId" value="12"/> <span>Category 12</span>--%>
                            <%--</li>--%>
                        </ul>
                    </div>
                </div>
                <div>
                    <button class="button_filter" type="button" onclick="updateData()">Filter</button>
                </div>
            </div>
            <div class="result_col">
                <!--<select name="sort" id="sort">-->
                <!--<option value="rating-up">Rating up</option>-->
                <!--<option value="rating-down">rating down</option>-->
                <!--<option value="name-up">name up</option>-->
                <!--<option value="name-down">name down</option>-->
                <!--<option value="cost-up">cost up</option>-->
                <!--<option value="cost-down">cost down</option>-->

                <!--</select>-->

                <table class="table_result">
                    <thead>
                    <tr>
                        <th class="col1 order_column" id="name_column" data-column="name" data-order="up"
                            data-current='true'
                            onclick="sortClicked(this);">
                            <span>Course Name</span>
                            <span class="order_sign">&#x21e7;</span>
                        </th>
                        <th class="col2 order_column" id="cost_column" data-column="cost" data-order="up"
                            data-current='false'
                            onclick="sortClicked(this);">
                            <span>Cost</span>
                            <span class="order_sign"></span></th>
                        <th class="col3 order_column" id="rating_column" data-column="rating" data-order="up"
                            data-current='false'
                            onclick="sortClicked(this);">
                            <span>Rating</span>
                            <span class="order_sign"></span>
                        </th>
                    </tr>
                    </thead>
                    <tbody id="xmlResult">
                    <%--<tr>--%>
                    <%--<td class="col1">--%>
                    <%--<div class="course_name">--%>
                    <%--<a href="https://edumall.vn/course/nghe-thuat-ban-hang-dinh-cao-voi-google-adwords-gia-toc">KỸ--%>
                    <%--THUẬT CHỈNH DÂY ĐÀN PIANO CƠ BẢN </a></div>--%>
                    <%--<div class="course_small_detail">--%>
                    <%--<img class="img_author_small"--%>
                    <%--src="//d1nzpkv5wwh1xf.cloudfront.net/320/k-5a3b209dce4b1465e6c2cf73/20180626-/vu-hoang-hiep-1.jpg">--%>
                    <%--<span class="author_name">  Vũ Hoàng Hiệp </span>--%>

                    <%--</div>--%>


                    <%--</td>--%>

                    <%--<td class="col2">799000</td>--%>
                    <%--<td class="col3">0</td>--%>
                    <%--</tr>--%>
                    </tbody>
                </table>
                <button id="btn_load_more" class="button" type="button" onclick="loadMore()">Load More</button>
                <div id="msg_no_result" class="message" style="display: none;"><h1>No result :(</h1></div>
            </div>

        </div>

        <!--<div class="check-box-div">-->

        <!--<input type="checkbox" name="categoryId" value="1"> Category 1 <br/>-->
        <!--<input type="checkbox" name="categoryId" value="2"> Category 2 <br/>-->
        <!--<input type="checkbox" name="categoryId" value="3"> Category 3 <br/>-->
        <!--<input type="checkbox" name="categoryId" value="4"> Category 4 <br/>-->
        <!--<input type="checkbox" name="categoryId" value="5"> Category 5 <br/>-->
        <!--</div>-->

        <!--<div class="check-box-div">-->

        <!--<input type="checkbox" name="domainId" value="3"> Domain 3 <br/>-->
        <!--<input type="checkbox" name="domainId" value="4"> Domain 4 <br/>-->

        <!--</div>-->


    </div>

</div>
</body>
</html>