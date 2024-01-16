package com.example.socialmediaapp.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.models.NotificationModel;
import com.example.socialmediaapp.application.repo.core.NotificationRepository;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.TimeParserUtil;
import com.example.socialmediaapp.view.AvatarView;
import com.example.socialmediaapp.view.container.ClickablePanel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ItemHolder> {
   private NotificationRepository repository;
   private List<RecyclerViewExtra> topViews, endViews;
   private Context context;

   public NotificationAdapter(Context context, NotificationRepository repository, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
      this.context = context;
      this.topViews = topViews;
      this.endViews = endViews;
      this.repository = repository;
   }

   @Override
   public NotificationAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
         LayoutInflater inflater = LayoutInflater.from(context);
         item = inflater.inflate(R.layout.item_notification, null, false);
         NotificationModel model = repository.get(pos - topViews.size());
         initViewModel((ClickablePanel) item, model);
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

   private void initViewModel(ClickablePanel item, NotificationModel model) {
      AvatarView avatarView = item.findViewById(R.id.avatar_view);
      TextView content = item.findViewById(R.id.notify_content);
      TextView time = item.findViewById(R.id.cnt_time);
      View icon = item.findViewById(R.id.noti_type_logo);
      content.setText(model.getContent());
      time.setText(TimeParserUtil.parseTime(model.getTime()));

      if (model.getAvatar() != null) {
         avatarView.setBackgroundContent(new BitmapDrawable(context.getResources(), model.getAvatar()), -1);
      }

      Bundle action = model.getAction();
      String type = action.getString("type");
      switch (type) {
         case "comment":
            String commnentId = action.getString("comment id");
            String postId = action.getString("post id");
            Integer accessId = action.getInt("access id");
            item.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  HomePage homePage = (HomePage) context;
                  homePage.openPostDetailsFragment(postId, accessId, commnentId);
               }
            });
            icon.setBackgroundResource(R.drawable.comment);
            break;
         case "friend-request":
            icon.setBackgroundResource(R.drawable.friend);
            item.setOnClickListener(view -> {
               String userId = action.getString("user id");
               Bitmap bitmap = model.getAvatar();
               UserBasicInfoModel userBasicInfo = new UserBasicInfoModel();
               userBasicInfo.setId(userId);
               userBasicInfo.setAvatar(bitmap);
               userBasicInfo.setFullname(action.getString("fullname"));
               HomePage homePage = (HomePage) context;
               homePage.openViewProfileFragment(userBasicInfo);
            });
            break;
         default:
            item.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  Toast.makeText(context, "Not supported", Toast.LENGTH_SHORT).show();
               }
            });
            icon.setBackgroundResource(R.drawable.comment);
            break;
      }
   }

   @Override
   public int getItemCount() {
      return topViews.size() + repository.length() + endViews.size();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      public ItemHolder(View itemView) {
         super(itemView);
      }
   }
}