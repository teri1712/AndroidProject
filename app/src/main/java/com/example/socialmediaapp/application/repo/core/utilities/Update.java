package com.example.socialmediaapp.application.repo.core.utilities;

import java.util.HashMap;
import java.util.Map;

public class Update {
   public enum Op {
      CHANGE, REMOVE, ADD, RECYCLE, END, HINT_UPDATE
   }

   public Op op;
   public Map<String, Object> data;

   public Update(Op op, Map<String, Object> data) {
      this.op = op;
      this.data = data;
   }
}
