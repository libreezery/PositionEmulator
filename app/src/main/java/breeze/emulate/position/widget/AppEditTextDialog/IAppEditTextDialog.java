package breeze.emulate.position.widget.AppEditTextDialog;

import android.view.View;

public interface IAppEditTextDialog {
    AppEditTextDialog build();
    AppEditTextDialog.Builder setTitle(String title);
    AppEditTextDialog.Builder setSubTitle(String subTitle);
    AppEditTextDialog.Builder setPositiveButton(String text, AppEditTextDialog.onFinishInputClickListener listener);


}
