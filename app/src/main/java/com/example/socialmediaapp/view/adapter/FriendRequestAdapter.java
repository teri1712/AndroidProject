package com.example.socialmediaapp.view.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.application.repo.core.FriendRequestRepository;
import com.example.socialmediaapp.models.user.FriendRequestModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.button.RoundedButton;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ItemHolder> {
   private FriendRequestRepository repository;
   private Context context;
   private List<RecyclerViewExtra> topViews, endViews;

   public FriendRequestAdapter(Context context, FriendRequestRepository repository, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
      this.context = context;
      this.repository = repository;
      this.topViews = topViews;
      this.endViews = endViews;
   }

   @Override
   public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = new FrameLayout(parent.getContext());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      v.setLayoutParams(params);
      ItemHolder vh = new ItemHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ItemHolder holder, int pos) {
      ViewGroup proxyHolder = (ViewGroup) holder.itemView;
      proxyHolder.removeAllViews();

      View item;
      if (pos >= topViews.size() && pos < topViews.size() + repository.length()) {
         FriendRequestModel model = repository.get(pos - topViews.size());
         LayoutInflater inflater = LayoutInflater.from(context);
         View view = (ViewGroup) inflater.inflate(R.layout.friend_request_item, null, false);
         item = view;
         initViewModel(model, view);
      } else {
         RecyclerViewExtra extra = pos < topViews.size() ? topViews.get(pos) : endViews.get(pos - topViews.size() - repository.length());
         item = extra.getView();
         if (!extra.isConfigured()) {
            extra.configure(item);
            extra.setConfigured(true);
         }
      }
      ViewGroup preParentOfView = (ViewGroup) item.getParent();
      if (preParentOfView != null)
         preParentOfView.removeView(item);
      proxyHolder.addView(item);
   }

   private void initViewModel(FriendRequestModel friendRequestModel, View itemView) {

      TextView fullname = itemView.findViewById(R.id.fullname);
      TextView time = itemView.findViewById(R.id.cnt_time);
      CircleButton avatarButton = itemView.findViewById(R.id.avatar_button);
      RoundedButton accept = itemView.findViewById(R.id.accept_button);
      RoundedButton refuse = itemView.findViewById(R.id.refuse_button);

      UserBasicInfoModel userBasicInfoModel = friendRequestModel.getUserModel();
      fullname.setText(userBasicInfoModel.getFullname());
      time.setText(friendRequestModel.getTime());
      avatarButton.setBackgroundContent(new BitmapDrawable(context.getResources(), userBasicInfoModel.getAvatar()), -1);
      final String alias = userBasicInfoModel.getAlias();

      accept.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            repository.accept(alias).observe((LifecycleOwner) context, new Observer<String>() {
               @Override
               public void onChanged(String s) {
                  Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
               }
            });
         }
      });
      refuse.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            repository.reject(alias).observe((LifecycleOwner) context, new Observer<String>() {
               @Override
               public void onChanged(String s) {
                  Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
               }
            });
         }
      });
   }

   @Override
   public int getItemCount() {
      return topViews.size() + endViews.size() + repository.length();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      public ItemHolder(View itemView) {
         super(itemView);
      }
   }
}