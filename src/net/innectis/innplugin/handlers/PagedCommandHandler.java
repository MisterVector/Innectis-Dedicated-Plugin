package net.innectis.innplugin.handlers;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.player.chat.ChatColor;

/**
 *
 * @author AlphaBlend
 *
 * Used to handle commands that rely on page numbers
 */
public final class PagedCommandHandler {

    public static final int MAX_LINES_PER_PAGE = 12;

    private List<String> commandInfoLines = new ArrayList<String>();
    private List<String> originalInfoLines = new ArrayList<String>();
    private int pageNo = 0;
    private int startIdx = 0;
    private int endIdx = 0;
    private boolean isValidPage = false; // true if the input was isValidPage
    private int currentLinesPerPage = MAX_LINES_PER_PAGE;

    private boolean isPageTooHigh() {
        return ((pageNo - 1) * currentLinesPerPage) >= commandInfoLines.size();
    }

    private boolean isValidConditions() {
        return !((pageNo < 1) || isPageTooHigh());
    }

    private void recalculateIndexes() {
        if (isValidConditions()) {
            isValidPage = true;
            startIdx = ((pageNo - 1) * currentLinesPerPage);
            endIdx = ((startIdx + currentLinesPerPage - 1) > commandInfoLines.size() - 1 ? commandInfoLines.size() - 1 : startIdx + currentLinesPerPage - 1);
        } else {
            isValidPage = false;
        }
    }

    public PagedCommandHandler(int pageNo, List<String> info) {
        setPageData(info);
        setNewPage(pageNo);
    }

    /**
     * Sets new title, and information, to replace the current
     *
     * @param title
     * @param info
     */
    public void setPageData(List<String> info) {
        if (info != null) {
            commandInfoLines.addAll(info);
            originalInfoLines.addAll(info);
        }
    }

    /**
     * Sets a new page number for the data
     *
     * @param pageNo
     */
    public void setNewPage(int pageNo) {
        this.pageNo = pageNo;
        recalculateIndexes();
    }

    public void setNewLinesPerPage(int lines) {
        currentLinesPerPage = lines;
        recalculateIndexes();
    }

    /**
     * Adds any additional information into the paged command handler
     *
     * @param info
     */
    public void addToInfo(String info) {
        commandInfoLines.add(info);
        originalInfoLines.add(info);
    }

    /**
     * Adjusts the output to show a certain amount of entries per line
     * Ex. entry1, entry2
     *     entry3
     *
     * @param perLine
     */
    public void adjustEntriesPerLine(int perLine) {
        adjustEntriesPerLine(perLine, null);
    }

    /**
     * Adjusts the output to show a certain amount of entries per line
     * Ex. entry1, entry2
     *     entry3
     *
     * @param perLine
     * @param separateColor Specifies the color of the separator (comma)
     *                      use null to not specify a color
     */
    public void adjustEntriesPerLine(int perLine, ChatColor separateColor) {
        List<String> tempLines = new ArrayList<String>();
        String line = null;
        int incr = 0;

        // If the entries per line is greater than the info array count, set to
        // the element count (or it won't return anything at all)
        perLine = (perLine > originalInfoLines.size() ? originalInfoLines.size() : perLine);

        for (int i = 0; i < originalInfoLines.size(); i++) {
            if (line == null) {
                line = originalInfoLines.get(i);
            } else {
                line += (separateColor != null ? separateColor : "") + ", " + originalInfoLines.get(i);
            }

            incr++;

            if (incr == perLine) {
                tempLines.add(line);
                line = null;
                incr = 0;
            }
        }

        if (line != null) {
            tempLines.add(line);
        }

        commandInfoLines.clear();
        commandInfoLines.addAll(tempLines);
        recalculateIndexes();
    }

    /**
     * Returns both the title and the page data
     *
     * @return
     */
    public List<String> getParsedInfo() {
        List<String> lines = new ArrayList<String>();

        for (int i = startIdx; i <= endIdx; i++) {
            lines.add(commandInfoLines.get(i));
        }

        return lines;
    }

    public String getInvalidPageNumberString() {
        String errorText = null;

        if (isPageTooHigh()) {
            errorText = "Page number is too high.";
        } else if (pageNo < 1) {
            errorText = "Page cannot be less than 1.";
        }

        return errorText;
    }

    public boolean isValidPage() {
        return isValidPage;
    }

    public int getStartLine() {
        return startIdx + 1;
    }

    public int getEndLine() {
        return endIdx + 1;
    }

    public int getMaxPage() {
        int maxPage = 0;
        if (commandInfoLines.size() < currentLinesPerPage) {
            return 1;
        }
        int idx = commandInfoLines.size();

        while (idx > 0) {
            idx -= currentLinesPerPage;
            maxPage++;
        }

        return maxPage;
    }

}
