package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private ParkingSpot parkingSpot;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(null);
		ticket.setParkingSpot(parkingSpot);
	}

	@Test
	@DisplayName("Calculate fare for a CAR")
	public void calculateFareCar() {
		ticket.setOutTime(ticket.getInTime().plusHours(1));
		fareCalculatorService.calculateFare(ticket);
		assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
	}

	@Test
	@DisplayName("Calculate fare for a BIKE")
	public void calculateFareBike() {
		ticket.setOutTime(ticket.getInTime().plusHours(1));
		parkingSpot.setParkingType(ParkingType.BIKE);
		fareCalculatorService.calculateFare(ticket);
		assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
	}

	@Test
	@DisplayName("Calculate fare for UNKNOW type")
	public void calculateFareUnkownType() {
		ticket.setOutTime(ticket.getInTime().plusHours(1));
		parkingSpot.setParkingType(null);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Calculate fare for a CAR in the future")
	public void calculateFareBikeWithFutureInTime() {
		ticket.setOutTime(ticket.getInTime().minusHours(1));
		parkingSpot.setParkingType(ParkingType.BIKE);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Calculate fare for a BIKE less than 1 hour")
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		ticket.setOutTime(ticket.getInTime().plusMinutes(45));
		parkingSpot.setParkingType(ParkingType.BIKE);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Calculate fare for a CAR less than 1 hour")
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		ticket.setOutTime(ticket.getInTime().plusMinutes(45));
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Calculate fare for a CAR more than 1 day")
	public void calculateFareCarWithMoreThanADayParkingTime() {
		ticket.setOutTime(ticket.getInTime().plusDays(1));
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Calculate fare for free less 30 minutes")
	public void calculateFareLessThirtyMinute() {
		ticket.setOutTime(ticket.getInTime().plusMinutes(15));
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(0);
	}

}
