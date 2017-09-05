package com.idragonit.inspection;

/**
 * Created by CJH on 2016.01.21.
 */
public interface Bridge {

    void onBack();
    void cleanBackStack();
    void switchTo(BaseFragment fragment, boolean cleanBackStack);

    void setProgressStatus(int progress);

}
