package page.model;

/**
 * One weapon configuration.
 * Choices and measures use the same kinds of values as the other system.
 * Config name, acoustic head mode, homing mode, sonar range, and comments are still text.
 * No Swing types.
 */
public class WeaponConfig {

    private WeaponType weapon;
    private String configName = "";
    private Speed initialSpeed;
    private Speed orderedSpeed;
    private Depth orderedDepth;
    private Speed searchSpeed;
    private Distance enbDist;
    private Distance udl;
    private Distance ldl;
    private AcousticHeadMode ash;
    private Distance maxRd;
    private String acousticHeadMode = "";
    private String attackSonarRange = "";
    private TargetType attackMode;
    private GuidanceMode wakeGuidance;
    private InternalGuidanceMode guidanceMode;
    private String homingMode = "";
    private String comments = "";

    public WeaponType getWeapon() {
        return weapon;
    }

    public void setWeapon(WeaponType weapon) {
        this.weapon = weapon;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName == null ? "" : configName;
    }

    public Speed getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(Speed initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    public Speed getOrderedSpeed() {
        return orderedSpeed;
    }

    public void setOrderedSpeed(Speed orderedSpeed) {
        this.orderedSpeed = orderedSpeed;
    }

    public Depth getOrderedDepth() {
        return orderedDepth;
    }

    public void setOrderedDepth(Depth orderedDepth) {
        this.orderedDepth = orderedDepth;
    }

    public Speed getSearchSpeed() {
        return searchSpeed;
    }

    public void setSearchSpeed(Speed searchSpeed) {
        this.searchSpeed = searchSpeed;
    }

    public Distance getEnbDist() {
        return enbDist;
    }

    public void setEnbDist(Distance enbDist) {
        this.enbDist = enbDist;
    }

    public Distance getUdl() {
        return udl;
    }

    public void setUdl(Distance udl) {
        this.udl = udl;
    }

    public Distance getLdl() {
        return ldl;
    }

    public void setLdl(Distance ldl) {
        this.ldl = ldl;
    }

    public AcousticHeadMode getAsh() {
        return ash;
    }

    public void setAsh(AcousticHeadMode ash) {
        this.ash = ash;
    }

    public Distance getMaxRd() {
        return maxRd;
    }

    public void setMaxRd(Distance maxRd) {
        this.maxRd = maxRd;
    }

    /** Acoustic Head Mode. Passive, Simultaneous, or Wake. */
    public String getAcousticHeadMode() {
        return acousticHeadMode;
    }

    public void setAcousticHeadMode(String acousticHeadMode) {
        this.acousticHeadMode = acousticHeadMode == null ? "" : acousticHeadMode;
    }

    /** The one sonar range, on Attack Parameters. */
    public String getAttackSonarRange() {
        return attackSonarRange;
    }

    public void setAttackSonarRange(String attackSonarRange) {
        this.attackSonarRange = attackSonarRange == null ? "" : attackSonarRange;
    }

    public TargetType getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(TargetType attackMode) {
        this.attackMode = attackMode;
    }

    public GuidanceMode getWakeGuidance() {
        return wakeGuidance;
    }

    public void setWakeGuidance(GuidanceMode wakeGuidance) {
        this.wakeGuidance = wakeGuidance;
    }

    public InternalGuidanceMode getGuidanceMode() {
        return guidanceMode;
    }

    public void setGuidanceMode(InternalGuidanceMode guidanceMode) {
        this.guidanceMode = guidanceMode;
    }

    public String getHomingMode() {
        return homingMode;
    }

    public void setHomingMode(String homingMode) {
        this.homingMode = homingMode == null ? "" : homingMode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? "" : comments;
    }
}
