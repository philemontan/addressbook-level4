package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.TypicalPersons.BIRTHDAY;
import static seedu.address.testutil.TypicalPersons.MEETING;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysEvent;

import org.junit.Test;

import guitests.guihandles.EventCardHandle;
import seedu.address.model.person.event.Event;
import seedu.address.model.person.event.ReadOnlyEvent;
import seedu.address.testutil.EventBuilder;

public class EventCardTest extends GuiUnitTest {

    @Test
    public void display() {
        Event event = new EventBuilder(MEETING).build();
        EventCard eventCard = new EventCard(event, 1);
        uiPartRule.setUiPart(eventCard);
        assertCardDisplay(eventCard, event, 1);

        // changes made to Event reflects on card
        guiRobot.interact(() -> {
            event.setHeader(BIRTHDAY.getHeader());
            event.setDesc(BIRTHDAY.getDesc());
            event.setEventDate(BIRTHDAY.getEventDate());
        });
        assertCardDisplay(eventCard, event, 1);
    }

    @Test
    public void equals() {
        Event event = new EventBuilder().build();
        EventCard eventCard = new EventCard(event, 0);

        // same person, same index -> returns true
        EventCard copy = new EventCard(event, 0);
        assertTrue(eventCard.equals(copy));

        // same object -> returns true
        assertTrue(eventCard.equals(eventCard));

        // null -> returns false
        assertFalse(eventCard.equals(null));

        // different types -> returns false
        assertFalse(eventCard.equals(0));

        // different person, same index -> returns false
        Event differentEvent = new EventBuilder().withHeader("differentHeader").build();
        assertFalse(eventCard.equals(new EventCard(differentEvent, 0)));

        // same person, different index -> returns false
        assertFalse(eventCard.equals(new EventCard(event, 1)));
    }

    /**
     * Asserts that {@code personCard} displays the details of {@code expectedPerson} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(EventCard eventCard, ReadOnlyEvent expectedEvent, int expectedId) {
        guiRobot.pauseForHuman();

        EventCardHandle eventCardHandle = new EventCardHandle(eventCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", eventCardHandle.getId());

        // verify person details are displayed correctly
        assertCardDisplaysEvent(expectedEvent, eventCardHandle);
    }
}
