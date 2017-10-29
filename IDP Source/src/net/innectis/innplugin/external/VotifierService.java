package net.innectis.innplugin.external;

/**
 * A class that contains a single votifier service
 * and all its parts
 *
 * @author AlphaBlend
 */
public class VotifierService {

    private String name = null;
    private String URL = null;
    private String title = null;

    public VotifierService(String name, String URL, String title) {
        this.name = name;
        this.URL = URL;
        this.title = title;
    }

    /**
     * Gets the name of this votifier service
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the URL of this votifier service
     * @return
     */
    public String getURL() {
        return URL;
    }

    /**
     * Gets the title of this votifier service
     * @return
     */
    public String getTitle() {
        return title;
    }

}
