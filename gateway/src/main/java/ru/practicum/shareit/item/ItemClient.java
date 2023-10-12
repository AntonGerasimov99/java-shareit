package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Integer userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Integer userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByUserId(Integer userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getItemsByUserIdWithPagination(Integer userId, Integer from, Integer size) {
        Map<String, Object> param = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, param);
    }

    public ResponseEntity<Object> searchPagination(String text, Integer from, Integer size) {
        Map<String, Object> param = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", 0, param);
    }

    public ResponseEntity<Object> addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
