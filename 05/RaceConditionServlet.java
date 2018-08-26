package rc;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* A servlet to illustrate a race condition: different threads could access the
   instance field 'balance' at the same time. 

   Fixes:
   
   There are relatively low-level and high-level approaches. At the lowest level,
   explicit 'locking' can be implemented in Java by using a synchronized block that
   ensures mutual exclusion on the synchronized memory location.

   At the high-level, the package java.util.concurrent and its subpackages provide
   data types that have the synchronization code baked into the type itself. For 
   example, the 'predictionsRS' web service seen earlier allows multiple threads to
   access a single list, and the threads can perform any of the CRUD operations.
   At the implementation level, each 'prediction' in the list has an integer ID
   and a 'who says what' entry: both the ID and the entry must be thread-safe.
   Accordingly, the application uses two convenient thread-safe types:

   import java.util.concurrent.atomic.AtomicInteger;
   import java.util.concurrent.CopyOnWriteArrayList;
*/

public class RaceConditionServlet extends HttpServlet {
    int counter = 0;  // one instance of this field, accessible to multiple thread.

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
	sendResponse(res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
	try {
	    int adjustment = Integer.parseInt(req.getParameter("adjustment")); // assume value is available
	    counter += adjustment;
	    sendResponse(res);
	}
	catch(Exception e) { }
    }

    private void sendResponse(HttpServletResponse res) {
	try {
	    String msg = "Current counter value is: " + this.counter;
	    res.getOutputStream().println(msg);
	}
	catch(Exception e) { }
    }
}
