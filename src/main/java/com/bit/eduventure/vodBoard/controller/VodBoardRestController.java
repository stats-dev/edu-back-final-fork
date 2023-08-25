package com.bit.eduventure.vodBoard.controller;


import com.bit.eduventure.ES1_User.Entity.CustomUserDetails;
import com.bit.eduventure.ES1_User.Entity.User;
import com.bit.eduventure.ES1_User.Service.UserService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import com.bit.eduventure.vodBoard.dto.*;
import com.bit.eduventure.vodBoard.entity.VodBoard;
import com.bit.eduventure.vodBoard.entity.VodBoardFile;
import com.bit.eduventure.vodBoard.service.VodBoardCommentService;
import com.bit.eduventure.vodBoard.service.VodBoardLikeService;
import com.bit.eduventure.vodBoard.service.VodBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vod")
@RequiredArgsConstructor
public class VodBoardRestController {
    private final VodBoardService vodBoardService;
    private final ObjectStorageService objectStorageService;
    private final VodBoardCommentService vodBoardCommentService;
    private final VodBoardLikeService vodBoardLikeService;
    private final UserService userService;
    //등록
    //value 주소 / cosumes 공식
    @PostMapping(value = "/board")
    public ResponseEntity<?> insertBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestPart(value = "boardDTO", required = false) VodBoardDTO boardDTO,
                                        @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
                                        @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                        @RequestPart(value = "fileList", required = false) MultipartFile[] fileList) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        List<VodBoardFile> uploadFileList = new ArrayList<>();
        String saveName;

        try {
            int userNo = Integer.parseInt(customUserDetails.getUsername());
            User user = userService.findById(userNo);
            boardDTO.setUserDTO(user.EntityToDTO());

            if (fileList != null) {
                for (MultipartFile file : fileList) {
                    VodBoardFile boardFile = new VodBoardFile();
                    saveName = objectStorageService.uploadFile(file);
                    boardFile.setVodOriginName(file.getOriginalFilename());
                    boardFile.setVodSaveName(saveName);
                    uploadFileList.add(boardFile);
                }
            }

            //메인 비디오 저장
            if (videoFile != null) {
                saveName = objectStorageService.uploadFile(videoFile);
                boardDTO.setOriginPath(objectStorageService.setObjectSrc(saveName));
                boardDTO.setSavePath(saveName);
            }

            //섬네일 저장
            //메인 비디오 저장
            if (thumbnail != null) {
                saveName = objectStorageService.uploadFile(thumbnail);
                boardDTO.setOriginThumb(objectStorageService.setObjectSrc(saveName));
                boardDTO.setSaveThumb(saveName);
            }

            VodBoard board = boardDTO.DTOTOEntity();
            board.setUser(userService.findById(userNo));

            vodBoardService.insertBoard(board, uploadFileList);

            responseDTO.setItem("저장되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //강의 목록(제목, 강사, 영상 다 포함됨)
    @GetMapping("/list")
    public ResponseEntity<?> getList() { // ResponseEntity<?> 스프링에서 제공하는 응답 객체
        //ResponseEntity 바디에 담아줄 객체(ResponseDTO) 선언
        //선언하면서 제네릭에 VodBoardFile 형태로 선언
        //했기때문에 items에는 List<VodBoardFile> 를 저장할 수 있다 items = List<VodBoardFile>
        //그리고 item에는 VodBoardFile 을 저장할 수 있다.
        ResponseDTO<VodBoardDTO> responseDTO = new ResponseDTO<>(); //응답 바디와 <데이터 형태>

        try {
            List<VodBoard> vodBoardList = vodBoardService.getVodBoardList();

            List<VodBoardDTO> vodBoardDTOList = vodBoardList.stream()
                    .map(VodBoard::EntityToDTO)
                    .collect(Collectors.toList());

            //데이터, 통신 오류나 상태 코드 등을 담기 위해서 responseDTO를 선언하고 사용한다.
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setItems(vodBoardDTOList);

            return ResponseEntity.ok().body(responseDTO); // 파일 목록을 보여줄 뷰 페이지로 이동
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //상세 페이지 보여주기
    @GetMapping("/board/{boardNo}")
    public ResponseEntity<?> getBoard(@PathVariable int boardNo) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();
        try {
            VodBoard board = vodBoardService.getBoard(boardNo);

            VodBoardDTO returnBoardDTO = board.EntityToDTO();

            List<VodBoardFile> boardFileList = vodBoardService.getBoardFileList(boardNo); //첨부파일 첨가

            //DTO형태의 list 선언해주기 하나씩 꺼내서 엔티티형태를 DTO형태의 list에 넣어주기
            List<VodBoardFileDTO> boardFileDTOList = boardFileList.stream()
                    .map(VodBoardFile::EntityToDTO)
                    .collect(Collectors.toList());
            
            List<VodBoardCommentDTO> commentList = vodBoardCommentService.getAllCommentList(boardNo);

            Map<String, Object> returnMap = new HashMap<>();

            returnMap.put("board", returnBoardDTO); //키 밸류값 게시글dto
            returnMap.put("boardFileList", boardFileDTOList); //첨부파일dto
            returnMap.put("commentList", commentList); //댓글

            responseDTO.setItem(returnMap);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping("/update/{boardNo}") // 수정 기능
    public ResponseEntity<?> updateVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int boardNo,
                                            @RequestPart(value = "boardDTO", required = false) VodBoardDTO updatedBoardDTO,
                                            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
                                            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                            @RequestPart(value = "fileList", required = false) MultipartFile[] fileList) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        String saveName;
        try {
            int userNo= Integer.parseInt(customUserDetails.getUsername());
            VodBoard vodBoard = vodBoardService.getBoard(boardNo);


            if (vodBoard.getUser().getId() != userNo) {
                responseDTO.setErrorMessage("수정 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(responseDTO);
            }

            if (updatedBoardDTO != null) {
                vodBoard = updatedBoardDTO.DTOTOEntity();
            }

            // 기존에 등록된 첨부파일 정보 가져오기
            List<VodBoardFile> existingFileList = vodBoardService.getBoardFileList(boardNo);

            // 새로 업로드한 파일 저장
            List<VodBoardFile> uploadFileList = new ArrayList<>();
            if (fileList != null) {
                for (MultipartFile file : fileList) {
                    if (!file.isEmpty()) {
                        VodBoardFile boardFile = new VodBoardFile();
                        saveName = objectStorageService.uploadFile(file);
                        boardFile.setVodOriginName(file.getOriginalFilename());
                        boardFile.setVodSaveName(saveName);
                        uploadFileList.add(boardFile);
                    }
                }
            }

            // 기존에 등록된 파일 삭제
            for (VodBoardFile existingFile : existingFileList) {
                objectStorageService.deleteObject(existingFile.getVodSaveName());
                vodBoardService.deleteFile(existingFile);
            }

            // 새로 업로드한 비디오 및 섬네일 파일 저장
            if (videoFile != null && !videoFile.isEmpty()) {
                saveName = objectStorageService.uploadFile(videoFile);
                vodBoard.setOriginPath(objectStorageService.setObjectSrc(saveName));
                vodBoard.setSavePath(saveName);
            }
            if (thumbnail != null && !thumbnail.isEmpty()) {
                saveName = objectStorageService.uploadFile(thumbnail);
                vodBoard.setOriginThumb(objectStorageService.setObjectSrc(saveName));
                vodBoard.setSaveThumb(saveName);
            }

            // 새로 업로드한 파일 등록
            vodBoardService.insertBoard(vodBoard, uploadFileList);

            // 수정 성공 메시지 등을 설정
            responseDTO.setItem("정상적으로 수정되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (NullPointerException npe) {
            responseDTO.setErrorMessage("요청에 필요한 정보가 부족합니다.");
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/delete/{boardNo}") //삭제 기능
    public ResponseEntity<?> deleteVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int boardNo) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        try {
            int userNo= Integer.parseInt(customUserDetails.getUsername());
            VodBoard vodBoard = vodBoardService.getBoard(boardNo);
            
            if (vodBoard.getUser().getId() != userNo) {
                responseDTO.setErrorMessage("삭제 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(responseDTO);
            }

            List<VodBoardFile> boardFileList = vodBoardService.getBoardFileList(boardNo); //첨부파일 첨가

            boardFileList.stream().forEach(vodBoardFile -> {
                // 오브젝트 스토리지
                objectStorageService.deleteObject(vodBoardFile.getVodSaveName());
                // 첨부파일 디비 삭제
                vodBoardService.deleteFile(vodBoardFile);
            });

            //게시물db삭제 (작은것 부터 삭제하는게 좋을것 같다. 첨부파일(오브젝트스토리지, 디비) -> 게시판
            vodBoardService.deleteVodBoard(boardNo);

            // 삭제 성공 메시지 등을 설정
            responseDTO.setItem("정상적으로 모두 삭제되었습니다");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    // ---------------------------------------- 댓글 ----------------------------------------

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody VodBoardCommentDTO vodBoardCommentDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        try {
            int userNo = Integer.parseInt(customUserDetails.getUsername());
            User user = userService.findById(userNo);
            vodBoardCommentDTO.setUserDTO(user.EntityToDTO());
            vodBoardCommentService.addComment(vodBoardCommentDTO);

            responseDTO.setItem("저장되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping("/comment")
    public ResponseEntity<?> modifyComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody VodBoardCommentDTO vodBoardCommentDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        try {
            int userNo = Integer.parseInt(customUserDetails.getUsername());

            if (userNo != vodBoardCommentService.getComment(vodBoardCommentDTO.getId()).getUser().getId()) {
                responseDTO.setErrorMessage("수정 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(responseDTO);
            }

            vodBoardCommentService.addComment(vodBoardCommentDTO);

            responseDTO.setItem("저장되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/comment/{commentNo}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable int commentNo) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        try {
            int userNo = Integer.parseInt(customUserDetails.getUsername());

            if (userNo != vodBoardCommentService.getComment(commentNo).getUser().getId()) {
                responseDTO.setErrorMessage("삭제 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(responseDTO);
            }
            
            vodBoardCommentService.deleteComment(commentNo);
            responseDTO.setItem("삭제되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // ---------------------------------------- 좋아요 ----------------------------------------

    // 좋아요 등록
    @PostMapping("/{vb_idx}/{m_idx}")
    public ResponseEntity<VodBoardLikeDTO> likeVodBoard(@PathVariable int vb_idx, @PathVariable int m_idx) {
        VodBoardLikeDTO liked = vodBoardLikeService.likeVodBoard(vb_idx, m_idx);
        System.out.println("등록 성공");
        return new ResponseEntity<>(liked, HttpStatus.OK);
    }

    // 좋아요 취소
    @DeleteMapping("/{vb_idx}/{m_idx}")
    public ResponseEntity<Void> unlikeVodBoard(@PathVariable int vb_idx, @PathVariable int m_idx) {
        vodBoardLikeService.unlikeVodBoard(vb_idx, m_idx);
        System.out.println("취소 성공");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}