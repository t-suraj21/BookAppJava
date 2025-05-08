package org.geeksforgeeks.demo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final ArrayList<BookInfo> bookInfoArrayList;
    private final Context context;

    // Constructor to initialize adapter with data
    public BookAdapter(ArrayList<BookInfo> bookInfoArrayList, Context context) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each RecyclerView item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rv_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Get current book info
        BookInfo bookInfo = bookInfoArrayList.get(position);

        // Set data to UI components
        holder.nameTV.setText(bookInfo.getTitle());
        holder.publisherTV.setText(bookInfo.getPublisher());
        holder.pageCountTV.setText("Pages : " + bookInfo.getPageCount());
        holder.dateTV.setText("Published On : " + bookInfo.getPublishedDate());

        // Load image from URL using Glide
        Glide.with(context).load(bookInfo.getThumbnail()).into(holder.bookIV);

        // Set click listener to open BookDetails activity with data
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetails.class);
            intent.putExtra("title", bookInfo.getTitle());
            intent.putExtra("subtitle", bookInfo.getSubtitle());
            intent.putExtra("authors", bookInfo.getAuthors());
            intent.putExtra("publisher", bookInfo.getPublisher());
            intent.putExtra("publishedDate", bookInfo.getPublishedDate());
            intent.putExtra("description", bookInfo.getDescription());
            intent.putExtra("pageCount", bookInfo.getPageCount());
            intent.putExtra("thumbnail", bookInfo.getThumbnail());
            intent.putExtra("previewLink", bookInfo.getPreviewLink());
            intent.putExtra("infoLink", bookInfo.getInfoLink());
            intent.putExtra("buyLink", bookInfo.getBuyLink());

            // Start new activity with intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookInfoArrayList.size();
    }

    // ViewHolder class to hold UI elements for each item
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, publisherTV, pageCountTV, dateTV;
        ImageView bookIV;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components
            nameTV = itemView.findViewById(R.id.bookTitle);
            publisherTV = itemView.findViewById(R.id.publisher);
            pageCountTV = itemView.findViewById(R.id.pageCount);
            dateTV = itemView.findViewById(R.id.date);
            bookIV = itemView.findViewById(R.id.bookImage);
        }
    }
}