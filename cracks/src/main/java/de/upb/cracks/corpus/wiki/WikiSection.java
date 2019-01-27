package de.upb.cracks.corpus.wiki;

public class WikiSection {

    private String title;
    private String text;

    public WikiSection(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "WikiSection{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

}
