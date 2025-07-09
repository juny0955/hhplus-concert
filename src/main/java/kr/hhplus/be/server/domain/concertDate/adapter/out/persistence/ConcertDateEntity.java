package kr.hhplus.be.server.domain.concertDate.adapter.out.persistence;

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
import kr.hhplus.be.server.common.config.jpa.BaseTimeEntity;
import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;
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

	public static ConcertDateEntity from(ConcertDate concertDate) {
		return ConcertDateEntity.builder()
			.id(concertDate.id() != null ? concertDate.id().toString() : null)
			.concertId(concertDate.concertId().toString())
			.date(concertDate.date())
			.deadline(concertDate.deadline())
			.build();
	}

	public ConcertDate toDomain() {
		return ConcertDate.builder()
			.id(UUID.fromString(id))
			.concertId(UUID.fromString(concertId))
			.date(date)
			.deadline(deadline)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
