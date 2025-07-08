package kr.hhplus.be.server.adapters.out.persistence.reservation;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.adapters.out.persistence.BaseTimeEntity;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RESERVATION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ReservationEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", length = 36)
	private String id;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "user_id", length = 36, nullable = false)
	private String userId;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "seat_id", length = 36, nullable = false)
	private String seatId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@ColumnDefault("'PENDING'")
	private ReservationStatus status;

	public static ReservationEntity from(Reservation reservation) {
		return ReservationEntity.builder()
			.id(reservation.id() != null ? reservation.id().toString() : null)
			.userId(reservation.userId().toString())
			.seatId(reservation.seatId().toString())
			.status(reservation.status())
			.build();
	}

	public Reservation toDomain() {
		return Reservation.builder()
			.id(UUID.fromString(id))
			.userId(UUID.fromString(userId))
			.seatId(UUID.fromString(seatId))
			.status(status)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
