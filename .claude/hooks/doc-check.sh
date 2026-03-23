#!/bin/bash
# PostToolUse hook: 소스 코드 수정 시 관련 문서 업데이트 확인

FILE_PATH="${CLAUDE_FILE_PATH:-}"

# docs 폴더 내 파일 수정은 무시
if [[ "$FILE_PATH" == *"/docs/"* ]]; then
  exit 0
fi

# .kt 소스 파일만 대상
if [[ "$FILE_PATH" != *.kt ]]; then
  exit 0
fi

# 관련 docs 파일 매핑
DOCS_DIR="docs/features"
HINTS=""

case "$FILE_PATH" in
  *Auth*|*Login*|*Signup*|*GoogleAuth*|*NaverAuth*|*KakaoAuth*)
    HINTS="auth-login.md, auth-logout.md, auth-signup.md" ;;
  *MyBook*|*StoreBook*|*HomeScreen*|*MainViewModel*)
    HINTS="mybook-store.md, mybook-save.md, mybook-update.md" ;;
  *History*|*HistoryScreen*)
    HINTS="mybook-history.md" ;;
  *Member*|*Setting*|*Nickname*)
    HINTS="member-update-nickname.md, member-get-info.md" ;;
  *BookInfo*|*BookEdit*|*BookDetail*)
    HINTS="mybook-detail.md, mybook-update.md, mybook-delete.md" ;;
  *)
    exit 0 ;;
esac

if [ -n "$HINTS" ]; then
  echo "[DOC] 소스 수정됨: $(basename "$FILE_PATH"). 관련 문서 확인 필요: $HINTS"
fi
