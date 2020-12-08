package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to capture the the user entry
 * 
 * @author JohnDWorld
 *
 */
public class InputReaderUtil {

	private static Scanner scan = new Scanner(System.in);
	private static final Logger logger = LogManager.getLogger("InputReaderUtil");

	/**
	 * Method to read the selection menu
	 * 
	 * @return input the number from user or -1 if it doesn't read the entry
	 */
	public int readSelection() {
		try {
			int input = Integer.parseInt(scan.nextLine());
			return input;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter valid number for proceeding further");
			return -1;
		}
	}

	/**
	 * Method to capture the vehicle registration number
	 * 
	 * @return vehicleRegNumber
	 * @throws Exception
	 */
	public String readVehicleRegistrationNumber() throws Exception {
		try {
			String vehicleRegNumber = scan.nextLine();
			if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
				throw new IllegalArgumentException("Invalid input provided");
			}
			return vehicleRegNumber;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
			throw e;
		}
	}

}
