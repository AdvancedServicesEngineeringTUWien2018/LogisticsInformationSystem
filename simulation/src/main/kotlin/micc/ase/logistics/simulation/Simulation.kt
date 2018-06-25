package micc.ase.logistics.simulation

import micc.ase.logistics.common.model.*
import micc.ase.logistics.common.sensor.GPSSensor
import micc.ase.logistics.edge.EdgentEdgeDevice
import micc.ase.logistics.simulation.learn.DateInfo
import micc.ase.logistics.simulation.learn.HolidayRetriever
import micc.ase.logistics.simulation.model.DemandDistribution
import micc.ase.logistics.simulation.model.live.LiveCustomer
import micc.ase.logistics.simulation.model.SimulatedCustomer
import micc.ase.logistics.simulation.model.SimulatedSupplier
import micc.ase.logistics.simulation.model.live.LiveVehicle
import micc.ase.logistics.simulation.model.live.VehicleStatus
import micc.ase.logistics.simulation.util.*
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import java.util.Locale



class Simulation {

    val LOG = LoggerFactory.getLogger(Simulation::class.java)

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

    /**
     * @param kafkaEndpoint <IP>:<port>
     */
    fun simulate(from: LocalDate, to: LocalDate, kafkaEndpoint: String = "localhost:9092", dryRun: Boolean = false) {

        if (from.isBefore(LocalDate.of(2015, Month.JANUARY, 1))) {
            throw IllegalArgumentException("from must be >= 1.1.2015")
        }

        if (to.isAfter(LocalDate.of(2018, Month.JANUARY, 1).minusDays(1))) {
            throw IllegalArgumentException("to must be < 1.1.2018")
        }


        LOG.info("Use kafka endpoint $kafkaEndpoint")

        val obiKrems = Customer(1, "OBI Krems", 48.406988, 15.654453)
        val bellafloraKrems = Customer(2, "Bellaflora Krems", 48.407813, 15.660239)
        val lagerhausTulln = Customer(3, "Lagerhaus Tulln", 48.321657, 16.078680)
        val obiTulln = Customer(4, "OBI Tulln", 48.317947, 16.019311)
        val fetterStockerau = Customer(5, "Fetter Stockerau", 48.389257, 16.182436)
        val obiStockerau = Customer(6, "OBI Stockerau", 48.387829, 16.181327)
        val fetterHollabrunn = Customer(7, "Fetter Hollabrunn", 48.564564, 16.075602)
        val lagerhausHollabrunn = Customer(8, "Lagerhaus Hollabrunn", 48.569676, 16.080248)
        val lagerhausMistelbach = Customer(9, "Lagerhaus Mistelbach", 48.564714, 16.562467)
        val lagerhausLaa = Customer(10, "Lagerhaus Laa/Thaya", 48.714777, 16.378221)
        val lagerhausEggenburg = Customer(11, "Lagerhaus Eggenburg", 48.645971, 15.823033)
        val lagerhausHorn = Customer(12, "Lagerhaus Horn", 48.663899, 15.639511)
        val obiNeusiedl = Customer(13, "OBI Neusiedl/See", 47.969247, 16.839363)
        val lagerhausMattersburg = Customer(14, "Lagerhaus Mattersburg", 47.733876, 16.410021)
        val lagerhausEisenstadt = Customer(15, "Lagerhaus Eisenstadt", 47.838738, 16.527066)
        val obiBaden = Customer(16, "OBI Baden", 47.976119, 16.281574)
        val lagerhausSchwechat = Customer(17, "Lagerhaus Schwechat", 48.144759, 16.466031)
        val lagerhausGerasdorf = Customer(18, "Lagerhaus Gerasdorf", 48.297028, 16.484233)
        val obiHadikgasse = Customer(19, "OBI Wien Hadikgasse", 48.192676, 16.282503)
        val obiTriesterstrasse = Customer(20, "OBI Wien Triesterstraße", 48.178657, 16.357772)
        val obiVoesendorf = Customer(21, "OBI Vösendorf", 48.118470, 16.314756)
        val obiStMarx = Customer(22, "OBI Wien St. Marx", 48.186285, 16.410233)
        val obiKlosterneuburg = Customer(23, "OBI Klosterneuburg", 48.294656, 16.337393)

        val allCustomers = setOf(obiKrems, bellafloraKrems, lagerhausTulln, obiTulln, fetterStockerau, obiStockerau, lagerhausHollabrunn, fetterHollabrunn, lagerhausMistelbach, lagerhausLaa, lagerhausEggenburg, lagerhausHorn, obiNeusiedl, lagerhausMattersburg, lagerhausEisenstadt, obiBaden, lagerhausSchwechat, lagerhausGerasdorf, obiHadikgasse, obiTriesterstrasse, obiVoesendorf, obiStMarx, obiKlosterneuburg)


        val holidayRetriever = HolidayRetriever()

        val holidays = holidayRetriever.holidays()
        println("holidays: $holidays")

        val latitudeRange = (allCustomers.map { it.latitude }.min()!!)..(allCustomers.map { it.latitude }.max()!!)
        val longitudeRange = (allCustomers.map { it.longitude }.min()!!)..(allCustomers.map { it.longitude }.max()!!)

        val suppliers = (100..150).map { supplierId ->

            val depot = Depot("Supplier $supplierId depot",
                    randomDouble(latitudeRange.start, latitudeRange.endInclusive),
                    randomDouble(longitudeRange.start, longitudeRange.endInclusive))
            val unloadDuration = UncertainDouble(randomDouble(6.0..14.0), randomDouble(2.0..4.0))
            val vehicleCount = randomInt(1,5)

            SimulatedSupplier(supplierId, depot, unloadDuration, vehicleCount, kafkaEndpoint)
        }

        System.out.print("starting edge devices... ")
        suppliers.forEach { supplier ->
            supplier.startEdgeDevices()
        }
        System.out.println("all edge devices started!")

        val simulatedCustomers = allCustomers.map { customer ->

            // assume one person at the customer location to check goods
            val unloadSlots = randomInt(1, 5)
            val checkSpeed = UncertainDouble(randomDouble(3.0, 6.0), randomDouble(1.0, 2.5))
            val deliveriesPerYear = when (unloadSlots) {

                in 1..2 -> randomInt(1200, 3500)
                else    -> randomInt(2800, 7200)
            }
            // demand
            val demandDistribution = {
                val weekly = when (randomlyChoose(SeasonType.values())) {
                    SeasonType.EVEN -> (1..52).map { 0.0 }
                    SeasonType.SPRING -> (1..52).map { week ->
                        when (week) {
                            in 11..17   -> 1.4
                            in 18..21   -> 1.0
                            in 22..23   -> 0.3
                            else        -> 0.0
                        }
                    }
                    SeasonType.CHRISTMAS -> (1..52).map { week ->
                        when (week) {
                            48 -> 0.8
                            49 -> 1.2
                            50 -> 1.5
                            51 -> 2.0
                            52 -> 0.2
                            else -> 0.0
                        }

                    }
                }.toTypedArray()

                val daily = when (randomlyChoose(BuyerHabit.values())) {
                    BuyerHabit.WEEKDAY -> arrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 0.4, 0.0)
                    BuyerHabit.WEEKEND -> arrayOf(1.0, 1.0, 1.0, 1.0, 1.3, 1.8, 0.0)
                    BuyerHabit.NONE    -> arrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0)
                }

                val daysBeforeClosed = when (randomlyChoose(CloseType.values())) {
                    CloseType.NEED_MORE_BEFORE_CLOSES   -> arrayOf(0.1, 0.3, 0.6).reversedArray()
                    CloseType.NONE                      -> emptyArray()

                }

                val monthType = randomlyChoose(MonthType.values())
                val (beginOfMonth, endOfMonth) = when(monthType) {
                    MonthType.BEGIN_OF_MONTH    -> Pair(arrayOf(0.45, 0.3, 0.2, 0.05), emptyArray<Double>())
                    MonthType.END_OF_MONTH      -> Pair(emptyArray(), arrayOf(0.05, 0.2, 0.3, 0.45).reversedArray())
                    MonthType.NONE              -> Pair(emptyArray(), emptyArray())
                }
                DemandDistribution(weekly, daily, daysBeforeClosed, beginOfMonth, endOfMonth)
            }.invoke()

