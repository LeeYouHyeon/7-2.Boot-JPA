const [sendBtn, fileInput, filePrintArea, totalSizeArea, titleInput] = ['sendBtn', 'fileInput', 'filePrintArea', 'totalSizeArea', 'titleInput'].map(e => document.getElementById(e));
const allFiles = new DataTransfer();

const reader = new FileReader();
const Editor = toastui.Editor;
const editor = new Editor({
  el: document.querySelector('#editor'),
  language: "ko-KR",
  height: '500px',
  initialEditType: 'markdown',
  previewStyle: 'vertical',
  hooks: {
    addImageBlobHook(blob, callback) {  // 이미지 업로드 로직 커스텀
      if (blob.size > 1024*300) {
        alert('그림이 너무 큽니다.');
        return;
      }
      const reader = new FileReader();
      reader.onloadend = () => {
        callback(reader.result, blob.name);
      };
      reader.readAsDataURL(blob);
    }
  },
  toolbarItems: [
    ['heading', 'bold', 'italic', 'strike'],
    ['hr', 'quote'],
    ['ul', 'ol', 'task', 'indent', 'outdent'],
    ['table', 'image', 'link'],
    ['code', 'codeblock'],
  ] // 기본 툴바
});

// 파일 업로드
for (const mode of ['markdown', 'wysiwig']) { // 모드별로 파일 업로드 지원
  editor.addCommand(mode, 'fileUpload', () => {
    fileInput.click();
    return true; // 에디터에게 "내가 이 명령어 처리했어. 더 이상 아무 것도 하지 마."
  });
}
editor.insertToolbarItem({groupIndex: 3, itemIndex: 3}, {
  name: "file",
  tooltip: "파일 업로드",
  className: "file-upload bi bi-file-earmark",
  command: 'fileUpload',
  text: ""
});
fileInput.addEventListener('change', () => {
  for (const file of fileInput.files) allFiles.items.add(file);
  flushFiles();
});


// 파일 출력

// 실행파일 막기 / 10MB 이상 파일 막기
// 개별 파일 검증으로, 전체 파일 총합은 fileInput의 eventListener에서 관리
const regExp = new RegExp("\.(exe|jar|msi|dll|sh|bat)$");
const maxSize = 1024*1024*10;
function fileValidation(fileName, fileSize) {
  if (regExp.test(fileName)) return 0;
  if (fileSize > maxSize) return 0;
  return 1;
}

function formattedSize(fileSize) {
  if (fileSize < 1024) return fileSize + " Bytes";
  if (fileSize < 1024*1024) return (fileSize/1024).toFixed(2) + " KiB";
  return (fileSize/1024/1024).toFixed(2) + " MiB";
}

function flushFiles() {
  let totalSize = 0;
  let isOk = 1;

  filePrintArea.innerHTML = '';
  for (let idx = 0; idx < allFiles.files.length; idx++) {
    const file = allFiles.files[idx];
    let valid = fileValidation(file.name, file.size);

    totalSize += file.size;
    let html = `
    <div class="row border-bottom">`;
    html += `
      <div class="col-7 border-end d-flex justify-content-center align-items-center">${file.name}</div>`;
    html += `
      <div class="col-3 border-end d-flex justify-content-center align-items-center">${formattedSize(file.size)}</div>`
    html += `
      <div class="col-1 border-end d-flex justify-content-center align-items-center">
        ${valid ? '' : `
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="red" class="bi bi-exclamation-triangle" viewBox="0 0 16 16">
            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
          </svg>
          `}
      </div>`
    html += `
      <div class="col-1 d-flex justify-content-center p-0">
        <button type="button" class="btn fileDeleteBtn" data-idx="${idx}">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-x-square" viewBox="0 0 16 16">
            <path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z"/>
            <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
          </svg>
        </button>
      </div>
    </div>`;
    filePrintArea.innerHTML += html;

    isOk *= valid;
  }

  totalSizeArea.innerText = "총 " + formattedSize(totalSize);
  if (totalSize > 20*1024*1024) {
    isOk = 0;
    totalSizeArea.classList.add('text-danger');
  } else totalSizeArea.classList.remove('text-danger');
  sendBtn.disabled = isOk == 0;
}

function deleteFile(idx)  {
  allFiles.items.remove(idx);
  flushFiles();
}

document.addEventListener('click', e => {
  const target = e.target;
  if (target.classList.contains('fileDeleteBtn')) deleteFile(target.dataset.idx);
});

sendBtn.addEventListener('click', () => {
  const formData = new FormData();
  if (allFiles.items.length > 0)
    for (const file of allFiles.files) formData.append('files[]', file);

  formData.append("bdto", new Blob([JSON.stringify({
    title: titleInput.value,
    writer: email,
    content: editor.getHTML()
  })], {
    type: "application/json"
  }));

  fetch('/board/register', {
    method: 'post',
    body: formData,
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    console.log(result);
    if (result == '0') alert('글 등록에 실패했습니다. 다시 시도해주세요.');
    else if (!isNaN(Number(result))) location.href = '/board/detail?bno=' + result;
    else alert(result);
  })
});