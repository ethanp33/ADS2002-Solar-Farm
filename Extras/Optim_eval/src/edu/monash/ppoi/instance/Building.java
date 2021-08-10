package edu.monash.ppoi.instance;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.monash.io.tsf.RecurringTimeSeriesView;
import edu.monash.io.tsf.TimeSeriesDB;
import edu.monash.ppoi.checker.IntLimit;

public class Building {

	private final int id;
	private final int small;
	private final int large;

	private Solar panel;
	private Battery battery;

	public Building(int id, int small, int large) {
		
		this.id = id;
		this.small = small;
		this.large = large;
		
		this.panel = null;
		this.battery = null;
	}

	public void attach(Solar s) {
		this.panel = s;
	}

	public void attach(Battery b) {
		this.battery = b;
	}

	public int getID() {
		return id;
	}

	public boolean hasSolar() {
		return panel != null;
	}
	
	public Solar getSolar() {
		return panel;
	}

	public Battery getBattery() {
		return battery;
	}

	public int getNumRooms(Room size) {
		int count;
		
		switch (size) {
			case Small: count = small; break;
			case Large: count = large; break;
			default:    count = 0; break;
		}
		
		return count;
	}

	/**
	 * Returns the base load of a building, including subtracted solar load.
	 */
	public List<Double> getBaseLoad(TimeSeriesDB db, LocalDateTime start, Duration duration) {

		String bldgName = "Building"+this.getID();
		RecurringTimeSeriesView base = db.getSeries(bldgName).getView(start, duration);
		RecurringTimeSeriesView solar = null;

		if (hasSolar()) {
			String solarName = "Solar"+panel.getSolarID();
			solar = db.getSeries(solarName).getView(start, duration);
		}
		
		// Accumulate base loads into an array of loads.
		LocalDateTime now = start;
		List<Double> baseload = new ArrayList<>();

		while (start.plus(duration).isAfter(now)) {
			
			double baseLoad = base.next().getValue();
			double solarLoad = solar == null ? 0d : solar.next().getValue();
			
			baseLoad = Double.isNaN(baseLoad) ? 0d : baseLoad;
			solarLoad = Double.isNaN(solarLoad) ? 0d : solarLoad;
			
			baseload.add(baseLoad + solarLoad);
			now = now.plus(db.getDelta());
		}
		
		return baseload;
	}

	public List<IntLimit> getRoomLimits(int horizon) {

		List<IntLimit> limits = new ArrayList<>();
		
		for (Room size : Room.values()) {
			limits.add(new IntLimit(getNumRooms(size), horizon));
		}

		return limits;
	}

	@Override
	public int hashCode() {
		return getID();
	}
	
	@Override
	public String toString() {
		return String.format("b %d %d %d", id, small, large);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Building) ? this.internalEquals((Building) obj) : false;
	}

	private boolean internalEquals(Building that) {
		return this.getID() == that.getID();
	}

	public static Building parseBuilding(String line) {

		String[] parts = line.split(" ");
		if (parts.length != 4 || !"b".equals(parts[0])) {
			throw new RuntimeException("Unexpected format for building.");
		}

		return new Building(
				Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]),
				Integer.parseInt(parts[3]));
	}
}
