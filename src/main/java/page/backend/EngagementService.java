package page.backend;

import java.util.ArrayList;
import java.util.List;

import page.model.Engagement;

/**
 * The backend for this sample. create() is the method that receives the form.
 * It does not know about buttons or text fields.
 * Engagements are kept in a list so Modify can offer their names.
 * In the other project, replace the body of create() with the save method that already exists.
 */
public class EngagementService {

    private final List engagements = new ArrayList();
    private Engagement lastCreated;

    public void create(Engagement engagement) {
        engagements.add(engagement);
        lastCreated = engagement;
    }

    /** Replace the stored engagement that has the same name. */
    public void update(Engagement engagement) {
        for (int i = 0; i < engagements.size(); i++) {
            Engagement stored = (Engagement) engagements.get(i);
            if (sameName(stored, engagement.getField2())) {
                engagements.set(i, engagement);
                lastCreated = engagement;
                return;
            }
        }
        engagements.add(engagement);
        lastCreated = engagement;
    }

    public Engagement getLastCreated() {
        return lastCreated;
    }

    /** The engagement whose name matches, or null. */
    public Engagement findByName(String name) {
        for (int i = 0; i < engagements.size(); i++) {
            Engagement stored = (Engagement) engagements.get(i);
            if (sameName(stored, name)) {
                return stored;
            }
        }
        return null;
    }

    /** One entry per stored engagement, in the order they were created. */
    public String[] names() {
        String[] names = new String[engagements.size()];
        for (int i = 0; i < engagements.size(); i++) {
            Engagement stored = (Engagement) engagements.get(i);
            names[i] = stored.getField2();
        }
        return names;
    }

    private static boolean sameName(Engagement stored, String name) {
        if (name == null) {
            return false;
        }
        return name.equals(stored.getField2());
    }
}
