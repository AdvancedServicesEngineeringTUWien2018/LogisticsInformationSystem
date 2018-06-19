package micc.ase.logistics.simulation

import micc.ase.logistics.common.model.*
import micc.ase.logistics.simulation.model.DemandDistribution
import micc.ase.logistics.simulation.model.live.LiveCustomer
import micc.ase.logistics.simulation.model.SimulatedCustomer
import micc.ase.logistics.simulation.model.SimulatedSupplier
import micc.ase.logistics.simulation.model.live.LiveVehicle
import micc.ase.logistics.simulation.model.live.VehicleStatus
import micc.ase.logistics.simulation.util.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Simulation {

    enum class SeasonType {
        EVEN,
        SPRING,
        CHRISTMAS
    }

    enum class BuyerHabit {
        NONE,
        WEEKEND,
        WEEKDAY
    }

    enum class CloseType {
        NONE,
        NEED_MORE_BEFORE_CLOSES
    }

    enum class MonthType {
        NONE,
        BEGIN_OF_MONTH,
        END_OF_MONTH
    }

    fun simulate() {

        val obiKrems = Customer("OBI Krems", 48.406988, 15.654453)
        val bellafloraKrems = Customer("Bellaflora Krems", 48.407813, 15.660239)
        val lagerhausTulln = Customer("Lagerhaus Tulln", 48.321657, 16.078680)
        val obiTulln = Customer("OBI Tulln", 48.317947, 16.019311)
        val fetterStockerau = Customer("Fetter Stockerau", 48.389257, 16.182436)
        val obiStockerau = Customer("OBI Stockerau", 48.387829, 16.181327)
        val fetterHollabrunn = Customer("Fetter Hollabrunn", 48.564564, 16.075602)
        val lagerhausHollabrunn = Customer("Lagerhaus Hollabrunn", 48.569676, 16.080248)
        val lagerhausMistelbach = Customer("Lagerhaus Mistelbach", 48.564714, 16.562467)
        val lagerhausLaa = Customer("Lagerhaus Laa/Thaya", 48.714777, 16.378221)
        val lagerhausEggenburg = Customer("Lagerhaus Eggenburg", 48.645971, 15.823033)
        val lagerhausHorn = Customer("Lagerhaus Horn", 48.663899, 15.639511)
        val obiNeusiedl = Customer("OBI Neusiedl/See", 47.969247, 16.839363)
        val lagerhausMattersburg = Customer("Lagerhaus Mattersburg", 47.733876, 16.410021)
        val lagerhausEisenstadt = Customer("Lagerhaus Eisenstadt", 47.838738, 16.527066)
        val obiBaden = Customer("OBI Baden", 47.976119, 16.281574)
        val lagerhausSchwechat = Customer("Lagerhaus Schwechat", 48.144759, 16.466031)
        val lagerhausGerasdorf = Customer("Lagerhaus Gerasdorf", 48.297028, 16.484233)
        val obiHadikgasse = Customer("OBI Wien Hadikgasse", 48.192676, 16.282503)
        val obiTriesterstrasse = Customer("OBI Wien Triesterstraße", 48.178657, 16.357772)
        val obiVoesendorf = Customer("OBI Vösendorf", 48.118470, 16.314756)
        val obiStMarx = Customer("OBI Wien St. Marx", 48.186285, 16.410233)
        val obiKlosterneuburg = Customer("OBI Klosterneuburg", 48.294656, 16.337393)

        val allCustomers = setOf(obiKrems, bellafloraKrems, lagerhausTulln, obiTulln, fetterStockerau, obiStockerau, lagerhausHollabrunn, fetterHollabrunn, lagerhausMistelbach, lagerhausLaa, lagerhausEggenburg, lagerhausHorn, obiNeusiedl, lagerhausMattersburg, lagerhausEisenstadt, obiBaden, lagerhausSchwechat, lagerhausGerasdorf, obiHadikgasse, obiTriesterstrasse, obiVoesendorf, obiStMarx, obiKlosterneuburg)


        // from https://www.feiertage-oesterreich.at/2018/
        val neujahr2018 = Holiday("Neujahr", LocalDate.of(2018, Month.JANUARY, 1))
        val hlDreiKoenige2018 = Holiday("Heilige Drei Könige", LocalDate.of(2018, Month.JANUARY, 6))
        val karfreitag2018 = Holiday("Karfreitag", LocalDate.of(2018, Month.MARCH, 30))
        val ostermontag2018 = Holiday("Ostermontag", LocalDate.of(2018, Month.APRIL, 2))
        val staatsfeiertag2018 = Holiday("Staatsfeiertag", LocalDate.of(2018, Month.MAY, 1))
        val christiHimmelfahrt2018 = Holiday("Christi Himmelfahrt", LocalDate.of(2018, Month.MAY, 10))
        val pfingsmontag2018 = Holiday("Pfingsmontag", LocalDate.of(2018, Month.MAY, 21))
        val fronleichnam2018 = Holiday("Fronleichnam", LocalDate.of(2018, Month.MAY, 31))
        val mariaHimmelfahrt2018 = Holiday("Maria Himmelfahrt", LocalDate.of(2018, Month.AUGUST, 15))
        val nationalfeiertag2018 = Holiday("Nationalfeiertag", LocalDate.of(2018, Month.OCTOBER, 26))
        val allerheiligen2018 = Holiday("Allerheiligen", LocalDate.of(2018, Month.NOVEMBER, 1))
        val mariaEmpfaengnis2018 = Holiday("Maria Empfängnis", LocalDate.of(2018, Month.DECEMBER, 8))
        val heiligerAbend2018 = Holiday("Heiliger Abend", LocalDate.of(2018, Month.DECEMBER, 24))
        val weihnachten2018 = Holiday("Weihnachten", LocalDate.of(2018, Month.DECEMBER, 25))
        val stefanitag2018 = Holiday("Stefanitag", LocalDate.of(2018, Month.DECEMBER, 26))
        val silvester2018 = Holiday("Silvester", LocalDate.of(2018, Month.DECEMBER, 31))
        val holidays: List<Holiday> = listOf(neujahr2018, hlDreiKoenige2018, karfreitag2018, ostermontag2018,
                staatsfeiertag2018, christiHimmelfahrt2018, pfingsmontag2018, fronleichnam2018, mariaHimmelfahrt2018,
                nationalfeiertag2018, allerheiligen2018, mariaEmpfaengnis2018, heiligerAbend2018, weihnachten2018,
                stefanitag2018, silvester2018)

        val latitudeRange = (allCustomers.map { it.latitude }.min()!!)..(allCustomers.map { it.latitude }.max()!!)
        val longitudeRange = (allCustomers.map { it.longitude }.min()!!)..(allCustomers.map { it.longitude }.max()!!)

        val suppliers = (100..300).map { supplierId ->

            val depot = Depot("Supplier $supplierId depot",
                    randomDouble(latitudeRange.start, latitudeRange.endInclusive),
                    randomDouble(longitudeRange.start, longitudeRange.endInclusive))
            val unloadDuration = UncertainDouble(randomDouble(6.0..14.0), randomDouble(2.0..4.0))

            SimulatedSupplier(supplierId, depot, unloadDuration, randomInt(1, 15))
        }

        println(suppliers.joinToString("\n- ") { supplier ->
            with(supplier) {
                "Supplier $id: ${vehicles} vehicles @(${depot.latitude}|${depot.longitude})"
            }
        })

        val simulatedCustomers = allCustomers.map { customer ->

            // assume one person at the customer location to check goods
            val unloadSlots = randomInt(1, 5)
            val checkSpeed = UncertainDouble(randomDouble(4.0, 8.0), randomDouble(1.0, 3.0))
            val deliveriesPerYear = when (unloadSlots) {

                in 1..2 -> randomInt(3000, 12000)
                else    -> randomInt(3000, 30000)
            }
            // demand
            val demandDistribution = {
                val weekly = when (randomlyChoose(SeasonType.values())) {
                    SeasonType.EVEN -> (1..52).map { 1.0 }
                    SeasonType.SPRING -> (1..52).map { week ->
                        when (week) {
                            in 11..17   -> 3.0
                            in 18..21   -> 3.0
                            in 22..23   -> 3.0
                            else        -> 1.0
                        }
                    }
                    SeasonType.CHRISTMAS -> (1..52).map { week ->
                        when (week) {
                            48 -> 1.8
                            49 -> 2.2
                            50 -> 2.5
                            51 -> 3.0
                            52 -> 1.2
                            else -> 1.0
                        }

                    }
                }.toTypedArray()

                val daily = when (randomlyChoose(BuyerHabit.values())) {
                    BuyerHabit.WEEKDAY -> arrayOf(0.18, 0.18, 0.18, 0.18, 0.18, 0.1, 0.0)
                    BuyerHabit.WEEKEND -> arrayOf(0.13, 0.13, 0.13, 0.13, 0.19, 0.29, 0.0)
                    BuyerHabit.NONE    -> arrayOf(0.17, 0.16, 0.17, 0.16, 0.17, 0.17, 0.0)
                }

                val daysBeforeClosed = when (randomlyChoose(CloseType.values())) {
                    CloseType.NEED_MORE_BEFORE_CLOSES   -> arrayOf(0.1, 0.3, 0.6)
                    CloseType.NONE                      -> emptyArray()

                }

                val monthType = randomlyChoose(MonthType.values())
                val (beginOfMonth, endOfMonth) = when(monthType) {
                    MonthType.BEGIN_OF_MONTH    -> Pair(arrayOf(0.05, 0.2, 0.3, 0.45), emptyArray<Double>())
                    MonthType.END_OF_MONTH      -> Pair(emptyArray(), arrayOf(0.05, 0.2, 0.3, 0.45))
                    MonthType.NONE              -> Pair(emptyArray(), emptyArray())
                }
                DemandDistribution(weekly, daily, daysBeforeClosed, beginOfMonth, endOfMonth)
            }.invoke()

            SimulatedCustomer(customer, unloadSlots, checkSpeed, deliveriesPerYear, demandDistribution)
        }


        simulatedCustomers.forEach { customer ->
            println(customer)
        }

        val firstDate = LocalDate.of(2017, Month.JANUARY, 1)
        val year2017: List<LocalDate> = (0..365L).map { day ->
            val today = firstDate.plusDays(day)
            today
        }

        val deliveryDistribution: Map<SimulatedCustomer, Map<LocalDate, Double>> = simulatedCustomers.map { customer ->

            // TODO make this more realistic

            customer to year2017.map { date ->
                date to if (date.dayOfWeek != DayOfWeek.SUNDAY) {
                    1.0 / 365.0
                } else {
                    0.0
                }
            }.toMap()

        }.toMap()

        val visitVariance = UncertainDouble(1.0, 0.15)

        val deliveries: Map<LocalDate, Map<SimulatedCustomer, Int>> = year2017.map { date ->
            date to simulatedCustomers.map { customer ->
                customer to (deliveryDistribution[customer]!![date]!! * customer.deliveriesPerYear * visitVariance.nextValue()).toInt()
            }.toMap()
        }.toMap()



        val tourLength = 4

        fun simulateDate(date: LocalDate) {

            val weekday = date.dayOfWeek.value - 1      // 0..6
            val startDatetime = date.atTime(7, 0)
            GLOBAL_TIME = startDatetime

            println("simulate ${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")

            val todaysDeliveries = deliveries[date]!!.map { it.value }.sum()
            val todaysDeliveriesPerCustomer = deliveries[date]!!.entries.joinToString("\n- ") { (customer, visits) ->
                "${customer.name}: $visits"
            }
            println("Today's deliveries in detail: $todaysDeliveriesPerCustomer")

            val liveCustomers = simulatedCustomers.map { customer ->
                customer.createLiveCustomer(weekday)
            }

            val remainingCustomerVisits: MutableMap<LiveCustomer, Int> = HashMap(liveCustomers.map { customer ->
                customer to deliveries[date]!![customer.simulatedCustomer]!!
            }.filter { it.second > 0 }
            .toMap())

            val availableVehicles = suppliers.flatMap { it.createLiveVehicles() }.shuffled(random).toMutableList()
            val scheduledVehicles: MutableSet<LiveVehicle> = HashSet(availableVehicles.size)

            println("Today's deliveries: $todaysDeliveries... needs at least ${todaysDeliveries / tourLength + 1} vehicles, available ${availableVehicles.size}")

            fun randomlyChooseCustomers(n: Int) = randomlyChoose(n, remainingCustomerVisits.keys)


            while (remainingCustomerVisits.isNotEmpty()) {

                val count = Math.min(tourLength, remainingCustomerVisits.size)
                val destinations = randomlyChooseCustomers(count).toList().shuffled(random)

                if (availableVehicles.isEmpty()) throw IllegalStateException("Too few vehicles available!")
                val chosenVehicle = availableVehicles.removeAt(0)
                chosenVehicle.giveDestinations(destinations)
                scheduledVehicles += chosenVehicle

                availableVehicles -= chosenVehicle
                destinations.forEach { customer ->
                    val remaining = remainingCustomerVisits[customer]!! - 1
                    if (remaining == 0) {
                        remainingCustomerVisits -= customer
                    } else {
                        remainingCustomerVisits[customer] = remaining
                    }
                }

            }


            println("scheduled vehicles: ${scheduledVehicles.size}:\n- ${scheduledVehicles.joinToString("\n- ") {
                "vehicle ${it.id}: ${it.tour}"
            }}")

            val remainingVehicles = scheduledVehicles.toMutableSet()

            remainingVehicles.forEach { vehicle ->
                vehicle.startTour()
            }

            var minutes = 0
            // simulate 1 minute every loop iteration
            while (remainingVehicles.isNotEmpty() && GLOBAL_TIME.isBefore(date.plusDays(1).atStartOfDay())) {

                // vehicles arrive, queue them
                // if slot is free -> dequeue a vehicle and put it into the slot
                // if vehicle in slot AND not finished unloading                            -> progress unloading
                // if vehicle in slot AND finished unloading AND not finished goods check   -> progress goods check
                // if vehicle in slot AND finished goods check                              -> go to next destination


                minutes++
                GLOBAL_TIME = GLOBAL_TIME.plusMinutes(1)
                println("minutes: $minutes, now it is ${GLOBAL_TIME.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}... remaining vehicles: ${remainingVehicles.size}... " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.AT_HOME }.size} at home, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.APPROACHING_CUSTOMER }.size} approaching customer, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.WAITING_IN_QUEUE }.size} waiting in queue, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.UNLOADING }.size} unloading, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.WAITING_FOR_GOODS_CHECK }.size} waiting for goods check, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.GOODS_CHECK }.size} at goods check, " +
                        "${remainingVehicles.filter { it.status == VehicleStatus.ON_THE_WAY_HOME }.size} on the way home, " +
                        "")

                remainingVehicles.forEach { vehicle ->
                    vehicle.processMinute()
                }
                liveCustomers.forEach { customer ->
                    customer.processMinute()
                }


                remainingVehicles.removeIf { it.finished }
            }



        }   // simulateDate method


        simulateDate(LocalDate.of(2017, Month.JANUARY, 2))

        /*
        var date = LocalDate.of(2017, Month.JANUARY, 1)
        val end = LocalDate.of(2018, Month.JANUARY, 1)
        while (date.isBefore(end)) {

            simulateDate(date)

            date = date.plusDays(1)
        }
        */


    }

}