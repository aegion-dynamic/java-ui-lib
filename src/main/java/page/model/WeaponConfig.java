package page.model;

/**
 * Plain data from Create Weapon Configuration.
 * No Swing types. Stage 6 and the backend receive this object, not the panel.
 */
public class WeaponConfig {

    private String weapon = "";
    private String configName = "";
    private String initialSpeed = "";
    private String orderedSpeed = "";
    private String orderedDepth = "";
    private String searchSpeed = "";
    private String enbDist = "";
    private String safetySonarRange = "";
    private String udl = "";
    private String ldl = "";
    private String ash = "";
    private String maxRd = "";
    private String acousticHeadMode = "";
    private String attackSonarRange = "";
    private String attackMode = "";
    private String wakeGuidance = "";
    private String guidanceMode = "";
    private String homingMode = "";
    private String comments = "";

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(String initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    public String getOrderedSpeed() {
        return orderedSpeed;
    }

    public void setOrderedSpeed(String orderedSpeed) {
        this.orderedSpeed = orderedSpeed;
    }

    public String getOrderedDepth() {
        return orderedDepth;
    }

    public void setOrderedDepth(String orderedDepth) {
        this.orderedDepth = orderedDepth;
    }

    public String getSearchSpeed() {
        return searchSpeed;
    }

    public void setSearchSpeed(String searchSpeed) {
        this.searchSpeed = searchSpeed;
    }

    public String getEnbDist() {
        return enbDist;
    }

    public void setEnbDist(String enbDist) {
        this.enbDist = enbDist;
    }

    public String getSafetySonarRange() {
        return safetySonarRange;
    }

    public void setSafetySonarRange(String safetySonarRange) {
        this.safetySonarRange = safetySonarRange;
    }

    public String getUdl() {
        return udl;
    }

    public void setUdl(String udl) {
        this.udl = udl;
    }

    public String getLdl() {
        return ldl;
    }

    public void setLdl(String ldl) {
        this.ldl = ldl;
    }

    public String getAsh() {
        return ash;
    }

    public void setAsh(String ash) {
        this.ash = ash;
    }

    public String getMaxRd() {
        return maxRd;
    }

    public void setMaxRd(String maxRd) {
        this.maxRd = maxRd;
    }

    public String getAcousticHeadMode() {
        return acousticHeadMode;
    }

    public void setAcousticHeadMode(String acousticHeadMode) {
        this.acousticHeadMode = acousticHeadMode;
    }

    public String getAttackSonarRange() {
        return attackSonarRange;
    }

    public void setAttackSonarRange(String attackSonarRange) {
        this.attackSonarRange = attackSonarRange;
    }

    public String getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(String attackMode) {
        this.attackMode = attackMode;
    }

    public String getWakeGuidance() {
        return wakeGuidance;
    }

    public void setWakeGuidance(String wakeGuidance) {
        this.wakeGuidance = wakeGuidance;
    }

    public String getGuidanceMode() {
        return guidanceMode;
    }

    public void setGuidanceMode(String guidanceMode) {
        this.guidanceMode = guidanceMode;
    }

    public String getHomingMode() {
        return homingMode;
    }

    public void setHomingMode(String homingMode) {
        this.homingMode = homingMode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
