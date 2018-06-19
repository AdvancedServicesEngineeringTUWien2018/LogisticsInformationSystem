package micc.ase.logistics.simulation

import org.junit.Assert
import org.junit.Test
import java.util.*

class QueueTest {


    @Test
    fun enqueue() {
        val queue: Queue<Int> = LinkedList()

        queue.add(1)
        queue.add(2)
        queue.add(3)
        queue.add(4)

        val r1 = queue.poll()
        val r2 = queue.poll()
        val r3 = queue.poll()
        val r4 = queue.peek()
        val r5 = queue.peek()

        Assert.assertEquals(r1, 1)
        Assert.assertEquals(r2, 2)
        Assert.assertEquals(r3, 3)
        Assert.assertEquals(r4, 4)
        Assert.assertEquals(r5, 4)

    }

}