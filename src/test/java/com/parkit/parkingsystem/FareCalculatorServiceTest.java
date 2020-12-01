package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    private Date inTime;
    private Date outTime;
    private ParkingSpot parkingSpot;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
    	ticket = new Ticket();
    	inTime = new Date();
    	outTime = new Date();
    	parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	ticket.setInTime(inTime);
    	ticket.setOutTime(outTime);
    	ticket.setParkingSpot(parkingSpot);
    }

    @Test
    public void calculateFareCar(){
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){
    	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    	parkingSpot.setParkingType(ParkingType.BIKE);
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType(){
    	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    	parkingSpot.setParkingType(null);
    	assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
    	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    	parkingSpot.setParkingType(ParkingType.BIKE);
    	assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
    	parkingSpot.setParkingType(ParkingType.BIKE);
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
    	inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(ticket.getPrice()).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
    }

}
