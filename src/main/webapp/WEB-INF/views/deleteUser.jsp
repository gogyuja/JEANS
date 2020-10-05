<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="th" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8" />
    <title>회원탈퇴</title>
    <link rel="stylesheet" href="/static/css/deleteUser.css" />
    <link rel="stylesheet" href="/static/css/jeans_header_.css" />
    <jsp:include page="libsStyles.jsp" flush="false" />
</head>

<body>


    <!--/*여기는 맨 위에 있는 바 부분*/ -->
    <img src="/static/images/imam-muhaimin-kHivKyO8E8U-unsplash.jpg" class="background" />
    <div class="webview">
        <jsp:include page="header.jsp" flush="false" />
        <!-- /*여기부터가 본문*/  -->

        <div class="container" class="layout">
            <form class="deleteUserForm" id="deleteUser" name="deleteUser" method="get">
                <h2 class="container_title">회원탈퇴</h2>
                <div class="infoWrap">
                    <h3 class="no_blit">회원 탈퇴시 유의사항</h3>
                    <ul class="txt_list del">
                        <li>웹 고객 탈퇴시 개인정보와 홈페이지 게시판에 올리신 글을 즉시 삭제됩니다.</li>
                        <li>사이트 이용에 불편을 느끼셨다면 깊이 사과 드리며, 더 나은 서비스를 위한 조언을 메일로 보내주시면 보다 나은 서비스 제공을 위해 노력하겠습니다.</li>
                        <li>재가입에 대한 제재는 없으나 기존 아이디를 사용하실 수 없으며, 기존 아이디로 이용한 모든 정보를 보실 수 없습니다.</li>
                    </ul>
                    <div class="refund_wrap delete mt_20">
                        <div class="pass_box">
                            <div class="refund_select">
                                <div class="refund_div">
                                    <div class="refun_box">
                                        <label class="del_pass" for="gnrlMmberPssrd">비밀번호<font color="red">*</font>
                                            </label>
                                        <input type="password" id="chckPass" name="chckPass" maxlength="15">
                                    </div>
                                </div>
                                <div class="delete_box">
                                    <ul class="txt_list">
                                        <li>회원탈퇴를 하시려면 비밀번호를 재입력 하셔야 합니다.</li>
                                        <!-- <li>개인정보 완전삭제를 원하시는 경우 하단 체크박스에 체크를 한 뒤 탈퇴하기 버튼을 눌러주세요.</li> -->
                                    </ul>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="footer_wrap mt_40">
                        <button class="defBtn" type="button" onClick="userDelete()">탈퇴하기</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="libsScript.jsp" flush="false" />
    <script src="/static/js/deleteUser/deleteUser.js"></script>
</body>

</html>