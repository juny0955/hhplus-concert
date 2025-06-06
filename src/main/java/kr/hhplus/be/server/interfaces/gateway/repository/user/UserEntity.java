package kr.hhplus.be.server.interfaces.gateway.repository.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.gateway.repository.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class UserEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "amount", precision = 10, nullable = false)
	@ColumnDefault("0")
	private BigDecimal amount;

	public User toDomain() {
		return User.builder()
			.id(UUID.fromString(id))
			.amount(amount)
			.createdAt(getCreatedAt())
			.updatedAt(getUpdatedAt())
			.build();
	}

	public UserEntity charge(BigDecimal point) {
		amount = amount.add(point);
		return this;
	}
}
