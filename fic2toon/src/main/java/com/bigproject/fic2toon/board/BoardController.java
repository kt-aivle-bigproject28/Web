package com.bigproject.fic2toon.board;

import com.bigproject.fic2toon.user.User;
import com.bigproject.fic2toon.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    @GetMapping
    public String getBoardList(HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 정보를 데이터베이스에서 조회
        Optional<User> optionalUser = userService.findByUid(loginUserId); // 사용자 ID로 사용자 정보 조회

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
        }

        User user = optionalUser.get(); // Optional에서 User 객체 추출

        model.addAttribute("userType", user.getType()); // 사용자 타입 추가
        model.addAttribute("boardList", boardService.getBoardListWithIndex()); // 게시판 목록 추가
        return "board/board"; // 게시판 뷰 반환
    }

    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 게시글 상세 보기
        model.addAttribute("board", boardService.getBoardById(id));
        return "board/detail";
    }

    @GetMapping("/form")
    public String createForm(HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 정보를 데이터베이스에서 조회
        Optional<User> optionalUser = userService.findByUid(loginUserId); // 사용자 ID로 사용자 정보 조회

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
        }

        User user = optionalUser.get();

        model.addAttribute("userType", user.getType());
        model.addAttribute("board", new BoardDto());
        return "board/form";
    }

    @PostMapping("/form")
    public String saveBoard(@ModelAttribute @Valid BoardDto boardDto,
                            HttpSession session,
                            Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 정보를 데이터베이스에서 조회
        Optional<User> optionalUser = userService.findByUid(loginUserId); // 사용자 ID로 사용자 정보 조회

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
        }

        // 작성자의 ID 설정
        boardDto.setUserId(optionalUser.get().getId());

        // 선택된 카테고리 처리
        BoardType boardType = boardDto.getType(); // BoardType으로 설정
        if (boardType == null) {
            model.addAttribute("error", "유효하지 않은 카테고리입니다.");
            return "board/form"; // 오류 발생 시 폼으로 돌아감
        }

        // 게시글 생성
        boardService.createBoard(boardDto);
        return "redirect:/board"; // 게시글 작성 후 게시판으로 리다이렉트
    }

    @DeleteMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // 권한 확인: 관리자 또는 작성자만 삭제 가능
        BoardDto board = boardService.getBoardById(id);
        /*
        if (user.getType() != 1 && !board.getUserId().equals(user.getId())) {
            model.addAttribute("error", "삭제 권한이 없습니다.");
            return "error/unauthorized";
        }

         */

        boardService.deleteBoard(id);

        // 삭제 후 게시글 목록으로 리다이렉트
        return "redirect:/board";
    }


    @GetMapping("/{id}/edit")
    public String editBoard(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // 권한 확인: 관리자 또는 작성자만 수정 가능
        BoardDto board = boardService.getBoardById(id);
        /*
        if (user.getType() != 1 && !board.getUserId().equals(user.getId())) {
            model.addAttribute("error", "수정 권한이 없습니다.");
            return "error/unauthorized";
        }

         */

        model.addAttribute("board", board);
        model.addAttribute("userType", user.getType());
        return "board/form"; // 수정 폼으로 이동
    }

    @PostMapping("/{id}")
    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardDto boardDto, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 권한 확인
        BoardDto existingBoard = boardService.getBoardById(id);
        /*
        if (user.getType() != 1 && !existingBoard.getUserId().equals(user.getId())) {
            model.addAttribute("error", "수정 권한이 없습니다.");
            return "error/unauthorized";
        }

         */

        // 업데이트 수행
        boardService.updateBoard(id, boardDto);
        return "redirect:/board";
    }
}