console.log("boardComment.js in")

let currentPage;
const [commentListArea, cmtWriterInput, cmtContent, cmtRegisterBtn] = ['commentListArea', 'cmtWriterInput', 'cmtContent', 'cmtRegisterBtn'].map(e => document.getElementById(e));

// Main Controller
loadComment();
document.addEventListener('click', e => {
  const classList = e.target.classList;
  if (classList.contains('page-link')) {
    loadComment(e.target.dataset.page);
    return;
  }

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
});

// 1. Print Comments
function loadComment(page = 1) {
  currentPage = page;
  fetch(`/comment/list/${bno}/${page}`)
  .then(resp => resp.json())
  .then(json => {
    console.log(json);
    const {hasNext, hasPrev, list, startPage, endPage, totalCount} = json;
    // commentListArea
    if (totalCount == 0) {
      commentListArea.innerHTML = '<div class="d-flex justify-content-center align-items-center">댓글이 없습니다.</div>';
      return;
    }
    let init = '<div class="row mb-3">';
      init += '<div class="col-2 d-flex justify-content-center align-items-center">작성자</div>';
      init += '<div class="col-6 d-flex justify-content-center align-items-center">내용</div>';
      init += '<div class="col-2 d-flex justify-content-center align-items-center">작성일</div>';
      init += `<div class="col-2 d-flex justify-content-center align-items-center">총 댓글 수 : ${totalCount}</div>`;
      init += '</div><hr>';
      commentListArea.innerHTML = init;
      for (const {cno, writer, content, regTimeOrDate} of list) {
        let cmt = `
        <div class="row mb-3 comment" data-cno=${cno}>`;
        cmt += `
          <div class="col-2 d-flex justify-content-center align-items-center">${writer}</div>`;
        cmt += `
          <div class="col-6 d-flex align-items-center cmtContent">${content}</div>`;
        cmt += `
          <div class="col-6 d-none align-items-center cmtContentInput">
            <input type="text" class="form-control" value=${content} />
          </div>`;
        cmt += `
          <div class="col-2 d-flex justify-content-center align-items-center">${regTimeOrDate}</div>`;
        cmt += `
          <div class="col-2 d-flex justify-content-end">`;
        cmt += `
          <button type="button" class="btn btn-warning me-3 cmtModify">수정</button>`;
        cmt += `
          <button type="button" class="btn btn-warning me-3 cmtUpdate d-none">확인</button>`;
        cmt += `
          <button type="button" class="btn btn-danger cmtRemove">삭제</button>`;
        cmt += `
          <button type="button" class="btn btn-secondary cmtCancel d-none">취소</button>
        </div>`;
        cmt += `
        </div>`;

        commentListArea.innerHTML += cmt;
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
  }).catch(error => {
    console.log(error);
    commentListArea.innerHTML = '<h5 class="text-center">댓글을 불러오지 못했습니다. 새로고침해주세요.</h5>';
  })
}

// 2. Post new comment
cmtRegisterBtn.addEventListener('click', () => {
  if (cmtWriterInput.value == '') {
    cmtWriterInput.focus();
    return;
  }
  if (cmtContent.value == '') {
    cmtContent.focus();
    return;
  }

  fetch('/comment/post', {
    method: 'post',
    headers: {
      'content-type': 'application/json; charset=utf-8'
    },
    body: JSON.stringify({
      'bno': bno,
      writer: cmtWriterInput.value,
      content: cmtContent.value
    })
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('댓글 등록에 실패했습니다.');
    else {
      cmtWriterInput.value = '';
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
    method: 'delete'
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('삭제에 실패했습니다.');
    else loadComment(currentPage);
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
      'content-type': 'application/json; charset=utf-8'
    },
    body: JSON.stringify({
      'cno': parsed.cno,
      content: input.value
    })
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('댓글 수정에 실패했습니다.');
    else loadComment(currentPage);
    
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