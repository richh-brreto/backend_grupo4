package school.sptech.back_end_PI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.chat.ChatRequest;
import school.sptech.back_end_PI.dto.chat.ChatResponse;
import school.sptech.back_end_PI.services.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_TELA_DASHBOARD')")
    public ResponseEntity<ChatResponse> conversar(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.conversar(request));
    }
}