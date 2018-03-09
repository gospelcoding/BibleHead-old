package org.gospelcoding.biblehead;


public class Language implements Comparable<Language> {

    private String code;
    private String name;

    public Language(String code, String name) {
        this.code = denullify(code);
        this.name = denullify(name);
    }

    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Language other) {
        return name.compareToIgnoreCase(other.getName());
    }

    public boolean equals(Language other) {
        return code.equals(other.getCode());
    }

    private String denullify(String s) {
        return (s==null) ? "" : s;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
