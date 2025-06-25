console.log("boardDetail.js in")

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

// modBtn을 누르면 수정 상태로 변경
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

const [titleArea, titleInputArea, contentArea, contentInputArea, defaultButtons, buttonsInModifying, cancelBtn, modBtn] =
['titleArea', 'titleInputArea', 'contentArea', 'contentInputArea', 'defaultButtons', 'buttonsInModifying', 'cancelBtn', 'modBtn'].map(e => document.getElementById(e));

const defaultArea = [titleArea, contentArea, defaultButtons];
const modifyingArea = [titleInputArea, contentInputArea, buttonsInModifying];

modBtn.addEventListener('click', () => {
  hide(defaultArea);
  show(modifyingArea);
})
cancelBtn.addEventListener('click', () => {
  hide(modifyingArea);
  show(defaultArea);
})