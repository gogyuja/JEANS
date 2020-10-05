<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8" />
    <title>로그인</title>
    <link rel="stylesheet" href="static/css/login.css" />
    <link rel="stylesheet" href="static/css/slideShow.css" />
    <jsp:include page="libsStyles.jsp" flush="false" />
</head>

<body>

    <!--/*여기는 맨 위에 있는 바 부분*/ -->
    <img src="/static/images/imam-muhaimin-kHivKyO8E8U-unsplash.jpg" class="login_background" />
    <div class="webview">
        <jsp:include page="header.jsp" flush="false" />
        <!-- End of Header-->

        <div class="container">
            <div class="container_inner">
                <form name="loginForm" class="container_inner_inner">
                    <div class="join_title">
                        <span class="join_title blue">JEANS</span>
                        <span class="join_title black">에 로그인!</span>
                        <div class="join_title sub"> 환영합니다.</div>
                    </div>
                    <input type="text" class="main_idInput" placeholder="ID" name="userid"></input>
                    <input type="password" class="main_pwInput" placeholder="P/W" name="password"></input>
                    <div class="btnZone">
                        <button class="login_formBtn tologin" type="button" onClick="userLogin()">LOGIN</button>
                        <button class="login_formBtn tojoin" type="button" onclick="location.href='joinUser'"
                            formaction="joinUser">JOIN</button>
                    </div>
                    <div class="main_bar">
                        <hr>
                        </hr>
                    </div>
                    <div style="height: 40px;">

                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="libsScript.jsp" flush="false" />
    <script src="/static/js/loginUser/login.js"></script>
</body>

</html>