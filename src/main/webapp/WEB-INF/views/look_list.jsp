<!-- 정적인 메인페이지 -->

<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


-->
<html>
  <head>
    <meta charset="UTF-8"/>
    <title>look_list</title>
      <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
        <link rel="stylesheet" href="static/css/look_list.css"/>
        <link rel="stylesheet" href="static/css/look_list_Look.css"/>
        <link rel="stylesheet" href="static/css/jeans_header_.css"/>
<%--      <script type="text/javascript" src="/static/js/mainScroll.js"></script>--%>

      <script>

              //백오브 캐시 있으면 다시 새로고침 이거 새로고침 안하고 조회수 부분만 바꾸도록 수정해야됨
              window.onpageshow = function(event) {
                  if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
                      window.location.reload(); //새로고침 다시 불러오기
                  }
              };

              $(document).ready(function() {

                  $(window).scroll(function () {

                      // 현재 위치의 높이
                      //var curHeight = $(window).height() + $(window).scrollTop();
                      var curHeight = $(window).scrollTop();
                      // 문서의 높이
                      var docHeight = $(document).height();
                      // 어느 정도 조건이 만족하면 내용 생성
                      if (curHeight > docHeight - 1200) {
                          // $('<div class="main"></div>').appendTo('body');

                            $('<h1>추가 <h1>').appendTo('body');

                      }
                  });
              });
          </script>
  </head>
  <body>
  <div class="webview">
  <!--/*여기는 맨 위에 있는 바 부분*/ -->
  <div class="jeans_root">
      <div class="jeans_header">
          <div class="search_left"></div>
          <div class="search_logo">
              <img src="static/images/search.jpg" alt="search" height="30" width="30" />
          </div>
          <div class="search_input"  style="margin-top: 20px">
              <form>
                  <input type="text" class = "search_text"/>
              </form>
          </div>
          <div class="logo_left"></div>
          <div class="logo" >
              <a class="header_a" href="main"><img src="static/images/logo.png" alt="logo" height="30" width="71" /></a>
          </div>

          <c:set var="userid" value="${sessionScope.userid}"/>
          <c:choose>
          <c:when test="${userid != null}">
              <a class="header_a" href="look_write"><div class="logo_right"> <span class="look_write">Look Write</span></div></a>
          </c:when>
              <c:otherwise>
                  <a class="header_a" href="joinUser"><div class="logo_right"><span class="look_write">회원가입</span></div></a>
              </c:otherwise>
          </c:choose>

          <div class="my_info">
              <div>
                  <c:set var="userid" value="${sessionScope.userid}"/>
                  <c:if test="${userid != null}">
                      <img src="static/images/mypicture.png" alt="pitcture" height="35" width="40" />
                  </c:if>
              </div>
              <span class="user_id"><c:out value="${sessionScope.usernickname}"></c:out></span>
          </div>

          <div class="logout_left"></div>

          <c:set var="userid" value="${sessionScope.userid}"/>
          <c:choose>
              <c:when test="${userid != null}">
                  <a class="header_a" href="logout"><div class="logout">logout</div></a>
              </c:when>

              <c:otherwise>
                  <a class="header_a" href="loginUser"><div class="login">login</div></a>
              </c:otherwise>
          </c:choose>

          <div class="logout_right"></div>
      </div>
  </div>
  <!-- /*여기부터가 본문*/  -->

  <div class="header_space" style="width: 60px"></div>

        <c:forEach items="${list}" var="dto" begin="0" end="3" step="1">
            <%-- item list 받을때 사용  --%>
      <a class="look_view_a"  href="view?look_num=${dto.look_num}">
        <div class="main">

              <div claas="main_container">

                <ul class="main_look_item">
                  <li id=1>
                    <div class="is_body" >
                      <!-- 헤더-->
                      <div class="my_img">
                          <img src="static/images/mypicture.png" alt="search" height="50" width="60" />
                        </div>
                      <div class="name">
                        <ul class="look_header_ul">
                          <li class="look_header_li" style="width: auto">
                              <span class="user_name" style="width: auto">${dto.nickname}</span>
                          </li>
                          <li class="look_header_li" style="width: auto; "></li>
                          <li class="look_header_li"
                              style="width: fit-content; text-align: right; float: right; font-size: 15px; font-weight: bold">
                               <span id="look_date">${dto.look_date}</span>
                          </li>
                        </ul>

                      </div>
                      <div class="title" >${dto.title}</div>

                      <!-- 본문-->
                      <div class="look_img">
                          <div class="look_img_in">
                            <img src="static/images/1.JPG" alt="look_image" class= "look_img_file"/>
                          </div>
                        </div>

                        <div class="look_textarea_space">
                          <form class="textarea_form" >
                            <textarea style="background-color: #F6F6F6" class = "look_textarea" placeholder="${dto.tag}"></textarea>
                          </form>
                        </div>

                        <!-- 푸터-->
                        <ul class="look_footer_ul">
                          <li class="look_footer_li">
                            <div class ="like_img_box">
                              <img src="static/images/heart.svg" alt="heart_image" class="like_img" />
                            </div>
                          </li>
                          <li class="look_footer_li">
                            <div class = "like_number">
                              <span>10.5K</span>
                            <%--  좋아요 개발해야됨 --%>
                            </div>
                          </li>
                          <li class="look_footer_li" style="width: 25px;"></li>
                          <li class="look_footer_li">
                            <div class= "count_img_box">
                              <img src="static/images/board_view_icon.svg" alt="board_view_icon" class="count_img"/>
                            </div>
                          </li>
                          <li class="look_footer_li">
                            <div class = "count_number">
                              <span>${dto.count}</span>
                            </div>

                        </ul>
                       <div class="space_end"></div>
                    </div>
                  </li>
                </ul>

                <!-- 1페이지에 8개의 룩 정적인 목록으로 띄워줌-->


            </div>

          </div>
        </a>
  </div>

    </c:forEach>
  </div>
  </body>
</html>