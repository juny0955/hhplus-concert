package kr.hhplus.be.server.interfaces.gateway.repository.concertDate;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.interfaces.gateway.repository.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONCERT_DATE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ConcertDateEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", length = 36)
	private String id;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "concert_id", length = 36, nullable = false)
	private String concertId;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name = "deadline", nullable = false)
	private LocalDateTime deadline;

	public ConcertDate toDomain() {
		return ConcertDate.builder()
			.id(UUID.fromString(id))
			.concertId(UUID.fromString(concertId))
			.date(date)
			.deadline(deadline)
			.createdAt(getCreatedAt())
			.updatedAt(getUpdatedAt())
			.build();
	}
}
