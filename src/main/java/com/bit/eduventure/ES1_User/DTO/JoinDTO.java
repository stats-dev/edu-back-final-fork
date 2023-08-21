package com.bit.eduventure.ES1_User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDTO {
    private UserDTO userDTO;
    private UserDTO parentDTO;
}