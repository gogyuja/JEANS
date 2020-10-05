//CSS클래스명 불러오기
const FLICK_PANEL = "flick_panel";
const LOOK_FLICK_CAMERA = "look_flick_camera";
const LOOK_IMG_FILE = "look_img_file";

//slide();
let slideList = 0
    , slideContents = 0
    , slideBtnNext = 0
    , slideBtnPrev = 0
    , slideLen = 0
    , slideWidth = 0
    , slideSpeed = 0
    , startNum = 0;

//previewImage();
let preview = 0;
let flick_panel = 0;
let img = 0;
let files = 0;
let fileBuffer = 0;  //formdData에 날릴 배열, input태그의 배열에 들어간 파일을 보내지 않음.


///////////  전역 변수  //////////////////
//////////////////////////////////////////
//////////////////////////////////////////


//게시글에서 수정 pen 버튼 누를경우 이벤트
//look_modify 리다이렉트
function lookModify(lookNum, lookUserId, userid) {

    var lookNum = lookNum;
    var lookUserId = lookUserId;

    if (userid == lookUserId) {
        //로그인 한 아이디 와 작성자 아이디 가 같으면 글수정
        modifyConfirm('', '게시글을 수정할까요?', lookNum);
    } else {
        swal("", "게시글 작성자만 수정 할수있습니다");
    }

}

function modifyConfirm(msg, title, lookNum) {
    swal({
        title: title,
        text: msg,
        type: "warning",
        showCancelButton: true,
        confirmButtonClass: "btn-danger",
        confirmButtonText: "예",
        cancelButtonText: "아니오",
        closeOnConfirm: false,
        closeOnCancel: false
    }, function (isConfirm) {
        if (isConfirm) {
            location.href = "/lookModify?look_num=" + lookNum;
        } else {
            swal('', '게시글 수정을 취소 하였습니다.');
        }

    });
}
// end of 게시글에서 수정 pen 버튼 누를경우 이벤트


function previewImage(result) { //(this,'View_area')
    preview = document.getElementById("View_area"); //div id
    const ua = window.navigator.userAgent;

    for (let i = 0; i < result.length; i++) {
        let file = result[i];
        // let imageType = /image.*/; //이미지 파일일경우만.. 뿌려준다.
        // if (!file.type.match(imageType))
        //     continue;

        flick_panel = document.createElement('div');
        img = document.createElement("img");
        //태그 생성 부분

        preview.appendChild(flick_panel);
        flick_panel.appendChild(img); //flick_panel의자식으로 img태그를 연결
        //태그 연결 부분

        preview.classList.add(LOOK_FLICK_CAMERA);

        flick_panel.classList.add(FLICK_PANEL);
        flick_panel.className = "flick_panel" + " s" + i;
        flick_panel.style.left = i * 300;
        flick_panel.id = "flick_panel";

        img.classList.add(LOOK_IMG_FILE);
        img.id = "prev_" + View_area;
        img.src = `data:image/jpg;base64, ${file}`
        // img.style.width = '100%';
        // img.style.height = '100%';
        //태그 속성 적용 부분
    }
}

function excuteSlide() {

    slideList = document.querySelector('.look_flick_camera');  // Slide parent dom
    slideContents = document.querySelectorAll('.flick_panel');  // each slide dom
    slideBtnNext = document.querySelector('#look_slide_button_right'); // next button
    slideBtnPrev = document.querySelector('#look_slide_button_left'); // prev button
    slideLen = slideContents.length;  // slide length
    slideWidth = 300; // slide width
    slideSpeed = 280; // slide speed
    startNum = 0; // initial slide index (0 ~ 4)

    slideList.style.width = slideWidth * (slideLen + 2) + "px";

    // Copy first and last slide
    let firstChild = slideList.firstElementChild;
    let lastChild = slideList.lastElementChild;
    let clonedFirst = firstChild.cloneNode(true);
    let clonedLast = lastChild.cloneNode(true);

    // Add copied Slides
    slideList.appendChild(clonedFirst);
    slideList.insertBefore(clonedLast, slideList.firstElementChild);

    slideList.style.transform = "translate3d(-" + (slideWidth * (startNum + 1)) + "px, 0px, 0px)";

    let curIndex = startNum; // current slide index (except copied slide)
    let curSlide = slideContents[curIndex]; // current slide dom
    curSlide.classList.add('slide_active');


    /** Next Button Event */
    slideBtnNext.addEventListener('click', function () {
        if (curIndex <= slideLen - 1) {
            slideList.style.transition = slideSpeed + "ms";
            slideList.style.transform = "translate3d(-" + (slideWidth * (curIndex + 2)) + "px, 0px, 0px)";
        }
        if (curIndex === slideLen - 1) {
            setTimeout(function () {
                slideList.style.transition = "0ms";
                slideList.style.transform = "translate3d(-" + slideWidth + "px, 0px, 0px)";
            }, slideSpeed);
            curIndex = -1;
        }
        curSlide.classList.remove('slide_active');
        curSlide = slideContents[++curIndex];
        curSlide.classList.add('slide_active');
    });

    /** Prev Button Event */
    slideBtnPrev.addEventListener('click', function () {
        if (curIndex >= 0) {
            slideList.style.transition = slideSpeed + "ms";
            slideList.style.transform = "translate3d(-" + (slideWidth * curIndex) + "px, 0px, 0px)";
        }
        if (curIndex === 0) {
            setTimeout(function () {
                slideList.style.transition = "0ms";
                slideList.style.transform = "translate3d(-" + (slideWidth * slideLen) + "px, 0px, 0px)";
            }, slideSpeed);
            curIndex = slideLen;
        }
        curSlide.classList.remove('slide_active');
        curSlide = slideContents[--curIndex];
        curSlide.classList.add('slide_active');
    });
}

function startAjax(lookNumber) {
    $.ajax({
        url: `/displayInthumbnail/${lookNumber}`,
        type: "GET",
        success: function (result) {
            previewImage(result);
            excuteSlide();
        },
        error: function (error) {
            //서버오류 500  찾는 자료없음 404  권한없음  401
            if (error.status == 404) {
                swal('찾는 자료가 없습니다', '', 'error');
            } else if (error.status == 401) {
                swal('접근 권한이 없습니다', '', 'error');
            } else if (error.status == 500) {
                swal('서버 오류 관리자에게 문의 하세요', '', 'error');
            }
        }
    })
}

function likey(num,id){

    $.ajax({
        url:"/likey/looknum/"+num+"/user/"+id,
        type:"post",
        success: function(result){
            swal(result,"",'error');
        },
        error: function(error){
            swal('로그인을 하세요',"",'error');
        }
    })
}

//JSP파일 서버변수 호출함수
function lookReady(lookNum) {
    startAjax(lookNum);
}

