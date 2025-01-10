package com.bigproject.fic2toon.board;

import com.bigproject.fic2toon.user.User;
import com.bigproject.fic2toon.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserService userService;

    @Transactional
    public void createBoard(BoardDto boardDto) {
        User user = userService.findById(boardDto.getUserId());

        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .boardType(boardDto.getBoardType()) // int로 설정
                .image(boardDto.getImage()) // 이미지 설정 (null 가능)
                .user(user) // 작성자 설정
                .build();

        boardRepository.save(board); // 게시글 저장
    }


    public List<BoardDto> getBoardList() {
        return boardRepository.findAll().stream()
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getBoardType(),
                        board.getImage(),
                        board.getCreatedTime(),
                        board.getUser() != null ? board.getUser().getId() : null // 작성자 ID 설정
                ))
                .collect(Collectors.toList());
    }

    public BoardDto getBoardById(Long id) {
        return boardRepository.findById(id)
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getBoardType(),
                        board.getImage(),
                        board.getCreatedTime(),
                        board.getUser() != null ? board.getUser().getId() : null // 작성자 ID 설정
                ))
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public List<Map<String, Object>> getBoardListWithIndex() {
        List<BoardDto> boardList = getBoardList();
        List<Map<String, Object>> boardListWithIndex = new ArrayList<>();

        for (int i = 0; i < boardList.size(); i++) {
            BoardDto board = boardList.get(i);
            Map<String, Object> boardWithIndex = new HashMap<>();
            boardWithIndex.put("index", i + 1); // 1부터 시작하는 번호
            boardWithIndex.put("board", board);
            boardListWithIndex.add(boardWithIndex);
        }

        return boardListWithIndex;
    }

    public void deleteBoard(Long board_id) {
        if (!boardRepository.existsById(board_id)) {
            throw new RuntimeException("삭제하려는 게시글이 존재하지 않습니다.");
        }
        boardRepository.deleteById(board_id);
    }

    public void updateBoard(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 기존 데이터를 업데이트
        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setBoardType(boardDto.getBoardType());
        boardRepository.save(board);
    }
}