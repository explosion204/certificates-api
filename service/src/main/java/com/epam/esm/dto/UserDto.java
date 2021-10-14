package com.epam.esm.dto;

import com.epam.esm.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserDto extends IdentifiableDto {
    private String username;
    private String password;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        return userDto;
    }
}
