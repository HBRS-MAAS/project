package org.maas.messages;

import java.util.Vector;

public class ProofingRequest extends DoughMessage {
    private float proofingTime;

    public ProofingRequest(String productType, Vector<String> guids, float proofingTime) {
        super(guids, productType);
        this.proofingTime = proofingTime;
    }

    public Float getProofingTime() {
        return proofingTime;
    }

    public void setProofingTime(Float proofingTime) {
        this.proofingTime = proofingTime;
    }

    @Override
    public String toString() {
        return "ProofingRequest [proofingTime=" + proofingTime + "]";
    }
}
