package breeze.emulate.position.widget.AppEditTextDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import breeze.emulate.position.R;

public class AppEditTextDialog {

    private final Context context;
    private final AlertDialog.Builder alertDialog;
    private AlertDialog dialog;
    private View basicView;

    protected AppEditTextDialog(Context context) {
        this.context = context;
        this.alertDialog = new AlertDialog.Builder(context);
        this.basicView = LayoutInflater.from(context).inflate(R.layout.view_edit_dialog,null);
        this.alertDialog.setView(this.basicView);
    }

    public void show() {
        dialog = this.alertDialog.create();
        dialog.show();
    }

    public void dismiss() {
        if (dialog!=null) {
            dialog.dismiss();
        }
    }

    public static class Builder implements IAppEditTextDialog {

        private AppEditTextDialog appEditTextDialog;

        public Builder(Context context) {
            this.appEditTextDialog = new AppEditTextDialog(context);
        }

        @Override
        public AppEditTextDialog build() {
            return appEditTextDialog;
        }

        @Override
        public Builder setTitle(String title) {
            TextView textView = this.appEditTextDialog.basicView.findViewById(R.id.dialog_title);
            textView.setText(title);
            return this;
        }

        @Override
        public Builder setSubTitle(String subTitle) {
            TextView textView = this.appEditTextDialog.basicView.findViewById(R.id.dialog_subtitle);
            textView.setText(subTitle);
            textView.setVisibility(View.VISIBLE);
            return this;
        }

        @Override
        public Builder setPositiveButton(String text, onFinishInputClickListener listener) {
            EditText editText = this.appEditTextDialog.basicView.findViewById(R.id.dialog_editext);
            Button button = this.appEditTextDialog.basicView.findViewById(R.id.dialog_positive_button);
            button.setText(text);
            button.setOnClickListener(v -> {
                String s = editText.getText().toString();
                listener.onFinish(s,appEditTextDialog);
            });
            return this;
        }

        public Builder setCancelable(boolean b) {
            appEditTextDialog.dialog.setCancelable(b);
            return this;
        }

        public Builder setMessage(String str) {
            TextView textView = appEditTextDialog.basicView.findViewById(R.id.dialog_subtitle);
            textView.setVisibility(View.VISIBLE);
            textView.setText(str);
            return this;
        }
    }

    public interface onFinishInputClickListener {
        void onFinish(String editText, AppEditTextDialog dialog);
    }

}
