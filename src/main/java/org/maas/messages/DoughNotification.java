package org.maas.messages;

import java.util.Vector;

public class DoughNotification extends DoughMessage {
    public DoughNotification(Vector<String> guids, String productType) {
        super(guids, productType);
    }
}
