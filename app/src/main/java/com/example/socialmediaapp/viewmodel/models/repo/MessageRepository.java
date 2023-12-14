package com.example.socialmediaapp.viewmodel.models.repo;

import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.repo.callback.DataEmit;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.HashMap;

public class MessageRepository extends Repository<MessageItem> {
    private class IncomingMessageProcessor implements DataEmitProcessor {
        @Override
        public void onResponse(DataEmit res) {
            String type = res.getType();
            if (!type.equals("new message"))
                return;
            MessageItem messageItem = (MessageItem) res.getData().get("item");
            loadedItems.add(0, messageItem);
            HashMap<String, Object> m = new HashMap<>();
            m.put("offset", 0);
            m.put("length", 1);
            itemUpdate.setValue(new Update(Update.Op.ADD, m));
        }
    }

    public MessageRepository(DataAccessHandler<MessageItem> dataAccessHandler) {
        super(dataAccessHandler);
        dataEmitProcessors.add(new IncomingMessageProcessor());
    }
}
