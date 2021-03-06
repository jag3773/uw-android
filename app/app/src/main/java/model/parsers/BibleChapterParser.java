/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;

import org.json.JSONException;

import model.daoModels.BibleChapter;
import model.daoModels.Book;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class to parse Text of BibleChapters
 */
public class BibleChapterParser extends UWDataParser{

    public static BibleChapter parseBibleChapter(Book parent, String number, String text, String singleChapterBookName) throws JSONException{

        number = number.trim();
        BibleChapter chapter = new BibleChapter();
        chapter.setNumber(number);
        chapter.setSlug(number);
        chapter.setUniqueSlug(parent.getUniqueSlug() + number);
        chapter.setText(text);
        chapter.setBookId(parent.getId());
        chapter.setSingleChapterBookName(singleChapterBookName);

        return chapter;
    }
}
