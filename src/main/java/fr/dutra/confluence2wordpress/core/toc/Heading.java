/**
 * Copyright 2011-2012 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package fr.dutra.confluence2wordpress.core.toc;

import java.util.ArrayList;
import java.util.List;

import fr.dutra.confluence2wordpress.core.author.Author;


public class Heading {

	private final int level;
	
    private String anchor;

    private String label;

    private Author author;

	private Heading parent;
	
    private List<Heading> children = new ArrayList<Heading>();

    public Heading(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

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

	public Heading getParent() {
		return parent;
	}

	public void setParent(Heading parent) {
		this.parent = parent;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(int i = 0; i < level; i ++) {
			sb.append("    ");
		}
		sb.append(level);
		sb.append(" ");
		sb.append(label);
		sb.append(" [");
		sb.append(anchor);
		sb.append("]");
		for (Heading child : children) {
			sb.append(child.toString());
		}
		return sb.toString();
	}

}
