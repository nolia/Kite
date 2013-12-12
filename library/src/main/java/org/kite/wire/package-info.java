/**<h1>Wire is up to simplify connecting to local {@link android.app.Service}s.</h1>
 * <br/>
 * Connecting to local services ( that are in same process as their clients)
 * using binding mechanism, and by default will also fill dependencies in client.
 * <br/>
 * <pre>
 public class MyService extends WiredService {
    &#064;Provided
    WorkerInterface worker = new WorkerImpl();
 }

 // it can be Activity, Fragment or any object
 // what you need, is Context
 public class MyClient {
    &#064;Wired
    WorkerInterface serviceWorker;

    // init
    wire = new Wire(context).from(MyService.class).to(this);

    // onStart():
    wire.connect();
    // serviceWorker will be filled with WorkerImpl instance from
    // service

    // onStop():
    wire.disconnect();
    // serviceWorker will be nullified
 }
 * </pre>
 * <p>Scopes</p>
 * By default, when you connect pointing service class,
 * you get all provided instances with ALL scope.
 * So if you want to adhere encapsulation, you can set scope provided instances:
 * <br/>
 * <pre>
     // will be available only when intent.action == SOME_INTENT_ACTION
    &#064;Provided(scope = Scope.ACTION, action = SOME_INTENT_ACTION )
    SomeInterface worker;

    // will be available with any intent and with no intent
    &#064;Provided(scope = Scope.ALL )
    SomeInterface worker;

    // default, will be available only when no intent is set in connection
    &#064;Provided(scope = Scope.DEFAULT)
    SomeInterface worker;

 * </pre>
 *
 * @author Nikolay Soroka
 */
package org.kite.wire;