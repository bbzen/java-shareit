package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.item.model.ItemInputDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createItem(Long sharerUserId, ItemInputDto itemInputDto) {
        return post("", sharerUserId, itemInputDto);
    }

    public ResponseEntity<Object> updateItem(Long sharerUserId, Long itemId, ItemInputDto item) {
        return patch("/" + itemId, sharerUserId, item);
    }

    public ResponseEntity<Object> findAllMatchesText(String text)  {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search", null, parameters);
    }

    public ResponseEntity<Object> findItem(Long sharerUserId, Long itemId) {
        return get("/" + itemId, sharerUserId);
    }

    public ResponseEntity<Object> saveComment(Long sharerUserId, Long itemId, CommentInputDto commentDto) {
        return post("/" + itemId + "/comment", sharerUserId, commentDto);
    }

    public ResponseEntity<Object> findAllUserItems(Long sharerUserId) {
        return get("", sharerUserId);
    }
}
