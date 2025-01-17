package com.bit.eduventure.vodBoard.service;


import com.bit.eduventure.vodBoard.entity.VodBoard;
import com.bit.eduventure.vodBoard.entity.VodBoardFile;
import com.bit.eduventure.vodBoard.repository.VodBoardFileRepository;
import com.bit.eduventure.vodBoard.repository.VodBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class VodBoardService {
    private final VodBoardRepository vodBoardRepository;
    private final VodBoardFileRepository vodBoardFileRepository;

    //게시글 등록하기 (게시물과 첨부파일 리스틀 받아 생성)
    @Transactional
    public void insertBoard(VodBoard board, List<VodBoardFile> fileList) {
        if (!StringUtils.hasText(board.getContent())
                || !StringUtils.hasText(board.getTitle())) {
            throw new NullPointerException();
        }

        board.setRegDate(LocalDateTime.now());
        board.setHits(0);

        vodBoardRepository.save(board);
        vodBoardRepository.flush();
        if (!fileList.isEmpty()) {
            fileList.stream().forEach(boardFile -> {
                boardFile.setVodBoardNo(board.getId());
                vodBoardFileRepository.save(boardFile);
            });
        }
    }

    //게시물 조회
    @Transactional
    public VodBoard getBoard(int boardNo) {
        VodBoard returnBoard = vodBoardRepository.findById(boardNo)
                .orElseThrow(() -> new NoSuchElementException());
        returnBoard.setHits(returnBoard.getHits() + 1); //조회수 증가
        vodBoardRepository.save(returnBoard);

        return returnBoard;
    }

    //게시물 전체 리스트
    @Transactional
    public List<VodBoard> getVodBoardList() {
        return vodBoardRepository.findAll();
    }

    //게시물 수정 기능
    @Transactional
    public void updateVodBoard(int boardNo, VodBoard updatedVodBoard) {
        VodBoard vodBoard= vodBoardRepository.findById(boardNo)
                .orElseThrow(() -> new NoSuchElementException());

        if (!StringUtils.hasText(vodBoard.getContent())
                || !StringUtils.hasText(vodBoard.getTitle())) {
            throw new NullPointerException();
        }

        vodBoard.setTitle(updatedVodBoard.getTitle());
        vodBoard.setContent(updatedVodBoard.getContent());
        vodBoard.setWriter(updatedVodBoard.getWriter());
        vodBoard.setModDate(LocalDateTime.now());

        vodBoard.setSavePath(updatedVodBoard.getSavePath());
        vodBoard.setOriginPath(updatedVodBoard.getOriginPath());
        vodBoard.setObjectPath(updatedVodBoard.getObjectPath());

        vodBoard.setSaveThumb(updatedVodBoard.getSaveThumb());
        vodBoard.setOriginThumb(updatedVodBoard.getOriginThumb());
        vodBoard.setObjectThumb(updatedVodBoard.getObjectThumb());

        vodBoardRepository.save(vodBoard);
    }

    //게시물 삭제 기능
    @Transactional
    public void deleteVodBoard(int boardNo) {
        vodBoardRepository.deleteById(boardNo);
    }


//    -----------첨부파일--------

    //첨부파일 리스트 불러오기
    @Transactional
    public List<VodBoardFile> getBoardFileList(int boardNo) {
        return vodBoardFileRepository.findAllByVodBoardNo(boardNo); // boardNo 해당하는 첨부 file 가져오기
    }

    //디비에 저장되어있는 파일 삭제
    @Transactional
    public void deleteAllFile(int vodNo) {
        vodBoardFileRepository.deleteAllByVodBoardNo(vodNo);
    }


}