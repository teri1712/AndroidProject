package com.example.socialmediaapp.home.fragment.extras;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.widget.EditText;

import androidx.lifecycle.LiveData;

import java.util.function.Function;

public class EditTextActionHelper {
   private Function<Bundle, LiveData<String>> actionOnEditText;
   private EditText commentEditText;

   public EditTextActionHelper(EditText commentEditText) {
      this.commentEditText = commentEditText;
   }

   public void setActionOnEditText(String name, Function<Bundle, LiveData<String>> action) {
      actionOnEditText = action;
      if (name == null) return;
      SpannableString spannableString = new SpannableString(name);

      BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#3F0866FF"));
      spannableString.setSpan(backgroundColorSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      commentEditText.setText(spannableString);
      commentEditText.setSelection(commentEditText.getText().length());

      commentEditText.requestFocus();
   }

   public LiveData<String> doAction(Bundle data) {
      if (actionOnEditText == null) return null;
      return actionOnEditText.apply(data);
   }
}
