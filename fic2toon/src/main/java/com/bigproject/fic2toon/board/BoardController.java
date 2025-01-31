package com.bigproject.fic2toon.board;

import com.bigproject.fic2toon.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        model.addAttribute("user", loginUserId); // 사용자 타입 추가
        model.addAttribute("boardList", boardService.getBoardList()); // 게시판 목록 추가
        return "board/board"; // 게시판 뷰 반환
    }

    @GetMapping("/game") // 🔥 "/game" URL 요청이 오면 game.html을 반환
    public String showGamePage() {
        return "board/game"; // 🔥 "game.html"을 찾아서 반환 (templates/game.html)
    }



    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", loginUserId);
        model.addAttribute("board", boardService.getBoardById(id));
        return "board/detail";
    }

    @GetMapping("/form")
    public String createForm(HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", loginUserId); // 사용자 타입 추가
        model.addAttribute("board", new BoardDto());
        return "board/form";
    }

    @PostMapping("/form")
    public String saveForm(@ModelAttribute @Valid BoardDto boardDto,
                           @RequestParam("file") MultipartFile file,
                           HttpSession session,
                           Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", loginUserId); // 사용자 타입 추가
        boardDto.setUserUid(loginUserId);

        // 파일 업로드 처리
        if (!file.isEmpty()) {
            String uploadDir = "C:/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // 업로드 폴더 생성
            }

            try {
                // 파일 이름 생성
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String filePath = uploadDir + fileName;

                // 파일 저장
                file.transferTo(new File(filePath));

                // 경로를 boardDto에 설정
                boardDto.setImage("/uploads/" + fileName);

                // 디버깅: 파일 경로 확인
                System.out.println("File saved: " + boardDto.getImage());

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "파일 업로드에 실패했습니다.");
                return "board/form";
            }
        }

        boardService.createBoard(boardDto);

        return "redirect:/board"; // 게시글 작성 후 게시판으로 리다이렉트
    }

    @DeleteMapping("/{id}/delete")
    public String deleteForm(@PathVariable Long id, HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", loginUserId);

        BoardDto board = boardService.getBoardById(id);

        // 권한 확인: 관리자 또는 작성자만 삭제 가능

        boardService.deleteBoard(id);

        // 삭제 후 게시글 목록으로 리다이렉트
        return "redirect:/board";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 사용자 ID를 가져옴

        if (loginUserId == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loginUserId);

        BoardDto board = boardService.getBoardById(id);

        if (!loginUserId.equals(board.getUserUid())) {
            model.addAttribute("error", "수정 권한이 없습니다.");
            return "board/board";
        }

        model.addAttribute("board", board);
        return "board/update";
    }

    @PostMapping("/update/{id}")
    public String updateForm(@PathVariable Long id,
                             @ModelAttribute BoardDto boardDto,
                             @RequestParam MultipartFile file,
                             HttpSession session,
                             Model model) {
        String loginUserId = (String) session.getAttribute("loginUser");

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", loginUserId);

        // 파일 업로드 처리
        if (!file.isEmpty()) {
            String uploadDir = "C:/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // 업로드 폴더 생성
            }

            try {
                // 파일 이름 생성
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String filePath = uploadDir + fileName;

                // 파일 저장
                file.transferTo(new File(filePath));

                // 경로를 boardDto에 설정
                boardDto.setImage("/uploads/" + fileName);

                // 디버깅: 파일 경로 확인
                System.out.println("File saved: " + boardDto.getImage());

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "파일 업로드에 실패했습니다.");
                return "board/form";
            }
        }

        boardService.updateBoard(id, boardDto);
        return "redirect:/board";
    }

}