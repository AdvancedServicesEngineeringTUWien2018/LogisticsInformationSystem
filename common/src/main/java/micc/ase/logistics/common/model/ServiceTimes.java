package micc.ase.logistics.common.model;

public class ServiceTimes {

    private DailyServiceTime monday;
    private DailyServiceTime tuesday;
    private DailyServiceTime wednesday;
    private DailyServiceTime thursday;
    private DailyServiceTime friday;
    private DailyServiceTime saturday;
    private DailyServiceTime sunday;
    private DailyServiceTime[] week;


    public ServiceTimes(DailyServiceTime weekdays, DailyServiceTime saturday) {
        this(weekdays, weekdays, weekdays, weekdays, weekdays,
                saturday, null);
    }


    public ServiceTimes(DailyServiceTime monday, DailyServiceTime tuesday, DailyServiceTime wednesday, DailyServiceTime thursday, DailyServiceTime friday, DailyServiceTime saturday, DailyServiceTime sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        week = new DailyServiceTime[] { monday, tuesday, wednesday, thursday, friday, saturday, sunday };
    }

    /**
     *
     * @param day 0 <= day <= 6
     * @return
     */
    public boolean hasOpened(int day) {
        return week[day] != null;
    }

    public DailyServiceTime[] getWeek() {
        return week;
    }

    public void setWeek(DailyServiceTime[] week) {
        this.week = week;
    }



}
