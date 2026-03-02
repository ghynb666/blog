package com.blog.vo;

import com.blog.entity.User;
import lombok.Data;

@Data
public class LoginVO {

    private String token;

    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;

        public static UserInfo fromUser(User user) {
            UserInfo info = new UserInfo();
            info.setId(user.getId());
            info.setUsername(user.getUsername());
            info.setNickname(user.getNickname());
            info.setAvatar(user.getAvatar());
            return info;
        }
    }
}
