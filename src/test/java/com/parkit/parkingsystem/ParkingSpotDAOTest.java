package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

class ParkingSpotDAOTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	private static ParkingSpotDAO parkingSpotDAO;
	private static ParkingSpot parkingSpot;
	private static ParkingType parkingType = ParkingType.CAR;

	@BeforeAll
	private static void setUp() {
		parkingSpotDAO = new ParkingSpotDAO();
	}

	@BeforeEach
	private void setUpPerTest() {
		dataBasePrepareService.clearDataBaseEntries();
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		;
		parkingSpot = new ParkingSpot(0, parkingType, true);
	}

	@Test
	@DisplayName("Unit test to get the next available place for CAR")
	public void getNextAvailableSlotCarTest() {
		int numberOfPlace = parkingSpotDAO.getNextAvailableSlot(parkingType);
		assertThat(numberOfPlace).isEqualTo(1);
	}

	@Test
	@DisplayName("Unit test to get the next available place for BIKE")
	public void getNextAvailableSlotBikeTest() {
		int numberOfPlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
		assertThat(numberOfPlace).isEqualTo(4);
	}

	@Test
	@DisplayName("Unit test to check to change the availability of parking spot")
	public void updateParkingTest() {
		parkingSpot.setAvailable(false);
		assertThat(parkingSpotDAO.updateParking(parkingSpot)).isEqualTo(false);
	}

}
