package micc.ase.logistics.common.model;

public class DailyServiceTime {

    private int opening;
    private int closing;

    public DailyServiceTime(int from, int to) {
        if (from >= to) {
            throw new IllegalArgumentException("From " + from + " must be before closing " + to + "!");
        }
        this.opening = from;
        this.closing = to;
    }

    public int getOpening() {
        return opening;
    }

    public int getClosing() {
        return closing;
    }

    public int getDuration() {
        return closing - opening;
    }
}

