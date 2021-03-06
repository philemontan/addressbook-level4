package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ReadOnlyPerson;

//@@author sidhmads
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code LocationCommand}.
 */
public class LocationCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToFind = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<String> list = new ArrayList<>();
        list.add("1");
        LocationCommand locationCommand = prepareCommand(list);

        String expectedMessage = String.format(LocationCommand.MESSAGE_FIND_LOCATION_SUCCESS);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        List<ReadOnlyPerson> person = new ArrayList<>();
        person.add(personToFind);
        expectedModel.findLocation(person);

        assertCommandSuccess(locationCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        List<String> list = new ArrayList<>();
        list.add(outOfBoundIndex.toString());
        LocationCommand locationCommand = prepareCommand(list);

        assertCommandFailure(locationCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        showFirstPersonOnly(model);

        ReadOnlyPerson personToFind = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<String> list = new ArrayList<>();
        list.add("1");
        LocationCommand locationCommand = prepareCommand(list);

        String expectedMessage = String.format(LocationCommand.MESSAGE_FIND_LOCATION_SUCCESS);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showFirstPersonOnly(expectedModel);
        List<ReadOnlyPerson> person = new ArrayList<>();
        person.add(personToFind);
        expectedModel.findLocation(person);

        assertCommandSuccess(locationCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        List<String> list = new ArrayList<>();
        list.add(outOfBoundIndex.toString());
        LocationCommand locationCommand = prepareCommand(list);

        assertCommandFailure(locationCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        List<String> first = new ArrayList<>();
        first.add("1");
        LocationCommand findFirstCommand = new LocationCommand(first);
        List<String> second = new ArrayList<>();
        second.add("2");
        LocationCommand findSecondCommand = new LocationCommand(second);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        List<String> firstCopy = new ArrayList<>();
        firstCopy.add("1");
        LocationCommand findFirstCommandCopy = new LocationCommand(firstCopy);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    /**
     * Returns a {@code LocationCommand} with the parameter {@code index}.
     */
    private LocationCommand prepareCommand(List<String> index) {
        LocationCommand locationCommand = new LocationCommand(index);
        locationCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return locationCommand;
    }
}
