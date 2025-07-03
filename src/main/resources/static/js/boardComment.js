console.log("boardComment.js in")

let currentPage = 1;
const [commentListArea, cmtContent, cmtRegisterBtn] = ['commentListArea', 'cmtContent', 'cmtRegisterBtn'].map(e => document.getElementById(e));

function show(input) {
  if (Array.isArray(input)) input.forEach(e => {show(e);});
  else input.classList.remove('d-none');
}
function hide(input) {
  if (Array.isArray(input)) input.forEach(e => {hide(e)});
  else input.classList.add('d-none');
}

let openReply = [];

// Main Controller
loadComment();
document.addEventListener('click', e => {
  console.log("click", e.target);
  const classList = e.target.classList;
  if (classList.contains('page-link')) {
    openReply = [];
    loadComment(e.target.dataset.page);
    return;
  }

  // 파일 삭제
  if (classList.contains('fileRemoveBtn')) {
    deleteFile(e.target);
    return;
  }

  // 답글
  if (classList.contains('postReplyBtn')) {
    const input = e.target.closest('.row').querySelector('input');
    const parent = e.target.closest('.replyArea').dataset.parent;
    if (input.value == '') {
      input.focus();
      return;
    }
    const json = {
      bno: bno,
      writer: email,
      content: input.value,
      parent: parent
    };
    input.value = '';

    fetch('/comment/post', {
      method: 'post',
      headers: {
        [csrfHeader]: [csrfToken],
        'content-type': 'application/json; charset=utf-8'
      },
      body: JSON.stringify(json)
    }).then(resp => resp.text())
    .then(result => {
      if (result == '0') {
        alert('답글 등록에 실패했습니다.');
        return;
      }
      cmt.querySelector('.replyArea').remove();
      loadComment(currentPage);
    })
  }

  // 댓글
  const cmt = e.target.closest('.comment');
  if (cmt == null) return;

  const cno = cmt.dataset.cno;
  const [cmtContent, cmtContentInput, cmtModify, cmtUpdate, cmtRemove, cmtCancel] =
  ['cmtContent', 'cmtContentInput', 'cmtModify', 'cmtUpdate', 'cmtRemove', 'cmtCancel'].map(e => cmt.querySelector('.' + e));
  const parsed = {
    cno : cno,
    content : cmtContent,
    input : cmtContentInput,
    modify : cmtModify,
    update : cmtUpdate,
    remove : cmtRemove,
    cancel : cmtCancel
  };

  if (classList.contains('cmtModify')) openModifyUI(parsed);
  else if (classList.contains('cmtRemove')) removeComment(cno);
  else if (classList.contains('cmtUpdate')) updateComment(parsed);
  else if (classList.contains('cmtCancel')) cancelModifyUI(parsed);
  else if (classList.contains('toggleReplyBtn')) {
    if (openReply.includes(cno)) openReply = openReply.filter(e => e != cno);
    else openReply.push(cno);
    loadComment(currentPage);
  }
});

// 1. Print Comments
function loadComment(page = 1) {
  currentPage = page;
  fetch(`/comment/list/${bno}/${page}`)
  .then(resp => resp.json())
  .then(json => {
    const {hasNext, hasPrev, list, startPage, endPage, totalCount} = json;
    // commentListArea
    if (totalCount == 0) {
      commentListArea.innerHTML = '<div class="d-flex justify-content-center align-items-center">댓글이 없습니다.</div>';
      return;
    }
    commentListArea.innerHTML = '';
    for (const comment of list) {
      commentListArea.innerHTML += commentToHTML(comment);
    }

    // paging area
    let paging = `
    <nav aria-label="Page navigation example" class="d-flex justify-content-center">
    <ul class="pagination justify-content-center">`;
    paging += `
      <li class="page-item btn p-0 border-0 ${hasPrev ? '' : 'disabled'}">
        <span class="page-link" data-page=${startPage - 1}>&laquo;</span>
      </li>
    `;
    for (let i = startPage; i <= endPage; i++) {
      paging += `
      <li class="page-item btn p-0 border-0 ${i == page ? 'active' : ''}">
        <span class="page-link" data-page=${i}>${i}</span>
      </li>
      `;
    }
    paging += `
      <li class="page-item btn p-0 border-0 ${hasNext ? '' : 'disabled'}">
        <span class="page-link" data-page=${endPage + 1}>&raquo;</span>
      </li>
    </ul></nav>`;
    commentListArea.innerHTML += paging;

    const comments = document.querySelectorAll('.comment');
    console.log(comments);
    comments.forEach(comment => {
      if (openReply.includes(comment.dataset.cno)) loadReplys(comment);
    })
  }).catch(error => {
    console.log(error);
    commentListArea.innerHTML = '<h5 class="text-center">댓글을 불러오지 못했습니다. 새로고침해주세요.</h5>';
  })
}

