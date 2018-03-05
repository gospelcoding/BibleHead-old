package org.gospelcoding.biblehead;


import org.json.JSONException;
import org.json.JSONObject;

public class DBLBibleBook {

    private String dam;
    private String bookId;
    private String bookName;

    public DBLBibleBook(JSONObject bookJson) throws JSONException {
        dam = bookJson.getString("dam_id");
        bookId = bookJson.getString("book_id");
        bookName = bookJson.getString("book_name");
    }

    public String getDam(){ return dam; }
    public String getBookId(){ return bookId; }
    public String getBookName(){ return bookName; }

    public String toString(){ return getBookName(); }
}
