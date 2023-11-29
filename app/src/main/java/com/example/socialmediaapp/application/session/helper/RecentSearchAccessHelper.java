package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecentSearchAccessHelper extends DataAccessHelper<UserBasicInfo> {

    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private AppDatabase db = ApplicationContainer.getInstance().database;
    private UserBasicInfoDao userBasicInfoDao;
    private DtoConverter dtoConverter;

    public RecentSearchAccessHelper() {
        userBasicInfoDao = db.getUserBasicInfoDao();
        dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
    }


    @Override
    public List<UserBasicInfo> tryToFetchFromLocalStorage(Bundle query) {
        List<UserBasicInfo> result = new ArrayList<>();
        List<com.example.socialmediaapp.application.entity.UserBasicInfo> userBasicInfos = userBasicInfoDao.getRecentSearchItems(session.getId());

        for (com.example.socialmediaapp.application.entity.UserBasicInfo user : userBasicInfos) {
            UserBasicInfo userBasicInfo = new UserBasicInfo();
            userBasicInfo.setFullname(user.getFullname());
            userBasicInfo.setAlias(user.getAlias());
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(user.getAvatarUri()));
            result.add(userBasicInfo);
        }
        return result;
    }

    @Override
    public Bundle fetchFromServer(Bundle query) throws IOException {
        Bundle result = new Bundle();
        Call<List<UserBasicInfoBody>> req = retrofit.create(UserApi.class).fetchRecentSearch();
        Response<List<UserBasicInfoBody>> res = req.execute();
        List<UserBasicInfoBody> users = res.body();
        List<com.example.socialmediaapp.application.entity.UserBasicInfo> batch = new ArrayList<>();
        for (UserBasicInfoBody u : users) {
            batch.add(dtoConverter.convertToUserBasicInfo(u, session.getId()));
        }
        db.runInTransaction(new Runnable() {
            @Override
            public void run() {
                userBasicInfoDao.insertAll(batch);
            }
        });
        result.putInt("count loaded", batch.size());
        return result;
    }

    @Override
    public UserBasicInfo uploadToServer(Bundle query) throws IOException, FileNotFoundException {
        String userAlias = query.getString("user alias");
        Call<UserBasicInfoBody> req = retrofit.create(UserApi.class).addToRecentSearch(userAlias);
        Response<UserBasicInfoBody> res = req.execute();
        return dtoConverter.convertToModelUserBasicInfo(res.body());
    }

    public String deleteRecentSearchItem(String userAlias) throws IOException {
        Call<ResponseBody> req = retrofit.create(UserApi.class).deleteRecentSearch(userAlias);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : " Failed";
    }

    @Override
    public void clean() {
        ArrayList<String> cached = dtoConverter.getCachedFiles();
        for (String fn : cached) {
            File file = new File(fn);
            file.delete();
        }
    }
}
