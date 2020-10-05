<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
        contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <title>글 상세보기</title>
    <link rel="stylesheet" href="static/css/jeans_info_body.css" />
    <link rel="stylesheet" href="static/css/slideShow.css" />
    <jsp:include page="libsStyles.jsp" flush="false" />
</head>

<body>
    <div class="webview">
        <!--/*여기는 맨 위에 있는 바 부분*/ -->
        <jsp:include page="header.jsp" flush="false" />
        <!-- /*여기부터가 본문*/  -->

        <div class="body_root">
            <div class="body_look">
                <div class="my_img">
                    <a class="mypageLink" href="mypageUser/${view.nickname}">
                        <img src="displayMthumbnail/${view.fk_userid_user_userid}" />
                    </a>
                </div>

                <div class="name">${view.nickname}</div>

                <div class="write_date">${view.look_date}</div>

                <div class="title">${view.title}</div>

                <c:set var="viewId" value="${view.fk_userid_user_userid}" />

                <div class="delete">
                    <img src="static/images/pen.png" alt="search" height="25" width="25"
                        onclick="lookModify(${view.look_num},'${viewId}','${sessionScope.userid}')" />
                    <img src="static/images/delete.png" alt="search" height="25" width="25"
                        onclick="lookDelete(${view.look_num},'${viewId}','${sessionScope.userid}')" />
                </div>

                <!-- 슬라이드 쇼-->

                <div class="look_img_container">
                    <div class="look_img_viewport">
                        <div class="look_flick_camera" id="View_area">
                            <!-- preview == View_area -->
                            <!-- <div  class="flick_panel" style="left: 0px;">
                                    <img/>
                                </div> JS로 태그 생성-->
                        </div>

                        <button type="button" class="look_slide_button" id="look_slide_button_left" style="left: 0">
                            <img src="static/images/look_slide_icon_left.png" class="look_slide_button_icon" />
                        </button>
                        <button type="button" class="look_slide_button" id="look_slide_button_right" style="right: 0">
                            <img src="static/images/look_slide_icon_right.png" class="look_slide_button_icon" />
                        </button>
                    </div>
                </div>

                <div class="look_textarea_space">
                    <form class="textarea_form">
                        <textarea disabled class="view_textarea" placeholder="${view.memo}"></textarea>
                    </form>
                </div>

                <div class="like">
                    <img src="static/images/heart.png" alt="heart_image" class="heart_img"
                        onclick="likey(${view.look_num},'${sessionScope.userid}')" />
                    <div class="like_number">
                        ${view.good}
                    </div>

                </div>

                <div class="views">
                    <img src="static/images/board_view_icon.svg" alt="search" height="25" width="25"
                        class="view_icon" />
                    ${view.count}
                </div>

                <%--무드로 교체. 성일아 이부분 바꿔줘 무드 체크버튼 있더록--%>
                <div class="look_tag">
                    <c:forEach items="${mood}" var="mood">
                        ${mood.mood}
                    </c:forEach>
                </div>

            </div>

            <%--댓글입력부분--%>
            <form name="commentForm" class="writeComment">
                <c:if test="${sessionScope.userid != null}">
                    <%--로그인 해야 작성가능--%>
                    <div class="look_comment_wrap">
                        <div class="look_comment">
                            <div class="comment_left">
                                <img src="/displayMthumbnail/${sessionScope.userid}">
                            </div>
                            <div class="comment_center">
                                <div class="comment_center_header">
                                    <span id="js-other_people_name"
                                        class="other_people_name">${sessionScope.usernickname}</span>
                                </div>
                                <div class="comment_center_textarea">
                                    <span id="js-view_comment_textarea" disabled clas="view_comment_textarea"></span>
                                    <input type="hidden" name="comment_sender_id" value="${sessionScope.userid}" />
                                    <%--작성자 아이디--%>
                                    <input type="hidden" name="comment_sender_name"
                                        value="${sessionScope.usernickname}" /> <%--작성자 닉네임--%>
                                    <input type="hidden" name="fk_look_num_Look_look_num" value="${view.look_num}" />
                                    <%--게시글 기본키인 게시글숫자--%>
                                    <textarea class="view_comment_textarea" placeholder="댓글 내용을 입력하세요"
                                        name="comment_content"></textarea>
                                </div>
                                <div class="comment_center_footer">
                                    <button class="comment_button" type="button" onclick="commentWrite()">댓글</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
            </form>
            <ul class="comment-list" id="js-comment-list">
                <!-- JS 영역 -->
            </ul>
        </div>
    </div>


    <jsp:include page="libsScript.jsp" flush="false" />
    <script type="text/javascript" src="/static/js/look_info/lookDelete.js"></script>
    <script type="text/javascript" src="/static/js/look_info/look_info.js"></script>
    <script type="text/javascript" src="/static/js/look_info/child_comment.js"></script>
    <script type="text/javascript" src="/static/js/look_info/look_comment.js"></script>


    <%--댓글 내용출력 위해 현재글에 기본키를 댓글 자바스크립트에 넘김--%>
    <script>
        commentReady(${ view.look_num }, '${sessionScope.userid}', '${sessionScope.usernickname}')
    </script>
    <script>
        lookReady(${ view.look_num })
    </script>
</body>

</html>