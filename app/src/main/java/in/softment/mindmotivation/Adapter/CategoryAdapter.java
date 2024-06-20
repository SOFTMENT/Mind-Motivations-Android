package in.softment.mindmotivation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import in.softment.mindmotivation.Model.CategoryModel;
import in.softment.mindmotivation.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<CategoryModel> categoryList;
    private final LayoutInflater inflater;
    private final CategoryClickListener clickListener;

    public interface CategoryClickListener {
        void onCategoryClick(CategoryModel category);
    }

    public CategoryAdapter(List<CategoryModel> categoryList, Context context, CategoryClickListener clickListener) {
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.category_view, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel current = categoryList.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final RoundedImageView categoryImage;
        final TextView title;
        final TextView totalTracks;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.textViewCategory);
            totalTracks = itemView.findViewById(R.id.tracks);
            itemView.setOnClickListener(this);
        }

        void bind(CategoryModel category) {
            title.setText(category.getTitle());
            totalTracks.setText(category.getTracks()+" Tracks");
            if (category.getThumbnail() != null && !category.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(category.getThumbnail())
                        .placeholder(R.drawable.placeholder)
                        .into(categoryImage);
            }
        }

        @Override
        public void onClick(View v) {
            clickListener.onCategoryClick(categoryList.get(getAdapterPosition()));
        }
    }
}
