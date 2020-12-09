package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpot parkingSpot;
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingService parkingService;
	private static String vehicleRegNumber = "ABCDEF";

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		;
		ticketDAO = new TicketDAO();
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		;
	}

	@Test
	@DisplayName("Integration test incoming car")
	public void testParkingACar() throws Exception {
		parkingService.processIncomingVehicle();
		parkingSpot = ticketDAO.getTicket(vehicleRegNumber).getParkingSpot();
		assertThat(ticketDAO.getTicket(vehicleRegNumber)).isNotNull();
		assertThat(parkingSpot.isAvailable()).isEqualTo(false);
	}

	@Test
	@DisplayName("Integration test exiting car")
	public void testParkingLotExit() throws InterruptedException {
		parkingService.processIncomingVehicle();
		Thread.sleep(1000);
		parkingService.processExitingVehicle();
		assertThat(ticketDAO.getTicket(vehicleRegNumber).getPrice()).isNotNull();
	}

}
