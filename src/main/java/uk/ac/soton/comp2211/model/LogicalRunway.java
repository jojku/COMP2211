package uk.ac.soton.comp2211.model;

public class LogicalRunway {

    private boolean recalculated = false;

    private int resa;
    private int heading;
    private int thresholdDisplacement;
    private Boolean takeoffToward = false;
    private Boolean landToward = false;

    private int oldTORA;
    private int oldTODA;
    private int oldASDA;
    private int oldLDA;
    private int oldALS;
    private int oldTOCS;

    private int newTORA;
    private int newTODA;
    private int newASDA;
    private int newLDA;

    private String toraCalcBreakdown;
    private String todaCalcBreakdown;
    private String asdaCalcBreakdown;
    private String ldaCalcBreakdown;

    private Obstacle obstacle;
    private int dThreshold;
    private int dCentre;
    private int stripEnd = 0;
    private char designator;
    private float minAscentRatio = 0;

    public LogicalRunway(
            int RESA,
            int heading,
            char designator,
            int thresholdDisplacement,
            int TORA,
            int TODA,
            int ASDA,
            int LDA) {
        this.setParameters(RESA, heading, designator, thresholdDisplacement, TORA, TODA, ASDA, LDA);
        this.setTORACalc();
        this.setTODACalc();
        this.setASDACalc();
        this.setLDACalc();
    }

    /**
     * Re-declares a logical runway, taking the longer logical runway if the obstacle bisects the runway
     */
    public void redeclare(float minAscentRatio, int blastDistance, int stripEnd){
        if (obstacle == null) {
            return;
        }
        takeoffToward = false;
        landToward = false;
        if (thresholdDisplacement + dThreshold > oldTORA/2) takeoffToward = true;
        if (dThreshold >= (oldTORA - thresholdDisplacement)/2) landToward = true;
        this.stripEnd = stripEnd;
        this.minAscentRatio = minAscentRatio;
        recalculated = true;

        if (landToward) landTowards();
        else landAway(minAscentRatio, blastDistance);

        if (takeoffToward) takeoffTowards(minAscentRatio, blastDistance);
        else takeoffAway(blastDistance);
    }

    private void landTowards() {
        landToward = true;
        // Landing
        int calcLDA = dThreshold - this.getRESA() - stripEnd;
        if (getOldLDA() > calcLDA) {
            this.newLDA = calcLDA;
            setLDATowards(dThreshold, stripEnd);
        } else {
            this.newLDA = getOldLDA();
            setLDACalc();
        }
    }

    private void takeoffTowards(float minAscentRatio, int blastDistance) {
        takeoffToward = true;
        // Takeoff
        var calcTORA = (int) (dThreshold + this.thresholdDisplacement - Math.max(Math.max(obstacle.getHeight() * minAscentRatio, this.getRESA()), blastDistance) - stripEnd);
        if (getOldTORA() > calcTORA) {
            this.newTORA = calcTORA;
            this.newTODA = calcTORA;
            this.newASDA = calcTORA;
            setTORATowards(dThreshold, this.thresholdDisplacement, obstacle.getHeight(), minAscentRatio, getRESA(), blastDistance, stripEnd);
            setTODATowards();
            setASDATowards();
        } else {
            this.newTORA = getOldTORA();
            this.newTODA = getOldTODA();
            this.newASDA = getOldASDA();
            setTORACalc();
            setTODACalc();
            setASDACalc();
        }
    }

    private void landAway(float minAscentRatio, int blastDistance) {
        landToward = false;
        // Landing
        int calcLDA = (int) (
            this.getOldLDA() - dThreshold - Math.max(Math.max(obstacle.getHeight() * minAscentRatio, this.getRESA()), blastDistance) - stripEnd
        );
        if (getOldLDA() > calcLDA) {
            this.newLDA = calcLDA;
            setLDAAway(dThreshold, obstacle.getHeight(), minAscentRatio, this.getRESA(), blastDistance, stripEnd);
        } else {
            this.newLDA = getOldLDA();
            setLDACalc();
        }
    }