function loadReplys(cmt) {
  if (typeof cmt != 'object') {
    const comments = document.querySelectorAll('.comment');
    for (const comment of comments) 
      if (comment.dataset.cno == cmt) {
        cmt = comment;
        break;
      }
  }

  const cno = cmt.dataset.cno;
  
  fetch('/comment/reply/' + cno)
  .then(resp => resp.json())
  .then(list => {
    let html = `
    <div class="replyArea bg-body-tertiary border rounded p-3" data-parent="${cno}">`;
    if (list.length == 0) html += '<div class="d-flex justify-content-center align-items-center my-3">답글이 없습니다.</div>';
    for (const reply of list) html += commentToHTML(reply, true);
    if (email) {
      html += `
      <div class="row mb-2 mt-5 px-3">
        <input class="form-control col me-3" type="text">
        <button class="col-1 btn btn-primary postReplyBtn">등록</button
      </div>
      `
    }
    html += '</div>';
    cmt.querySelector('hr').insertAdjacentHTML('beforebegin', html);
  });
} 

function commentToHTML({cno, writer, content, regTimeOrDate, modTimeOrDate, replyCount}, isReply) {
  let cmt = `
  <div class="mb-3 comment" data-cno=${cno} data-open="${openReply.includes(String(cno)) ? 'true' : 'false'}">
    <div class="row mb-3">
      <div class="col-3 d-flex align-items-center">${writer}</div>
      <div class="col"></div>
      <div class="col-3 d-flex justify-content-end align-items-center">`;
  if (writer == email) cmt += `
        <button type="button" class="btn cmtModify">수정</button>
        <button type="button" class="btn cmtRemove">삭제</button>
        <button type="button" class="btn cmtUpdate d-none">확인</button>
        <button type="button" class="btn cmtCancel d-none">취소</button>
        `;
  cmt += `
      </div>
    </div>

    <div class="row mb-3">
      <div class="col mb-3 d-flex align-items-center cmtContent">${content}</div>
      <div class="col mb-3 d-none cmtContentInput">
        <input type="text" value="${content}" class="form-control">
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-6 d-flex align-items-center">`;
  if (!isReply) cmt += `
        <button type="button" class="btn btn-outline-secondary toggleReplyBtn">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chat-left-text me-2 toggleReplyBtn" viewBox="0 0 16 16">
            <path d="M14 1a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.414A2 2 0 0 0 3 11.586l-2 2V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12.793a.5.5 0 0 0 .854.353l2.853-2.853A1 1 0 0 1 4.414 12H14a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z"/>
            <path d="M3 3.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5M3 6a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9A.5.5 0 0 1 3 6m0 2.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5"/>
          </svg>
          ${replyCount}</button>`;
  cmt += `</div>
      <div class="col-6 d-flex flex-column justify-content-center align-items-end">
        <span>작성일 ${regTimeOrDate}</span>
        <span>수정일 ${modTimeOrDate}</span>
      </div>
    </div>
    <hr>
  </div>`;
  return cmt;
}

// 2. Post new comment
cmtRegisterBtn.addEventListener('click', () => {
  if (cmtContent.value == '') {
    cmtContent.focus();
    return;
  }

  fetch('/comment/post', {
    method: 'post',
    headers: {
      'content-type': 'application/json; charset=utf-8',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify({
      'bno': bno,
      writer: email,
      content: cmtContent.value
    })
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('댓글 등록에 실패했습니다.');
    else {
      cmtContent.value = '';
      loadComment();
    }
  }).catch(error => {
    alert('오류가 발생했습니다.');
    console.log(error);
  })
});

// 3. Open UI for modifying comment
function openModifyUI(parsed) {
  hide([parsed.content, parsed.modify, parsed.remove]);
  show([parsed.input, parsed.update, parsed.cancel]);
}

// 4. Delete a comment
function removeComment(cno) {
  if (!confirm('댓글을 삭제하시겠습니까?')) return;

  fetch('/comment/delete/' + cno, {
    method: 'delete',
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') {
      alert('삭제에 실패했습니다.');
    }
    openReply = openReply.filter(e => e != String(cno));
    loadComment(currentPage);
  }).catch(error => {
    console.log(error);
    alert('오류가 발생했습니다.');
  });
}

// 5. Update comment
function updateComment(parsed) {
  const input = parsed.input.querySelector('input');
  if (input.value == '') {
    input.focus();
    return;
  }

  fetch('/comment/update', {
    method: 'put',
    headers: {
      'content-type': 'application/json; charset=utf-8',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify({
      'cno': parsed.cno,
      content: input.value
    })
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('댓글 수정에 실패했습니다.');
    else {
      parsed.content.innerText = input.value;
      loadComment(currentPage);
    }
    
  }).catch(error => {
    alert('오류 발생');
    console.log(error);
  });
} 

// 6. Cancel modifying
function cancelModifyUI(parsed) {
  show([parsed.content, parsed.modify, parsed.remove]);
  hide([parsed.input, parsed.update, parsed.cancel]);
}