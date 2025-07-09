package kr.hhplus.be.server.domain.user.port.out;

import kr.hhplus.be.server.domain.user.domain.User;

public interface SaveUserPort {
	User saveUser(User user);
}
