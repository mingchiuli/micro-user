package org.chiu.micro.user.event;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.chiu.micro.user.constant.UserOperateEnum;

@Data
@AllArgsConstructor
public class UserIndexMessage implements Serializable {

    private Long userId;

    private UserOperateEnum userOperateEnum;

}
