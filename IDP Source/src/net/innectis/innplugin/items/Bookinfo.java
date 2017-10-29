package net.innectis.innplugin.items;

import java.util.List;

/**
 *
 * @author Hret
 *
 *
 */
public class Bookinfo {

    private String author;
    private String title;
    private List<String> pages;

    public Bookinfo() {
    }

    public Bookinfo(String author, String title, List<String> pages) {
        this.author = author;
        this.title = title;
        this.pages = pages;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The amount of pages that is max allowed.
     * @return the pages.
     */
    public static int getMaxPages(){
        return 50;
    }

    /**
     * @return the pages
     */
    public List<String> getPages() {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(List<String> pages) {
        this.pages = pages;
    }
    
}
