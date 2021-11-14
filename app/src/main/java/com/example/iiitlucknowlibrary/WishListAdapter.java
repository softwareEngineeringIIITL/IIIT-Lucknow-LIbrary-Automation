package com.example.iiitlucknowlibrary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iiitlucknowlibrary.UserPortal.BookList;

import com.example.iiitlucknowlibrary.administration.RemoveABook;
import com.example.iiitlucknowlibrary.ui.notifications.WishlistFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class WishListAdapter  extends RecyclerView.Adapter<WishListAdapter.MyViewHolder> {
    Context context;
    WishListAdapter myAdapter;
    ArrayList<Book> bookList;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId;

    public WishListAdapter(Context context, ArrayList<Book> book_list) {
        this.context = context;
        this.bookList = book_list;
    }
    @NonNull
    @Override
    public WishListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_booklist_row,parent,false);
        return  new WishListAdapter.MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Book book = bookList.get(position);
        holder.quantity.setText("Quantity: "+  book.getQuantity() );
        holder.author_name.setText("Author Name: "+ book.getAuthor());
        holder.book_status.setText("Status: "+  book.getStatus() );
        holder.book_id.setText("ID: "+  book.getBookID());
        Picasso.get().load(book.getImageUri()).into(holder.book_category_image);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Remove from WishList")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                bookList.remove(position);
                                notifyItemRemoved(position);
                               firebaseDatabase = FirebaseDatabase.getInstance();
                               userId  = auth.getCurrentUser().getUid();
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("user").child(userId).child("enrolment");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                          String rollNo = snapshot1.getValue().toString();
                                          String bookId = book.getBookID();
                                          DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("WishList").child(rollNo);
                                          reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                  if(snapshot2.exists()) {
                                                      if (bookId == null) {
                                                          progressDialog.dismiss();
                                                          Toast.makeText(context.getApplicationContext(), "Error in deletion", Toast.LENGTH_SHORT).show();
                                                      } else {
                                                          Query applesQuery = firebaseDatabase.getReference().child("WishList").child(rollNo).child(bookId);
                                                          applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                              @Override
                                                              public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                                  for (DataSnapshot appleSnapshot : snapshot3.getChildren()) {
                                                                      appleSnapshot.getRef().removeValue();
                                                                      notifyItemRemoved(position);
                                                                      progressDialog.dismiss();
                                                                      Toast.makeText(context, "Book is successfully Removed", Toast.LENGTH_SHORT).show();
                                                                  }
                                                              }

                                                              @Override
                                                              public void onCancelled(@NonNull DatabaseError error) {

                                                              }
                                                          });
                                                      }
                                                  }
                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError error) {

                                              }
                                          });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }
        });

    }
    @Override
    public int getItemCount() {
        return bookList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView quantity,book_id , book_status,author_name;
        CircleImageView book_category_image;
        public MyViewHolder(@NonNull View itemView ) {
            super(itemView);
            quantity = itemView.findViewById(R.id.quantity);
            author_name = itemView.findViewById(R.id.author_name);
            book_id = itemView.findViewById(R.id.book_id);
            book_status = itemView.findViewById(R.id.book_status);
            book_category_image = itemView.findViewById(R.id.book_category_image);

        }

    }

}
