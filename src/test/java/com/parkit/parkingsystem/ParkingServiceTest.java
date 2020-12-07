package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;
	private static Ticket ticket;
	ParkingSpot parkingSpot;
	LocalDateTime outTime;
	LocalDateTime inTime;
	private static String vehicleRegNumber = "ABCDEF";

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

			parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket = new Ticket();

			outTime = LocalDateTime.now();
			inTime = outTime.minusHours(1);
			ticket.setInTime(inTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	@DisplayName("Unit test incoming CAR")
	public void processIncomingCarTest() {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

		parkingService.processIncomingVehicle();

		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
		verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	@DisplayName("Unit test incoming BIKE")
	public void processIncomingBikeTest() {
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

		parkingService.processIncomingVehicle();

		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
		verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	@DisplayName("Unit test exiting VEHICULE")
	public void processExitingVehicleTest() {
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		parkingService.processExitingVehicle();

		verify(ticketDAO, times(1)).checkNumberVisitsUser(anyString());
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	@DisplayName("Unit test exiting CAR with discount")
	public void processExitingCarWithDiscountTest() {
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(1);

		parkingService.processExitingVehicle();

		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		assertThat(ticketDAO.getTicket(vehicleRegNumber).getPrice())
				.isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR / 100 * 5));
	}

	@Test
	@DisplayName("Unit test exiting BIKE with discount")
	public void processExitingBikeWithDiscountTest() {
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.checkNumberVisitsUser(anyString())).thenReturn(1);

		parkingService.processExitingVehicle();

		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		assertThat(ticketDAO.getTicket(vehicleRegNumber).getPrice())
				.isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR / 100 * 5));
	}

}