    private void takeoffAway(int blastDistance) {
        takeoffToward = false;
        // Takeoff
        int calcTORA = this.getOldTORA() - dThreshold - this.getThresholdDisplacement() - blastDistance;
        if (getOldTORA() > calcTORA) {
            this.newTORA = this.getOldTORA() - dThreshold - this.getThresholdDisplacement() - blastDistance;
            this.newTODA = this.getOldTODA() - dThreshold - this.getThresholdDisplacement() - blastDistance;
            this.newASDA = this.getOldASDA() - dThreshold - this.getThresholdDisplacement() - blastDistance;
            setTORAAway(dThreshold, blastDistance);
            setTODAAway(dThreshold, blastDistance);
            setASDAAway(dThreshold, blastDistance);
        } else {
            this.newTORA = getOldTORA();
            this.newTODA = getOldTODA();
            this.newASDA = getOldASDA();
            setTORACalc();
            setTODACalc();
            setASDACalc();
        }
    }

    /**
     * Sets unobstructed runway parameters
     * @param RESA RESA
     * @param thresholdDisplacement the displacement of the threshold
     * @param TORA take off runway available
     * @param TODA take off distance available
     * @param ASDA accelerate stop distance available
     * @param LDA landing distance available
     */
    public void setParameters(
            int RESA,
            int heading,
            char designator,
            int thresholdDisplacement,
            int TORA,
            int TODA,
            int ASDA,
            int LDA) {
        recalculated = false;
        this.resa = RESA;
        this.heading = heading;
        this.designator = designator;
        this.thresholdDisplacement = thresholdDisplacement;
        this.oldTORA = TORA;
        this.oldTODA = TODA;
        this.oldASDA = ASDA;
        this.oldLDA = LDA;
        this.oldALS = thresholdDisplacement;
        this.oldTOCS = TORA;
        this.toraCalcBreakdown = "No obstacle";
        this.todaCalcBreakdown = "No obstacle";
        this.asdaCalcBreakdown = "No obstacle";
        this.ldaCalcBreakdown = "No obstacle";
    }

    public void setObstacle(Obstacle obstacle, int dThreshold, int dCentre) {
        recalculated = false;
        landToward = null;
        takeoffToward = null;
        if (obstacle == null) {
            this.obstacle = null;
            this.dCentre = 0;
            this.dThreshold = 0;
            return;
        }
        this.obstacle = obstacle;
        this.dThreshold = dThreshold;
        this.dCentre = dCentre;
        this.toraCalcBreakdown = "No obstacle";
        this.todaCalcBreakdown = "No obstacle";
        this.asdaCalcBreakdown = "No obstacle";
        this.ldaCalcBreakdown = "No obstacle";
    }

    // Towards Calculation Breakdown setters
    public void setTORATowards(int dThreshold, int threshDisp, int height, float ascent, int resa, int blast, int stripEnd) {
        this.toraCalcBreakdown =
            "TORA = "+dThreshold+" - "+threshDisp+" - max(["+height+"*"+ascent+", "+resa+", "+blast+"]) - "+stripEnd+"\n"+
                "     = "+this.getNewTORA() + "m";
    }

    public void setTODATowards() {
        this.todaCalcBreakdown =
            "TODA = (R) TORA\n"+
                "     = "+this.getNewTORA() + "m";
    }

    public void setASDATowards() {
        this.asdaCalcBreakdown =
            "ASDA = (R) TORA\n"+
                "     = "+this.getNewASDA() + "m";
    }

    public void setLDATowards(int dThreshold, int stripEnd) {
        this.ldaCalcBreakdown =
            "LDA  = "+dThreshold+" - "+this.getRESA()+" - "+stripEnd+"\n"+
                "     = "+this.getNewLDA() + "m";
    }

    // Away Calculation Breakdown setters
    public void setTORAAway(int dThreshold, int blast) {
        this.toraCalcBreakdown =
            "TORA = "+getOldTORA()+" - "+dThreshold+" - "+this.getThresholdDisplacement()+" - "+blast+"\n"+
                "     = "+this.getNewTORA() + "m";
    }

