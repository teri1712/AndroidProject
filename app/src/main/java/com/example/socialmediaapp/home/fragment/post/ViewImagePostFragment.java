package com.example.socialmediaapp.home.fragment.post;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.PostHandlerStore;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.view.button.LikeButton;
import com.example.socialmediaapp.view.button.PostButton;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.view.container.DragPanel;
import com.example.socialmediaapp.view.textview.PostContentTextVIew;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.view.action.LikeHelper;
import com.example.socialmediaapp.models.post.ImagePostModel;
import com.example.socialmediaapp.viewmodel.item.PostViewModel;

public class ViewImagePostFragment extends Fragment implements FragmentAnimation {

   private Rect bound;
   private Bitmap image;
   private ViewGroup commandContent;

   public ViewImagePostFragment(Rect bound, Bitmap image) {
      this.bound = bound;
      this.image = image;
   }

   public ViewImagePostFragment() {
   }

   public static ViewImagePostFragment newInstance(HandlerAccess handlerAccess, Rect bound) {
      PostSessionHandler handler = (PostSessionHandler) handlerAccess.access();
      ImagePostModel imagePostModel = (ImagePostModel) handler.getPostData().getValue();
      ViewImagePostFragment fragment = new ViewImagePostFragment(bound, imagePostModel.getImage().getValue());
      fragment.handlerAccess = handlerAccess;
      Bundle args = new Bundle();
      args.putInt("access id", handlerAccess.getId());
      args.putString("post id", imagePostModel.getId());
      fragment.setArguments(args);
      return fragment;
   }

