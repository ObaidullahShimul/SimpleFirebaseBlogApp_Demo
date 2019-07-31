package www.siit.com.simpleblogapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<Upload> uploadList;


    public MyAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.blog_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Upload upload=uploadList.get(i);
        myViewHolder.postTitle.setText(upload.getImageName());
        myViewHolder.postDesc.setText(upload.getdescription());
        //myViewHolder.userName.setText(upload.getUserName());
        Picasso.with(context)
                .load(upload.getImgeUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(myViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }


    //-----------MyViewHolder--------------
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postTitle;
        TextView postDesc;
        //TextView userName;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            //postTitle=itemView.findViewById(R.id.post_TitleId);
            //userName=itemView.findViewById(R.id.user_NameId);
            postTitle=itemView.findViewById(R.id.post_TitleId);
            postDesc=itemView.findViewById(R.id.post_DescId);
            imageView=itemView.findViewById(R.id.post_Image);
        }
    }
}
