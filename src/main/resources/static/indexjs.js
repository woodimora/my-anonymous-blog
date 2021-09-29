let page;

$(document).ready(function () {
    page = 0;
    get_posts()
    $('div.body-post-list').addClass('active')
    $('div.body-post-view').removeClass('active')
    $('div.body-post-form').removeClass('active')
})

//p번째 페이지 호출
function get_page(p) {
    page = p - 1;
    get_posts()
}

//게시글 조회 갯수 : 10 개
function get_posts() {
    $('#post-table').empty()
    $('#pagination-ul').empty()
    $.ajax({
        type: 'GET',
        url: `/api/posts?page=${page}&display=10`,
        success: function (response) {
            let nowPage = page + 1;
            let totalPage = response['totalPages'] > 0 ? response['totalPages'] : 1;

            let paginationHtml = '';
            let leftPage = nowPage - 1;
            let rightPage = totalPage - nowPage;
            //좌측 페이지가 4개 이상이면 ...으로 변환
            if (leftPage > 4) {
                paginationHtml = `<li><a class="pagination-link" aria-label="Goto page 1" onclick="get_page(1)">1</a></li>`
                paginationHtml += `<li><span class="pagination-ellipsis">&hellip;</span></li>`
                for (let i = nowPage - 2; i < nowPage; i++) {
                    paginationHtml += `<li><a class="pagination-link" aria-label="Goto page ${i}" onclick="get_page(${i})">${i}</a></li>`
                }
            } else {
                for (let i = 1; i < nowPage; i++) {
                    paginationHtml += `<li><a class="pagination-link" aria-label="Goto page ${i}" onclick="get_page(${i})">${i}</a></li>`
                }
            }

            paginationHtml += `<li><a class="pagination-link is-current" aria-label="Goto page ${nowPage}" onclick="get_page(${nowPage})">${nowPage}</a></li>`

            //우측 페이지가 4개 이상이면 ...으로 변환
            if (rightPage > 4) {
                for (let i = nowPage + 1; i <= nowPage + 2; i++) {
                    paginationHtml += `<li><a class="pagination-link" aria-label="Goto page ${i}" onclick="get_page(${i})">${i}</a></li>`
                }
                paginationHtml += `<li><span class="pagination-ellipsis">&hellip;</span></li>`
                paginationHtml += `<li><a class="pagination-link" aria-label="Goto page ${totalPage}" onclick="get_page(${totalPage})">${totalPage}</a></li>`
            } else {
                for (let i = nowPage + 1; i <= totalPage; i++) {
                    paginationHtml += `<li><a class="pagination-link" aria-label="Goto page ${i}" onclick="get_page(${i})">${i}</a></li>`
                }
            }

            $('#pagination-ul').append(paginationHtml);

            if (response['empty'] === true)
                return;
            let posts = response['content']

            for (let i = 0; i < response['numberOfElements']; i++) {
                let tempHtml = `<tr onclick="get_post_one(${posts[i]['id']})">
                                            <th>${posts[i]['id']}</th>
                                            <td>${posts[i]['writer']}</td>
                                            <td>${posts[i]['title']}</td>
                                            <td>${time2str(posts[i]['createdAt'])}</td>
                                            <td>${posts[i]['view_count']}</td>
                                            <td>${posts[i]['comments_count']}</td>
                                        </tr>`
                $('#post-table').append(tempHtml)
            }
        }
    })
}

//게시글 상세 페이지
function get_post_one(id) {
    $('#post-box').empty()
    $.ajax({
        type: 'GET',
        url: '/api/posts/' + id,
        success: function (response) {
            //상세페이지로 전환
            $('div.body-post-form').removeClass('active')
            $('div.body-post-list').removeClass('active')
            $('div.body-post-view').addClass('active')

            //서버로 받은 데이터 삽입
            let title = response['title']
            let writer = response['writer']
            let createdAt = response['createdAt']
            let view_count = response['view_count']
            let contents = response['contents']
            let tempHtml = `<div class="post-box">
                                        <div class="edit-button-area">
                                            <button class="button is-link" onclick="check_post_pwd(${id},'edit')">수정</button>
                                            <button class="button is-danger" onclick="check_post_pwd(${id},'delete')">삭제</button>
                                            <button class="button" onclick="post_cancel()">목록</button>
                                        </div>
                                        <div class="post-title">
                                            제목 : <span id="view-title">${title}</span>
                                        </div>
                                        <div class="post-writer">
                                            글쓴이 : <span id="view-writer">${writer}</span>
                                        </div>
                                        <div class="post-date">
                                            작성 날짜 : <span>${date2str(createdAt)}</span>
                                        </div>
                                        <div class="post-view-count">
                                            조회수 : <span>${view_count}</span>
                                        </div>
                                        <div class="post-contents" id="view-contents">${contents}</div>
                                    </div>`
            $('#post-box').append(tempHtml)
        }
    })
}

