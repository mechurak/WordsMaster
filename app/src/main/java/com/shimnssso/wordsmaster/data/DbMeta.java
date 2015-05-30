package com.shimnssso.wordsmaster.data;

import com.shimnssso.wordsmaster.R;

/**
 * Created by Shim on 2015-05-26.
 */
public class DbMeta {
    public static final String DATABASE_NAME = "word.db";
    public static final int DATABASE_VERSION = 1;

    public static final class CategoryTableMeta {
        public static final String TABLE_NAME = "category";

        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String SIZE = "size";
    }

    public static final class WordTableMeta {
        public static final String TABLE_NAME = "word";

        public static final String ID = "_id";
        public static final String SPELLING = "spelling";
        public static final String MEANING = "meaning";
        public static final String PHONETIC = "phonetic";
        public static final String AUDIO_PATH = "audio";
        public static final String CATEGORY = "category";

        public static final String[] WORD_COLUMNS = {WordTableMeta.SPELLING, WordTableMeta.PHONETIC, WordTableMeta.MEANING};
        public static final int[] ID_COLUMNS = {R.id.spelling, R.id.phonetic, R.id.meaning};
    }

    public static final String[][] tempBook = {
        {"word1", "phonetic1", "meaning1", "path1", "1"},
        {"word2", "phonetic2", "meaning2", "path2", "1"},
        {"word3", "phonetic3", "meaning3", "path3", "1"},
        {"word4", "phonetic4", "meaning4", "path4", "1"},
        {"word5", "phonetic5", "meaning5", "path5", "1"},
        {"word6", "phonetic6", "meaning6", "path6", "1"},
        {"word7", "phonetic7", "meaning7", "path7", "1"},
        {"word8", "phonetic8", "meaning8", "path8", "1"},
        {"word9", "phonetic9", "meaning9", "path9", "1"},
        {"word10", "phonetic10", "meaning10", "path10", "1"},
        {"word11", "phonetic11", "meaning11", "path11", "1"},
        {"word12", "phonetic12", "meaning12", "path12", "1"},
    };
}
