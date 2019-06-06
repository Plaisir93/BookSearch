package com.pcp.booksearch;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Book {
    private String openLibraryId;
    private String author;
    private String title;

    public String getOpenLibraryId(){
        return openLibraryId;
    }

    public String getAuthor(){
        return author;
    }

    public String getTitle(){
        return title;
    }

    public String getCoverUrl(){
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-M.jpg?default=false";
    }

    public String getLargeCoverUrl(){
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }

    // Returns a book given the expected object fields
    public static Book fromJson(JSONObject jsonObject){
        Book book = new Book();
        try{
            // Deserialize json into object field s
            // Check if a cover edition is available
            if (jsonObject.has("cover_edition_key")){
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            }else if(jsonObject.has("edition_key")){
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }
            book.title = jsonObject.has("title_suggest")? jsonObject.getString("title_suggest"):"";
            book.author = getAuthor(jsonObject);
        }catch (JSONException e){
            e.printStackTrace();
            return  null;
        }
        // return the new object
        return book;
    }

    // Return comma seprted author list when there is more than one author
        private static String getAuthor (JSONObject jsonObject){
        try{
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int numAuthors = authors.length();
            final String[] authorStrings = new String [numAuthors];
            for (int i=0; i<numAuthors; i++){
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        }catch (JSONException e){
            return "";
        }
    }

    // Decodes array of book json results into business model objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray){
        ArrayList<Book> books = new ArrayList<Book>(jsonArray.length());
        // process each result result in json array, decode and convert to business
        // object
        for (int i=0; i<jsonArray.length(); i++){
            JSONObject bookJson = null;
            try{
                bookJson = jsonArray.getJSONObject(i);
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }
            Book book = Book.fromJson(bookJson);
            if(book != null){
                books.add(book);
            }
        }
        return books;
    }
}
