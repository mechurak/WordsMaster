package com.shimnssso.wordsmaster;

public class Word {
    private String meaning;
    private String spelling;
    private String phonetic;
    private String audioPath;

    public Word(String meaning, String spelling, String phonetic, String audioPath) {
        this.meaning = meaning;
        this.spelling = spelling;
        this.phonetic = phonetic;
        this.audioPath = audioPath;
    }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public String getSpelling() { return spelling; }
    public void setSpelling(String spelling) { this.spelling = spelling; }
    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }
}
