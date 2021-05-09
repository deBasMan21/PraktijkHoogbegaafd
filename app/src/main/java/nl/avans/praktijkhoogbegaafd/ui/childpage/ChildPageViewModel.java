package nl.avans.praktijkhoogbegaafd.ui.childpage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChildPageViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ChildPageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}