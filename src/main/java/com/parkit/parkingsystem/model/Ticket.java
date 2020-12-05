package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private LocalDateTime inTime;
	private LocalDateTime outTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public LocalDateTime getInTime() {
		return inTime;
	}

	public LocalDateTime setInTime(LocalDateTime inTime) {
		return this.inTime = inTime;
	}

	public LocalDateTime getOutTime() {
		return outTime;
	}

	public LocalDateTime setOutTime(LocalDateTime outTime) {
		return this.outTime = outTime;
	}
}
