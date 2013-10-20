package com.ted.pilots.esper.synch.http;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.ted.pilots.esper.synch.http.events.OrderEvent;

/**
 * Espert Quick Start Class.
 * <p/>See http://esper.codehaus.org/tutorials/tutorial/quickstart.html
 *
 * @author <a href="mailto:tedd824@gmail.com">Ted Won</a>
 * @version 1.0
 */
public class SimpleEsperEventProcessor {

    private EPServiceProvider epService;
    private EPStatement statement;

    public SimpleEsperEventProcessor() {
        Configuration config = new Configuration();
        config.addEventTypeAutoName("com.ted.pilots.esper.synch.http.events");
        epService = EPServiceProviderManager.getDefaultProvider(config);
        String expression = "select price*2 as myprice from OrderEvent";
        statement = epService.getEPAdministrator().createEPL(expression);
    }

    public String process(String price) {
        OrderEvent reqEvent = new OrderEvent("itemName", Double.parseDouble(price));

        // Send Request Event
        epService.getEPRuntime().sendEvent(reqEvent);

        // Receive Result Events
        java.util.Iterator<EventBean> resultEvents = statement.iterator();
        EventBean event = resultEvents.next();

        return "" + event.get("myprice");
    }
}
