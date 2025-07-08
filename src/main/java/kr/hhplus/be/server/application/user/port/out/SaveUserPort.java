package kr.hhplus.be.server.application.user.port.out;

import kr.hhplus.be.server.domain.user.User;

public interface SaveUserPort {
	User saveUser(User user);
}
