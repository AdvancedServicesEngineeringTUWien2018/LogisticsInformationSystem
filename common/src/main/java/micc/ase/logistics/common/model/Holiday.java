package micc.ase.logistics.common.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Holiday {

    private String name;
    private LocalDate date;

    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "name='" + name + '\'' +
                ", date=" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                '}';
    }
}