   private boolean willPerformStart;
   private HandlerAccess handlerAccess;
   private LiveData<HandlerAccess> handlerAccessLiveData;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      willPerformStart = false;
      Bundle args = getArguments();
      if (handlerAccess != null) {
         handlerAccessLiveData = new MutableLiveData<>(handlerAccess);
      } else {
         String postId = args.getString("post id");
         Integer accessId = args.getInt("access id");
         assert accessId != null;
         handlerAccessLiveData = PostHandlerStore
                 .getInstance()
                 .findHandlerAccess(postId, accessId);
      }
   }

   private ImageView zoom_out_image;
   private ClickablePanel viewImageFrame;
   private View command_panel;
   private boolean command_panel_is_enable;
   private DragPanel root;
   private PostContentTextVIew statusTextView;
   private TextView fullnameTextView;
   private View backgroundPanel;
   private PostViewModel viewModel;
   private TextView cntTimeTextView, cntLikeTextView, cntCommentTextView;
   private LikeButton like;
   private PostButton comment;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      root = (DragPanel) inflater.inflate(R.layout.fragment_view_image, container, false);
      zoom_out_image = root.findViewById(R.id.zoom_out_image);
      viewImageFrame = root.findViewById(R.id.view_image_frame);
      command_panel = root.findViewById(R.id.command_panel);
      statusTextView = root.findViewById(R.id.status_content);
      fullnameTextView = root.findViewById(R.id.fullname);
      backgroundPanel = root.findViewById(R.id.background_panel);
      commandContent = root.findViewById(R.id.command_content);
      cntTimeTextView = root.findViewById(R.id.cnt_time);
      cntLikeTextView = root.findViewById(R.id.cnt_like);
      cntCommentTextView = root.findViewById(R.id.cnt_comment);
      like = root.findViewById(R.id.like_button);
      comment = root.findViewById(R.id.comment_button);

      like.setWhite(true);
      command_panel_is_enable = true;
      if (savedInstanceState == null) {
         willPerformStart = image != null;
      } else {
         init();
      }
      initDragAction();
      return root;
   }

   @Override
   public void onStart() {
      super.onStart();
      if (willPerformStart) {
         performStart();
      }
   }

   private void initDragAction() {

      root.setDragHelper(new DragPanel.ChildBoundDragHelper(viewImageFrame));
      root.setDragListener(new DragPanel.DragListener() {
         @Override
         public void onStartDrag() {
            if (command_panel.getAlpha() != 0)
               command_panel.setAlpha(0);
            if (backgroundPanel.getAlpha() != 0.9f) backgroundPanel.setAlpha(0.9f);
         }

         @Override
         public void onDrag() {
            float cur_stran = viewImageFrame.getTranslationY();
            int h = viewImageFrame.getHeight();
            float alpha = 1 - Math.abs(cur_stran) / h;
            root.setAlpha(alpha);
         }

         @Override
         public void onRelease() {
            backgroundPanel.animate().alpha(1f).setDuration(200);
            command_panel.animate().alpha(1f).setDuration(200);
            root.setAlpha(1);

         }

         @Override
         public void onFinish() {
            FragmentActivity activity = getActivity();
            if (activity != null) {
               activity.getSupportFragmentManager()
                       .popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
         }
         @Override
         public void onAboutToFinish() {
            root.animate().alpha(0).setDuration(200).start();
         }
      });
   }

   public void initOnClick(HandlerAccess handlerAccess) {
      comment.setOnClickListener(view -> {
         HomePage activity = (HomePage) getActivity();
         activity.openCommentFragment(handlerAccess);
      });
      viewImageFrame.setOnClickListener(view -> {
         if (command_panel_is_enable) {
            command_panel.animate()
                    .alpha(0)
                    .setDuration(300)
                    .withEndAction(() -> command_panel.setVisibility(GONE))
                    .start();
         } else {
            command_panel.setVisibility(VISIBLE);
            command_panel.animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
         }
         command_panel_is_enable = !command_panel_is_enable;
      });
   }

   private void initViewModel(HandlerAccess handlerAccess) {
      HomePage homePage = (HomePage) getActivity();
      PostSessionHandler handler = handlerAccess.access();
      LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
      LiveData<String> hostName = homePage.getViewModel().getFullname();
      viewModel = new PostViewModel(handler.getPostData(), hostName);
      ImagePostModel postModel = (ImagePostModel) handler.getPostData().getValue();
      fullnameTextView.setText(postModel.getAuthor().getFullname());
      statusTextView.setStatusContent(postModel.getContent());
      if (postModel.getContent() == null) {
         commandContent.removeView(statusTextView);
         statusTextView = null;
      } else {
         statusTextView.setStatusContent(postModel.getContent());
      }

      LiveData<Bitmap> imageLiveData = postModel.getImage();
      imageLiveData.observe(lifecycleOwner, new Observer<Bitmap>() {
         @Override
         public void onChanged(Bitmap bitmap) {
            zoom_out_image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
         }
      });
      viewModel.getTime().observe(lifecycleOwner, s -> cntTimeTextView.setText(s));
      viewModel.getCountLikeContent().observe(lifecycleOwner, s -> {
         if (s.isEmpty()) {
            cntLikeTextView.setVisibility(GONE);
         } else {
            cntLikeTextView.setText(s);
            if (cntLikeTextView.getVisibility() == GONE) {
               cntLikeTextView.setVisibility(VISIBLE);
            }
         }
      });
      viewModel.getCountComment().observe(lifecycleOwner, integer -> {
         if (integer == 0) {
            cntCommentTextView.setVisibility(GONE);
         } else {
            String txt = integer + " comments";
            cntCommentTextView.setText(txt);
            if (cntCommentTextView.getVisibility() == GONE) {
               cntCommentTextView.setVisibility(VISIBLE);
            }
         }
      });

      like.initLikeView(lifecycleOwner, viewModel.getLike());
      like.setLikeHelper(new LikeHelper() {
         @Override
         public void like() {
            handler.doLike();
         }

         @Override
         public void unlike() {
            handler.doUnLike();
         }
      });

      initOnClick(handlerAccess);
   }

   @Override
   public void performEnd(Runnable endAction) {
      viewModel.clean();
      endAction.run();
   }

   public void init() {
      handlerAccessLiveData.observe(getViewLifecycleOwner(), new Observer<HandlerAccess>() {
         @Override
         public void onChanged(HandlerAccess HandlerAccess) {
            if (handlerAccess == null) {
               getActivity().getSupportFragmentManager()
                       .popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
               return;
            }
            initViewModel(HandlerAccess);
         }
      });
   }

   @Override
   public void performStart() {
      viewImageFrame.requestFocus();
      zoom_out_image.setImageDrawable(new BitmapDrawable(getResources(), image));
      ViewGroup.LayoutParams params = zoom_out_image.getLayoutParams();
      if (bound == null) {
         //click on avatar
         params.width = ViewGroup.LayoutParams.MATCH_PARENT;
         params.height = ViewGroup.LayoutParams.MATCH_PARENT;
         zoom_out_image.requestLayout();

         zoom_out_image.setAlpha(0.5f);
         backgroundPanel.animate()
                 .alpha(1)
                 .setDuration(100)
                 .start();
         zoom_out_image.animate()
                 .alpha(1)
                 .setDuration(200)
                 .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                       init();
                       command_panel.setVisibility(VISIBLE);
                       command_panel.animate().alpha(1).setDuration(150).start();
                    }
                 }).start();
         return;
      }

      int h = root.getHeight();
      params.width = (bound.right - bound.left);
      params.height = (bound.bottom - bound.top);
      zoom_out_image.requestLayout();

      int targetY = (h - params.height) / 2;
      int iniTranslationY = bound.top - targetY;
      zoom_out_image.setTranslationY(iniTranslationY);

      int time = 200 + Math.abs(targetY - bound.top) * 200 / Math.max(targetY, h - targetY);
      backgroundPanel.animate().alpha(1).setDuration(10).start();
      zoom_out_image.animate()
              .translationY(0)
              .setDuration(time)
              .setInterpolator(new DecelerateInterpolator())
              .withEndAction(new Runnable() {
                 @Override
                 public void run() {
                    zoom_out_image.requestLayout();
                    init();
                    command_panel.setVisibility(VISIBLE);
                    command_panel.animate().alpha(1).setDuration(150).start();
                 }
              }).start();


      // my new bie stuffs, better to use animator object
      Thread animator = new Thread(new Runnable() {
         double curHeight = params.height;
         double unitHeight = 1;
         int cntFrames = (int) ((h - curHeight) / unitHeight);

         @Override
         public void run() {
            for (int i = 0; i < cntFrames / 2; i++) {
               try {
                  Thread.sleep(0, 50 * 1000000 / cntFrames);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               curHeight += unitHeight;
               final int hei = (int) curHeight;
               zoom_out_image.post(new Runnable() {
                  @Override
                  public void run() {
                     params.height = hei;
                     zoom_out_image.requestLayout();
                  }
               });
            }
            zoom_out_image.post(new Runnable() {
               @Override
               public void run() {
                  root.setLayerType(View.LAYER_TYPE_NONE, null);
                  params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                  params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                  zoom_out_image.requestLayout();
               }
            });
         }
      });
      animator.setPriority(Thread.MAX_PRIORITY);
      animator.start();
   }
}