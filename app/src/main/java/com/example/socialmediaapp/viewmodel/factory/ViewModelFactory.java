package com.example.socialmediaapp.viewmodel.factory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.example.socialmediaapp.viewmodel.fragment.MainCommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.CreatePostViewModel;
import com.example.socialmediaapp.viewmodel.LoginFormViewModel;
import com.example.socialmediaapp.viewmodel.fragment.PostDetailsFragmentViewModel;
import com.example.socialmediaapp.viewmodel.messenger.MainMessageFragmentViewModel;
import com.example.socialmediaapp.viewmodel.RegistrationViewModel;
import com.example.socialmediaapp.viewmodel.SetupInformationViewModel;
import com.example.socialmediaapp.viewmodel.UpdateAvatarViewModel;
import com.example.socialmediaapp.viewmodel.UpdateBackgroundViewModel;

public class ViewModelFactory extends AbstractSavedStateViewModelFactory {

   public ViewModelFactory(SavedStateRegistryOwner owner, Bundle defaultArgs) {
      super(owner, defaultArgs);
   }


   @NonNull
   @Override
   protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
      if (modelClass.isAssignableFrom(LoginFormViewModel.class)) {
         return (T) new LoginFormViewModel(handle);
      } else if (modelClass.isAssignableFrom(CreatePostViewModel.class)) {
         return (T) new CreatePostViewModel(handle);
      } else if (modelClass.isAssignableFrom(MainCommentFragmentViewModel.class)) {
         return (T) new MainCommentFragmentViewModel(handle);
      } else if (modelClass.isAssignableFrom(UpdateAvatarViewModel.class)) {
         return (T) new UpdateAvatarViewModel(handle);
      } else if (modelClass.isAssignableFrom(UpdateBackgroundViewModel.class)) {
         return (T) new UpdateBackgroundViewModel(handle);
      } else if (modelClass.isAssignableFrom(SetupInformationViewModel.class)) {
         return (T) new SetupInformationViewModel(handle);
      } else if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
         return (T) new RegistrationViewModel(handle);
      } else if (modelClass.isAssignableFrom(MainMessageFragmentViewModel.class)) {
         return (T) new MainMessageFragmentViewModel(handle);
      } else if (modelClass.isAssignableFrom(PostDetailsFragmentViewModel.class)) {
         return (T) new PostDetailsFragmentViewModel(handle);
      }

      return null;
   }
}