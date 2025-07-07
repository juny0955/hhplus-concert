package kr.hhplus.be.server.concert.adapters.out.persistence.concert;

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
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.infrastructure.persistence.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONCERT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ConcertEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "title", length = 100, nullable = false)
	private String title;

	@Column(name = "artist", length = 50, nullable = false)
	private String artist;

	@Column(name = "open_time", nullable = false)
	private LocalDateTime openTime;

	@Column(name = "sold_out_time")
	private LocalDateTime soldOutTime;

	public static ConcertEntity from(Concert concert) {
		return ConcertEntity.builder()
			.id(concert.id() != null ? concert.id().toString() : null)
			.title(concert.title())
			.artist(concert.artist())
			.openTime(concert.openTime())
			.soldOutTime(concert.soldOutTime())
			.build();
	}

	public Concert toDomain() {
		return Concert.builder()
			.id(UUID.fromString(id))
			.title(title)
			.artist(artist)
			.openTime(openTime)
			.soldOutTime(soldOutTime)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
