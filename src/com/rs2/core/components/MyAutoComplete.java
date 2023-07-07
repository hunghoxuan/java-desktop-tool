package com.rs2.core.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.ArrayUtils;

import javafx.collections.transformation.SortedList;

import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

import com.rs2.Main;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.core.settings.Settings;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.Utils;

import java.awt.Point;
import java.awt.Rectangle;

//hold local params (data) for each tab.
public class MyAutoComplete {
    private final JTextComponent textField;
    public MyPane myPane;

    private Window container;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;

    private TreeSet<String> suggestionsWords = new TreeSet<String>();
    // private JFrame autoSuggestionPopUpWindow;
    private String typedWord;
    private final Collection<String> dictionary = new LinkedList<String>(); // temporary data for current suggestions
    public Map<String, Object> commands = new LinkedHashMap<String, Object>(); // full hiearchy of data
    private int currentIndexOfSpace, tW, tH;

    public static String[] startChars = Settings.autoCompleteStartChars;
    public static String endChar = Settings.autoCompleteEndChar;
    public boolean autoCompleteFullTextSearch = Settings.autoCompleteFullTextSearch;
    public boolean autoCompleteCaseSensitive = false;
    public int MaxSuggestions = Settings.MaxSuggestions;
    public Method actionPerform = null;
    public String lastKeyword = "";
    public String lastCommand = "";
    public boolean temporaryKeyword = false; // if true, then ignore keyword when replace text
    public boolean isActive = true; // active = false -> stop functioning.

    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            refreshSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            refreshSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            refreshSuggestions();
        }
    };

    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;

    public MyAutoComplete(JTextComponent textField, Collection<String> words) {
        this(textField, Main.mainScreen.getFrame(), words, Settings.ColorReadOnlyBG, Color.BLUE, Color.RED, 1);
    }

    public MyAutoComplete(JTextComponent textField, Window mainWindow, Collection<String> words, Color popUpBackground,
            Color textColor, Color suggestionFocusedColor, float opacity) {
        this.textField = textField;
        this.suggestionsTextColor = textColor;
        this.container = mainWindow;
        this.suggestionFocusedColor = suggestionFocusedColor;
        this.textField.getDocument().addDocumentListener(documentListener);

        setDictionary(words);

        typedWord = "";
        currentIndexOfSpace = 0;
        tW = 0;
        tH = 0;

        if (autoSuggestionPopUpWindow == null) {
            autoSuggestionPopUpWindow = new JWindow(mainWindow);
            autoSuggestionPopUpWindow.setOpacity(opacity);
            autoSuggestionPopUpWindow.setAlwaysOnTop(true);
        }

        if (suggestionsPanel == null) {
            suggestionsPanel = new JPanel();
            suggestionsPanel.setLayout(new GridLayout(0, 1));
            suggestionsPanel.setBackground(popUpBackground);
        }

        addKeyBindingToRequestFocusInPopUpWindow();
    }

    public void hide() {
        hidePopUpWindow();
    }

    public void clear() {
        dictionary.clear();
        hidePopUpWindow();
    }

    public void show(Collection<String> words) {
        setDictionary(words);
        initSuggestions("");
        showPopUpWindow();
    }

    public void show() {
        initSuggestions("");
        showPopUpWindow();
    }

    public void show(String typedWord) {
        initSuggestions(typedWord);
        showPopUpWindow();
    }

    public void clearSuggestions() {
        suggestionsPanel.removeAll();
        tW = 0; // recalculate popup window width and height --> important
        tH = 0;
    }

    public boolean isSuggestionsVisible() {
        if (autoSuggestionPopUpWindow != null)
            return autoSuggestionPopUpWindow.isVisible();
        return false;
    }

    public void setActionPerform(Method actionPerform) {
        this.actionPerform = actionPerform;
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (suggestionsPanel != null) {

                    List<MyAutoCompleteLabel> sls = getAddedSuggestionLabels();
                    boolean isFocused = false;
                    for (int i = 0; i < sls.size(); i++) {
                        MyAutoCompleteLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            isFocused = true;
                            break;
                        }
                    }
                }
            }
        });

        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
                "Down");
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                "Cancel");
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
                "Cancel");
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true),
                "Cancel");
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true),
                "Cancel");
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true), "OK");

        textField.getActionMap().put("Down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {// focuses the first label on popwindow
                focusPopUpWindow();
            }
        });

        textField.getActionMap().put("OK", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {// focuses the first label on popwindow
                List<MyAutoCompleteLabel> sls = getAddedSuggestionLabels();
                if (sls.size() > 0) {
                    setText(sls.get(0).getText());
                }
            }
        });

        textField.getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {// focuses the first label on popwindow
                hidePopUpWindow();
                ;
            }
        });

        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down");
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "Up");

        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "Cancel");

        suggestionsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true), "Ok");
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "Ok");
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Ok");

        suggestionsPanel.getActionMap().put("Down", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {
                List<MyAutoCompleteLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {// more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        MyAutoCompleteLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                refreshSuggestions();
                                focusPopUpWindow(lastFocusableIndex);
                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            if (i < max) {
                                focusPopUpWindow(i);
                                lastFocusableIndex = i;
                                break;
                            } else {
                                hidePopUpWindow();
                                break;
                            }
                        }
                    }
                } else {// only a single suggestion was given
                    refreshSuggestions();
                }
            }
        });

        suggestionsPanel.getActionMap().put("Up", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {
                List<MyAutoCompleteLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {// more than 1 suggestion
                    for (int i = max - 1; i >= 0; i--) {
                        MyAutoCompleteLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == 0) {
                                lastFocusableIndex = max - 1;
                                sl.setFocused(false);
                                refreshSuggestions();
                                focusPopUpWindow(lastFocusableIndex);
                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex >= i) {
                            if (i >= 0) {
                                focusPopUpWindow(i);
                                lastFocusableIndex = i;
                                break;
                            } else {
                                lastFocusableIndex = max - 1;
                                hidePopUpWindow();
                                break;
                            }
                        }
                    }
                } else {// only a single suggestion was given
                    refreshSuggestions();
                }
            }

        });

        suggestionsPanel.getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                hide();
            }

        });

        suggestionsPanel.getActionMap().put("Ok", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                clear();
            }
        });
    }

    private void setFocusToTextField() {
        if (container != null) {
            container.toFront();
            container.requestFocusInWindow();
        }
        textField.requestFocusInWindow();
    }

    public List<MyAutoCompleteLabel> getAddedSuggestionLabels() {
        List<MyAutoCompleteLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof MyAutoCompleteLabel) {
                MyAutoCompleteLabel sl = (MyAutoCompleteLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void refreshSuggestions() {
        refreshSuggestions(getCurrentlyTypedWord());
    }

    private void refreshSuggestions(String typedWord) {
        if (!isActive)
            return;
        clearSuggestions(); // remove previos words/jlabels that were added

        boolean added = wordTyped(typedWord);

        if (!added) {
            hidePopUpWindow();
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    protected void addWordToSuggestions(String word) {
        addWordToSuggestions(word, "");
    }

    protected void addWordToSuggestions(String word, String typedKeyword) {
        suggestionsWords.add(word);
        if (suggestionsPanel.getComponentCount() < MaxSuggestions) {
            MyAutoCompleteLabel suggestionLabel = new MyAutoCompleteLabel(Utils.getLabelHtml(word, typedKeyword),
                    suggestionFocusedColor, suggestionsTextColor, this);

            calculatePopUpWindowSize(suggestionLabel);

            suggestionsPanel.add(suggestionLabel);
        }
    }

    public String getCurrentlyTypedWord() {// get newest word after last white spaceif any or the first word if no white
                                           // spaces
        String text = textField.getText().trim();
        String wordBeingTyped = "";
        String lastChar = "";
        int lastIndex = 0;

        if (text.contains("@")) {
            lastIndex = text.lastIndexOf("@");
            lastChar = "@";
        } else {
            for (String tryStartChar : startChars) {
                if (text.contains(tryStartChar)) {
                    if (text.lastIndexOf(tryStartChar) > lastIndex) {
                        lastChar = tryStartChar;
                        lastIndex = text.lastIndexOf(tryStartChar);
                    }
                }
            }
        }

        if (lastIndex > 0) {
            if (lastIndex < text.length() - 1) {
                currentIndexOfSpace = lastIndex;
                wordBeingTyped = text.substring(lastIndex + 1).trim();
            } else {
                currentIndexOfSpace = lastIndex;
                wordBeingTyped = text.substring(lastIndex).trim();
            }
            // System.out.println(text + " " + wordBeingTyped);
            if (!wordBeingTyped.equalsIgnoreCase(lastChar))
                wordBeingTyped = wordBeingTyped.replaceAll(lastChar, "");
        }

        if (wordBeingTyped.isEmpty())
            wordBeingTyped = text.trim();

        if (wordBeingTyped.length() > 200) // avoid too long words
            return "";
        return wordBeingTyped;
    }

    private void calculatePopUpWindowSize(JLabel label) {
        // so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width;
        }
        tH += label.getPreferredSize().height;
    }

    public Point getParentPosition() {
        return Utils.getComponentLocation(textField);
    }

    private void hidePopUpWindow() {
        if (autoSuggestionPopUpWindow != null)
            autoSuggestionPopUpWindow.setVisible(false);
        // setFocusToTextField(); --> cause flickering / recursive focus lost ?
    }

    private void focusPopUpWindow() {
        focusPopUpWindow(0);
    }

    private void focusPopUpWindow(int i) {
        if (suggestionsPanel.getComponentCount() == 0) {
            return;
        }
        suggestionsPanel.requestFocusInWindow();
        autoSuggestionPopUpWindow.toFront();
        autoSuggestionPopUpWindow.setAlwaysOnTop(true);
        autoSuggestionPopUpWindow.requestFocusInWindow();

        ((MyAutoCompleteLabel) suggestionsPanel.getComponent(i)).setFocused(true);
        suggestionsPanel.getComponent(i).requestFocusInWindow();
    }

    private void showPopUpWindow() {
        tW = 400;
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);

        if (textField instanceof JTextArea || textField instanceof JTextPane) {
            autoSuggestionPopUpWindow.setMaximumSize(new Dimension(400, 35));
            autoSuggestionPopUpWindow.setSize(tW, tH);
        } else {
            autoSuggestionPopUpWindow.setMaximumSize(new Dimension(textField.getWidth(), 35));
            autoSuggestionPopUpWindow.setSize(tW, tH);
        }
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        Point parentPosition = getParentPosition();

        int parentX = parentPosition.x;
        int parentY = parentPosition.y;

        if (textField instanceof JTextArea || textField instanceof JTextPane) {
            Rectangle rect = null;
            try {
                rect = textField.getUI().modelToView(textField, textField.getCaret().getDot());// get carets position
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            windowX = (int) (rect.getX());
            windowY = (int) (rect.getY() + (rect.getHeight()));
            windowX = windowX + parentX;
            windowY = windowY + parentY;
        } else {

            windowX = parentX;
            windowY = parentY + textField.getHeight() + 2;
        }

        autoSuggestionPopUpWindow.setAlwaysOnTop(true);
        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        int width = textField.getWidth();
        if (windowX > parentX)
            width = width - (windowX - parentX);

        if (textField instanceof JTextArea || textField instanceof JTextPane) {
            autoSuggestionPopUpWindow.setMaximumSize(new Dimension(400, 35));
            autoSuggestionPopUpWindow.setSize(tW, tH);
        } else {
            autoSuggestionPopUpWindow.setMaximumSize(new Dimension(width, 35));
            autoSuggestionPopUpWindow.setSize(tW, tH);
        }

        autoSuggestionPopUpWindow.setSize(width, tH);
        autoSuggestionPopUpWindow.setBackground(Settings.ColorReadOnlyBG);
        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();
    }

    public void setWords(Collection<String> words) {
        setDictionary(words);
    }

    public void setDictionary(String command) {
        command = command.toLowerCase();
        if (commands.containsKey(command)) {
            if (commands.get(command) instanceof Collection)
                setDictionary((Collection<String>) commands.get(command));
            else if (commands.get(command) instanceof Map)
                setDictionary(((HashMap) commands.get(command)).keySet());
        } else {
            for (String key : commands.keySet()) {
                if (commands.get(key) instanceof Map) {
                    if (((HashMap) commands.get(key)).containsKey(command)) {
                        Collection<String> data = (Collection<String>) ((HashMap) commands.get(key)).get(command);
                        setDictionary(data);

                        if (key.equalsIgnoreCase("table")) {
                            setDictionary("column", data);
                            setDictionary("field", data);
                            Map<String, List<String>> autoCompletes = DataViewerService
                                    .getAutoCompletes(data.toArray(new String[data.size()]));
                            setDictionary("data", autoCompletes);
                        }

                        break;
                    }
                }
            }
        }
    }

    public void setCommand(String command) {
        lastCommand = command;
        setDictionary(command);
    }

    public void setDictionary(Collection<String> words) {
        dictionary.clear();
        if (words == null) {
            return;
        }
        for (String word : words) {
            dictionary.add(word.toLowerCase());
        }
    }

    public void setDictionary(String command, Object words) {
        setDictionary(-1, command, words);
    }

    public void setDictionary(Integer index, String command, Object words) {
        if (commands == null) {
            commands = new LinkedHashMap<String, Object>();
        }

        if (words instanceof Collection) {
            Collection<String> lowerCaseWords = new LinkedList<String>();
            for (String word : (Collection<String>) words) {
                lowerCaseWords.add(word.toLowerCase());
            }
            if (index > -1)
                Utils.insertElementToHashMap(commands, index, command.toLowerCase(),
                        lowerCaseWords);
            else
                commands.put(command.toLowerCase(), lowerCaseWords);
        } else {
            Map<String, Object> lowerCaseWords = new LinkedHashMap<String, Object>();
            for (String word : ((Map<String, Object>) words).keySet()) {
                lowerCaseWords.put(word.toLowerCase(), ((Map<String, Object>) words).get(word));
            }
            if (index > -1)
                Utils.insertElementToHashMap(commands, index, command.toLowerCase(), lowerCaseWords);
            else
                commands.put(command.toLowerCase(), lowerCaseWords);
        }

    }

    public JWindow getAutoSuggestionPopUpWindow() { // to close suggestion window after selection.
        return autoSuggestionPopUpWindow;
    }

    // public Window getContainer() {
    // return container;
    // }

    public JTextComponent getTextField() {
        return textField;
    }

    public void setText(String suggestedWord) {
        suggestedWord = Utils.getLabelOriginal(suggestedWord); // clean up all html tags

        String typedWord = getCurrentlyTypedWord();
        // if load from file
        if (suggestedWord.startsWith("@file:") || suggestedWord.startsWith("@include:")
                || suggestedWord.startsWith("@open:")
                || suggestedWord.toLowerCase().startsWith("c:\\")
                || suggestedWord.toLowerCase().startsWith("...")) {
            String file = suggestedWord.contains(":") ? Utils.substringBetween(suggestedWord, ":", "") : suggestedWord;
            if (file.startsWith("..."))
                file = Utils.getFileNameFull(file);
            String content = Utils.getContentFromFile(file);

            if (suggestedWord.startsWith(Settings.paramCommandStart)) {
                textField.setText(content);
                if (myPane != null)
                    myPane.autoCompleteActionPerformed(suggestedWord);
            } else {
                String text = textField.getText();

                String t = text.substring(0, text.lastIndexOf(typedWord));
                String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, "-- " + file);
                textField.setText(tmp + "\n" + content + MyAutoComplete.endChar);
                lastCommand = "";
                lastKeyword = "";
                clear();
            }
            return;
        }

        // TODO:
        // if (typedWord.startsWith("@") && commands.containsKey(lastCommand) &&
        // commands.get(lastCommand) instanceof Map
        // && ((Map) commands.get(lastCommand)).containsKey(lastKeyword)) {
        // Collection<String> data = (Collection<String>) ((Map)
        // commands.get(lastCommand)).get(lastKeyword);
        // clearSuggestions();
        // show(data);
        // return;
        // }

        // if normal suggested word
        if (suggestedWord.contains(Settings.lookupKeyValueSeperator)) { // if this is data, not table or field
            suggestedWord = MyService.getKeyFromDisplayKeyValue(suggestedWord);
            suggestedWord = "'" + suggestedWord.trim() + "'";
        } else if (suggestedWord.contains("@") && !suggestedWord.startsWith("@")) { // if this is data, not table or
                                                                                    // field
            suggestedWord = suggestedWord.split("@")[0].trim();
        } else {
            lastKeyword = suggestedWord;
        }

        String text = textField.getText();
        String t = text.substring(0, text.lastIndexOf(typedWord));
        String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord)
                .replace("\t", " ").trim();

        if (temporaryKeyword) {
            if (!text.endsWith(" ") && !text.endsWith("."))
                text = text + " ";
            tmp = text + suggestedWord;
            tmp = tmp.replace("\t", " ");
            temporaryKeyword = false;
        }

        if (actionPerform != null) {
            Utils.invokeMethod(actionPerform, this, new String[] { suggestedWord });
        }

        textField.setText(tmp + MyAutoComplete.endChar);
        clear();
    }

    public boolean isCommand(String suggestedWord) {
        return suggestedWord.startsWith(Settings.paramCommandStart) || commands.containsKey(suggestedWord);
    }

    public void addToDictionary(String word) {
        dictionary.add(word.toLowerCase());
    }

    public boolean wordTyped(String typedWord) {
        if (!isActive)
            return false;

        typedWord = typedWord.toLowerCase();
        if (Utils.inArray(typedWord, new String[] { "*" })) // special chars that break expression check
            return false;

        if (dictionary.contains(typedWord)) { // if keyword is typed manually, not select from auto complete list ->
                                              // must assign lastKeyword to impact next autocomplete
            lastKeyword = typedWord;
            typedWord = "";
        }

        System.out.println("Typed word: [" + typedWord + "] " + " Last Keyword: [" + lastKeyword + "]"
                + " Last command: [" + lastCommand + "] " + (temporaryKeyword ? "true" : "false"));

        if (typedWord.isEmpty()) {
            return false;
        }

        if (temporaryKeyword && !typedWord.equals(lastKeyword))
            temporaryKeyword = false;

        if (lastKeyword.equals(typedWord)) {
            // System.out.println("here 1");
            return initSuggestions("");
        } else if (!lastKeyword.isEmpty() && !lastCommand.isEmpty() &&
                (Utils.endsWith(typedWord, new String[] { ",", "." }))) {
            // System.out.println("here 2");
            setCommand(lastCommand); // continue with last command
            typedWord = typedWord.substring(typedWord.length() - 1);
            temporaryKeyword = true;
            return initSuggestions("");
        } else if (!lastKeyword.isEmpty() && !lastCommand.isEmpty() &&
                (Utils.endsWith(typedWord, new String[] { "=" }))) {
            // System.out.println("here 3");
            if (!typedWord.equals("=")) {
                setDictionary(typedWord.substring(0, typedWord.length() - 1));
            } else
                setDictionary(lastKeyword); // continue with last command
            typedWord = typedWord.substring(typedWord.length() - 1);
            temporaryKeyword = true;
            return initSuggestions("");
        }

        // temporaryKeyword = false;
        // lastKeyword = typedWord;
        if (commands != null && commands.size() > 0) {
            if (typedWord.equals(Settings.paramCommandStart)) {
                setDictionary(commands.keySet());
            } else if (Utils.inArray(typedWord, new String[] { "from", "join" })) {
                if (commands.containsKey("table")) {
                    setCommand("table");
                    temporaryKeyword = true;
                    typedWord = "";
                }

            } else if (Utils.inArray(typedWord, new String[] { "where", "and", "or", "by", "(" })) {
                if (commands.containsKey("table") && commands.get("table") instanceof Map) {
                    Map<String, Collection<String>> map = (Map<String, Collection<String>>) commands.get("table");
                    if (map.containsKey(lastKeyword)) {
                        lastCommand = lastKeyword;
                    }

                    setCommand(lastCommand);

                    temporaryKeyword = true;
                    typedWord = "";
                }

            } else {
                boolean found = false;
                for (String startCommand : commands.keySet()) {
                    if (
                    // startCommand.toLowerCase().equalsIgnoreCase(typedWord.toLowerCase() + ":") ||
                    typedWord.toLowerCase().contains(startCommand.toLowerCase())) {
                        typedWord = Utils.replaceBetween(typedWord, startCommand, "")
                                .replace(Settings.paramCommandStart, "")
                                .replace(":", "");
                        setCommand(startCommand);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (typedWord.startsWith(Settings.paramCommandStart)) {
                        setDictionary(commands.keySet());
                    }
                }
            }
        }

        return initSuggestions(typedWord);
    }

    private boolean initSuggestions(String typedWord) {
        boolean suggestionAdded = false;
        typedWord = typedWord.trim();

        String[] keywords = typedWord.split(" ");
        List<String> matchedWords = new LinkedList<String>();
        int maxMatched = 0;

        for (String word : dictionary) {// get words in the dictionary which we added
            int matched = 0;
            if (word.toLowerCase().startsWith(typedWord.toLowerCase())) {
                matched = 1000;
            } else if (word.toLowerCase().contains(typedWord.toLowerCase())) {
                matched = 100;
            } else {
                for (String keyword : keywords) {// each string in the word
                    boolean keyWordMatched = false;
                    if (!keyword.isEmpty()
                            && !keyword.startsWith(Settings.paramCommandStart)) {
                        keyWordMatched = true;
                        if (autoCompleteFullTextSearch) {
                            if (!word.toLowerCase().contains(keyword.toLowerCase()))
                                keyWordMatched = false;
                            else if (word.toLowerCase().startsWith(keyword.toLowerCase())) {
                                matched += 2;
                            } else if (word.toLowerCase().contains(keyword.toLowerCase())) {
                                matched += 1;
                            }
                        } else {
                            for (int i = 0; i < keyword.length(); i++) {// each string in the word
                                if (!keyword.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)),
                                        i)) { // check for match
                                    keyWordMatched = false;
                                    break;
                                }
                            }
                        }
                    } else if (keyword.isEmpty()) {
                        keyWordMatched = true;
                    }

                    if (keyWordMatched) {
                        matched += 5; // count how many words matched
                    }
                }
            }

            if (matched > 0) {
                if (typedWord.isEmpty()) {
                    matchedWords.add(word);
                    if (matchedWords.size() > Settings.MaxSuggestions)
                        break;
                } else if (matched > maxMatched
                // || (matched == maxMatched && maxMatched > 0)
                ) {
                    if (matchedWords.size() > Settings.MaxSuggestions)
                        matchedWords.remove(matchedWords.size() - 1);
                    matchedWords.add(0, word);
                    maxMatched = matched;
                } else {
                    if (matchedWords.size() > Settings.MaxSuggestions)
                        matchedWords.remove(matchedWords.size() - 1);
                    matchedWords.add(word);
                }
            }
        }

        int i = 0;
        for (String matchedWord : matchedWords) {
            suggestionAdded = true;
            if (i > Settings.MaxSuggestions)
                break;
            addWordToSuggestions(matchedWord, typedWord);
        }

        return suggestionAdded;
    }
}