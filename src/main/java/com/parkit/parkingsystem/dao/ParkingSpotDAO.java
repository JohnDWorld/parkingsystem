package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * Class to get and update parking spot in DB.
 * 
 * @author JohnDWorld
 *
 */
public class ParkingSpotDAO {

	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public void setDataBaseConfig(final DataBaseConfig dbConfig) {
		this.dataBaseConfig = dbConfig;
	}

	/**
	 * Method to get the next parking spot available.
	 * 
	 * @param parkingType
	 * @return result the number of the next parking spot available or -1 if no
	 *         parking spot available
	 */
	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

	/**
	 * Method to update the availability of parking spot.
	 * 
	 * @param parkingSpot
	 * @return boolean true (updateRowCount == 1) or false if it doesn't update
	 *         parking spot
	 */
	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability of that parking slot
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

}
