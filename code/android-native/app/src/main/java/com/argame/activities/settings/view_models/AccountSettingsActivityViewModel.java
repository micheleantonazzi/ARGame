package com.argame.activities.settings.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.argame.utilities.Database;
import com.argame.utilities.data_structures.user_data.UserInterface;

public class AccountSettingsActivityViewModel extends ViewModel {

    private MutableLiveData<UserInterface> userData;

    public LiveData<UserInterface> getUserData() {
        if (userData == null)
            this.userData = new MutableLiveData<>(Database.getInstance().getUserData());

        return userData;
    }

    public void setUserData(UserInterface userData) {
        this.userData.setValue(userData);
    }
}
