// HTML의 요소 받아오기
const [titleInput, fileInput, totalSizeArea, fileDTOArea, filePrintArea, cancelBtn, sendBtn] = 
['titleInput', 'fileInput', 'totalSizeArea', 'fileDTOArea', 'filePrintArea', 'cancelBtn', 'sendBtn']
.map(e => document.getElementById(e));

const uploadFiles = new DataTransfer();

// 유틸리티 1. 파일 크기 형식
function formattedSize(fileSize) {
  if (fileSize < 1024) return fileSize + " Bytes";
  if (fileSize < 1024*1024) return (fileSize/1024).toFixed(2) + " KiB";
  return (fileSize/1024/1024).toFixed(2) + " MiB";
}
// 함수 1. 개별 파일 검증 : 실행파일 막기 / 10MB 이상 파일 막기
const regExp = new RegExp("\.(exe|jar|msi|dll|sh|bat)$");
const maxSize = 1024*1024*10;
function fileValidation(fileName, fileSize) {
  if (regExp.test(fileName)) return 0;
  if (fileSize > maxSize) return 0;
  return 1;
}
// 함수 2. 파일 출력
function flushFiles() {
  let totalSize = 0;
  let isOk = 1;

  // 기존 파일 출력 : 이미 등록된 파일들이므로 fileValidation 생략
  fileDTOArea.innerHTML = '';
  for (let idx = 0; idx < fileDTOList.length; idx++) {
    const file = fileDTOList[idx];
    totalSize += file.fileSize;
    let html = `
    <div class="row border-bottom">
    `;
    html += `
      <div class="col-7 border-end d-flex justify-content-center align-items-center">${file.fileName}</div>`;
    html += `
      <div class="col-3 border-end d-flex justify-content-center align-items-center">${formattedSize(file.fileSize)}</div>`;
    html += `
      <div class="col-1 border-end d-flex justify-content-center align-items-center">기존</div>`;
    html += `
      <div class="col-1 d-flex justify-content-center py-3">
        <button type="button" class="btn btn-outline-secondary deleteExistingFile" data-idx="${idx}">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
            <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
            <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
          </svg>
        </button>
      </div>
    </div>`;
    fileDTOArea.innerHTML += html;
  }

  // 추가 파일 출력 : fileValidation 시행
  filePrintArea.innerHTML = '';
  for (let idx = 0; idx < uploadFiles.files.length; idx++) {
    const file = uploadFiles.files[idx];
    let valid = fileValidation(file.name, file.size);

    totalSize += file.size;
    let html = `
    <div class="row border-bottom">`;
    html += `
      <div class="col-7 border-end d-flex justify-content-center align-items-center">${file.name}</div>`;
    html += `
      <div class="col-3 border-end d-flex justify-content-center align-items-center">${formattedSize(file.size)}</div>`;
    html += `
      <div class="col-1 border-end d-flex justify-content-center align-items-center">
        ${valid ? '' : `
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="red" class="bi bi-exclamation-triangle" viewBox="0 0 16 16">
            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
          </svg>
          `}
      </div>`;
    html += `
      <div class="col-1 d-flex justify-content-center py-3">
        <button type="button" class="btn btn-outline-secondary deleteNewFile" data-idx="${idx}">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-square" viewBox="0 0 16 16">
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

// 함수 3. 기존 파일 삭제
function deleteExistingFile(idx) {
  console.log(fileDTOList[idx]);
  fetch('/board/file/' + fileDTOList[idx].uuid, {
    method: 'delete',
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('파일 삭제에 실패했습니다.');
    else {
      fileDTOList.splice(idx, 1);
      flushFiles();
    }
  })
}

// 함수 4. 추가한 파일 삭제
function deleteNewFile(idx) {
  uploadFiles.items.remove(idx);
  flushFiles();
}

// 사전작업 1. 토스트 에디터 출력
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

// 사전작업 2. 토스트 에디터 툴바에 파일 업로드 UI 추가 및 작업 설정
for (const mode of ['markdown', 'wysiwig']) { // 모드별로 파일 업로드 지원
  editor.addCommand(mode, 'fileUpload', () => {
    fileInput.click();
    return true; // 에디터에게 개발자가 명령을 처리했음을 알리는 return. 리턴이 없으면 기본 동작이 실행될 수 있음.
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
  for (const file of fileInput.files) uploadFiles.items.add(file);
  flushFiles();
});

// 사전작업 3. 기존 내용 출력
editor.setHTML(contentBefore);
flushFiles();

// 1. 파일 삭제
document.addEventListener('click', e => {
  const target = e.target;
  if (target.classList.contains('deleteExistingFile')) deleteExistingFile(target.dataset.idx);
  else if (target.classList.contains('deleteNewFile')) deleteNewFile(target.dataset.idx);
});

// 2. 취소 버튼
cancelBtn.addEventListener('click', () => {
  if (confirm('게시글 수정을 취소하고 돌아갑니다.')) location.href = `/board/detail?bno=${bno}&isReal=false`;
})

// 3. 수정 버튼
sendBtn.addEventListener('click', () => {
  const formData = new FormData();
  if (uploadFiles.items.length > 0)
    for (const file of uploadFiles.files) formData.append('files[]', file);
  formData.append('bno', bno);
  formData.append('title', titleInput.value);
  formData.append('content', editor.getHTML());

  fetch('/board/update', {
    method: 'post',
    body: formData,
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    if (result == '0') alert('글 수정에 실패했습니다.');
    else if (result == '1') location.href = `/board/detail?bno=${bno}&isReal=false`;
    else alert(result);
  })
});