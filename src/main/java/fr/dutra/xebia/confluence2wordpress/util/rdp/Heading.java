package fr.dutra.xebia.confluence2wordpress.util.rdp;

import java.util.ArrayList;
import java.util.List;


public class Heading {

    private String anchor;

    private String label;

    private List<Heading> children = new ArrayList<Heading>();

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String url) {
        this.anchor = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Heading> getChildren() {
        return children;
    }

    public void addChild(Heading e) {
        children.add(e);
    }

}
