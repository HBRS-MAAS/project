package org.maas.messages;

import java.util.Vector;

public class PreparationNotification extends DoughMessage {

    public PreparationNotification(Vector<String> guids, String productType) {
        super(guids, productType);
    }
}
