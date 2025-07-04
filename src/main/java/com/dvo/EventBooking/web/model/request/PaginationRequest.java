package com.dvo.EventBooking.web.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
    @Min(value = 1, message = "pageSize должно быть больше 0")
    @NotNull(message = "pageSize должно быть указано")
    private Integer pageSize;

    @Min(value = 0, message = "pageNumber должно быть заполнено")
    @NotNull(message = "pageNumber должно быть указано")
    private Integer pageNumber;

    public PageRequest pageRequest() {
        return PageRequest.of(pageNumber, pageSize);
    }
}
