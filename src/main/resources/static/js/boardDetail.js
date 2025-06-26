console.log("boardDetail.js in");

// delBtn을 누르면 /board/remove로 이동(bno를 가지고 이동)
const delBtn = document.getElementById('delBtn');
delBtn.addEventListener('click', () => {
  if (confirm('글을 삭제하시겠습니까?')) {
    fetch('/board/delete?bno=' + bno)
    .then(resp => resp.text())
    .then(result => {
      if (result == '0') alert('삭제에 실패했습니다.');
      else {
        alert("삭제되었습니다.");
        location.href = '/board/list';
      }
    }).catch(error => {
      console.log(error);
      alert('오류가 발생했습니다.');
    })
  }
});

function hide(input) {
  if (Array.isArray(input)) {
    for (const e of input) hide(e);
  } else {
    input.classList.remove('d-flex');
    input.classList.add('d-none');
  }
}
function show(input) {
  if (Array.isArray(input)) {
    for (const e of input) show(e);
  } else {
    input.classList.remove('d-none');
    input.classList.add('d-flex');
  }
}

// modBtn을 누르면 수정 상태로 변경
const [titleArea, titleInputArea, contentArea, contentInputArea, defaultButtons, buttonsInModifying, cancelBtn, modBtn, fileUploadArea, commentArea, uploadFileZone] =
['titleArea', 'titleInputArea', 'contentArea', 'contentInputArea', 'defaultButtons', 'buttonsInModifying', 'cancelBtn', 'modBtn', 'fileUploadArea', 'commentArea', 'fileZone'].map(e => document.getElementById(e));

const defaultArea = [titleArea, contentArea, defaultButtons];
const modifyingArea = [titleInputArea, contentInputArea, buttonsInModifying, fileUploadArea];

modBtn.addEventListener('click', () => {
  hide(defaultArea);
  show(modifyingArea);
  uploadFileZone.classList.remove('d-none');
  commentArea.classList.add('d-none');
  document.querySelectorAll('.fileRemoveBtn').forEach(e => e.classList.remove("invisible"));
})
cancelBtn.addEventListener('click', () => {
  hide(modifyingArea);
  show(defaultArea);
  uploadFileZone.classList.add('d-none');
  commentArea.classList.remove('d-none');
  document.querySelectorAll('.fileRemoveBtn').forEach(e => e.classList.add("invisible"));
})

function deleteFile(deleteFileBtn) {
  if (!confirm('파일을 삭제하시겠습니까?')) return;

  const file = deleteFileBtn.closest('.row');
  fetch('/board/file/' + file.dataset.uuid, {
    method: 'delete'
  }).then(resp => resp.text())
  .then(result => {
    if (result == 0) alert('파일 삭제에 실패했습니다.');

    uploadedTotalSize -= Number(file.dataset.realsize);
    flushFileSizes();
    file.remove();
  }).catch(console.log);
}

flushFileSizes(1);