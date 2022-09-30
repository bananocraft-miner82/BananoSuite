package miner82.bananosuite.classes;

public enum MonKeyMapStatus {
    ReadyForGeneration("Ready for Generation"),
    AwaitingPayment("Awaiting Payment"),
    AwaitingCollection("Awaiting Collection"),
    Complete("Complete");

    private final String display;

    MonKeyMapStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return this.display;
    }
}
