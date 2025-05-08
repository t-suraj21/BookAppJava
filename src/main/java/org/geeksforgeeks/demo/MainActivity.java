package org.geeksforgeeks.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// MainActivity handles the UI and API request to fetch books from Google Books API
public class MainActivity extends AppCompatActivity {

    // Variables for networking and UI components
    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private ImageButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initializing UI components
        progressBar = findViewById(R.id.progressBar);
        searchEdt = findViewById(R.id.searchEditText);
        searchBtn = findViewById(R.id.searchButton);

        // Setting click listener on the search button
        searchBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar while searching
            String query = searchEdt.getText().toString();
            if (query.isEmpty()) {
                searchEdt.setError("Please enter search query"); // Error if input is empty
                return;
            }
            getBooksInfo(query); // Fetch books based on the search query
        });
    }

    // Function to fetch book data from Google Books API
    private void getBooksInfo(String query) {
        bookInfoArrayList = new ArrayList<>(); // Initialize the book list
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.getCache().clear(); // Clear cache before making a new request

        // Construct API URL using the search query
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Make API request to get book data
        JsonObjectRequest booksObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar after fetching data
                    try {
                        JSONArray itemsArray = response.getJSONArray("items"); // Get book items array
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemsObj = itemsArray.getJSONObject(i);
                            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");

                            // Extract book details with default values
                            String title = volumeObj.optString("title", "N/A");
                            String subtitle = volumeObj.optString("subtitle", "N/A");
                            JSONArray authorsArray = volumeObj.optJSONArray("authors");
                            String publisher = volumeObj.optString("publisher", "N/A");
                            String publishedDate = volumeObj.optString("publishedDate", "N/A");
                            String description = volumeObj.optString("description", "N/A");
                            int pageCount = volumeObj.optInt("pageCount", 0);

                            // Get book thumbnail if available
                            JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                            String thumbnail = (imageLinks != null) ? imageLinks.optString("thumbnail", "") : "";

                            // Get book preview and info links
                            String previewLink = volumeObj.optString("previewLink", "");
                            String infoLink = volumeObj.optString("infoLink", "");

                            // Get buy link from sale info (if available)
                            JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
                            String buyLink = (saleInfoObj != null) ? saleInfoObj.optString("buyLink", "") : "";

                            // Convert authors JSONArray to ArrayList<String>
                            ArrayList<String> authorsArrayList = new ArrayList<>();
                            if (authorsArray != null) {
                                for (int j = 0; j < authorsArray.length(); j++) {
                                    authorsArrayList.add(authorsArray.optString(j, "Unknown"));
                                }
                            }

                            // Create BookInfo object with fetched details
                            BookInfo bookInfo = new BookInfo(
                                    title, subtitle, authorsArrayList, publisher, publishedDate,
                                    description, pageCount, thumbnail, previewLink, infoLink, buyLink
                            );

                            // Add book details to the list
                            bookInfoArrayList.add(bookInfo);
                        }

                        // Set up RecyclerView with BookAdapter to display the book list
                        RecyclerView recyclerView = findViewById(R.id.rv);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        BookAdapter adapter = new BookAdapter(bookInfoArrayList, this);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "No Data Found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle API request error
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add request to the Volley queue
        queue.add(booksObjRequest);
    }
}