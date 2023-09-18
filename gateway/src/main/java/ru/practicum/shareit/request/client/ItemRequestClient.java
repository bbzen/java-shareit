package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.request.model.ItemRequestInputDto;

import java.util.Map;

public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(Long requesterUserId, ItemRequestInputDto itemRequestInputDto) {
        return post("", requesterUserId, itemRequestInputDto);
    }

    public ResponseEntity<Object> findOwnerRequests(Long requesterUserId) {
        return get("", requesterUserId);
    }

    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAllPaging(Long requesterUserId, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", requesterUserId, params);
    }
}
