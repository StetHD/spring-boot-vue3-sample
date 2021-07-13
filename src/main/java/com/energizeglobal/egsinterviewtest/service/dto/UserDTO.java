package com.energizeglobal.egsinterviewtest.service.dto;

import com.energizeglobal.egsinterviewtest.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    public UserDTO(User user) {
        this.id = user.getId();

        this.username = user.getUsername();
    }
}
