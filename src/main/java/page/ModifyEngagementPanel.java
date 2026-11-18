package page;

/**
 * Modify Engagement. A separate form from Create Engagement.
 * The header says MODIFY ENGAGEMENT.
 * Engagement Name is a drop-down of names already stored.
 * Choosing a name loads that engagement into the rest of the fields.
 */
public class ModifyEngagementPanel extends CreateEngagementPanel {

    private static final long serialVersionUID = 1L;

    public ModifyEngagementPanel() {
        super("MODIFY ENGAGEMENT", true);
    }
}
