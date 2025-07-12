package kr.hhplus.be.server.user.port.out;

import kr.hhplus.be.server.user.domain.User;

public interface SaveUserPort {
	User saveUser(User user);
}
