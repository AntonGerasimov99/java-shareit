package ru.practicum.shareit.request.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Integer userId);

    ItemRequestDto findByUserIdAndRequestId(Integer userId, Integer requestId);

    List<ItemRequestDto> findAllByUserId(Integer userId);

    List<ItemRequestDto> findAllPageableByUserId(Integer userId, Integer from, Integer size);
}
