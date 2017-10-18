package seedu.address.ui;

import java.net.URL;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import seedu.address.MainApp;
import seedu.address.commons.auth.GoogleApiAuth;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.logic.GoogleApiAuthServiceCredentialsSetupCompleted;
import seedu.address.commons.events.logic.GoogleAuthRequestEvent;
import seedu.address.commons.events.ui.FindLocationRequestEvent;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.model.person.ReadOnlyPerson;


/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "default.html";
    public static final String GOOGLE_MAPS_URL = "https://www.google.com.sg/maps/place/";
    public static final String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com.sg/search?safe=off&q=";
    public static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";

    private static final String FXML = "BrowserPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private WebView browser;

    private String currentUrl = "";
    private GoogleApiAuth authService;

    public BrowserPanel() {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage();
        registerAsAnEventHandler(this);
    }


    private void loadPersonPage(ReadOnlyPerson person) {
        loadPage(GOOGLE_SEARCH_URL_PREFIX + person.getName().fullName.replaceAll(" ", "+")
                + GOOGLE_SEARCH_URL_SUFFIX);
    }

    /**
     * Loads Google Maps.
     */
    private void loadPersonLocation(ReadOnlyPerson person) {
        String add = person.getAddress().toString();
        String[] address = add.split("\\s");
        String location = "";
        for (String s: address) {
            location += s;
            location += "+";
        }
        loadPage(GOOGLE_MAPS_URL + location);
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonPage(event.getNewSelection().person);
    }

    /**Event listener for Google Auth Requests Events
     * Fires an event when authentication succeeds and GoogleCredential is set up correctly
     * @param event
     */
    @Subscribe
    private void handleGoogleAuthRequestEvent(GoogleAuthRequestEvent event) {
        authService = event.getAuthServiceRef();
        loadPage(authService.getAuthContactWriteUrl());

        /**Listener for URL : Code adapted from https://gist.github.com/tewarid/57031d4b2f0a27765fa82abd10c21351
         * Fires a GoogleAuthSuccessEvent when a URL change to GoogleApiAuth.redirectUrl is detected
         */
        browser.getEngine().locationProperty().addListener(((observable, oldValue, newValue) -> {
            currentUrl = (String) newValue;
            if (authSuccessUrlDetected(currentUrl)) {
                setGoogleCredentials(extractAuthCode(currentUrl));
            }
        }));
    }

    /**Helper method: Sets up GoogleCredentials within the GoogleApiAuth service, and fires an event on success
     * @param authCode the auth code extracted from the current url
     */
    private void setGoogleCredentials(String authCode) {
        if (authService.setupCredentials(authCode)) {
            EventsCenter.getInstance().post(new GoogleApiAuthServiceCredentialsSetupCompleted());
        }
    }

    /**Helper method: Extracts the auth code based on a set regex
     * @param currentUrl a snapshot of the URL
     * @return the extracted authentication code
     */
    private String extractAuthCode(String currentUrl) {
        return currentUrl.split("=")[1].split("&")[0];
    }

    private boolean authSuccessUrlDetected(String currentUrl) {
        return currentUrl.contains(GoogleApiAuth.REDIRECT_URL);
    }

    /**
     * Event listener for Find Location Events
     * @param event
     */
    @Subscribe
    private void handleFindLocationRequestEvent(FindLocationRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Getting location of " + event.targetPerson.getName().fullName));
        loadPersonLocation(event.targetPerson);
    }
}
