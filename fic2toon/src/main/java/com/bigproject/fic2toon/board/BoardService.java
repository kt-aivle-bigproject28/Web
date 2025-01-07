package com.bigproject.fic2toon.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public Board createBoard(BoardDto boardDto) {
        Board board = Board.builder()
                .author_id(boardDto.getAuthor_id())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .image(boardDto.getImage())
                .post_type(boardDto.getPost_type())
                .created_time(boardDto.getCreated_time())
                .build();

        return boardRepository.save(board);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public List<BoardDto> getBoardList() {
        return boardRepository.findAll().stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());
    }

    public BoardDto getBoardById(Long board_id) {
        return boardRepository.findById(board_id)
                .map(BoardDto::new)
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

        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setPost_type(boardDto.getPost_type());
        board.setCreated_time(boardDto.getCreated_time());
        boardRepository.save(board);
    }
}
