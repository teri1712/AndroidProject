package com.example.socialmediaapp.viewmodels.factory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.example.socialmediaapp.viewmodels.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodels.CreatePostViewModel;
import com.example.socialmediaapp.viewmodels.EditInformationViewModel;
import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.LoginFormViewModel;
import com.example.socialmediaapp.viewmodels.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodels.RegistrationViewModel;
import com.example.socialmediaapp.viewmodels.SearchFragmentViewModel;
import com.example.socialmediaapp.viewmodels.SetupInformationViewModel;
import com.example.socialmediaapp.viewmodels.UpdateAvatarViewModel;
import com.example.socialmediaapp.viewmodels.UpdateBackgroundViewModel;
import com.example.socialmediaapp.viewmodels.ViewProfileViewModel;

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
        } else if (modelClass.isAssignableFrom(PostFragmentViewModel.class)) {
            return (T) new PostFragmentViewModel(handle);
        } else if (modelClass.isAssignableFrom(ViewProfileViewModel.class)) {
            return (T) new ViewProfileViewModel(handle);
        } else if (modelClass.isAssignableFrom(UpdateAvatarViewModel.class)) {
            return (T) new UpdateAvatarViewModel(handle);
        } else if (modelClass.isAssignableFrom(UpdateBackgroundViewModel.class)) {
            return (T) new UpdateBackgroundViewModel(handle);
        } else if (modelClass.isAssignableFrom(EditInformationViewModel.class)) {
            return (T) new EditInformationViewModel(handle);
        } else if (modelClass.isAssignableFrom(SetupInformationViewModel.class)) {
            return (T) new SetupInformationViewModel(handle);
        } else if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel(handle);
        } else if (modelClass.isAssignableFrom(SearchFragmentViewModel.class)) {
            return (T) new SearchFragmentViewModel(handle);
        }

        return (T) new HomePageViewModel(handle);
    }
}