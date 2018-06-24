package micc.ase.logistics.simulation

import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun main(args: Array<String>) {

    val simulation = Simulation()

    val kafkaEndpoint = when(args.size) {
        2 -> "localhost:9092"
        3,4 -> args[2]
        else -> throw IllegalArgumentException("Must provide the following arguments: fromDate toDate [kafkaEndpoint]   " +
                "where fromDate and toDate must are of the format 'yyyy-MM-dd and must be between 2015-01-01 and 2017-12-31'")
    }

    val dryRun = if (args.size == 4) {
        args[3] == "dry"
    } else {
        false
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val from = LocalDate.parse(args[0], formatter)
    val to = LocalDate.parse(args[1], formatter)

    simulation.simulate(from, to, kafkaEndpoint, dryRun)
}