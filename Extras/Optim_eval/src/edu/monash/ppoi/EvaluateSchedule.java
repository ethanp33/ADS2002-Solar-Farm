package edu.monash.ppoi;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;

import edu.monash.io.FileUtils;
import edu.monash.io.tsf.TimeSeriesDB;
import edu.monash.ppoi.checker.DateHandler;
import edu.monash.ppoi.checker.ScheduleChecker;
import edu.monash.ppoi.instance.Instance;
import edu.monash.ppoi.solution.Schedule;

public class EvaluateSchedule {

	public static final int year = 2020;
	public static final Month month = Month.SEPTEMBER;
	public static final LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
	public static final Duration duration = Duration.ofDays(month.length(Year.isLeap(year)));
	
	public static void main(String[] args) {

		try {
			System.out.println(checkScheduleObjective(Paths.validationTSF, args[0], args[1], Paths.priceFile));
		} catch (Exception ex) {
			System.out.println(Double.NaN);
			
			if (args.length != 2) {
				System.err.println("Expected two input arguments, instance file and schedule file.");
			} else {
				System.err.println("Error while evaluating schedule.");
				ex.printStackTrace();
			}
		}
	}

	public static double checkScheduleObjective(
			String tsfFilename, String instanceFilename, String scheduleFilename, String priceFilename) {
		return checkSchedule(tsfFilename, instanceFilename, scheduleFilename, priceFilename).getObjective();
	}

	public static ScheduleChecker checkSchedule(
			String tsfFilename, String instanceFilename, String scheduleFilename, String priceFilename) {

		// Read the baseload data. 
		TimeSeriesDB db = TimeSeriesDB.parse(new File(tsfFilename));

		// Read in the instance
		Instance instance = Instance.parseInstance(new File(instanceFilename));
		Schedule schedule = Schedule.parseSchedule(new File(scheduleFilename), instance);
		
		// Extract electricity prices.
		List<Double> prices = FileUtils.readPriceCSV(priceFilename);

		// Date handler helper object.
		DateHandler dates = new DateHandler(year, month);

		return new ScheduleChecker(db, schedule, prices, dates);
	}
}
