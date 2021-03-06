package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Class to save, to get, to update and to check a ticket in DB
 * 
 * @author JohnDWorld
 *
 */
public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	private LocalDateTime inTime = null;
	private LocalDateTime outTime = null;

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public void setDataBaseConfig(final DataBaseConfig dbConfig) {
		this.dataBaseConfig = dbConfig;
	}

	/**
	 * Method to save a ticket in DB
	 * 
	 * @param ticket
	 * @return boolean true (ps.execute()) or false it doesn't save the ticket
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);

			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			// ps.setInt(1,ticket.getId());
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());

			inTime = ticket.getInTime();
			ps.setObject(4, inTime);

			outTime = ticket.getOutTime();
			ps.setObject(5, outTime);
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
			dataBaseConfig.closePreparedStatement(ps);
		}
		return false;
	}

	/**
	 * Method to get a ticket from DB
	 * 
	 * @param vehicleRegNumber
	 * @return ticket who are in the DB
	 */
	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));

				inTime = ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());

				outTime = ticket.setOutTime((rs.getTimestamp(5) == null) ? null : rs.getTimestamp(5).toLocalDateTime());
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	/**
	 * Method to update a ticket in DB
	 * 
	 * @param ticket
	 * @return boolean true (ps.execute()) or false if id doesn't update the ticket
	 *         in DB
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());

			ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));

			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * Method to check the old tickets in DB
	 * 
	 * @param vehicleRegNumber
	 * @return numberOfUserVisits number of all old tickets find
	 */
	public int checkNumberVisitsUser(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int numberOfUserVisits = 0;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.CHECK_EXISTING_OLD_TICKETS);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				numberOfUserVisits = rs.getInt(1);
			}
		} catch (Exception ex) {
			logger.error("Error during check existing old tickets process.", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		}
		return numberOfUserVisits;
	}
}
