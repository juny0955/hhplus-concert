package kr.hhplus.be.server.reservation.usecase.input;

public interface ReservationCreateInput {
	void reserveSeat(ReserveSeatCommand command) throws Exception;

}
