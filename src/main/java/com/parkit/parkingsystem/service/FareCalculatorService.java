package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Class to contain the fare's calculator
 * 
 * @author JohnDWorld
 *
 */
public class FareCalculatorService {

	/**
	 * Method to calculate the fare with duration staying to park
	 * 
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		LocalDateTime inHour = ticket.getInTime();
		LocalDateTime outHour = ticket.getOutTime();
		Duration durationBetweenInTimeAndOutTime = Duration.between(inHour, outHour);
		double diffMin = durationBetweenInTimeAndOutTime.toMinutes();
		double diffHour = diffMin / 60;

		if (diffHour > 0.5) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(diffHour * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(diffHour * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}

	}
}