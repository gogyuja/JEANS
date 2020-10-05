let look_num = 20000; //게시글 6개씩 불러 오기 위해 look_num 값넣는 변수 초기값 은  2000000000
let mainScrollTime = true; //스크롤 중복 방지 변수
let end = true //게시글 없을 경우 데이터 가져오지 않는 변수
var nearuser = new Array();

function setUserId (id){
    //로그인한 아이디 저장
    userId=id;

    $.ajax({
        url: "http://13.125.21.192:5000/id/"+userId,
        type: "GET", //데이터 전달방식
        success: function (result) {
            for(var i=0;i<result.near_user.length;i++){
             nearuser[i]=result.near_user[i];
            }
            //아이디 5개 받아서 배열에 넣음
            start(nearuser);
        },
        error: function (error) {
            //서버오류 500  권한없음  401
            if (error.status == 401) {
                swal('접근 권한이 없습니다', '', 'error');
            } else if (error.status == 500) {
                swal('서버 오류 관리자에게 문의 하세요', '', 'error');
            }
        }
    })

}

//on load html 이미지나 자바스크립트 링크가 다오고 실행됨
$(window).on('load', function() {

    $(window).scroll(function() { //스크롤 감지 이벤트

        let scroll = $(document).scrollTop(); //현재 스크롤 값
        let documentHeight = $(document).height(); //문서 전체높이
        let windowHeight = window.innerHeight; //윈도우 높이
        //윈도우 높이에 스크롤값을 계속더해서 문서 전체 길이에서 100 px 앞에 스크롤이 왔을때 데이터 불러옴
        if ((windowHeight + scroll) >= documentHeight - 100) {
            if (mainScrollTime == true && end == true) {
                start(nearuser);
            }
        }
    })
});

function start(nearuser) {

    //무한 스크롤 중복 방지
    mainScrollTime = false;

    $.ajax({
        url: "/preference/" + nearuser[0] + "/" +  nearuser[1] + "/" + nearuser[2]+ "/" + nearuser[3]+ "/" + nearuser[4]+ "/" +look_num,
        type: "GET",
        dataType: "json", //json 으로 받기
        success: function(result) {

            for (var i = 0; i < result.length; i++) {

                let html = "";
                html += `<div class=\"main\">`;
                html += '<div claas=\"main_container\">';
                html += '<ul class=\"main_look_item\">';
                html += '<li id=1>';
                html += '<div class=\"is_body\" >';
                html += '<!-- 헤더-->';
                html += `<div class="my_img" id="js-my_img">`;
                html += `<a class="mypageLink" href="mypageUser/${result[i].nickname}">`;
                html += `<img src=displayMthumbnail/${result[i].fk_userid_user_userid}>`;
                html += `</img>`;
                html += `</a>`;
                html += ' </div>';

                html += '<div class=\"name\">';
                html += '<span class=\"user_name\" style=\"width: auto\">' + result[i].nickname + '</span>';
                html += '<span class="look_date" id=\"look_date\">' + result[i].look_date + '</span>';
                html += ' </div>'; //<div class=\"name\">

                html += '<div class=\"title\" >' + result[i].title + '</div>';
                html += '<!-- 본문-->';
                html += ' <div class=\"look_img\">';
                html += `<a class="look_view_a"  href="look?look_num=${result[i].look_num}">`;
                html += '<div class=\"look_img_in\">';
                // html += '<img src=\"static/images/1.JPG\" alt=\"look_image\" class= \"look_img_file\"/>';
                html += '<img class= \"look_img_file\" src=\"displayLthumbnail/' + result[i].look_num + '\">';
                html += ' </div>';
                html += ' </div>';
                html += '<div class=\"look_textarea_space\">';
                html += '<form class=\"textarea_form\" >';
                html += '<textarea  class = \"view_look_textarea\" placeholder=\"' + result[i].memo + '\"></textarea>';
                html += '</form>';
                html += ' </div>';
                html += '<!-- 푸터-->';
                html += ' <ul class=\"look_footer_ul\">';
                html += '<li class=\"look_footer_li\">';
                html += '<div class =\"like_img_box\">';
                html += '<img src=\"static/images/heart.svg\" alt=\"heart_image\" class=\"like_img\" />';
                html += ' </div>';
                html += ' </li>';
                html += '<li class=\"look_footer_li\">';
                html += '<div class = \"like_number\">';
                html += '<span>' + result[i].good + '</span>';
                html += ' </div>';
                html += ' </li>';
                html += '<li class=\"look_footer_li\" style=\"width: 25px;\"></li>';
                html += ' <li class=\"look_footer_li\">';
                html += '<div class= \"count_img_box\">';
                html += '<img src=\"static/images/board_view_icon.svg\" alt=\"board_view_icon\" class=\"count_img\"/>';
                html += ' </div>';
                html += ' </li>';
                html += '<li class=\"look_footer_li\">';
                html += ' <div class = \"count_number\">';
                html += '<span>' + result[i].count + '</span>';
                html += ' </div>';
                html += '</ul>';
                html += ' </a>'; //<a class="look_view_a"
                html += ' <div class=\"space_end\"></div>';
                html += ' </div>';
                html += ' </li>';
                html += '</ul>';
                html += ' </div>';
                html += ' </div>';

                $(".body_root").append(html);
            }

            //다음 게시글 6개 가져 오기 위해 마지막 게시글 기본키 값 넘겨줌
            look_num = result[result.length - 1].look_num;

            setTimeout(function() {
                mainScrollTime = true;
            }, 400); //스크롤 이벤트 0.2초뒤 실행 중복방지 위해

        },
        error: function(error) {
            //서버오류 500  권한없음 401  찾는내용없음 400
            if (error.status == 500) {
                swal('서버오류', '', 'error');
            } else if (error.status == 404) {
                end = false;
                //가져올 게시글이 없어서 더이상 데이터를 가져오지 않게 바꿈
            }
        }
    });
}