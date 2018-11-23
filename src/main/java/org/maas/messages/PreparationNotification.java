package org.maas.messages;

import java.util.Vector;

public class PreparationNotification extends GenericGuidMessage {

    public PreparationNotification(Vector<String> guids, String productType) {
        super(guids, productType);
    }
}