    public void setTODAAway(int dThreshold, int blast) {
        this.todaCalcBreakdown =
            "TODA = "+getOldTODA()+" - "+dThreshold+" - "+this.getThresholdDisplacement()+" - "+blast+"\n"+
                "     = "+this.getNewTODA() + "m";
    }

    public void setASDAAway(int dThreshold, int blast) {
        this.asdaCalcBreakdown =
            "ASDA = "+this.getOldASDA()+" - "+dThreshold+" - "+this.getThresholdDisplacement()+" - "+blast+"\n"+
                "     = "+this.getNewASDA() + "m";
    }

    public void setLDAAway(int dThreshold, int height, float ascent, int resa, int blast, int stripEnd) {
        this.ldaCalcBreakdown =
            "LDA  = " + this.getOldLDA()+" - "+dThreshold+" - max(["+height+"*"+ascent+", "+resa+", "+blast+"]) - "+stripEnd+"\n"+
                "     = "+this.getNewLDA() + "m";
    }

    // Original Values Breakdown setters
    public void setTORACalc() {
        this.toraCalcBreakdown = "TORA = "+this.getOldTORA()+" (Original)";
    }

    public void setTODACalc() {
        this.todaCalcBreakdown = "TODA = "+this.getOldTODA()+" (Original)";
    }

    public void setASDACalc() {
        this.asdaCalcBreakdown = "ASDA = "+this.getOldASDA()+" (Original)";
    }

    public void setLDACalc() {
        this.ldaCalcBreakdown = "LDA  = "+this.getOldLDA()+" (Original)";
    }

    // Getters
    public int getRESA() {
        return resa;
    }

    public int getThresholdDisplacement() {
        return thresholdDisplacement;
    }

    public int getOldTORA() {
        return oldTORA;
    }

    public int getOldTODA() {
        return oldTODA;
    }

    public int getOldASDA() {
        return oldASDA;
    }

    public int getOldLDA() {
        return oldLDA;
    }

    public int getOldALS() {
        return oldALS;
    }

    public int getOldTOCS() {
        return oldTOCS;
    }

    public boolean isRecalculated() {
        return recalculated;
    }

    public int getNewTODA() {
        if (!recalculated) return oldTODA;
        return newTODA;
    }

    public int getNewASDA() {
        if (!recalculated) return oldASDA;
        return newASDA;
    }

    public int getNewLDA() {
        if (!recalculated) return oldLDA;
        return newLDA;
    }

    public int getdCentre() {
        return dCentre;
    }

    public int getNewALS() {
        if (!recalculated || landToward || obstacle == null) return oldALS;
        return (int)(obstacle.getHeight()*minAscentRatio);
    }

    public int getNewTOCS() {
        if (!recalculated || !takeoffToward || obstacle == null) return 0;
        return (int)(obstacle.getHeight()*minAscentRatio);
    }

    public int getNewTORA() {
        if (!recalculated) return oldTORA;
        return newTORA;
    }

    public int getOldStopway() {
        return getOldASDA() - getOldTORA();
    }

    public int getNewStopway() {
        return getNewASDA() - getNewTORA();
    }

    public int getOldClearway() {
        return getOldTODA() - getOldTORA();
    }

    public int getNewClearway() {
        return getNewTODA() - getNewTORA();
    }

    public int getHeading() {
        return heading;
    }

    public String getDesignator() {
        return ("" + designator);
    }

    public Boolean getLandToward() {
        return landToward;
    }

    public Boolean getTakeoffToward() {
        return takeoffToward;
    }

    public String getTORACalcBreakdown() {
        return toraCalcBreakdown;
    }

    public String getTODACalcBreakdown() {
        return todaCalcBreakdown;
    }

    public String getASDACalcBreakdown() {
        return asdaCalcBreakdown;
    }

    public String getLDACalcBreakdown() {
        return ldaCalcBreakdown;
    }
}