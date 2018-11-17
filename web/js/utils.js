function buildUrl(baseUrl, paramMap) {
    var url = baseUrl + "?";

    paramMap.forEach(function (value, key, map) {
        url += key + "=";
        if (Array.isArray(value)) {
            var valueArray = value;
            for (var i = 0; i < valueArray.length; i++) {
                url += valueArray[i];
                if (i < valueArray.length - 1) {
                    url += ",";
                }
            }
        } else {
            url += value;
        }

        url += "&";

    });


    //remove '&' at the end of the url
    url = url.substring(0, url.length - 1);
    return url;


}

function getXmlHttpObject() {
    var xmlHttp = null;
    if (window.XMLHttpRequest) {
        //for modern browser
        xmlHttp = new XMLHttpRequest();
    } else {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlHttp;
}

