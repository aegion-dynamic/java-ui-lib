package page.model;

/**
 * One weapon configuration.
 * The config name is a char array.
 * Speeds and depth are doubles. Distances and sonar are floats.
 * Those numbers become Speed, Depth, or Distance only through the to-methods,
 * which attach the unit.
 * ASH and Wake Guidance are OnOff. No Swing types.
 */
public class WeaponConfig {

    private WeaponConfigState weapon;
    private char[] configName = new char[0];
    private Double initialSpeed;
    private Double orderedSpeed;
    private Double orderedDepth;
    private Double searchSpeed;
    private Float enbDist;
    private Float udl;
    private Float ldl;
    private OnOff ash;
    private Float maxRd;
    private AcousticHeadMode acousticHeadMode;
    private Float attackSonarRange;
    private TargetType attackMode;
    private OnOff wakeGuidance;
    private GuidanceMode guidanceMode;
    private InternalGuidanceMode homingMode;
    private String comments = "";

    public WeaponConfigState getWeapon() {
        return weapon;
    }

    public void setWeapon(WeaponConfigState weapon) {
        this.weapon = weapon;
    }

    public char[] getConfigName() {
        char[] copy = new char[configName.length];
        System.arraycopy(configName, 0, copy, 0, configName.length);
        return copy;
    }

    public void setConfigName(char[] configName) {
        if (configName == null) {
            this.configName = new char[0];
            return;
        }
        this.configName = new char[configName.length];
        System.arraycopy(configName, 0, this.configName, 0, configName.length);
    }

    /** The config name as text, for a drop-down or a label. */
    public String configNameText() {
        return new String(configName);
    }

    /** Initial Speed in knots, or null when the box was left empty. */
    public Double getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(Double initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    public Speed toInitialSpeed() {
        return initialSpeed == null ? null : new Speed(initialSpeed.doubleValue());
    }

    /** Ordered Speed in knots. */
    public Double getOrderedSpeed() {
        return orderedSpeed;
    }

    public void setOrderedSpeed(Double orderedSpeed) {
        this.orderedSpeed = orderedSpeed;
    }

    public Speed toOrderedSpeed() {
        return orderedSpeed == null ? null : new Speed(orderedSpeed.doubleValue());
    }

    /** Ordered Depth in metres. */
    public Double getOrderedDepth() {
        return orderedDepth;
    }

    public void setOrderedDepth(Double orderedDepth) {
        this.orderedDepth = orderedDepth;
    }

    public Depth toOrderedDepth() {
        return orderedDepth == null ? null : new Depth(orderedDepth.doubleValue());
    }

    /** Search Speed in knots. The form label is Search Speed. */
    public Double getSearchSpeed() {
        return searchSpeed;
    }

    public void setSearchSpeed(Double searchSpeed) {
        this.searchSpeed = searchSpeed;
    }

    public Speed toSearchSpeed() {
        return searchSpeed == null ? null : new Speed(searchSpeed.doubleValue());
    }

    /** Enb Dist in metres. */
    public Float getEnbDist() {
        return enbDist;
    }

    public void setEnbDist(Float enbDist) {
        this.enbDist = enbDist;
    }

    public Distance toEnbDist() {
        return enbDist == null ? null : new Distance(enbDist.floatValue());
    }

    public Float getUdl() {
        return udl;
    }

    public void setUdl(Float udl) {
        this.udl = udl;
    }

    public Distance toUdl() {
        return udl == null ? null : new Distance(udl.floatValue());
    }

    public Float getLdl() {
        return ldl;
    }

    public void setLdl(Float ldl) {
        this.ldl = ldl;
    }

    public Distance toLdl() {
        return ldl == null ? null : new Distance(ldl.floatValue());
    }

    public OnOff getAsh() {
        return ash;
    }

    public void setAsh(OnOff ash) {
        this.ash = ash;
    }

    /** Max RD in metres. */
    public Float getMaxRd() {
        return maxRd;
    }

    public void setMaxRd(Float maxRd) {
        this.maxRd = maxRd;
    }

    public Distance toMaxRd() {
        return maxRd == null ? null : new Distance(maxRd.floatValue());
    }

    public AcousticHeadMode getAcousticHeadMode() {
        return acousticHeadMode;
    }

    public void setAcousticHeadMode(AcousticHeadMode acousticHeadMode) {
        this.acousticHeadMode = acousticHeadMode;
    }

    /** Sonar range in metres. The one sonar range, on Attack Parameters. */
    public Float getAttackSonarRange() {
        return attackSonarRange;
    }

    public void setAttackSonarRange(Float attackSonarRange) {
        this.attackSonarRange = attackSonarRange;
    }

    public Distance toSonarRange() {
        return attackSonarRange == null ? null : new Distance(attackSonarRange.floatValue());
    }

    public TargetType getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(TargetType attackMode) {
        this.attackMode = attackMode;
    }

    public OnOff getWakeGuidance() {
        return wakeGuidance;
    }

    public void setWakeGuidance(OnOff wakeGuidance) {
        this.wakeGuidance = wakeGuidance;
    }

    public GuidanceMode getGuidanceMode() {
        return guidanceMode;
    }

    public void setGuidanceMode(GuidanceMode guidanceMode) {
        this.guidanceMode = guidanceMode;
    }

    public InternalGuidanceMode getHomingMode() {
        return homingMode;
    }

    public void setHomingMode(InternalGuidanceMode homingMode) {
        this.homingMode = homingMode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? "" : comments;
    }
}
