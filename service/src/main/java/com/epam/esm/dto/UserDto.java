package com.epam.esm.dto;

import com.epam.esm.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserDto extends IdentifiableDto {
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

    public User toUser() {
        User user = new User();

        user.setId(getId());
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole().name());

        return userDto;
    }
}