//게시글 작성
function post_save() {
    //modal id 확인
    let id = $('#post-modal-id').val()
    let url = '/api/posts'

    //id 값이 있다면 edit
    if (id > 0) {
        url = '/api/posts/' + id;
    }
    let writer = $('#writer').val();
    let password = $('#password').val();
    let title = $('#title').val();
    let contents = $('#contents').val();

    //입력 확인
    if (writer === '') {
        alert("이름을 입력하세요.")
        return;
    }
    if (password === '') {
        alert("암호를 입력하세요.")
        return;
    }
    if (title === '') {
        alert("제목을 입력하세요.")
        return;
    }
    if (contents === '') {
        alert("내용을 입력하세요.")
        return;
    }

    //게시글 생성/수정 요청
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify({
            'writer': writer,
            'password': password,
            'title': title,
            'contents': contents
        }),
        contentType: 'application/json',
        success: function (response) {
            alert("저장이 완료되었습니다.")
            window.location.reload()
        }
    })
}

//게시글 수정페이지로 이동.
function go_edit_page() {
    $('#password-modal').removeClass('is-active')
    let id = $('#post-modal-id').val()
    let password = $('#post-modal-pwd').val()
    if (password === '') {
        alert("암호를 입력하세요.")
        return;
    }
    let data = `password=${password}`;
    //게시글 수정 인증 요청
    $.ajax({
        type: 'GET',
        url: '/api/posts/auth/' + id,
        data: data,
        success: function (response) {
            console.log(response)
            if (response === '') {
                alert('암호가 올바르지 않습니다.')
            } else {
                $('#writer').val(response['writer'])
                $('#title').val(response['title'])
                $('#contents').val(response['contents'])
                $('#password').val('')
                $('div.body-post-form').addClass('active')
                $('div.body-post-list').removeClass('active')
                $('div.body-post-view').removeClass('active')
            }
        }
    })
}

//게시글 작성 취소/목록으로 이동
function post_cancel() {
    window.location.reload();
    $('div.body-post-form').removeClass('active')
    $('div.body-post-list').addClass('active')
    $('div.body-post-view').removeClass('active')
}

//게시글 작성 페이지 전환
function post_form() {
    $('div.body-post-form').addClass('active')
    $('div.body-post-list').removeClass('active')
    $('div.body-post-view').removeClass('active')
    $('#post-modal-id').val(0)
    $('#writer').attr("disabled", false)
    $('#writer').val('')
    $('#title').val('')
    $('#contents').val('')
    $('#password').val('')
}

//게시글 비밀번호와 일치하는지 확인
function check_post_pwd(id, action) {
    $('#post-modal-pwd').val('')
    $('#password-modal').addClass('is-active')
    $('#post-modal-id').val(id)
    // edit 또는 delete 버튼 입력시 모달 버튼 기능 변경
    if (action === 'edit') {
        $('#writer').attr("disabled", true)
        $('#modal-ok-button').attr("onclick", "go_edit_page()");
    } else {
        $('#modal-ok-button').attr("onclick", `delete_post(${id})`);
    }
}

//게시글 삭제 요청
function delete_post(id) {
    let password = $('#post-modal-pwd').val()
    if (password === '') {
        alert("암호를 입력하세요.")
        return;
    }
    $.ajax({
        type: 'DELETE',
        url: '/api/posts/' + id,
        data: JSON.stringify({
            'password': password,
        }),
        contentType:"application/json",
        success: function (response) {
            if (response === 'success') {
                alert("삭제가 완료되었습니다.")
                window.location.reload();
            }
            else {
                alert('암호가 올바르지 않습니다.')
            }
        }
    })
}

//날짜 변환
function date2str(dateStr) {
    // 클라이언트 Timezone offset
    let offset = (new Date()).getTimezoneOffset() * 1000 * 60;
    // 서버에서 받은 시간
    let utcTime = new Date(dateStr)
    // 서버 기준시에서 클라이언트의 Timezone offset 을 뺀 시간
    let day = new Date(utcTime - offset);
    // let day = new Date(dateStr);
    let year = `${day.getFullYear()}`;
    let month = day.getMonth() < 10 ? `0${day.getMonth()}` : `${day.getMonth()}`;
    let date = day.getDate() < 10 ? `0${day.getDate()}` : `${day.getDate()}`;
    let hours = day.getHours() < 10 ? `0${day.getHours()}` : `${day.getHours()}`;
    let minutes = day.getMinutes() < 10 ? `0${day.getMinutes()}` : `${day.getMinutes()}`;
    let seconds = day.getSeconds() < 10 ? `0${day.getSeconds()}` : `${day.getSeconds()}`;

    return `${year}년 ${month}월 ${date}일 ${hours}:${minutes}:${seconds}`
}

//시간 변환
function time2str(dateStr) {
    // 클라이언트 Timezone offset
    let offset = (new Date()).getTimezoneOffset() * 1000 * 60;
    // 서버에서 받은 시간
    let utcTime = new Date(dateStr)
    // 서버 기준시에서 클라이언트의 Timezone offset 을 뺀 시간
    let day = new Date(utcTime - offset);
    // let day = new Date(dateStr);
    //오늘 날짜 (년월일)
    let today = new Date(new Date()).getDate()

    if (today === day.getDate()) {
        let hours = day.getHours() < 10 ? `0${day.getHours()}` : `${day.getHours()}`;
        let minutes = day.getMinutes() < 10 ? `0${day.getMinutes()}` : `${day.getMinutes()}`;

        return `${hours} : ${minutes}`;
    }
    return `${day.getFullYear()}.${day.getMonth() + 1}.${day.getDate()}`
}