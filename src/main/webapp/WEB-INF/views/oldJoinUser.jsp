<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="th" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>register</title>
    <link rel="stylesheet" href="static/css/register.css"/>
    <link rel="stylesheet" href="static/css/slideShow.css" />
</head>
<body>


<!--/*여기는 맨 위에 있는 바 부분*/ -->
<div class="webview">
      <jsp:include page="header.jsp" flush="false"/>
    <!-- /*여기부터가 본문*/  -->


    <div>
        <form:form modelAttribute="userDto">
            <div class="main"></div>
            <div class="main_title">회원가입</div>


            <div class="register_id">아이디</div>
            <input type="text" id="id" name="userid" class="register_idInput" placeholder="아이디를 입력하세요"></input>
            <div class="register_idError" id="id_check"></div>

            <!--<button type="button" class = "main_duplicate">중복확인</button>
            <div class="main_duplicateResult">사용가능</div>-->

            <div class="register_pw">비밀번호</div>
            <input type="password" id="pw" name="password" class="register_pwInput" placeholder="비밀번호를 입력하세요">
            <div class="register_pwError" id="pass_check"></div>

            <div class="register_nick">닉네임</div>
            <input type="text" id="nick" name="nickname" class="register_nickInput" placeholder="닉네임을 입력하세요">
            <div class="register_nickError" id="nick_check"></div>



            <div class="select_profile">
                <input type="file" id="input_profile" name="picture" accept=".jpg,.jpeg,.png,.bmp" onchange="previewImage(this,'View_area')"/>
            </div>
            <div class="upload_image" id="View_area">
                <!-- JS로 img태그 생성 -->
            </div>

            <div class="register_bodyType">공개/비공개</div>
            <div class="register_lock">공개</div>
            <div class="register_lockButton">
                <input type="radio" name="privacy" checked="checked" value="bodyOpen"/>
            </div>
            <div class="register_unlock">비공개</div>
            <div class="register_unlockButton">
                <input type="radio" name="privacy" value="bodyClose"/>
            </div>

            <div class="register_heightTitle">키</div>
            <input type="number" id="height" name="height" class="register_heightInt" ></input>
            <div class="register_heightCm">cm</div>
            <div class="register_heightError" id="height_check"></div>

            <div class="register_weightTitle">몸무게</div>
            <input type="number" id="weight" name="weight" class="register_weightInt" ></input>
            <div class="register_weightKg">Kg</div>
            <div class="register_weightError" id="weight_check"></div>

            <div class="register_genderTitle">성별</div>
            <div class="register_genderMale">남</div>
            <div class="register_genderMaleRadio">
                <input type="radio" type="number" name="sex" value="1"/>
            </div>

            <div class="register_genderFemale">여</div>
            <div class="register_genderFemaleRadio">
                <input type="radio" type="number" name="sex" value="0"/>
            </div>

            <div class="register_genderError" id="gender_check"></div>

            <!--
            <div class="main_memoTitle">메모</div>
            <input type="text" class="main_memo"></input>
            -->

            <div class="register_emailTitle">이메일</div>
            <div class="register_email">
                <input type="email" id="emailbox" name="email" />
            </div>
            <div class="register_emailError" id="email_check"></div>

            <!--<button class="register_save" type="submit" onclick="y" value="회원가입">Save</button>-->
            <button class="register_save" type="button" onClick="joinUser()">Save</button>
        </form:form>

    </div>
</div>

<script
        src="https://code.jquery.com/jquery-3.5.1.min.js"
        integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
        crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
<script src="/static/js/joinUser.js"/>
<script src="/static/js/id_nickname_session.js"></script>
<%--서버세션이 종료되어 자바스크립트 session 종료--%>
<c:set var="userid" value="${sessionScope.userid}"/>
<c:if test="${userid == null}">
    <script>sessionRemove()</script>
</c:if>
<%--header 부분 초기화--%>
<script>headerReset()</script>
<script src="static/js/ex2.js"></script>
</body>
</html>