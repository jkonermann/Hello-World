package sandbox.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

public class DemoActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Create Props for an actor of this type.
     * @param magicNumber The magic number to be passed to this actor’s constructor.
     * @return a Props for creating this actor, which can then be further configured
     *         (e.g. calling `.withDispatcher()` on it)
     */
    public static Props props(final int magicNumber) {
        return Props.create(new Creator<DemoActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DemoActor create() throws Exception {
                return new DemoActor(magicNumber);
            }
        });
    }

    final int magicNumber;

    public DemoActor(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            log.info("Received String message: {} magicNumber='{}'", message, magicNumber);
            getSender().tell(message, getSelf());
        } else
            unhandled(message);
    }

}
