package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.*;
import ru.otus.hw.services.CommentService;
import java.time.Duration;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@DisplayName("Контроллер для работы с комментариями ")
@WebFluxTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private CommentService commentService;

    private final List<CommentReadDto> comments = List.of(
            CommentReadDto.builder().id(1L).text("Test comment 1").bookId(100L).build(),
            CommentReadDto.builder().id(2L).text("Test comment 2").bookId(100L).build());

    @Test
    void shouldGetCommentById() {
        CommentReadDto comment = comments.get(0);
        when(commentService.findById(1L)).thenReturn(Mono.just(comment));
        CommentReadDto returnedComment = webClient.get().uri("/api/comments/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedComment).isNotNull()
                .isEqualTo(comment);
        Mockito.verify(commentService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnErrorWhenCommentNotFound() {
        when(commentService.findById(1L)).thenReturn(Mono.empty());
        webClient.get().uri("/api/comments/1")
                .exchange()
                .expectStatus().isNotFound();
        Mockito.verify(commentService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnAllCommentsByBookId() {
        when(commentService.findByBookId(100L)).thenReturn(Flux.fromIterable(comments));
        List<CommentReadDto> returnedComments = webClient.get().uri("/api/comments/book/100")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CommentReadDto.class)
                .getResponseBody()
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();
        assertThat(returnedComments)
                .hasSize(comments.size())
                .containsExactlyInAnyOrderElementsOf(comments);
        Mockito.verify(commentService, times(1)).findByBookId(100L);
    }

    @Test
    void shouldSaveComment() {
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder()
                .text("Test comment text")
                .bookId(10L)
                .build();
        CommentReadDto commentReadDto = CommentReadDto.builder()
                .id(1L)
                .text(commentCreateEditDto.getText())
                .bookId(commentCreateEditDto.getBookId())
                .build();
        when(commentService.create(commentCreateEditDto)).thenReturn(Mono.just(commentReadDto));

        CommentReadDto returnedComment = webClient.post().uri("/api/comments")
                .contentType(APPLICATION_JSON)
                .bodyValue(commentCreateEditDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CommentReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedComment).isNotNull()
                .isEqualTo(commentReadDto);
        Mockito.verify(commentService, times(1)).create(commentCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenCreateParamsNotValid() {
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder()
                .text("")
                .bookId(null)
                .build();
        webClient.post().uri("/api/comments")
                .contentType(APPLICATION_JSON)
                .bodyValue(commentCreateEditDto)
                .exchange()
                .expectStatus().isBadRequest();
        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void shouldUpdateComment() {
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder()
                .text("Test comment text")
                .bookId(10L)
                .build();
        CommentReadDto commentReadDto = CommentReadDto.builder()
                .id(1L)
                .text(commentCreateEditDto.getText())
                .bookId(commentCreateEditDto.getBookId())
                .build();
        when(commentService.update(1L, commentCreateEditDto)).thenReturn(Mono.just(commentReadDto));

        CommentReadDto returnedComment = webClient.put().uri("/api/comments/1")
                .contentType(APPLICATION_JSON)
                .bodyValue(commentCreateEditDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedComment).isNotNull()
                .isEqualTo(commentReadDto);
        Mockito.verify(commentService, times(1)).update(1L, commentCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenUpdatedCommentParamsNotValid() {
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder()
                .text("")
                .bookId(null)
                .build();
        webClient.put().uri("/api/comments/1")
                .contentType(APPLICATION_JSON)
                .bodyValue(commentCreateEditDto)
                .exchange()
                .expectStatus().isBadRequest();
        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void shouldDeleteComment() {
        when(commentService.deleteById(1L)).thenReturn(Mono.empty());
        webClient.delete().uri("/api/comments/1")
                .exchange()
                .expectStatus().isNoContent();
        Mockito.verify(commentService, times(1)).deleteById(1L);
    }
}