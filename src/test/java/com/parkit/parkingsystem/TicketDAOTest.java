package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	private static TicketDAO ticketDAO;
	private static Ticket ticket;
	private static String vehicleRegNumber = "ABCDEF";

	@BeforeAll
	private static void setUp() {
		ticketDAO = new TicketDAO();
	}

	@BeforeEach
	private void setUpPerTest() {
		dataBasePrepareService.clearDataBaseEntries();
		TicketDAO.dataBaseConfig = dataBaseTestConfig;
		ticket = new Ticket();
		ticket.getId();
		ticket.setInTime(LocalDateTime.now().minusHours(1));
		ticket.setOutTime(LocalDateTime.now());
		ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Unit test to save in DB and to get ticket from DB")
	public void saveTicketTest() {
		ticketDAO.saveTicket(ticket);
		ticket = ticketDAO.getTicket(vehicleRegNumber);
		assertThat(ticket).isNotNull();
	}

	@Test
	@DisplayName("Unit test to update ticket in DB")
	public void updateTicketTest() {
		assertThat(ticketDAO.updateTicket(ticket)).isEqualTo(true);
	}

	@Test
	@DisplayName("Unit test to check old tickets in DB")
	public void checkNumberVisitsUserTest() throws InterruptedException {
		ticketDAO.saveTicket(ticket);
		ticketDAO.saveTicket(ticket);
		assertThat(ticketDAO.checkNumberVisitsUser(vehicleRegNumber)).isEqualTo(2);
	}
}