            SimulatedCustomer(customer, unloadSlots, checkSpeed, deliveriesPerYear, demandDistribution)
        }


        simulatedCustomers.forEach { customer ->
            println(customer)
        }

        val firstDate = LocalDate.of(2015, Month.JANUARY, 1)
        val days2015To2017: List<LocalDate> = (0..(365*3L)).map { day ->
            val today = firstDate.plusDays(day)
            today
        }


        val deliveryDistribution: Map<SimulatedCustomer, Map<LocalDate, Double>> = simulatedCustomers.map { customer ->

            val demand = customer.demandDistribution

            val freeDays = days2015To2017.filter { demand.dailyDemand[it.dayOfWeek.value-1] <= 0.0 }
                    .union(holidays.map { it.date })

            val firstFree = freeDays.toList().sorted().first()
            val first = days2015To2017.first()


            val firstSection = days2015To2017.takeWhile { it.isBefore(firstFree) }.map { it to firstFree }

            val nextFreeDays = freeDays.toList().sorted().windowed(2, 1, false).flatMap {
                val last = it[0]
                val next = it[1]

                var current = last
                val daysBetween: MutableList<LocalDate> = LinkedList()

                while (current < next) {
                    daysBetween.add(current)
                    current = current.plusDays(1)
                }

                daysBetween.map { it to next }
            }.union(firstSection).toMap()


            customer to days2015To2017.map { date ->
                val isFree = date in freeDays
                date to if (isFree) {
                    0.0
                } else {

                    val d = DateInfo(date, nextFreeDays[date]!!, 4)

                    val dayInfluence = demand.dailyDemand[d.dayOfWeek-1]

                    val weekInfluence = demand.weeklyDemand[if (d.weekOfYear-1 > 51) 51 else d.weekOfYear-1]    // 53th week

                    val beginOfMonthInfluence = if (d.dayOfMonth <= demand.beginOfMonth.size) {
                        demand.beginOfMonth[d.dayOfMonth - 1]
                    } else {
                        0.0
                    }

                    val endOfMonthInfluence = if (d.daysBeforeEndOfMonth < demand.endOfMonth.size) {
                        demand.endOfMonth[d.daysBeforeEndOfMonth]
                    } else {
                        0.0
                    }

                    val beforeClosedInfluence = if (d.nextFreeDay != null) {
                        if (d.daysBeforeFree < demand.daysBeforeClosed.size) {
                            demand.daysBeforeClosed[d.daysBeforeFree - 1]
                        } else {
                            0.0
                        }
                    } else {
                        0.0
                    }

                    val result = dayInfluence + weekInfluence + beginOfMonthInfluence + endOfMonthInfluence + beforeClosedInfluence

                    if (LOG.isTraceEnabled) {
                        LOG.trace("date: ${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} -> result $result consists of day $dayInfluence, week $weekInfluence, begin of month $beginOfMonthInfluence, end of month $endOfMonthInfluence, before closed $beforeClosedInfluence")
                    }

                    result
                }
            }.toMap()

        }.toMap()

        val yearlyCustomerDistributionSums = simulatedCustomers.map { customer ->
            customer to deliveryDistribution[customer]!!.entries.groupBy { it.key.year }.map { (year, entries) ->
                year to entries.sumByDouble { it.value }
            }.toMap()
        }.toMap()

        val normalizedDeliveryDistributions = deliveryDistribution.entries.map { (customer, values) ->

            customer to values.map { (date, value) ->
                val sum = yearlyCustomerDistributionSums[customer]!![date.year]!!
                date to value / sum
            }.toMap()

        }.toMap()

        val visitVariance = UncertainDouble(1.0, 0.15)

        val deliveries: Map<LocalDate, Map<SimulatedCustomer, Int>> = days2015To2017.map { date ->
            date to simulatedCustomers.map { customer ->
                customer to (normalizedDeliveryDistributions[customer]!![date]!! * customer.deliveriesPerYear * visitVariance.nextValue()).toInt()
            }.toMap()
        }.toMap()


        val tourLength = 5


        fun simulateDate(date: LocalDate) {

            val weekday = date.dayOfWeek.value - 1      // 0..6
            val startDatetime = date.atTime(7, 0)
            GLOBAL_TIME = startDatetime

            println("simulate ${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")

            val todaysDeliveries = deliveries[date]!!.map { it.value }.sum()
//            val todaysDeliveriesPerCustomer = deliveries[date]!!.entries.joinToString("\n- ") { (customer, visits) ->
//                "${customer.name}: $visits"
//            }
//            println("Today's deliveries in detail: $todaysDeliveriesPerCustomer")

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

            fun randomlyChooseCustomers(n: Int) = randomlyChoose(n, remainingCustomerVisits.keys,
                    { customer: LiveCustomer -> remainingCustomerVisits.get(customer)!! })


            while (remainingCustomerVisits.isNotEmpty()) {

                val remainingTotal = remainingCustomerVisits.entries.sumBy { it.value }
                val count = Math.min(tourLength, remainingTotal)
                val destinations = randomlyChooseCustomers(count).toList().shuffled(random)
                println("choose $count, because length=$tourLength and remaining Total is $remainingTotal, destinations: $destinations")

                if (availableVehicles.isEmpty()) throw IllegalStateException("Too few vehicles available! $remainingTotal destinations to go, no vehicle left!")
                val chosenVehicle = availableVehicles.removeAt(0)
                chosenVehicle.giveDestinations(destinations)
                scheduledVehicles += chosenVehicle

                destinations.forEach { customer ->
                    val remaining = remainingCustomerVisits[customer]!! - 1
                    if (remaining == 0) {
                        remainingCustomerVisits -= customer
                    } else {
                        remainingCustomerVisits[customer] = remaining
                    }
                }

            }

            println("Destinations distributed among vehicles, ${availableVehicles.size} left")



            System.out.print("update tours on devices... ")
            scheduledVehicles.forEach { vehicle ->
                vehicle.edgeDevice.changeTour(vehicle.tour)
            }
            System.out.println("done!")

            val remainingVehicles = scheduledVehicles.toMutableSet()

            remainingVehicles.forEach { vehicle ->
                vehicle.startTour()
            }


            val oneHourInXSeconds = 4
            val gap = (oneHourInXSeconds * 1000.0 / 60.0).toLong()
            var lastTimeMillis = System.currentTimeMillis()

            System.out.println("Gap = $gap millis")

            var lastReturned: LocalDateTime? = null

            var minutes = 0
            // simulate 1 minute every loop iteration
            while (GLOBAL_TIME.isBefore(date.plusDays(1).atStartOfDay().minusHours(4))) {

                val now = System.currentTimeMillis()
                val elapsedMillis = now - lastTimeMillis
                lastTimeMillis = now
                val sleep = Math.max(0L, gap - elapsedMillis)
                Thread.sleep(sleep)

                // vehicles arrive, queue them
                // if slot is free -> dequeue a vehicle and put it into the slot
                // if vehicle in slot AND not finished unloading                            -> progress unloading
                // if vehicle in slot AND finished unloading AND not finished goods check   -> progress goods check
                // if vehicle in slot AND finished goods check                              -> go to next destination

                if (GLOBAL_TIME.minute == 0) {
                    println("It is ${GLOBAL_TIME.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
                }

                minutes++
                GLOBAL_TIME = GLOBAL_TIME.plusMinutes(1)

                scheduledVehicles.forEach { vehicle ->
                    vehicle.processMinute()
                }
                liveCustomers.forEach { customer ->
                    customer.processMinute()
                }


                remainingVehicles.removeIf { it.finished }

                if (remainingVehicles.isEmpty() && lastReturned == null) {
                    lastReturned = GLOBAL_TIME
                    println("Last came back at ${GLOBAL_TIME.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                }
            }

            if (lastReturned == null) {
                println("There are still ${remainingVehicles.size} on tour!")
            }

            println("End of day :)")

        }   // simulateDate method


        fun dryRun(date: LocalDate): Pair<Int, Int> {
            val weekday = date.dayOfWeek.value - 1      // 0..6
            val startDatetime = date.atTime(7, 0)
            GLOBAL_TIME = startDatetime

            println("simulate ${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")

            val todaysDeliveries = deliveries[date]!!.map { it.value }.sum()
            val todaysDeliveriesPerCustomer = deliveries[date]!!.entries.joinToString("\n- ") { (customer, visits) ->
                "${customer.name}: $visits"
            }
            val maxPerCustomer = deliveries[date]!!.maxBy { it.value }!!

            println("Today's deliveries in detail: $todaysDeliveriesPerCustomer")

            println("Today's deliveries: $todaysDeliveries (max: ${maxPerCustomer.key.name} ${maxPerCustomer.value})... needs at least ${todaysDeliveries / tourLength + 1} vehicles")

            return Pair(maxPerCustomer.value, todaysDeliveries)
        }


        var date = from
        val end = to

        var overallMaxCustomer = 0
        var overallMaxTotal = 0
        while (!date.isAfter(end)) {

            if (dryRun) {
                val (maxCustomer, total) = dryRun(date)
                if (maxCustomer > overallMaxCustomer) {
                    overallMaxCustomer = maxCustomer
                }
                if (total > overallMaxTotal) {
                    overallMaxTotal = total
                }
            } else {
                simulateDate(date)
                Thread.sleep(1000L)
            }

            date = date.plusDays(1)
        }

        println("max customer: $overallMaxCustomer, max total: $overallMaxTotal")


        Thread.sleep(3000L)


    }

}