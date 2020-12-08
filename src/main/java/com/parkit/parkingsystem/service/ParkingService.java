package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * Class to contain services of parking
 * 
 * @author JohnDWorld
 *
 */
public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");
	private static final int DAYS_FOR_RECURRENCE = 1;

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private int numberOfVisit;
	private double priceDiscount;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * Method to capture informations, to check and to save ticket in DB
	 */
	public void processIncomingVehicle() {
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehicleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);// allot this parking space and mark it's availability as
															// false

				LocalDateTime inTime = LocalDateTime.now();
				String inTimeFormatter = inTime.format(formatter);

				Ticket ticket = new Ticket();
				// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
				// ticket.setId(ticketID);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);

				ticket.setInTime(inTime);
				ticket.setOutTime(null);
				ticketDAO.saveTicket(ticket);

				numberOfVisit = ticketDAO.checkNumberVisitsUser(vehicleRegNumber);
				if (numberOfVisit >= DAYS_FOR_RECURRENCE) {
					System.out.println(
							"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount");
				}
				System.out.println("Generated Ticket and saved in DB");
				System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
				System.out
						.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is: " + inTimeFormatter);
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	/**
	 * Method to get the vehicle registration number
	 * 
	 * @return vehicle registration number enter by user
	 * @throws Exception
	 */
	private String getVehicleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * Method to get the next parking spot number available and to create a parking
	 * spot for user
	 * 
	 * @return parkingSpot
	 */
	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	/**
	 * Method to get the vehicle type enter by user
	 * 
	 * @return parking type
	 */
	private ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	/**
	 * Method to update out time and calculate the fare for a user
	 */
	public void processExitingVehicle() {
		try {
			String vehicleRegNumber = getVehicleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

			LocalDateTime outTime = LocalDateTime.now();
			String outTimeFormatter = outTime.format(formatter);

			numberOfVisit = ticketDAO.checkNumberVisitsUser(vehicleRegNumber);

			ticket.setOutTime(outTime);
			fareCalculatorService.calculateFare(ticket);
			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);

				if (numberOfVisit >= DAYS_FOR_RECURRENCE) {
					priceDiscount = ticket.getPrice();
					ticket.setPrice(priceDiscount - (priceDiscount / 100 * 5));
					System.out.println("Please pay the parking fare with 5% discount:" + ticket.getPrice());
					System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is: "
							+ outTimeFormatter);
				} else {
					System.out.println("Please pay the parking fare:" + ticket.getPrice());
					System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is: "
							+ outTimeFormatter);
				}

			} else {
				System.out.println("Unable to update ticket information. Error occurred");
			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}
}
