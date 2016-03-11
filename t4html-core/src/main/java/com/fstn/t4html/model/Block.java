package com.fstn.t4html.model;

/**
 * Created by stephen on 11/03/2016.
 */
public class Block {
    private String name;
    private String verb;
    private String content;

    public Block(String name, String position, String content) {
        this.name = name;
        this.verb = position;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Block{" +
                "name='" + name + '\'' +
                ", verb='" + verb + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
