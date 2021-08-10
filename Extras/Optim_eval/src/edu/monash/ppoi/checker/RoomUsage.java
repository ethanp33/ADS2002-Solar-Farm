package edu.monash.ppoi.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.monash.ppoi.instance.Activity;
import edu.monash.ppoi.instance.Building;
import edu.monash.ppoi.instance.Instance;
import edu.monash.ppoi.instance.Room;
import edu.monash.ppoi.solution.ActivitySchedule;

public class RoomUsage {

	private final List<RoomLimit> limits = new ArrayList<>();

	public RoomUsage(Instance instance, int horizon) {

		List<Building> buildings = instance.getAllBuildings();
		
		for (Room size : Room.values()) {
			limits.add(new RoomLimit(buildings, size, horizon));
		}
	}

	public RoomUsage(List<Integer> largeLimits, List<Integer> smallLimits, int horizon) {

		for (Room size : Room.values()) {
			List<Integer> roomLimits = new ArrayList<>();
			
			switch (size) {
				case Small: roomLimits.addAll(smallLimits); break;
				case Large: roomLimits.addAll(largeLimits); break;
				default: throw new RuntimeException("Function expected only two room sizes.");
			}

			limits.add(new RoomLimit(roomLimits, horizon));
		}
	}

	public boolean isValid() {

		boolean valid = true;
		for (RoomLimit limit : limits)
			valid = valid && limit.isValid();
		return valid;
	}

	public void incrementRandomLimit(Room size, Random rng) {
		getLimit(size).increaseRandomLimit(rng);
	}

	public List<Integer> getMaxLargeUse() {
		return getMaxUse(Room.Large);
	}

	public List<Integer> getMaxSmallUse() {
		return getMaxUse(Room.Small);
	}

	public List<Integer> getLimits(Room size) {
		return getLimit(size).getLimits();
	}

	public List<Integer> getMaxUse(Room size) {
		return getLimit(size).getMaxUse();
	}

	public int getUsage(int time, int bID, Room size) {
		return getLimit(size).getUsage(bID, time);
	}

	public int[] getAvailable(Activity activity, int start) {
		return getLimit(activity).getAvailable(start, activity.getDuration());
	}

	public <A extends Activity> boolean scheduleActivity(Activity activity, int start, Building b) {
		return scheduleActivity(activity, start, expand(b, activity));
	}

	public <A extends Activity> boolean scheduleActivity(ActivitySchedule<A> schedule) {
		return scheduleActivity(schedule.getActivity(), schedule.getStartTime(), schedule.getBuildings());
	}

	public <A extends Activity> boolean scheduleActivity(Activity activity, int start, List<Integer> bIDs) {
		return getLimit(activity).accumulate(bIDs, start, activity.getDuration());
	}

	public <A extends Activity> boolean unscheduleActivity(Activity activity, int start, Building b) {
		return unscheduleActivity(activity, start, expand(b, activity));
	}

	public <A extends Activity> boolean unscheduleActivity(Activity activity, int start, List<Integer> bIDs) {
		return getLimit(activity).retract(bIDs, start, activity.getDuration());
	}

	private RoomLimit getLimit(Activity activity) {
		return getLimit(activity.getRoomSize());
	}

	private RoomLimit getLimit(Room size) {
		return limits.get(size.ordinal());
	}

	private static List<Integer> expand(Building building, Activity activity) {
		
		List<Integer> bIDs = new ArrayList<>();
		
		for (int i = 0; i < activity.getNumRooms(); i++)
			bIDs.add(building.getID());
		
		return bIDs;
	}
}
