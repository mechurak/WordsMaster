package com.shimnssso.wordsmaster.data;

public class DbMeta {
    public static final String DATABASE_NAME = "word.db";
    public static final int DATABASE_VERSION = 1;

    public static final class GlobalTableMeta {
        public static final String TABLE_NAME = "global";

        public static final String ID = "_id";
        public static final String MODE = "mode";
        public static final String CUR_ID = "cur_id";
    }

    public static final class WordTableMeta {
        public static final String TABLE_NAME = "word";

        public static final String ID = "_id";
        public static final String SPELLING = "spelling";
        public static final String PHONETIC = "phonetic";
        public static final String MEANING = "meaning";
        public static final String AUDIO_PATH = "audio";
        public static final String CATEGORY = "category";
    }

    public static final String[][] tempBook = {
        {"word1", "phonetic1", "meaning1", "path1", "default"},
        {"word2", "phonetic2", "meaning2", "path2", "default"},
        {"word3", "phonetic3", "meaning3", "path3", "default"},
        {"word4", "phonetic4", "meaning4", "path4", "default"},
        {"word5", "phonetic5", "meaning5", "path5", "default"},
        {"word6", "phonetic6", "meaning6", "path6", "default"},
        {"word7", "phonetic7", "meaning7", "path7", "default"},
        {"word8", "phonetic8", "meaning8", "path8", "default"},
        {"word9", "phonetic9", "meaning9", "path9", "default"},
        {"word10", "phonetic10", "meaning10", "path10", "default"},
        {"word11", "phonetic11", "meaning11", "path11", "default"},
        {"word12", "phonetic12", "meaning12", "path12", "default"},
    };
}
